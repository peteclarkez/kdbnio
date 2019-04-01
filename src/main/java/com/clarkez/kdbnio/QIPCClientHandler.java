package com.clarkez.kdbnio;

import com.clarkez.kdbnio.client.QIPCIncomingListener;
import com.clarkez.kdbnio.qpic.QIPCMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import kx.QConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handles a server-side channel.
 */
public class QIPCClientHandler extends ChannelDuplexHandler {

	private final static Logger log = LoggerFactory.getLogger(QIPCClientHandler.class);
	Queue<QIPCMessage> requestQ = new ConcurrentLinkedQueue<QIPCMessage>();
	
	private QIPCIncomingListener listener;

	public QIPCClientHandler(QIPCIncomingListener listener) {
		this.listener = listener;
	}
	

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {        
    	if(!(msg instanceof QIPCMessage)) {
    		log.info("Unsupported Message type"+msg.getClass().toString());
    		return;
    	}

    	log.debug("channelRead Received Message "+msg.getClass().toString());
    	QIPCMessage qmsg = (QIPCMessage)msg;
    	
    	if(qmsg.getMsgType()== QConst.CALLTYPE_RESPONSE) {
			log.debug("Received Response Message From Q "+qmsg);
			
			QIPCMessage req = requestQ.poll();
			if(req.getResponseListener()!=null){
				req.getResponseListener().onResponse(req, qmsg);
			}else{
				log.warn("Received Unhandled Repsonse Message From Q "+qmsg);
			}
    	} else if(qmsg.getMsgType()==QConst.CALLTYPE_SYNC) {
    		Object data = Boolean.TRUE;
    		if(listener!=null) {
    			data = listener.onSyncMessage(qmsg.getData());
    		}else {
    			log.warn("Received Unhandled Sync Message From Q, Response with default answer "+qmsg);
    		}
    		QIPCMessage returnmsg =new QIPCMessage(QConst.ARCH_BIGENDIAN,QConst.CALLTYPE_RESPONSE,QConst.COMPRESSION_DISABLED,-1,data);
    		ctx.channel().write(returnmsg);
    		ctx.channel().flush();
    	}else {
    		if(listener!=null) {
    			listener.onAsyncMessage(qmsg.getData());
    		}else {
    			log.warn("Received Unhandled Async Message From Q "+qmsg);
    		}
    	}
    }

    @Override
    	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
	    	if(promise.isDone()){
	    		log.warn("Promise already done??? "+promise);
	    	}

	    	if(!(msg instanceof QIPCMessage)) {
	    		log.info("Unsupported Message type"+msg.getClass().toString());
	    		return;
	    	}
	    	QIPCMessage qmsg = (QIPCMessage)msg;
	    	log.trace("write Received QIPCMessage Message "+qmsg);
	    	if(qmsg.getMsgType()==QConst.CALLTYPE_SYNC){
	    		requestQ.add(qmsg);
	    	}
	    	ctx.write(qmsg,promise);

    	}
   
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
    	log.error("Exception Caught",cause);        
        ctx.close();
    }    
}