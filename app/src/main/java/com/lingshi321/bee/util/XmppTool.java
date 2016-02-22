package com.lingshi321.bee.util;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.animation.AnimatorSet.Builder;

public class XmppTool {

	private static final String IP = DataInterfaceConstants.HOST_OPENFIRE;
	private static final int PORT = DataInterfaceConstants.PORT_OPENFIRE;

	private static XMPPConnection con = null;

	private static void openConnection() {
		try {
			ConnectionConfiguration connConfig = new ConnectionConfiguration(
					IP, PORT);
			connConfig.setReconnectionAllowed(true);
			con = new XMPPConnection(connConfig);
			con.connect();
		} catch (XMPPException xe) {
			xe.printStackTrace();
		}
	}

	public static XMPPConnection getConnection() {
		if (con == null) {
			openConnection();
		}
		return con;
	}

	public static void closeConnection() {
		if (con != null) {
			con.disconnect();
			con = null;
		}
	}
}
