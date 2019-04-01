package com.clarkez.kdbnio.qpic;

import kx.QConst;

public class QIPCLoginMessage extends AbstractQIPCMessage{

	private final int qVersion;	
	private final String userpass;

//	private final String login;
//	private final String password;

	public QIPCLoginMessage(String userpass,int qVersion) {
		super(QConst.ARCH_BIGENDIAN,QConst.CALLTYPE_SYNC,QConst.COMPRESSION_DISABLED,userpass.length()+1);
		this.qVersion = qVersion;
		this.userpass = userpass;
	}

	public int getQ_version() {
		return qVersion;
	}
	public String getLoginName() {
		return userpass;
	}

}
