package com.clarkez.kdbnio;

import java.util.Arrays;

import com.clarkez.kdbnio.client.QIPCIncomingListener;
import com.clarkez.kdbnio.qpic.QIPCMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kx.c;

import io.netty.channel.Channel;

public class SingleSocketTest {

	private final static Logger log = LoggerFactory.getLogger(QIPCClient.class);

	public static void main(String[] args) throws Exception {
		SingleSocketTest test =  new SingleSocketTest();
//		test.init2();
		Channel ch = test.init();
		test.go(ch);
	}

	private void go(Channel ch) {
		// is a wait needed?
		
	}


	private void init2() throws Exception {
		c cClient = new c("localhost",6780,"peter:peter");
		
		Object rc = cClient.k(".z.w");
               
        if(rc !=null && rc instanceof Integer) {
        	int handle = ((Integer)rc).intValue();
        	log.info(""+handle);
        }
	}


	private Channel init() throws Exception {
		QIPCClient c = new QIPCClient("127.0.0.1",6780,"peter:peter");
        Channel ch = c.connectClient(new QIPCIncomingListener() {

			@Override
			public void onAsyncMessage(Object msg) {
				if(msg instanceof QIPCMessage) {
					QIPCMessage qmsg = (QIPCMessage)msg;
					if(qmsg.getData() instanceof Object[] ) {
						log.info("Received Async Message "+Arrays.deepToString((Object[])qmsg.getData()));
					}else {
						log.info("Received Async Message "+String.valueOf(qmsg.getData()));
					}
				}else {
					log.info("Received Async Message "+String.valueOf(msg));

				}
			}

			@Override
			public Object onSyncMessage(Object msg) {
				log.info("Received Sync Message "+String.valueOf(msg));
				if(msg!=null) {
					return msg;
				}else {
					return Boolean.TRUE;
				}
			}
        	
        });
        
        Thread.sleep(1000);
        
        if(!ch.isOpen() || !ch.isActive()){
			log.info("Channel Not Active "+ch);
			return null;
        }else{
        	log.info("Channel Active "+ch);
        }
        
		log.info("Sending .z.w Message ");

		int handle = 0;
        Object rc = c.sendSyncMessage(".z.w".toCharArray(), ch);
        if(rc !=null && rc instanceof Integer) {
        	handle = ((Integer)rc).intValue();
			log.info("Received Handle "+handle);
        }else {
        	return null;
        }
        c.sendSyncMessage((".qnty.x:0;").toCharArray(), ch);
        c.sendSyncMessage((".qnty.p:{[msg] 0N!(msg;.qnty.x); neg["+handle+"](msg;"+handle+";`handle);:"+handle+"}").toCharArray(), ch);
        c.sendSyncMessage((".z.ts:{[] .qnty.p[\"timer-\"]}").toCharArray(), ch);
        c.sendSyncMessage(("system\"t 100\"").toCharArray(), ch);
        
        
        for(int i=0;i<100;i++) {
            log.info("Sending Sync message");
        	//Object counter = c.sendSyncMessage(("{[].qnty.x:(.qnty.x+1);:.qnty.x}[]").toCharArray(), ch);
        	// Add Async send to handle within the sync function
        	Object counter = c.sendSyncMessage(("{[].qnty.x:(.qnty.x+1);.qnty.p[\"sync\"];:.qnty.x}[]").toCharArray(), ch);

            if(counter !=null && counter instanceof Integer) {
            	int cc = ((Integer)counter).intValue();
    			log.info("Received Sync Response as Integer "+cc);
            }else if(counter !=null && counter instanceof Long) {
            	long cc = ((Long)counter).longValue();
    			log.info("Received Sync Response as Long "+cc);
            }else {
            	log.warn("Received unexpected Sync Response "+counter);
            }
    		Thread.sleep(500);
        }
        log.info("Disabling Timer");
        c.sendSyncMessage(("system\"t 0\"").toCharArray(), ch);
        return ch;
	}
}
