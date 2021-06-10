package net.seleucus.wsp.server.commands;

import keypair.EncryptionUtil;
import net.seleucus.wsp.server.WSServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;

public class WSUserAdd extends WSCommandOption {

	public WSUserAdd(WSServer myServer) {
		super(myServer);
	}

	@Override
	protected void execute() throws IOException, ClassNotFoundException {
		
		String fullName = myServer.readLineRequired("Enter the New User's Full Name");
		
		boolean passPhraseInUse = true;
		CharSequence passSeq;
		
		do {
			
			passSeq = myServer.readPasswordRequired("Enter the New User's Pass-Phrase");
						
			passPhraseInUse = myServer.getWSDatabase().passPhrases.isPassPhraseInUse(passSeq);
			
			if(passPhraseInUse == true) {
				myServer.println("This Pass-Phrase is already taken and in use by another user");
				myServer.println("WebSpa pass-phrases have to be unique for each user");
			}
			
			
		} while(passPhraseInUse);

		String eMail = myServer.readLineOptional("Please enter the New User's Email Address");
		String phone = myServer.readLineOptional("Please enter the New User's Phone Number");

		ObjectInputStream inputStream;
		inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PUBLIC_KEY_FILE));
		final PublicKey publicKey = (PublicKey) inputStream.readObject();
		byte[] encryptPassSeq = EncryptionUtil.encrypt(String.valueOf(passSeq), publicKey);

		myServer.getWSDatabase().users.addDupUser(fullName, encryptPassSeq.toString(), eMail, phone);
		
	} // execute method
	
	@Override
	public boolean handle(final String cmd) throws IOException, ClassNotFoundException {

		boolean validCommand = false;

		if(isValid(cmd)) {
			validCommand = true;
			this.execute();
		}
		
		return validCommand;
		
	} // handle method

	@Override
	protected boolean isValid(final String cmd) {
		
		boolean valid = false;
		
		if(cmd.equalsIgnoreCase("user add")) {
			
			valid = true;
		
		}
		
		return valid;
		
	}  // isValid method
}
