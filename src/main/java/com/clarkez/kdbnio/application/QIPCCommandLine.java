package com.clarkez.kdbnio.application;

import com.clarkez.kdbnio.QIPCClient;
import com.clarkez.kdbnio.client.QIPCIncomingListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class QIPCCommandLine {

    private final static Logger log = LoggerFactory.getLogger(QIPCCommandLine.class);

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
        readFromStdIn(c,ch);
        //ch.closeFuture().sync();
        ch.closeFuture();
    }

    private static void readFromStdIn(QIPCClient c,Channel ch) {
        // Read commands from the stdin.
        ChannelFuture lastWriteFuture = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            String line;
            try {
                line = in.readLine();

                if (line == null) {
                    continue;
                }

                if ("\\\\".equals(line)) {
                    break;
                }

                if ("bye".equals(line.toLowerCase())) {
                    break;
                }


                Object response = c.sendSyncMessage(line.toCharArray(),ch);
                log.info("Operation onResponse {} {}",line,String.valueOf(response));

            } catch (IOException e) {
                log.error("IOException Error {}",e.getMessage());
            } catch (DecoderException e) {
                log.error("DecoderException Error {}",e.getMessage());
//            } catch (kx.c.KException e) {
//                log.error("IOException Error {}",e.getMessage());
            }

        }
    }


}
