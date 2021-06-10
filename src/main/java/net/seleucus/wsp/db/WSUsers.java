package net.seleucus.wsp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSUsers {

	private final static Logger LOGGER = LoggerFactory.getLogger(WSUsers.class);    

	private final Connection wsConnection;

	public WSUsers(Connection wsConnection) {
		
		this.wsConnection = wsConnection;
		
	}

	public synchronized void addUser(String fullName, CharSequence passSeq, String eMail,
			String phone) {
		
		String sqlPassPhrase = "INSERT INTO PASSPHRASES (PASSPHRASE, CREATED) VALUES (?, CURRENT_TIMESTAMP);";
		
		String sqlUsers = "INSERT INTO PUBLIC.USERS (PPID, FULLNAME, EMAIL, PHONE, CREATED, MODIFIED) VALUES " +
				"(SELECT PPID FROM PUBLIC.PASSPHRASES WHERE PASSPHRASE = ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);";

		LOGGER.info("Adding user {} to the database...", fullName); 
		
	    try {
			
	    	PreparedStatement psPassPhrase = wsConnection.prepareStatement(sqlPassPhrase);
	    	psPassPhrase.setString(1, passSeq.toString());
	    	psPassPhrase.executeUpdate();
	    	
	    	psPassPhrase.close();
	    	
	    	PreparedStatement psUsers = wsConnection.prepareStatement(sqlUsers);
	    	psUsers.setString(1, passSeq.toString());
	    	psUsers.setString(2, fullName);
	    	psUsers.setString(3, eMail);
	    	psUsers.setString(4, phone);
	    	psUsers.executeUpdate();
	    	
	    	psUsers.close();
	    	
	    	LOGGER.info("User {} added.", fullName);
	    	
		} catch (SQLException ex) {
			
			 LOGGER.error("User Add - A Database error occured: {}", ex.getMessage());
			 
		}
	
	}

	public synchronized void addDupUser(String fullName, CharSequence passSeq, String eMail,
									 String phone) {

		String sqlPassPhrase = "INSERT INTO PASSPHRASES_DUP (PASSPHRASE, CREATED) VALUES (?, CURRENT_TIMESTAMP);";

		String sqlUsers = "INSERT INTO PUBLIC.USERS (PPID, FULLNAME, EMAIL, PHONE, CREATED, MODIFIED) VALUES " +
				"(SELECT PPID FROM PUBLIC.PASSPHRASES_DUP WHERE PASSPHRASE = ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);";

		LOGGER.info("Adding Dup user {} to the database...", fullName);

		try {

			PreparedStatement psPassPhrase = wsConnection.prepareStatement(sqlPassPhrase);
			psPassPhrase.setString(1, passSeq.toString());
			psPassPhrase.executeUpdate();

			psPassPhrase.close();

			PreparedStatement psUsers = wsConnection.prepareStatement(sqlUsers);
			psUsers.setString(1, passSeq.toString());
			psUsers.setString(2, fullName);
			psUsers.setString(3, eMail);
			psUsers.setString(4, phone);
			psUsers.executeUpdate();

			psUsers.close();

			LOGGER.info("User {} added.", fullName);

		} catch (SQLException ex) {

			LOGGER.error("User Add - A Database error occured: {}", ex.getMessage());
		}
	}

	public synchronized void addDecoyKnocks(String decoyKnock ) {
		String sqlUsers = "INSERT INTO PUBLIC.DECOY_KNOCK(DECOYKNOCK, CREATED, MODIFIED) " + "VALUES (?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);";
		try {
			PreparedStatement psUsers = wsConnection.prepareStatement(sqlUsers);
			psUsers.setString(1, decoyKnock);
			psUsers.executeUpdate();
			psUsers.close();

			LOGGER.info("Decoy Knock was sucessfully recorded...", decoyKnock);
		} catch (SQLException ex) {
			LOGGER.error("User Add - A Database error occured: {}", ex.getStackTrace());
		}
	}

	public synchronized void addDecoyUsers(String fullName, CharSequence passSeq, String eMail,
										String phone) {
		int cust_id = 0;
		String sqlUsers = "INSERT INTO PUBLIC.DECOY_USERS(PPID, FULLNAME, DECOY_PASSPHRASE, EMAIL, PHONE, CREATED, MODIFIED) " +
				"VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);";
		LOGGER.info("Adding Decoy user {} to the database...", fullName);
		try {
			PreparedStatement psUsers = wsConnection.prepareStatement(sqlUsers);
			cust_id = 11;
			psUsers.setInt(1, cust_id);
			psUsers.setString(2, fullName);
			psUsers.setString(3, passSeq.toString());
			psUsers.setString(4, eMail);
			psUsers.setString(5, phone);
			psUsers.executeUpdate();

			psUsers.close();

			LOGGER.info("User {} added.", fullName);

		} catch (SQLException ex) {

			LOGGER.error("User Add - A Database error occured: {}", ex.getStackTrace());
		}
	}

	public synchronized void addDuplicatePassPhraseForUser(String fullName, CharSequence passSeq, String eMail,
										String phone, Integer number) {

		String sqlPassPhrase = "INSERT INTO PASSPHRASES_DUP (PASSPHRASE, CREATED) VALUES (?, CURRENT_TIMESTAMP);";

		String sqlUsers = "INSERT INTO PUBLIC.USERS (PPID, FULLNAME, EMAIL, PHONE, CREATED, MODIFIED) VALUES " +
				"(SELECT PPID FROM PUBLIC.PASSPHRASES_DUP WHERE PASSPHRASE = ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);";

		/*if (number >= 1) {
			LOGGER.info(+number + "-" + "Duplicate Pass-Phrases Dup pass-phrase to user {} to the database...", fullName);
		} else {
			LOGGER.info("Adding Dup pass-phrase to user {} to the database...", fullName);
		}*/


		try {

			PreparedStatement psPassPhrase = wsConnection.prepareStatement(sqlPassPhrase);
			psPassPhrase.setString(1, passSeq.toString());
			psPassPhrase.executeUpdate();

			psPassPhrase.close();

			PreparedStatement psUsers = wsConnection.prepareStatement(sqlUsers);
			psUsers.setString(1, passSeq.toString());
			psUsers.setString(2, fullName);
			psUsers.setString(3, eMail);
			psUsers.setString(4, phone);
			psUsers.executeUpdate();

			psUsers.close();

			//0LOGGER.info("User {} Pass-Phrase added.", fullName);

		} catch (SQLException ex) {

			LOGGER.error("User Add - A Database error occured: {}", ex.getMessage());
		}
	}
	
	public synchronized String getUsersFullName(int ppID) {
		
		String fullName = "Invalid User"; 
		
		if(ppID > 0) {
			
			String sqlActivationLookup = "SELECT FULLNAME FROM USERS WHERE PPID = ? ;";
			try {
				PreparedStatement psPassPhrase = wsConnection.prepareStatement(sqlActivationLookup);
				psPassPhrase.setInt(1, ppID);
				ResultSet rs = psPassPhrase.executeQuery();
				
				if (rs.next()) {
					fullName = rs.getString(1);
				}
				
				rs.close();
				psPassPhrase.close();
				
			} catch (SQLException ex) {
	
				 LOGGER.error("User Full Name - A Database error occured: {}", ex.getMessage());
	
			}
		}

		return fullName;
	}

	public synchronized String showUsers() {
		
		StringBuffer resultsBuffer = new StringBuffer();
		resultsBuffer.append('\n');
		resultsBuffer.append("Users:");
		resultsBuffer.append('\n');
		resultsBuffer.append("___________________________________________________________");
		resultsBuffer.append('\n');
		resultsBuffer.append(StringUtils.rightPad("ID", 4));
		resultsBuffer.append(StringUtils.rightPad("Active", 8));
		resultsBuffer.append(StringUtils.rightPad("Full Name", 24));
		resultsBuffer.append(StringUtils.rightPad("Last Modified", 25));
		resultsBuffer.append('\n');
		resultsBuffer.append("-----------------------------------------------------------");
		resultsBuffer.append('\n');
		
		final String sqlPassUsers = "SELECT PPID, ACTIVE, FULLNAME, MODIFIED FROM PASSPHRASES JOIN USERS ON PASSPHRASES.PPID = USERS.PPID;";

		try {

			Statement stmt = wsConnection.createStatement();
			ResultSet rs = stmt.executeQuery(sqlPassUsers);

			while (rs.next()) {
				resultsBuffer.append(StringUtils.rightPad(rs.getString(1), 4));
				resultsBuffer.append(StringUtils.rightPad(rs.getString(2), 8));
				resultsBuffer.append(StringUtils.rightPad(StringUtils.abbreviate(rs.getString(3), 23), 24));
				resultsBuffer.append(rs.getString(4), 0, 23);
				resultsBuffer.append('\n');
			}
			resultsBuffer.append("___________________________________________________________");
			resultsBuffer.append('\n');

			rs.close();
			stmt.close();

		} catch (SQLException ex) {

			LOGGER.error("User Show - A Database error occured: {}", ex.getMessage());

		}

		return resultsBuffer.toString();
	}

	public synchronized String showAllUsers() {

		StringBuffer resultsBuffer = new StringBuffer();
		resultsBuffer.append('\n');
		resultsBuffer.append("Users:");
		resultsBuffer.append('\n');
		resultsBuffer.append("___________________________________________________________");
		resultsBuffer.append('\n');
		resultsBuffer.append(StringUtils.rightPad("ID", 4));
		resultsBuffer.append(StringUtils.rightPad("Active", 8));
		resultsBuffer.append(StringUtils.rightPad("Full Name", 24));
		resultsBuffer.append(StringUtils.rightPad("Last Modified", 25));
		resultsBuffer.append('\n');
		resultsBuffer.append("-----------------------------------------------------------");
		resultsBuffer.append('\n');

		final String sqlPassUsers = "SELECT PPID, ACTIVE, FULLNAME, MODIFIED FROM PASSPHRASES_DUP JOIN USERS ON PASSPHRASES_DUP.PPID = USERS.PPID;";

		try {

			Statement stmt = wsConnection.createStatement();
			ResultSet rs = stmt.executeQuery(sqlPassUsers);

			while (rs.next()) {
				resultsBuffer.append(StringUtils.rightPad(rs.getString(1), 4));
				resultsBuffer.append(StringUtils.rightPad(rs.getString(2), 8));
				resultsBuffer.append(StringUtils.rightPad(StringUtils.abbreviate(rs.getString(3), 23), 24));
				resultsBuffer.append(rs.getString(4), 0, 23);
				resultsBuffer.append('\n');
			}
			resultsBuffer.append("___________________________________________________________");
			resultsBuffer.append('\n');

			rs.close();
			stmt.close();

		} catch (SQLException ex) {

			LOGGER.error("User Show - A Database error occured: {}", ex.getMessage());

		}

		return resultsBuffer.toString();
	}

	public synchronized String showAllDecoyKnocks() {

		StringBuffer resultsBuffer = new StringBuffer();
		resultsBuffer.append('\n');
		resultsBuffer.append("Decoy Knocks Table:");
		resultsBuffer.append('\n');
		resultsBuffer.append("___________________________________________________________");
		resultsBuffer.append('\n');
		resultsBuffer.append(StringUtils.rightPad("ID", 4));
		resultsBuffer.append(StringUtils.rightPad("Decoy Knock", 108));
		resultsBuffer.append(StringUtils.rightPad("Last Modified", 25));
		resultsBuffer.append('\n');
		resultsBuffer.append("-----------------------------------------------------------");
		resultsBuffer.append('\n');

		final String sqlPassUsers  = "SELECT USID, DECOYKNOCK, MODIFIED FROM DECOY_KNOCK;";

		try {

			Statement stmt = wsConnection.createStatement();
			ResultSet rs = stmt.executeQuery(sqlPassUsers);

			while (rs.next()) {
				resultsBuffer.append(StringUtils.rightPad(rs.getString(1), 4));
				resultsBuffer.append(StringUtils.rightPad(rs.getString(2), 108));
				resultsBuffer.append(rs.getString(3), 0, 23);
				resultsBuffer.append('\n');
			}
			resultsBuffer.append("___________________________________________________________");
			resultsBuffer.append('\n');

			rs.close();
			stmt.close();

		} catch (SQLException ex) {

			LOGGER.error("User Show - A Database error occured: {}", ex.getMessage());

		}

		return resultsBuffer.toString();
	}

}
