package com.clarkez.kdbnio;

import com.clarkez.kdbnio.client.AbstractQIPCMessageListener;
import com.clarkez.kdbnio.client.QIPCMessageListener;
import com.clarkez.kdbnio.client.QIPCResponseListener;
import com.clarkez.kdbnio.qpic.QIPCMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class AbstractQIPCService {
    protected final  Logger log = LoggerFactory.getLogger(this.getClass());

    public AbstractQIPCService(){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error(String.format("Thread %s Finished with Error %s",String.valueOf(t),e.getMessage()),e);
            }
        });
    }


    protected ChannelPromise getNewPromise(final QIPCMessage qmsg, final Channel ch) {
        return getNewPromise(qmsg,ch,new AbstractQIPCMessageListener());
    }

    protected ChannelPromise getNewPromise(final QIPCMessage qmsg, final Channel ch, final QIPCMessageListener listener) {
        final ChannelPromise promise = ch.newPromise();

        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                if(!f.isDone()) {
                    log.warn("Operation Didn't Complete? {}",String.valueOf(qmsg));
                    return;
                }
                if (f.isCancelled()) {
                    log.debug("Operation Cancelled {}",String.valueOf(qmsg));
                    if(listener!=null) {
                        listener.onCancellation(qmsg);
                    }

                } else if (!f.isSuccess()) {
                    log.debug(String.format("Operation Unsuscessful %s %s",String.valueOf(qmsg),String.valueOf(f.cause())),f.cause());
                    if(listener!=null) {
                        listener.onError(qmsg, f.cause());
                    }
                } else {
                    log.debug("Operation Completed Successfully {}",String.valueOf(qmsg));
                    if(listener!=null) {
                        listener.onSuccess(qmsg);
                    }
                }
            }
        });

        return promise;
    }

    public Object sendSyncMessage(Object data, Channel ch) {

        final BlockingQueue<QIPCMessage> reqResp = new SynchronousQueue<QIPCMessage>();

        QIPCResponseListener resp = new QIPCResponseListener() {
            @Override
            public void onResponse(QIPCMessage msg, QIPCMessage response) {
                log.trace("Added to Response Queue");
                try {
                    reqResp.put(response);
                } catch (InterruptedException e) {
                    log.warn("InterruptedException "+e.getMessage());
                }
                log.trace("Added to Response Queue - Finished");
            }
        };
        QIPCMessage qmsg = QIPCMessage.getSync(data,resp);

        ChannelPromise promise = getNewPromise(qmsg, ch);
        try {
            ChannelFuture f = ch.writeAndFlush(qmsg,promise).sync();
            f.await();
        } catch (InterruptedException e) {
            log.warn("InterruptedException "+e.getMessage());
        }

        QIPCMessage rc=null;
        try {
            log.trace("Waiting on Response Queue");
            rc = reqResp.take();
            log.trace("Waiting on Response Queue - Finished");
        } catch (InterruptedException e) {
            log.warn("InterruptedException "+e.getMessage());
        }
        if(rc==null || rc.getData()==null){
            log.trace("Got Null Response");
            return null;
        }
        log.trace("Waiting on Response Queue - Finished "+rc.getData());
        return rc.getData();
    }


    public void sendASyncMessage(Object data, Channel ch) {
        QIPCMessage qmsg = QIPCMessage.getASync(data);
        ChannelPromise promise = getNewPromise(qmsg, ch);
        ch.writeAndFlush(qmsg,promise);
    }

}
