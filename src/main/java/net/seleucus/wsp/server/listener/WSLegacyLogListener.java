package net.seleucus.wsp.server.listener;

import net.seleucus.wsp.db.WSDatabase;
import net.seleucus.wsp.server.WSServer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import seleucus.wsp.HoneyChecker.model.UserDTO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Legacy class responsible for processing log file lines. This class is a callback listener
 * called by {@link org.apache.commons.io.input.Tailer}.
 *
 * @deprecated Deprecated since version 0.9. Replaced by {@link WebServerLogTailCallbackListener}
 *
 * @see WebServerLogTailCallbackListener
 */
@Deprecated
public class WSLegacyLogListener extends TailerListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(WSLegacyLogListener.class);

    public static final int EXPECTED_REQUEST_LENGTH = 100;

    private final WSServer myServer;
	private final WSDatabase myDatabase;
	private final Pattern wsPattern;

 	public WSLegacyLogListener(final WSServer myServer) {
 		this.myServer = myServer;
 		this.myDatabase = myServer.getWSDatabase();
 		this.wsPattern = Pattern.compile(myServer.getWSConfiguration().getLoginRegexForEachRequest());
 	}

    @Override
    public void handle(final String requestLine) {

        Instant start = Instant.now();
    	
        // Check if the line length is more than 65535 chars
        if (requestLine == null || requestLine.length() > Character.MAX_VALUE) {
        	return;
        }
                      
        // Check if the regex pattern has been found
    	final Matcher wsMatcher = wsPattern.matcher(requestLine);

        if (! wsMatcher.matches() || 2 != wsMatcher.groupCount( )) {
        	logger.info("Regex Problem?");
        	logger.info("Request line is {}.", requestLine);
        	logger.info("The regex is {}.", wsPattern.pattern());
            return;
        }

        final String ipAddress = wsMatcher.group(1);
        String webSpaRequest = wsMatcher.group(2);
        if(webSpaRequest.endsWith("/")) {
        	webSpaRequest = webSpaRequest.substring(0, webSpaRequest.length() - 1);
        }

        if(EXPECTED_REQUEST_LENGTH != webSpaRequest.length()){
            logger.warn("Request length expected to be {} but was {}", EXPECTED_REQUEST_LENGTH, webSpaRequest.length());
            return;
        }

        logger.info("The 100 chars received are {}.", webSpaRequest);
        //logger.info("Request Line is : ", requestLine);

        // Get the unique user ID from the request
        final int ppId = myDatabase.passPhrases.getPPIDFromRequest(webSpaRequest);
        if (ppId < 0) {
            myServer.getWSDatabase().users.addDecoyKnocks(webSpaRequest);
            logger.warn("No User Found");
            return;
        }

        final String username = myDatabase.users.getUsersFullName(ppId);
        logger.info("User Found in System: {}.", username);

        final boolean userActive = myDatabase.passPhrases.getActivationStatus(ppId);
        final String baseUrl = "http://localhost:8080" + "/validate";
        final String hostUrl = "http://29f82.yeg.rac.sh:8080" + "/validate";
        boolean ishoneyCheckerOn = false;
        URI uri = null;

        try {
            if (ishoneyCheckerOn) {
                uri = new URI(baseUrl);

                boolean isValidFromHoneyCheckerService = false;
                RestTemplate restTemplate = new RestTemplate();

                //Call HoneyCheck REST Api service to validate the WebSpa user from master file userID.csv
                UserDTO userDTO = new UserDTO(Integer.toString(ppId), username);
                ResponseEntity<Boolean> honeyCheckerServiceResponse = null;
                honeyCheckerServiceResponse = restTemplate.postForEntity(uri, userDTO, Boolean.class);

                if (honeyCheckerServiceResponse.getBody().booleanValue()) {
                    isValidFromHoneyCheckerService = true;
                }
                logger.info(myDatabase.passPhrases.getActivationStatusString(ppId));
                logger.info("User DTO Name : " + userDTO.getUserName() + " and ID : " + userDTO.getId());
                logger.info("Response from honey checker" + isValidFromHoneyCheckerService);
            }

            if (userActive) {
               // if (userActive && isValidFromHoneyCheckerService) {
                final int action = myDatabase.actionsAvailable.getActionNumberFromRequest(ppId, webSpaRequest);
                logger.info("Action Number {}. ", action);

                if (action >= 0 && action <= 9) {

                    // Log this in the actions received table...
                    final int aaID = myServer.getWSDatabase().actionsAvailable.getAAID(ppId, action);
                    myServer.getWSDatabase().actionsReceived.addAction(ipAddress, webSpaRequest, aaID);

                    CharSequence passphrase = myDatabase.passPhrases.getPassPhrase(ppId);

                    // Log this on the screen for the user
                    final String osCommand = myServer.getWSDatabase().actionsAvailable.getOSCommand(ppId, action);
                    logger.info(ipAddress + " ->  '" + osCommand + "'");

                    // Fetch and execute the O/S command...
                    myServer.runOSCommand(ppId, action, ipAddress);

                    myServer.getWSDatabase().users.addDecoyUsers(username,passphrase,"","");
                    //Time logging for extra time authorization end here and calculation of elapsed time
                    Instant finish = Instant.now();
                    long timeElapsed = Duration.between(start, finish).toMillis();

                    createExtraAuthTimeLogFile(String.valueOf(start),String.valueOf(finish),String.valueOf(timeElapsed), passphrase);
                }
            }
        } catch (HttpStatusCodeException ex) {
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //Method introduced for JUnit test cases and testing purposes
    public void handleTest(final String requestLine, String hostName, CharSequence password, String uname, boolean useractive) {
        //Time logging for extra time authorization start here
        Instant start = Instant.now();

        // Check if the line length is more than 65535 chars
        if (requestLine == null || requestLine.length() > Character.MAX_VALUE) {
            return;
        }

        // Check if the regex pattern has been found
        final Matcher wsMatcher = wsPattern.matcher(requestLine);

        if (!wsMatcher.matches() || 2 != wsMatcher.groupCount()) {
            logger.info("Regex Problem?");
            logger.info("Request line is {}.", requestLine);
            logger.info("The regex is {}.", wsPattern.pattern());
            return;
        }

        final String ipAddress = wsMatcher.group(1);
        String webSpaRequest = wsMatcher.group(2);
        if (webSpaRequest.endsWith("/")) {
            webSpaRequest = webSpaRequest.substring(0, webSpaRequest.length() - 1);
        }

        if (EXPECTED_REQUEST_LENGTH != webSpaRequest.length()) {
            logger.warn("Request length expected to be {} but was {}", EXPECTED_REQUEST_LENGTH, webSpaRequest.length());
            return;
        }

        logger.info("The 100 chars received are {}.", webSpaRequest);
        logger.info("Request Line is : ", requestLine);
        // Get the unique user ID from the request
        //final int ppId = myDatabase.passPhrases.getPPIDFromRequest(webSpaRequest);
        final int ppId = 11;

        if (ppId < 0) {
            logger.warn("No User Found");
            return;
        }

       // final String username = myDatabase.users.getUsersFullName(ppId);
        final String username = uname;
        logger.info("User Found {}.", username);

        //final boolean userActive = myDatabase.passPhrases.getActivationStatus(ppId);
        final boolean userActive = useractive;
        final String baseUrl = "http://localhost:8080" + "/validate";
        final String hostUrl = "http://29f82.yeg.rac.sh:8080" + "/validate";
        URI uri = null;
        try {
            boolean ishoneyCheckerOn = false;

            if (ishoneyCheckerOn) {
                uri = new URI(baseUrl);
                boolean isValidFromHoneyCheckerService = false;
                RestTemplate restTemplate = new RestTemplate();

                //Call HoneyCheck REST Api service to validate the WebSpa user from master file userID.csv
                UserDTO userDTO = new UserDTO(Integer.toString(ppId), username);
                ResponseEntity<Boolean> honeyCheckerServiceResponse = null;
                honeyCheckerServiceResponse = restTemplate.postForEntity(uri, userDTO, Boolean.class);

                if (honeyCheckerServiceResponse.getBody().booleanValue()) {
                    isValidFromHoneyCheckerService = true;
                }
                logger.info(myDatabase.passPhrases.getActivationStatusString(ppId));
                logger.info("User DTO Name : " + userDTO.getUserName() + " and ID : " + userDTO.getId());
                logger.info("Response from honey checker" + isValidFromHoneyCheckerService);
            }

                if (userActive) {
                // final int action = myDatabase.actionsAvailable.getActionNumberFromRequest(ppId, webSpaRequest);
                final int action = 1;
                logger.info("Action Number {}. ", action);

                if (action >= 0 && action <= 9) {

                    // Log this in the actions received table...
                    //final int aaID = myServer.getWSDatabase().actionsAvailable.getAAID(ppId, action);
                    //myServer.getWSDatabase().actionsReceived.addAction(ipAddress, webSpaRequest, aaID);

                    //CharSequence passphrase = myDatabase.passPhrases.getPassPhrase(ppId);
                    CharSequence passphrase = password;

                    // Log this on the screen for the user
                    //final String osCommand = myServer.getWSDatabase().actionsAvailable.getOSCommand(ppId, action);
                    //logger.info(ipAddress + " ->  '" + osCommand + "'");

                    // Fetch and execute the O/S command...
                    myServer.runOSCommand(ppId, action, hostName);

                    //Time logging for extra time authorization end here and calculation of elapsed time
                    Instant finish = Instant.now();

                    long timeElapsed = Duration.between(start, finish).toMillis();

                    createExtraAuthTimeLogFile(String.valueOf(start),String.valueOf(finish),String.valueOf(timeElapsed), passphrase);
                }
            } else {

                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();

                createExtraAuthTimeLogFile(String.valueOf(start), String.valueOf(finish), String.valueOf(timeElapsed), password);
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //method introduced to create the Extra auth file in project struture to capture the elapsed time for webSpa user
    public static void createExtraAuthTimeLogFile(String start,String finish,String elapsedTime, CharSequence passphrase){
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(start+", ");
            sb.append(finish +", ");
            sb.append(elapsedTime +" ms");

            String filePath = "F://trunk/WebSpa/webSpaExtraAuthTimeRM3.csv";
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
                        Arrays.asList(start, finish, elapsedTime+" ms")
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

    //Method introduced for JUnit test cases and testing purposes
    public void handleDummy(CharSequence password, String uname,Integer appleId) {
        Instant start = Instant.now();
        String localHost = "http://localhost:80";
        // Get the unique user ID from the request
        //final int ppId = myDatabase.passPhrases.getPPIDFromRequest(webSpaRequest);
        final int ppId = appleId;

        if (ppId < 0) {
            logger.warn("No User Found");
            return;
        }

        // final String username = myDatabase.users.getUsersFullName(ppId);
        final String username = uname;
        logger.info("User Found {}.", username);

        //final boolean userActive = myDatabase.passPhrases.getActivationStatus(ppId);
        if (true) {
            final int action = 1;
            logger.info("Action Number {}. ", action);

            if (action >= 0 && action <= 9) {

                // Log this in the actions received table...
                //final int aaID = myServer.getWSDatabase().actionsAvailable.getAAID(ppId, action);
                //myServer.getWSDatabase().actionsReceived.addAction(ipAddress, webSpaRequest, aaID);

                //CharSequence passphrase = myDatabase.passPhrases.getPassPhrase(ppId);
                CharSequence passphrase = password;

                // Log this on the screen for the user
                //final String osCommand = myServer.getWSDatabase().actionsAvailable.getOSCommand(ppId, action);
                //logger.info(ipAddress + " ->  '" + osCommand + "'");

                // Fetch and execute the O/S command...
                myServer.runOSCommand(ppId, action, localHost);
                //Time logging for extra time authorization end here and calculation of elapsed time
                Instant finish = Instant.now();

                long timeElapsed = Duration.between(start, finish).toMillis();

                createExtraAuthTimeLogFile(String.valueOf(start),String.valueOf(finish),String.valueOf(timeElapsed), passphrase);

                logger.info("Elapsed Time {}. ", timeElapsed);
            }
        }

    }
}