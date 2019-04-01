package com.clarkez.kdbnio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang.StringUtils;
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
public class QIPCPasswordHandlerForClient extends ChannelInboundHandlerAdapter {

    private ByteBuf buf;
	private final static Logger log = LoggerFactory.getLogger(QIPCPasswordHandlerForClient.class);

	private boolean loggedIn = false;
	private String userpass;
	private final String UPEND_V6 = "\006\000"; // sent from latest c.java
	private final String UPEND_V3 = "\003\000"; // sent from Q 3.0
	private final String UPEND_V1 = "\001\000"; // sent from Q 2.6
	private final String UPEND_V0 = "\000";
	
	public QIPCPasswordHandlerForClient(String userpass) {
		if(!StringUtils.isEmpty(userpass)) {
			this.userpass = userpass;
		}else {
			this.userpass = System.getProperty("user.name");
		}
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
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	log.info("Channel InActive {}",ctx.channel());
    }
    
    @Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("Channel Active - Writing username and password");

		String s =userpass+UPEND_V1;
        byte[] b = s.getBytes(Charset.forName("ISO-8859-1"));
        final ByteBuf buf = ctx.alloc().buffer(b.length);
        buf.writeBytes(b);
		ctx.writeAndFlush(buf);	
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

    	if(loggedIn) {
    		log.info("Logged In Already");
    		ctx.fireChannelRead(msg);
    		return;
    	}
    	
        ByteBuf m = (ByteBuf) msg;
        
        if(m.readableBytes() > 0) {
        	String preHex = bytesToHex(m.toString(Charset.defaultCharset()).getBytes());
			log.info("Received {} Bytes {}",m.readableBytes(),preHex);
        }else {
        	log.info("Received {} Bytes",+m.readableBytes());
    		String s =userpass+UPEND_V0;
            byte[] b = s.getBytes(Charset.forName("ISO-8859-1"));
            final ByteBuf buf = ctx.alloc().buffer(b.length);
            buf.writeBytes(b);
    		ctx.writeAndFlush(buf);	    
    		m.release();
    		return;
        }

		byte loginstatus;
		String loginReply;
		if(0==m.readableBytes()) {     
        	return;
        }else {
            loginReply = m.toString(0,1,Charset.defaultCharset());        
            loginstatus = m.readByte();        	
        }
        
        if(loginstatus>0) {
        	loggedIn = true;
    		log.info("Successful LogIn Received {}", bytesToHex(new byte[]{loginstatus}));
    		log.info("Removing Password Handler");
            ctx.pipeline().remove(this);            
        }else {
        	log.info("Unsuccessful LogIn Received",loginReply);
        }
        m.release();
                
        if(!loggedIn) {
        	log.info("Not Logged in Requesting Disconnect");
        	ChannelFuture cf = ctx.close();            	
        }    
    }

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		log.error("Exception Caught",cause);
		ctx.close();
    }

}