package net.seleucus.wsp.server.commands;

import net.seleucus.wsp.server.WSServer;
import net.seleucus.wsp.util.WSUtil;

import java.util.Random;

public class WSPassPhraseDuplicateAdd extends WSCommandOption {

	public WSPassPhraseDuplicateAdd(WSServer myServer) {
		super(myServer);
	}

	@Override
	protected void execute() {
		
		final String users = this.myServer.getWSDatabase().users.showAllUsers();
		myServer.println(users);
		
		int ppID = myServer.readLineOptionalInt("Select a User ID to Enter another Pass-Phrase");
		boolean userIDFound = myServer.getWSDatabase().passPhrases.isPPIDInUse(ppID);

		if (userIDFound == false) {

			myServer.println("User ID Not Found");

		} else {
			
			String userName = myServer.getWSDatabase().users.getUsersFullName(ppID);
			
			final String choice = myServer.readLineOptional("Add duplicate pass-phrase [y/N]");

			//Call dup user adding method here
			addDupUserPassPhrase(ppID);

			if (WSUtil.isAnswerPositive(choice)) {
				
				final CharSequence passSeq = myServer.getWSDatabase().passPhrases.getPassPhrase(ppID);
				final String lastModifiedDate = myServer.getWSDatabase().passPhrases.getLastModifiedDate(ppID);

				for (int i = 0; i > ppID; i++) {
					myServer.println("");
					myServer.println("ID: " + ppID);
					myServer.println("Full Name: " + userName);
					myServer.println("Pass-Phrase: " + passSeq.toString());
					myServer.println("Last Modified: " + lastModifiedDate );
					myServer.println("");
				}
			}
			
		}
		
	} // execute method

	@Override
	public boolean handle(String cmd) {

		boolean validCommand = false;

		if (isValid(cmd)) {
			validCommand = true;
			this.execute();
		}

		return validCommand;

	} // handle method

	@Override
	protected boolean isValid(String cmd) {
		boolean valid = false;
		if (cmd.equalsIgnoreCase("add dup pass-phrase")) {
			valid = true;
		}
		return valid;
	}  // isValid method

	public void addDupUserPassPhrase(int ppID) {

		String currentUser = myServer.getWSDatabase().users.getUsersFullName(ppID);

		boolean passPhraseInUse = true;
		CharSequence passSeq;
		int numberofPassPhrases;

		do {
			//passSeq = myServer.readPasswordRequired("Enter duplicate Pass-Phrase for User"+" " +currentUser);
			numberofPassPhrases = myServer.readLineOptionalInt("Enter number of Pass-Phrases to be generated by WebSpa admin to User - " +" "+currentUser);
			passSeq = getAlphaNumericPassPhraseString();
			passPhraseInUse = myServer.getWSDatabase().passPhrases.isPassPhraseInUse(passSeq);

			if (passPhraseInUse == true) {
				myServer.println("This Pass-Phrase is already taken and in use by another user");
				myServer.println("WebSpa pass-phrases have to be unique for each user");
			}

		} while(passPhraseInUse);

		String eMail = "admin@gmail.com";
		String phone = "780-963-1236";

		//String eMail = myServer.readLineOptional("Please enter the New User's Email Address");
		//String phone = myServer.readLineOptional("Please enter the New User's Phone Number");

		for (int j = 0; j < numberofPassPhrases; j++) {
			passSeq = getAlphaNumericPassPhraseString();
			myServer.getWSDatabase().users.addDuplicatePassPhraseForUser(currentUser, passSeq, eMail, phone, numberofPassPhrases);
			}
		System.out.println(+numberofPassPhrases+ "-" + "Duplicate Pass-Phrases Added to user {} to the database... for the user " +currentUser);
	}
	// function to generate a random pass-phrase string of length n
	String getAlphaNumericPassPhraseString() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 20;
		String generatedString = null;
		Random random = new Random();

		generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint,
						StringBuilder::append)
				.toString();
		// System.out.println(generatedString);
		return generatedString;
	}

}
