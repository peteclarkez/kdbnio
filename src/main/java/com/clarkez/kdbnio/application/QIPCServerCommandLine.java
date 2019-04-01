package com.clarkez.kdbnio.application;

import com.clarkez.kdbnio.QIPCClient;
import com.clarkez.kdbnio.QIPCServer;
import com.clarkez.kdbnio.client.QIPCIncomingListener;
import com.clarkez.kdbnio.server.QIPCServerAuthHandler;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QIPCServerCommandLine {
    private final static Logger log = LoggerFactory.getLogger(QIPCServerCommandLine.class);

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 6789;
        }

        QIPCIncomingListener qi = new QIPCIncomingListener() {

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

        };

        QIPCServerAuthHandler auth = new QIPCServerAuthHandler() {
            public boolean authenticate(String user, String password) {
                return "password".equalsIgnoreCase(password);
            }
        };

        QIPCServer qs  = new QIPCServer(port,qi,auth);
        qs.runServer();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    log.info("Shutting down ...");
                    qs.shutDownServer();
                } catch (InterruptedException e) {
                    log.warn("Shutting down Interrupted...");
                } catch (Exception e) {
                    log.error("Shutting down Interrupted...",e);
                }
            }
        });


   }
}
