package com.clarkez.kdbnio.qpic;

import kx.QConst;
import com.clarkez.kdbnio.client.QIPCResponseListener;
import kx.QType;

public class QIPCMessage extends AbstractQIPCMessage{

	private final Object data;
	private QIPCResponseListener responseListener;
	
	public static QIPCMessage getSync(Object data,QIPCResponseListener responseListener) {
		QIPCMessage qmsg = new QIPCMessage(QConst.ARCH_BIGENDIAN,QConst.CALLTYPE_SYNC,QConst.COMPRESSION_DISABLED,-1,data);
		qmsg.setResponseListener(responseListener);
		return qmsg;
	}
	
	public static QIPCMessage getASync(Object data) {
		return new QIPCMessage(QConst.ARCH_BIGENDIAN,QConst.CALLTYPE_ASYNC,QConst.COMPRESSION_DISABLED,-1,data);
	}

	public static QIPCMessage getResponse(Object data) {
		return new QIPCMessage(QConst.ARCH_BIGENDIAN,QConst.CALLTYPE_RESPONSE,QConst.COMPRESSION_DISABLED,-1,data);
	}
	
	public QIPCMessage(int encoding, int msgType, int compression, long msgLength, Object o) {
		super(encoding, msgType, compression, msgLength);
		if(o instanceof QIPCMessage){
			this.data  = ((QIPCMessage) o).getData();
		}else{
			this.data = o;
		}
	}

	public Object getData() {
		return data;
	}

	public QIPCResponseListener getResponseListener() {
		return responseListener;
	}

	public void setResponseListener(QIPCResponseListener responseListener) {
		this.responseListener = responseListener;
	}

	public String toString(){
		return "QIPCMessage["+msgType+"|"+String.valueOf(data)+"]";
	}
}
