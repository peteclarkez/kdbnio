package com.clarkez.kdbnio;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kx.c;
import kx.c.KException;

public class cServer {

	private final static Logger log = LoggerFactory.getLogger(QIPCClient.class);

	private final int port;

	public static void main(String[] args) throws IOException, KException {
		cServer server = new cServer(6788);
		server.init();
		server.run();
	}
	c cserver;

	public cServer(int port) {
		this.port = port;
	}
	
	public void init() throws IOException, KException {
		ServerSocket s = new ServerSocket(port);
		cserver = new c(s);
	}
	
	public void run() {
		
		while(true) {
			try {
			Object o = cserver.k();
			if(cserver.getSync()>0) {
				cserver.kr("1+1".toCharArray());
			}
			}catch(IOException ioe) {
				log.warn("IOException " + ioe.getMessage());
			} catch (KException ke) {
				log.warn("KException " + ke.getMessage());
			}
		}

	}
}
