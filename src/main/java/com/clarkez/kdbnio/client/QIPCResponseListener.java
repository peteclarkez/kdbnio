package com.clarkez.kdbnio.client;

import com.clarkez.kdbnio.qpic.QIPCMessage;

public interface QIPCResponseListener {
	public void onResponse(QIPCMessage msg, QIPCMessage response);
}
