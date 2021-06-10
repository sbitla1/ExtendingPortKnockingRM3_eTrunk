package net.seleucus.wsp.server.commands;

import net.seleucus.wsp.server.WSServer;

public class WSDecoyKnockShow extends WSCommandOption {

	public WSDecoyKnockShow(WSServer myServer) {
		super(myServer);
	}

	@Override
	protected void execute() {

		final String decoyKnocks = this.myServer.getWSDatabase().users.showAllDecoyKnocks();

			myServer.println(decoyKnocks);

	} // execute method

	@Override
	public boolean handle(final String cmd) {

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
		
		if(cmd.equalsIgnoreCase("knock show")) {
			
			valid = true;
		
		}
		
		return valid;
		
	}  // isValid method

}
