package com.clarkez.kdbnio.qpic;

/**
 * 0 - v2.5, no compression, no timestamp, no timespan, no uuid
 * 1..2 - v2.6-2.8, compression, timestamp, timespan
 * 3 - v3.0, compression, timestamp, timespan, uuid
 * 
 */
public class AbstractQIPCMessage {
	
	protected final int encoding;
	protected final int msgType;
	protected final int compression;
	protected int unused = 0x00;
	protected final long msgLength;
	
	protected AbstractQIPCMessage(int encoding,int msgType,int compression, long msgLength) {
		this.encoding = encoding;
		this.msgType = msgType;
		this.compression = compression;
		this.msgLength = msgLength;
	}

	public int getEncoding() {
		return encoding;
	}

	public int getMsgType() {
		return msgType;
	}

	public int getCompression() {
		return compression;
	}

	public long getMsgLength() {
		return msgLength;
	}

}
