package net.seleucus.wsp.server.listener;

import net.seleucus.wsp.console.WSConsole;
import net.seleucus.wsp.main.WebSpa;
import net.seleucus.wsp.server.WSServer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class WSLegacyLogListenerTest {

    @Test
    void handle() throws Exception {

        WSServer wsServer = new WSServer(new WebSpa(WSConsole.getWsConsole()));
        WSLegacyLogListener wsLegacyLogListener = new WSLegacyLogListener(wsServer);
        String arr = "";

//        String requestLine1 = "127.0.0.1 - - [06/Nov/2020:12:55:11 -0700] \"GET /ZH3wMkSDtEvoaLobKnI9Y2ICV633OidyoOvgmIlfAuaQJ2xG2T0lgrNv8qA6TiOp5H5AZ5yvzwNfATdJJGz76kKeu23P23MXNX3s/ HTTP/1.1\" 404 196.";
//
//        Pattern wsPattern = Pattern.compile("(^[0-9]{1,3}\\\\.[0-9]{1,3}\\\\.[0-9]{1,3}\\\\.[0-9]{1,3}) - - \\\\[\\\\d{2}\\\\/\\\\w{3}\\\\/\\\\d{4}:\\\\d{2}:\\\\d{2}:\\\\d{2} .....\\\\] \"GET \\\\/(\\\\S*)\\\\/ HTTP\\\\/1\\\\...");

        String requestLine1 = "127.0.0.1 - - [07/Nov/2020:12:55:11 -0700] \"GET /ZH3wMkSDtEvoaLobKnI9Y2ICV633OidyoOvgmIlfAuaQJ2xG2T0lgrNv8qA6TiOp5H5AZ5yvzwNfATdJJGz76kKeu23P23MXNX3s/ HTTP/1.1\" 404 196.";
//Mack123
        String requestLine2 = "127.0.0.1 - - [07/Nov/2020:03:34:54 -0700] \"GET /SH56Py6CX5asA1rz6XuBbbmcK6mkHHp9Fru8TgRTPRVHegSZBJoI92xsiB7SWG7ajv_XsNLjbTpM7HsmjStn1hstadcYRGVvama9/ HTTP/1.1\" 404 196";

     /*   Pattern wsPattern = Pattern.compile("(^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}) - - \\[\\d{2}\\/\\w{3}\\/\\d{4}:\\d{2}:\\d{2}:\\d{2} .....\\] \\\"GET \\/(\\S*)\\/ HTTP\\/1.1\" [0-9]{1,3} [0-9]{1,3}.");

 String[] passphrases = {"Volvo", "BMW", "Ford", "Mazda"};
        final Matcher wsMatcher = wsPattern.matcher(requestLine1);
        System.out.println("Matches" + wsMatcher.matches());
        System.out.println("Group Count" + wsMatcher.groupCount());

//        if (! wsMatcher.matches() || 2 != wsMatcher.groupCount( )) {
//            logger.info("Regex Problem?");
//            logger.info("Request line is {}.", requestLine);
//            logger.info("The regex is {}.", wsPattern.pattern());
//            return;
//        }
*/

        String[] passphrases = new String[3];
        passphrases[0] = requestLine2;

        for (int i = 0; i < passphrases.length; i++) {
            wsLegacyLogListener.handle(passphrases[i]);
        }
    }

    @Test
    void testWSServerCsvWithTRUE() throws Exception {

        WSServer wsServer = new WSServer(new WebSpa(WSConsole.getWsConsole()));
        WSLegacyLogListener wsLegacyLogListener = new WSLegacyLogListener(wsServer);

        //Incorrect
        String requestLine1 = "127.0.0.1 - - [17/Nov/2020:14:18:39 -0700] \"GET /_8nliF9OLreqLazrEfMx5jbyUaa9JUEEfznecAKftXpZle0AC1E20Lw3Uzy2O3iQrf7tVR1fZ0-Egs9GuS1PhNV3slQaxS8YBeem/ HTTP/1.1\" 404 196";

        //correct pass skbitla
        String requestLine = "127.0.0.1 - - [17/Nov/2020:13:06:12 -0700] \"GET /Q5iKXZYNjcCghittooYHo8oE-9WBS5ShjLrsJL9uC6sAA4wSw64uVwbuwaCE0VMV94KLzuUaxn4zjY0nfYIGs1DNrdXUz7Uetkas/ HTTP/1.1\" 404 196";

        String[] usernames = {"skbitla"};
        String[] passphrases = {"Pass@123"};

        String localHost = "http://localhost:80";

        for (int i = 0; i < passphrases.length; i++) {
            for (int j = 0; j < 30; j++) {
                wsLegacyLogListener.handleTest(requestLine, localHost, passphrases[i], usernames[i], true);
            }
        }
    }

    @Test
    void testWSServerCsvWithFalse() throws Exception {

        WSServer wsServer = new WSServer(new WebSpa(WSConsole.getWsConsole()));
        WSLegacyLogListener wsLegacyLogListener = new WSLegacyLogListener(wsServer);

        //Incorrect
        String requestLine1 = "127.0.0.1 - - [17/Nov/2020:14:18:39 -0700] \"GET /_8nliF9OLreqLazrEfMx5jbyUaa9JUEEfznecAKftXpZle0AC1E20Lw3Uzy2O3iQrf7tVR1fZ0-Egs9GuS1PhNV3slQaxS8YBeem/ HTTP/1.1\" 404 196";

        String[] usernames1 = {"mack123"};
        String[] passphrases1 = {"Value*12345","klemgrj5357","lknloej3rnj4","dfffejk34","dbbkb3334","Pass1234"};

        List<String> intList = Arrays.asList(passphrases1);
        Collections.shuffle(intList);
        intList.toArray(passphrases1);

        String localHost = "http://localhost:80";
        String ipAddressToKnock = "http://29f7a.yeg.rac.sh:80";

        for (int i = 0; i < passphrases1.length; i++) {
            for (int j = 0; j < 30; j++) {
                wsLegacyLogListener.handleTest(requestLine1, localHost, passphrases1[i], usernames1[i], false);
            }
        }
    }


    @Test
    void mIlliSecondTest() throws Exception {
        long start = System.nanoTime();
        Instant startTime = Instant.now();

        Thread.sleep(1000);

        long finish = System.nanoTime();
        Instant endTime = Instant.now();
        long timeElapsed = finish - start;

        long nanoTime = Duration.between(startTime, endTime).toMillis();


        System.out.println("Stats nanoTime-- start time :"+start +" finsih time :"+finish +", Elapsed:"+timeElapsed/1000000+" ms");
        System.out.println("Stats Instant-- start time :"+startTime +" finsih time :"+endTime +", Elapsed:"+nanoTime+" ms");

    }


    @Test
    void runOSCommandTest() throws Exception {

        WSServer wsServer = new WSServer(new WebSpa(WSConsole.getWsConsole()));

        //String[] passphrases = {"Pass@123", "User@123", "Value@123434324", "Mack@123", "Test@123"};
        String[] passphrases = {"Pass@123"};
        int[] actionIds = {1,2,3,2,5};
        int[] ppIds = {11,14,13,22,15};
        String localHost = "http://localhost:80";
        String ipAddressToKnock = "http://29f7a.yeg.rac.sh:80";

        for (int i=0;i<passphrases.length;i++){
            for (int j = 0; j < 30; j++) {
               // wsServer.runOSCommandTest(ppIds[i], actionIds[i], localHost, passphrases[i]);
            }
        }
    }

    @Test
    void runOSCommandTest1() throws Exception {
        try {
            StringBuilder sb = new StringBuilder();

            String filePath = "F://trunk/WebSpa/webSpaExtraAuthTime.csv";
            File myObj = new File(filePath);
            if (!myObj.exists()) {
                myObj.createNewFile();
            } else {
                List<List<String>> rows = Arrays.asList(
                        Arrays.asList("Jean", "author", filePath),
                        Arrays.asList("David", "editor", filePath),
                        Arrays.asList("Scott", "editor", filePath)
                );

                FileWriter csvWriter = new FileWriter(filePath, true);
                csvWriter.append("WebSpa Server started processing time :");
                csvWriter.append(",");
                csvWriter.append("Response time from HoneyChecker Recived :");
                csvWriter.append(",");
                csvWriter.append("Time in ms");
                csvWriter.append("\n");

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

    @Test
    void testWSServerDummy() throws Exception {

        WSServer wsServer = new WSServer(new WebSpa(WSConsole.getWsConsole()));
        WSLegacyLogListener wsLegacyLogListener = new WSLegacyLogListener(wsServer);

        Integer[] ppIdArray = {11,12,14,15,16,13};
        String[] usernames = {"skbitla","afafuser123","456htrh","hthtrhr454","rgeg5ht6","Pass@1234"};
        String[] passphrases = {"Pass@123","afafgr56t5g","456htrh","hthtrhr454","rgeg5ht6","Pass@1234"};

        List<Integer> intList = Arrays.asList(ppIdArray);
        Collections.shuffle(intList);
        intList.toArray(ppIdArray);

        for (int i = 0; i < ppIdArray.length; i++) {
            for (int j = 0; j < 30; j++) {
                wsLegacyLogListener.handleDummy(passphrases[i], usernames[i], ppIdArray[i]);
            }
        }
    }
}