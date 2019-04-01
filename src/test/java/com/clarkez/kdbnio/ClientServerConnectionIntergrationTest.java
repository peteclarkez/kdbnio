package com.clarkez.kdbnio;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import junit.framework.Assert;

import org.junit.Test;

import kx.c;
import kx.c.KException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientServerConnectionIntergrationTest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	
	private static final int PORT = 9125;
	@Test
	public void startServerAndConnectClientTest() throws IOException, KException, InterruptedException {

		Executor thread = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setDaemon(true);
				return thread;
			}
		});
		thread.execute(new Runnable() {
			@Override
			public void run() {
				cServer server = new cServer(PORT);
				try {
					server.init();
				} catch (IOException e) {
					log.error("IOException",e);
				} catch (KException e) {
					log.error("KException",e);
				}
				server.run();
			}
		});

		Thread.sleep(400);
		c conn = new c("localhost",PORT,"peter:password");
		log.info("LOGGED IN");
		Object rc = conn.k("1+1");
		String rcString = (rc instanceof char[] )?String.valueOf((char[])rc):String.valueOf(rc);
		log.info("GOT DATA BACK "+ rcString);
		Assert.assertNotNull(rc);
	}
}
