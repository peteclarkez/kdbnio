package com.clarkez.kdbnio.client;

import com.clarkez.kdbnio.QIPCClient;
import com.clarkez.kdbnio.qpic.QIPCMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractQIPCMessageListener implements QIPCMessageListener {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onSuccess(QIPCMessage msg) {
        log.trace("Success Sending Message {}",msg);
    }

    @Override
    public void onCancellation(QIPCMessage msg) {
        log.trace("Success Sending Message {}",msg);
    }

    @Override
    public void onError(QIPCMessage msg, Throwable error) {
        log.error("Error Sending Message {} {}",msg,error.getMessage());
    }

}
