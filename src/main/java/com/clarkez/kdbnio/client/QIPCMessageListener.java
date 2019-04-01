package com.clarkez.kdbnio.client;

import com.clarkez.kdbnio.qpic.QIPCMessage;

public interface QIPCMessageListener  {
	public void onSuccess(QIPCMessage msg);
	public void onCancellation(QIPCMessage msg);
	public void onError(QIPCMessage msg,Throwable error);
}
