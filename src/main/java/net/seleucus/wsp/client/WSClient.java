package net.seleucus.wsp.client;

import javafx.concurrent.Task;
import net.seleucus.wsp.console.WSConsole;
import net.seleucus.wsp.db.WSDatabase;
import net.seleucus.wsp.main.WSGestalt;
import net.seleucus.wsp.main.WebSpa;
import net.seleucus.wsp.server.WSServer;
import net.seleucus.wsp.util.WSKnownHosts;
import net.seleucus.wsp.util.WSUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.CertificateEncodingException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class WSClient extends WSGestalt {

	private final static Logger LOGGER = LoggerFactory.getLogger(WSClient.class);
	String names[] = {"JAMES", "JOHN", "ROBERT", "MICHAEL", "WILLIAM", "DAVID", "RICHARD", "CHARLES", "JOSEPH", "THOMAS", "CHRISTOPHER", "DANIEL", "PAUL", "MARK", "DONALD", "GEORGE", "KENNETH", "STEVEN", "EDWARD", "BRIAN", "RONALD", "ANTHONY", "KEVIN", "JASON", "MATTHEW", "GARY", "TIMOTHY", "JOSE", "LARRY", "JEFFREY", "FRANK", "SCOTT", "ERIC"};
	int actionNumber_array[] = {1,2,3,4,5,6,7,8,9};
	String[] pass_array = {"Value*12345", "Pass123", "Secure*12", "Password123","password1","111111", "qwerty", "1q2w3e4r", "adobe123","1qaz2wsx","qwertyuiop","trustno1"};
	private WSServer wsServer;

	public WSClient(final WebSpa myWebSpa) {
		super(myWebSpa);
	}

	@Override
	public void exitConsole() {
		LOGGER.info("Goodbye!");
	}

	@Override
	public void runConsole() throws SQLException, IOException, ClassNotFoundException {

		LOGGER.info("");
		LOGGER.info("WebSpa - Single HTTP/S Request Authorisation");
//		LOGGER.info("version " + WSVersion.getValue() + " (webspa@seleucus.net)");
		LOGGER.info("");

		String host = readLineRequired("Host [e.g. https://localhost/]");
		CharSequence password = readPasswordRequired("Your pass-phrase for that host");
		int action = readLineRequiredInt("The action number", 0, 9);

		final String choice = readLineOptional("Testing Feature: Do you want to Add some Decoy Knocks as well? [y/N]");

		Hashtable<Integer, String> decoyKnockMap = new Hashtable<>();
		decoyKnockMap.put(1, "Value*12345");
		decoyKnockMap.put(2, "Secure*12");
		decoyKnockMap.put(3, "1q2w3e4r");
		decoyKnockMap.put(4, "adobe123");
		decoyKnockMap.put(5, "1qaz2wsx");
		decoyKnockMap.put(action,password.toString());

		if (WSUtil.isAnswerPositive(choice)) {
			//int count = readLineOptionalInt("How many Decoy Knocks you want to Add into WebSpa?");
			//for (int i = 0; i < count; ++i) {
			List<Map.Entry<Integer, String>> entries = new ArrayList<>(decoyKnockMap.entrySet());
			Collections.shuffle(entries);
			for (Map.Entry<Integer, String> entry : entries) {
					clientBodyMethod(host, entry.getValue(), entry.getKey());
				LOGGER.info("Your WebSpa Knock is for PassPhrase: {}", entry.getValue());
			}
		} else {
			clientBodyMethod(host, password, action);
		}
	}

	public void runConsoleForRM3(boolean flag, String originalPassphrase) throws SQLException, IOException, ClassNotFoundException {


		//String host = readLineRequired("Host [e.g. https://localhost/]");
		//CharSequence password = readPasswordRequired("Your pass-phrase for that host");
		//int action = readLineRequiredInt("The action number", 0, 9);

		final boolean choice = flag;

		Hashtable<Integer, String> decoyKnockMap = new Hashtable<>();
		decoyKnockMap.put(1, "Value*12345");
		decoyKnockMap.put(2, "Secure*12");
		decoyKnockMap.put(3, "1q2w3e4r");
		decoyKnockMap.put(4, "adobe123");
		decoyKnockMap.put(5, "1qaz2wsx");
		decoyKnockMap.put(1, originalPassphrase);

		if (choice) {
			//int count = readLineOptionalInt("How many Decoy Knocks you want to Add into WebSpa?");
			//for (int i = 0; i < count; ++i) {
			List<Map.Entry<Integer, String>> entries = new ArrayList<>(decoyKnockMap.entrySet());
			Collections.shuffle(entries);

			for (Map.Entry<Integer, String> entry : entries) {
				clientBodyMethod("http://localhost:80", entry.getValue(), entry.getKey());
				LOGGER.info("Your WebSpa Knock is for PassPhrase: {}", entry.getValue());
			}
		}
	}

	public void clientBodyMethod(String host, CharSequence password, int action){
		long startTime = System.nanoTime();
		Instant start = Instant.now();
		WSRequestBuilder myClient = new WSRequestBuilder(host, password, action);
		String knock = myClient.getKnock();

		LOGGER.info("Your WebSpa Knock is: {}", knock);

		// URL nonsense
		//final String sendChoice = readLineOptional("Send the above URL [Y/n]");

		//if (WSUtil.isAnswerPositive(sendChoice) || sendChoice.isEmpty()) {

		if(true){

			WSConnection myConnection = new WSConnection(knock);

			LOGGER.info(myConnection.getActionToBeTaken());

			myConnection.sendRequest();

			// is the connection HTTPS
			if (myConnection.isHttps()) {
				try {
					WSKnownHosts knownHosts = new WSKnownHosts();

					LOGGER.info(myConnection.getCertSHA1Hash());

					String ipAddr = getIpAddr(host);
					String certAlgorithm = myConnection.getCertificateAlgorithm();
					String certFingerprint = myConnection.getCertificateFingerprint();

					Boolean fingerprintMatches = knownHosts.check(ipAddr, certAlgorithm, certFingerprint);

					if (fingerprintMatches == null) {
						final String storeNewFingerprint = readLineOptional("New fingerprint, add it to the WebSPA known hosts file [Y/n]");

						if (WSUtil.isAnswerPositive(storeNewFingerprint)) {
							knownHosts.addKnownHost(ipAddr, certAlgorithm, certFingerprint);
						}
					} else if (fingerprintMatches.booleanValue()) {
						LOGGER.info("fingerprint matches");
					} else {
						myConsole.println("WARNING: The certificate fingerprint does NOT match with the known hosts file");
					}

				} catch (NullPointerException npEx) {
					LOGGER.info("Couldn't get the SHA1 hash of the server certificate - probably a self signed certificate.");

					if (!WSUtil.hasMinJreRequirements(1, 7)) {
						LOGGER.error("Be sure to run WebSpa with a JRE 1.7 or greater.");
					} else {
						LOGGER.error("An exception was raised when reading the server certificate.");
						npEx.printStackTrace();
					}
				} catch (SSLPeerUnverifiedException e) {
					e.printStackTrace();
				} catch (CertificateEncodingException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				final String trustChoice = readLineOptional("Continue connecting [Y/n]");

				//if (WSUtil.isAnswerPositive(trustChoice) || sendChoice.isEmpty()) {
					if (true) {

					myConnection.sendRequest();
					LOGGER.info(myConnection.responseMessage());
					LOGGER.info("HTTPS Response Code: {}", myConnection.responseCode());

				} else {

					LOGGER.info("Nothing was sent.");

				}

			} else {
				myConnection.sendRequest();

				Instant finish = Instant.now();
				long timeElapsed = Duration.between(start, finish).toMillis();

				createExtraAuthRM3(String.valueOf(startTime), String.valueOf(finish), String.valueOf(timeElapsed), password.toString());

				//createExtraAuthTimeLogFile(String.valueOf(timeElapsed), password,"");
				LOGGER.info("Elapsed Time: {}", timeElapsed);
				LOGGER.info(myConnection.responseMessage());
				LOGGER.info("HTTP Response Code: {}", myConnection.responseCode());
				//LOGGER.info("Instant Time: {}", "Start "+start+"finish "+finish+" "+timeElapsed);
			}

		}
	}

	private String getIpAddr(String host) throws MalformedURLException, UnknownHostException {
		URL url = new URL(host);
		return InetAddress.getByName(url.getHost()).getHostAddress();
	}

	//method introduced to create the Extra auth file in project struture to capture the elapsed time for webSpa user
	public static void createExtraAuthRM3(String start,String finish,String elapsedTime, CharSequence passphrase){
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(start+", ");
			sb.append(finish +", ");
			sb.append(elapsedTime +" ms");

			String filePath = "F://trunk/WebSpa/webSpaAERM3.csv";
			File myObj = new File(filePath);
			if (!myObj.exists()) {
				myObj.createNewFile();
			} else {
             /*   FileWriter fileWriter = new FileWriter(filePath, true); //Set true for append mode
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println(sb);  //New line
                printWriter.close();*/
				//LOGGER.info("File created for Extra Auth"+result);

				List<List<String>> rows = Arrays.asList(
						Arrays.asList(start, finish, elapsedTime)
				);

				FileWriter csvWriter = new FileWriter(filePath, true);
              /*  csvWriter.append("WebSpa Server started processing time :");
                csvWriter.append(",");
                csvWriter.append("Response time from HoneyChecker Recieved :");
                csvWriter.append(",");
                csvWriter.append("Time in ms");
                csvWriter.append("\n");*/

				for (int i = 0; i < rows.size(); i++) {
					csvWriter.append(String.join(",", rows.get(i)));
					csvWriter.append("\n");
				}
				csvWriter.flush();
				csvWriter.close();
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static void createExtraAuthTimeLogFile(String str, CharSequence passphrase, String hostName){
		try {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			StringBuilder sb = new StringBuilder();
			sb.append("WebSpa Client knock extra Auth Time:  "+str +" ms , ");
			sb.append("Pass-phrase :"+passphrase+" , ");
			sb.append("Timestamp :"+timestamp+" , ");
			//sb.append("HostName :"+hostName+" , ");
			//sb.append("Timestamp :"+timestamp.toString());
			//String filePath = "F://trunk/WebSpa/webSpaAuthTimeRM3.csv";
			String filePath = "F://trunk/WebSpa/extraAuthTimeFileC1ient.txt";
			File myObj = new File(filePath);
			if (!myObj.exists()) {
				myObj.createNewFile();
			} else {
				FileWriter fileWriter = new FileWriter(filePath, true); //Set true for append mode
				PrintWriter printWriter = new PrintWriter(fileWriter);
				printWriter.println(sb);  //New line
				printWriter.close();
				//LOGGER.info("File created for Extra Auth Time : "+sb);
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static void writeExtraAuthTimeToCSV(String str, CharSequence passphrase, String hostName){
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("WebSpa Client knock extra Auth Time:  "+str +" ms , ");
			sb.append("Pass-phrase :"+passphrase+" , ");
			sb.append("HostName :"+hostName+" , ");

			String filePath = "F://trunk/WebSpa/webSpaExtraAuthTime.csv";
			File myObj = new File(filePath);
			if (!myObj.exists()) {
				myObj.createNewFile();
			} else {

				List<List<String>> rows = Arrays.asList(
						Arrays.asList("Jean", "author", "Java"),
						Arrays.asList("David", "editor", "Python"),
						Arrays.asList("Scott", "editor", "Node.js")
				);

				FileWriter csvWriter = new FileWriter(filePath, true);
				csvWriter.append("WebSpa Server started processing time :");
				csvWriter.append(",");
				csvWriter.append("Response time from HoneyChecker Recived :");
				csvWriter.append(",");
				csvWriter.append("Time in MS");
				csvWriter.append("\n");

				PrintWriter printWriter = new PrintWriter(csvWriter);
				printWriter.println(sb);  //New line
				printWriter.close();

				for (List<String> rowData : rows) {
					csvWriter.append(String.join(",", rowData));
					csvWriter.append("\n");
				}
				csvWriter.flush();
				csvWriter.close();
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public void runConsoleTest() {
		for (int i = 0; i < 5; i++) {
			wsServer.getWSDatabase().users.addDecoyUsers(names[new Random().nextInt(names.length)], RandomStringUtils.random(10, true, true), "", "");
		}
	}
}
