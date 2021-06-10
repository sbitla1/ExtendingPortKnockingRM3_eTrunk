package net.seleucus.wsp.server.commands;

import net.seleucus.wsp.console.WSConsole;
import net.seleucus.wsp.main.WebSpa;
import net.seleucus.wsp.server.WSServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WSPassPhraseDuplicateAddTest {

    @Test
    void getAlphaNumericPassPhraseString() throws Exception {

        WSServer wsServer = new WSServer(new WebSpa(WSConsole.getWsConsole()));
        WSPassPhraseDuplicateAdd wsPassPhraseDuplicateAdd = new WSPassPhraseDuplicateAdd(wsServer);
int i=1;
        for (int j = 0; j < 10; j++) {
            System.out.println(i++ +": "+wsPassPhraseDuplicateAdd.getAlphaNumericPassPhraseString());
        }
    }
}