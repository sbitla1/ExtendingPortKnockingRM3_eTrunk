package net.seleucus.wsp.main;

import net.seleucus.wsp.console.WSConsole;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class WSVersionTest {

	private static final int VERSION_MIN = 0;
	private static final int VERSION_MAX = 10;

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@Before
	public void setUpStreams() {

		System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));

	}

	@After
	public void cleanUpStreams() {
	    System.setOut(null);
	    System.setErr(null);
	}

	@Test
	public final void testGetMajor() {
		int majorVersion = WSVersion.getMajor();

		assertTrue(
			"Major version is between [0,10): " + majorVersion,
			( (majorVersion >= VERSION_MIN) && (majorVersion < VERSION_MAX) )
		);
	}

	@Test
	public final void testGetMinor() {
		int minorVersion = WSVersion.getMinor();

		assertTrue(
			"Major version is between [0,10): " + minorVersion,
			( (minorVersion >= VERSION_MIN) && (minorVersion < VERSION_MAX) )
		);
	}

	@Test
	public final void testGetValue() {
		assertEquals(WSVersion.getValue(), WSVersion.getMajor() + "." + WSVersion.getMinor());
	}

	@Test
	public final void shouldBe3Characters() {
		String version = WSVersion.getValue();
		assertEquals(3, version.length());
	}

	@Test
	public final void testIsValidTrue() {
		assertTrue(WSVersion.isCurrentVersion("0.8"));
	}

	@Test
	public final void testIsValidFalse() {
		assertFalse(WSVersion.isCurrentVersion("0.4"));
	}

	@Test
	public void testRunConsole() throws SQLException {

		WSVersion myVersion = new WSVersion(new WebSpa(WSConsole.getWsConsole()));
		myVersion.runConsole();
		assertEquals(WSVersion.getValue(), outContent.toString().trim());

	}

}
