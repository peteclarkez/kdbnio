package com.clarkez.kdbnio;

import com.clarkez.kdbnio.qpic.QIPCMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import kx.QConst;
import kx.c;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.clarkez.kdbnio.util.HexUtils.bytesToHex;


public class QIPCEncoder extends MessageToByteEncoder<Object> {
	private final static Logger log = LoggerFactory.getLogger(QIPCEncoder.class);

	c ipcencoder= new c();

	
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
    	
    	byte[] outbytes;
    	if(msg instanceof QIPCMessage) {
    		QIPCMessage qmsg = (QIPCMessage)msg;
    		log.debug("Received QIPCMessage to encoder {}",qmsg.getMsgType());
    		outbytes = ipcencoder.serialize(qmsg.getMsgType(), qmsg.getData(),false);
    	}else {
			log.debug("Received Other Object to encoder {}",msg);
			outbytes = ipcencoder.serialize(QConst.CALLTYPE_ASYNC, msg,false);
    	}
    	
    	out.writeBytes(outbytes);

    	if(log.isDebugEnabled()) {
			String preHex = bytesToHex(outbytes);
			log.info("Encoder Sending Message - "+preHex);
		}
    }

}
