package net.seleucus.wsp.db;

import net.seleucus.wsp.client.WSConnection;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static net.seleucus.wsp.db.WSDatabase.DB_PATH;
import static org.junit.jupiter.api.Assertions.*;

class WSUsersTest {

    @Test
    void getUsersFullNameTest() {
        final String DB_PATH = "webspa-db";
      /*  Class.forName("org.hsqldb.jdbcDriver");
        final Connection wsConnection = DriverManager.getConnection("jdbc:hsqldb:" + DB_PATH);;

        WSUsers wsUsers = new WSUsers(wsConnection);
        wsUsers.getUsersFullNameTest(11);*/
    }

    @Test
    void getDecoyUsersTest() throws SQLException, ClassNotFoundException {
        final String DB_PATH = "webspa-db";
        final Connection wsConnection;

        Class.forName("org.hsqldb.jdbcDriver");
        wsConnection = DriverManager.getConnection("jdbc:hsqldb:" + DB_PATH);
        WSUsers wsUsers = new WSUsers(wsConnection);

        wsUsers.addDecoyUsers("test2","test1","test","test");
    }

    @Test
    public void givenUsingApache_whenGeneratingRandomStringBounded_thenCorrect() {

        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

        System.out.println(generatedString);
    }
}