package net.seleucus.wsp.client;

import keypair.EncryptionUtil;
import net.seleucus.wsp.console.WSConsole;
import net.seleucus.wsp.main.WebSpa;
import net.seleucus.wsp.server.WSServer;
import net.seleucus.wsp.server.listener.WSLegacyLogListener;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

class WSClientIntegrationTest {

    @Test
    void clientBodyMethod() throws Exception {
        final WebSpa webSpa = new WebSpa(WSConsole.getWsConsole());
        WSClient wsClient = new WSClient(webSpa);

        CharSequence password = "Pass@123";
        String localHost = "http://localhost:80";

        List<Input> inputs = new ArrayList<>();

        String orginalRequest = "Pass@123"; //original webspa db record
        String fakeRequest1 = "cFfStoidEGet0yqKpNrh-0etD4Zr2kUbzaodA3TmJ9N2UVBkJOM0OyNy5PtzWtDfSMIA8YB1iKSaTV9jkW7rKYLModQhockIxy32"; //decoy
        String fakeRequest2 = "timePass"; // tim epass

        inputs.add(new Input(localHost, "-0etD4Zr2kUbzaodA3TmJ9N2UVBkJOM0OyNy5PtzWtDfSMIA8YB1iKSaTV9jkW7rKYLModQhockIxy32", 1));
        inputs.add(new Input(localHost, "User@123", 4));
        inputs.add(new Input(localHost, "Value@123434324", 2));
        inputs.add(new Input(localHost, "Mack@123", 2));
        inputs.add(new Input(localHost, "Test@123", 5));
        inputs.add(new Input(localHost, "123456", 1));

        Collections.shuffle(inputs);

        for (Input input : inputs) {
                wsClient.clientBodyMethod(input.getHost(), input.password.toString(), input.getAction());
            }

    }

    class Input{
        String host ;
        CharSequence password ;
        int action ;

        public Input(String host, CharSequence password, int action) {
            this.host = host;
            this.password = password;
            this.action = action;
        }

        public String getHost() {
            return host;
        }

        public CharSequence getPassword() {
            return password;
        }

        public int getAction() {
            return action;
        }
    }

    @Test
    void decoyNameTesterMethod() throws Exception {
        final WebSpa webSpa = new WebSpa(WSConsole.getWsConsole());
        WSClient wsClient = new WSClient(webSpa);

        int actionNumber_array[] = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        String[] pass_array = {"Value*12345", "Pass123", "Secure*12", "Password123","password1","111111", "qwerty", "1q2w3e4r", "adobe123","1qaz2wsx","qwertyuiop","trustno1"};
        List<String> list = Arrays.asList(pass_array);

        Map<Integer, String> decoyKnockMap = new HashMap<Integer, String>();
        decoyKnockMap.put(1, "Value*12345");
        decoyKnockMap.put(2, "Secure*12");
        decoyKnockMap.put(3, "Password123");
        decoyKnockMap.put(4, "password1");
        decoyKnockMap.put(5, "111111");

        Set<String> hset = new HashSet<>(Arrays.stream(pass_array).collect(Collectors.toSet()));
        hset.add("Hello124");

        Iterator<String> itr = hset.iterator();
        for (Map.Entry<Integer, String> entry : decoyKnockMap.entrySet()) {
            for (int i = 0; i < 30; ++i) {
                wsClient.clientBodyMethod("http://localhost:80", entry.getValue(), entry.getKey());
            }
        }
    }

    @Test
    public void givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect() {
        int length = 7;
        boolean useLetters = true;
        boolean useNumbers = true;

        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

        System.out.println(generatedString);
    }

    @Test
    public void givenUsingPlainJava1() {
        for (int i = 0; i < 5; i++) {
            randomUsernameGenerator();
        }
    }

    @Test
    public void givenUsingPlainJava2() {
        String originalPassPhrase = "Missm*123";
        Hashtable<Integer, String> decoyKnockMap = new Hashtable<>();
        decoyKnockMap.put(1, "Value*12345");
        decoyKnockMap.put(2, "Secure*12");
        decoyKnockMap.put(3, "Password123");
        decoyKnockMap.put(4, "password1");
        decoyKnockMap.put(5, "111111");
        decoyKnockMap.put(9, originalPassPhrase);

        List<Map.Entry<Integer, String>> entries = new ArrayList<>(decoyKnockMap.entrySet());
        Collections.shuffle(entries);

        for (Map.Entry<Integer, String> entry : entries) {
            System.out.println("Hashmap returns: " + entry.getValue());
        }
    }

    public String randomUsernameGenerator() {
        String names[] = {"JAMES", "JOHN", "ROBERT", "MICHAEL", "WILLIAM", "DAVID", "RICHARD", "CHARLES", "JOSEPH", "THOMAS", "CHRISTOPHER", "DANIEL", "PAUL", "MARK", "DONALD", "GEORGE", "KENNETH", "STEVEN", "EDWARD", "BRIAN", "RONALD", "ANTHONY", "KEVIN", "JASON", "MATTHEW", "GARY", "TIMOTHY", "JOSE", "LARRY", "JEFFREY", "FRANK", "SCOTT", "ERIC"};

        return names[new Random().nextInt(names.length)];
    }

    @Test
    public void runConsoleTest() throws Exception {
        final WebSpa webSpa = new WebSpa(WSConsole.getWsConsole());
        WSClient wsClient = new WSClient(webSpa);
        for (int j = 0; j < 5; j++) {
            wsClient.runConsoleForRM3(true, "Pass@123");
        }

    }

    @Test
    void clientRM3TestwithoutAsymetricEncryption() throws Exception {

        final WebSpa webSpa = new WebSpa(WSConsole.getWsConsole());
        WSClient wsClient = new WSClient(webSpa);

            Integer[] ppIdArray = {1,2,4,5,6,3};
            String[] passphrases = {"Pass@123","afafgr56t5g","456htrh","hthtrhr454","rgeg5ht6","Pfbhweb234"};

            List<Integer> intList = Arrays.asList(ppIdArray);
            Collections.shuffle(intList);
            intList.toArray(ppIdArray);

            for (int i = 0; i < ppIdArray.length; i++) {
                for (int j = 0; j < 5; j++) {
                    wsClient.clientBodyMethod("http://localhost:80", passphrases[i], ppIdArray[i]);
                }
            }
        }

    @Test
    void clientRM3TestwithAsymetricEncryption() throws Exception {

        final WebSpa webSpa = new WebSpa(WSConsole.getWsConsole());
        WSClient wsClient = new WSClient(webSpa);

        ObjectInputStream inputStream;
        inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PUBLIC_KEY_FILE));
        final PublicKey publicKey = (PublicKey) inputStream.readObject();
        //byte[] encryptPassSeq = EncryptionUtil.encrypt(String.valueOf(passSeq), publicKey);

        Integer[] ppIdArray = {1,2,4,5,6,3};
        String[] passphrases = {EncryptionUtil.encrypt("Pass@123", publicKey).toString(),EncryptionUtil.encrypt("wbgk42gu3r3", publicKey).toString(),
                EncryptionUtil.encrypt("bfjkb2332", publicKey).toString()
                ,EncryptionUtil.encrypt(";ojli4jih44", publicKey).toString(),
                EncryptionUtil.encrypt("456htrh", publicKey).toString(),EncryptionUtil.encrypt("rgjkhw4ku", publicKey).toString()};

        List<Integer> intList = Arrays.asList(ppIdArray);
        Collections.shuffle(intList);
        intList.toArray(ppIdArray);

        for (int i = 0; i < ppIdArray.length; i++) {
            for (int j = 0; j < 5; j++) {
                wsClient.clientBodyMethod("http://localhost:80", passphrases[i], ppIdArray[i]);
            }
        }
    }
}