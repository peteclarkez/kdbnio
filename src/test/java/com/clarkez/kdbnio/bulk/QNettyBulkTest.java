package com.clarkez.kdbnio.bulk;

import com.clarkez.kdbnio.client.QIPCIncomingListener;
import com.clarkez.kdbnio.util.NanoClockSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kx.c;

import io.netty.channel.Channel;
import com.clarkez.kdbnio.QIPCClient;

public class QNettyBulkTest {

	private final static Logger log = LoggerFactory.getLogger(QNettyBulkTest.class);

	private final static int WARMUP = 10000;
	private final static int MSGCOUNT= 100000;
	
	public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 6780;
        }
        QIPCClient c = new QIPCClient("localhost",port,null);
        Channel ch = c.connectClient(new QIPCIncomingListener() {

			@Override
			public void onAsyncMessage(Object msg) {
				log.info("Received Async Message "+String.valueOf(msg));
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

        defineTSTablesAndUpdates(c,ch);
        
        
        for(int i = 0;i<WARMUP;i++) {
        	//log.info("Sending {}",i);
        	sendTSUpdate(c,ch,i);
        }
        ch.flush();
        log.info("Finished WarmUp");
        long timein = System.nanoTime();
        for(int i = 0;i<MSGCOUNT;i++) {
        	//log.info("Sending {}",i);
        	sendTSUpdate(c,ch,i);
        }
        ch.flush();
        long timeout = System.nanoTime();
        
    	log.info("Total Time = {}ns",timeout-timein);
    	log.info("Avg Time = {}us",(timeout-timein)/MSGCOUNT/1000);
    	log.info("Rate = {} msg/sec",(long)(1000000/((timeout-timein)/MSGCOUNT/1000)));
    	
    	
    	getResults(c,ch);
    	
        //ch.closeFuture().sync();    	
        ch.close().sync();
        
        System.exit(0);
    	
    	
    }


	private static void sendUpdate(QIPCClient c, Channel ch, int i) {
		
		Object[] data = new Object[] {((i%2==0)?"EVEN":"ODD"),i,1000};
		Object[] func = new Object[] {"upd","trade",data};
		//c.sendSyncMessage(func, ch);
		c.sendASyncMessage(func, ch);
	}


	private static void defineTablesAndUpdates(QIPCClient c, Channel ch) {
		
    	log.info("Sending Table Definitions");

		c.sendSyncMessage("upd:{[t;x] t insert x}".toCharArray(),ch);
		c.sendSyncMessage("trade:([] sym:`$();id:`int$();volume:`int$())".toCharArray(),ch);
	}
	

	private static void sendTSUpdate(QIPCClient c, Channel ch, int i) {
		
		Object[] data = new Object[] {((i%2==0)?"EVEN":"ODD"),i,1000, NanoClockSource.getTime()};
		Object[] func = new Object[] {"upd","trade",data};
		//c.sendSyncMessage(func, ch);
		c.sendASyncMessage(func, ch);
	}


	private static void defineTSTablesAndUpdates(QIPCClient c, Channel ch) {
		
    	log.info("Sending Table Definitions");

		c.sendSyncMessage("upd:{[t;x] t insert (.z.p,x)}".toCharArray(),ch);
		c.sendSyncMessage("trade:([] time:`timestamp$();sym:`$();id:`int$();volume:`int$();genTime:`timestamp$())".toCharArray(),ch);
	}
	
	private static void getResults(QIPCClient c, Channel ch) {
		String query="{[x] c:count x;pp:(50 90 99 99.9 99.99); (`count`min`avg`max!(c,`long$0.001*(x[0];`long$avg x;`long$x[c-1]))),(`$string each pp)!`long$0.001*{[x;c;p] x[`int$(p%100)*c]}[x;c] each pp } (x where {[] x>0} x:asc raze value exec ts:`long$time-genTime from trade)";
		Object o = c.sendSyncMessage(query.toCharArray(),ch);
		c.Dict d = ((c.Dict)o);
		log.info("Got Dictionary {}",d.x);
		log.info("Got Dictionary {}",d.y);		
	}
}
