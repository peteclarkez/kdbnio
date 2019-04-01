package com.clarkez.kdbnio.bulk;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kx.c;
import kx.c.KException;

import com.clarkez.kdbnio.util.NanoClockSource;

public class QBulkTest {

	private final static Logger log = LoggerFactory.getLogger(QBulkTest.class);


	private final static int WARMUP = 10000;
	private final static int MSGCOUNT= 100000;
	
	public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 6780;
        }
        
        c conn = new c("localhost",port);
        
        defineTSTablesAndUpdates(conn);
        

        for(int i = 0;i<WARMUP;i++) {
        	//log.info("Sending {}",i);
        	sendTSUpdate(conn,i);
        }
    	log.info("Finished WarmUp");

        long timein = System.nanoTime();
        for(int i = 0;i<MSGCOUNT;i++) {
        	//log.info("Sending {}",i);
        	sendTSUpdate(conn,i);
        }
        long timeout = System.nanoTime();
        
    	log.info("Total Time = {}ns",timeout-timein);
    	log.info("Avg Time = {}us",(timeout-timein)/MSGCOUNT/1000);
    	log.info("Rate = {} msg/sec",(long)(1000000/((timeout-timein)/MSGCOUNT/1000)));

    	getResults(conn);
        conn.close();
        
        System.exit(0);
    }


	private static void sendUpdate(c conn, int i) throws IOException {
		
		Object[] data = new Object[] {((i%2==0)?"EVEN":"ODD"),i,1000};
		Object[] func = new Object[] {"upd","trade",data};
		//c.sendSyncMessage(func, ch);
		conn.ks(func);
	}


	private static void defineTablesAndUpdates(c conn) throws KException, IOException {
		
    	log.info("Sending Table Definitions");

		conn.k("upd:{[t;x] t insert x}".toCharArray());
		conn.k("trade:([] sym:`$();id:`int$();volume:`int$())".toCharArray());
	}
	

	private static void sendTSUpdate(c conn, int i) throws IOException {
		
		Object[] data = new Object[] {((i%2==0)?"EVEN":"ODD"),i,1000,NanoClockSource.getTime()};
		Object[] func = new Object[] {"upd","trade",data};
		conn.ks(func);
	}


	private static void defineTSTablesAndUpdates(c conn) throws KException, IOException {
		
    	log.info("Sending Table Definitions");

    	conn.k("upd:{[t;x] t insert (.z.p,x)}".toCharArray());
    	conn.k("trade:([] time:`timestamp$();sym:`$();id:`int$();volume:`int$();genTime:`timestamp$())".toCharArray());
	}
	
	private static void getResults(c conn) throws KException, IOException {
		String query="{[x] c:count x;pp:(50 90 99 99.9 99.99); (`count`min`avg`max!(c,`long$0.001*(x[0];`long$avg x;`long$x[c-1]))),(`$string each pp)!`long$0.001*{[x;c;p] x[`int$(p%100)*c]}[x;c] each pp } (x where {[] x>0} x:asc raze value exec ts:`long$time-genTime from trade)";
		Object o = conn.k(query.toCharArray());
		c.Dict d = ((c.Dict)o);
		log.info("Got Dictionary {}",d.x);
		log.info("Got Dictionary {}",d.y);		
	}
}
