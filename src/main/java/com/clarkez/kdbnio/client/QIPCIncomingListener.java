package com.clarkez.kdbnio.client;

public interface QIPCIncomingListener {
	public void   onAsyncMessage(Object msg);
	public Object onSyncMessage (Object msg);	
}

