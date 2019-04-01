package com.clarkez.kdbnio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.List;

import kx.c;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clarkez.kdbnio.qpic.QIPCMessage;

import static com.clarkez.kdbnio.util.HexUtils.bytesToHex;

/**
 * Handles a QIPC Protocol Messages 
 * <p> http://code.kx.com/wiki/Reference/ipcprotocol </p>
 * @author peter
 *
 */
public class QIPCDecoder extends ByteToMessageDecoder {
	private final static Logger log = LoggerFactory.getLogger(QIPCDecoder.class);

	private static final int HEADER_LEN = 8;
	c ipdDecoder = new c();
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		
		log.trace("Received "+in.readableBytes()+" Bytes  ");

		if(in.readableBytes()==0){
			return;
		}
		QIPCMessage o = decodeMessage(in);
		if(o!=null) {
			out.add(o);	
		}
	}
	protected QIPCMessage decodeMessage(ByteBuf in) throws c.KException, UnsupportedEncodingException {
		// Look up the header Bytes to get the length.
		if (in.readableBytes() < HEADER_LEN) {
			return null;
		}
		
    	String preHex = bytesToHex(in.toString(Charset.defaultCharset()).getBytes());
    	log.trace("Decoder Received "+in.readableBytes()+" Bytes - "+preHex);
    	
		in.markReaderIndex();

        int endian = in.readUnsignedByte();   	
        ByteOrder border = 1==endian?ByteOrder.LITTLE_ENDIAN:ByteOrder.BIG_ENDIAN;
        int msgType = in.readUnsignedByte(); 	
        int compression = in.readUnsignedByte();
        int buffer = in.readUnsignedByte();		        
        long msgLength= in.order(border).readUnsignedInt();

        if(in.readableBytes() < msgLength-HEADER_LEN) {
        	log.debug("Decoder Got msgLength "+msgLength +" and readable bytes of "+in.readableBytes() +" need "+ (msgLength-HEADER_LEN)+" bytes");
            in.resetReaderIndex();
        	return null;
        }
        in.resetReaderIndex();	

        // Convert the received data into a new BigInteger.
        byte[] decoded = new byte[(int) (msgLength)];
        
        in.readBytes(decoded);
        
        String hexe = bytesToHex(decoded);
    	log.trace("Decoder Received Completed Msg "+hexe);

		Object data;
		try {
			data = ipdDecoder.deserialize(decoded);
		}catch (c.KException kxe){
			data = kxe;
		}catch (IOException ioxe) {
			data = ioxe;
		}catch (Exception e){
			data = e;
		}
    	log.trace("Decoder Received Completed Data "+String.valueOf(data));

    	//TODO PJC Need to check that these messages are being parsed properly
		// Looks like msgType isn't being correctly set.

        QIPCMessage msg = new QIPCMessage(endian,msgType,compression,msgLength,data);
		return msg;
	}
	
	public boolean acceptInboundMessage(Object msg) throws Exception {
        return true;
    }


}
