package com.clarkez.kdbnio;

import com.clarkez.kdbnio.server.QIPCServerAuthHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

import io.netty.channel.ChannelInboundHandlerAdapter;
import kx.c;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.clarkez.kdbnio.util.HexUtils.bytesToHex;

/**
 * Handles a QIPC Logon 
 * <p> http://code.kx.com/wiki/Reference/ipcprotocol </p>
 * <ul>
 * <li>0 - v2.5, no compression, no timestamp, no timespan, no uuid</li>
 * <li>1..2 - v2.6-2.8, compression, timestamp, timespan</li>
 * <li>3 - v3.0, compression, timestamp, timespan, uuid</li>
 * <li>6 - v3.4, compression, timestamp, timespan, uuid, and 1TB payload support</li>
 * </ul>
 */
public class QIPCPasswordHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf buf;
    private int msgLen=0;
	private final static Logger log = LoggerFactory.getLogger(QIPCPasswordHandler.class);
	private boolean loggedIn = false;
	private QIPCServerAuthHandler authHandler;

	public QIPCPasswordHandler(QIPCServerAuthHandler authHandler) {
		this.authHandler = authHandler;
	}

	@Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buf = ctx.alloc().buffer(64);        
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        buf.release();
        buf = null;
    }
    
    @Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	if(loggedIn) {
    		log.debug("Logged In Already");
    		ctx.fireChannelRead(msg);
    		return;
    	}
    	
        ByteBuf m = (ByteBuf) msg;
    	if(log.isDebugEnabled()) {
			log.debug("Received {} Bytes", m.readableBytes());
		}

        msgLen += m.readableBytes();
        buf.writeBytes(m);    
        byte lastByte = m.getByte(m.capacity()-1);
        m.release();
        
        
    	String hexe = bytesToHex(buf.toString(Charset.defaultCharset()).getBytes());
    	log.debug("Message Buffer {}",hexe);

    	if(0x00==lastByte) {
        	
        	String text = buf.toString(0,msgLen-2,Charset.defaultCharset());
        	String[] up = text.trim().split(":");
        	        	
        	String hex = bytesToHex(text.getBytes());
        	boolean logonOK;
        	if(up.length>1) {
        		logonOK = checkLogon(up[0],up[1]);
        	}else {
        		logonOK = checkLogon(up[0],null);
        	}

            ByteBuf buf = ctx.alloc().buffer(4);
            
            if(logonOK) {
            	buf.writeByte(text.charAt(0));
	        	log.info("Successful Logon Received " + text +" "+hex);
	        	loggedIn = true;
            }else {
//				// TODO - Figure how to return an access exception
//            	try {
//					byte[] access = new c().serialize(0, new Exception("access"),false);
//					buf.writeBytes(access)
//				} catch (UnsupportedEncodingException e) {
//	            	log.info("OOPS ");
//				}
            	log.info("Logon Failed " + text +" "+hex);
            	loggedIn = false;            	
            }
            ChannelFuture cfw = ctx.writeAndFlush(buf);
            buf.clear();
            msgLen=0;
            if(!loggedIn) {
            	log.info("Not Logged in Requesting Disconnect");
            	ChannelFuture cf = ctx.close();            	
            }
        }

    }

    private boolean checkLogon(String user, String password) {
		if(authHandler==null) {
			return user.equals(password);
		}else{
			return authHandler.authenticate(user,password);
		}
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
		log.error("Exception Caught",cause);
		ctx.close();
    }

}