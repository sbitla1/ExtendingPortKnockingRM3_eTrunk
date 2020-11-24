package net.seleucus.wsp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class WSActionsReceived {

	private final static Logger LOGGER = LoggerFactory.getLogger(WSActionsReceived.class);    

	private final Connection wsConnection;
	
	protected WSActionsReceived(Connection wsConnection) {
		
		this.wsConnection = wsConnection;
	}

	public synchronized void addAction(final String ipAddress, final String webSpaRequest, final int aaID) {

		final String sqlInsert = "INSERT INTO PUBLIC.ACTIONS_RECEIVED (IP_ADDR, RECEIVED, KNOCK, AAID) VALUES (?, CURRENT_TIMESTAMP, ?, ?); ";
		
		try {
			
			PreparedStatement preStatement = wsConnection.prepareStatement(sqlInsert);
			preStatement.setString(1, ipAddress);
			preStatement.setString(2, webSpaRequest);
			preStatement.setInt(3, aaID);
			
			preStatement.executeUpdate();
			preStatement.close();
			
		} catch (SQLException ex) {
			
			 LOGGER.error("Action Add - A Database exception has occured: {}.", ex.getMessage());	
			
		}
	}

}
