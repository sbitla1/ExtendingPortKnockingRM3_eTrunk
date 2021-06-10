package net.seleucus.wsp.server.commands;

import net.seleucus.wsp.server.WSServer;

import java.io.IOException;

public abstract class WSCommandOption {

	protected WSServer myServer;

	public WSCommandOption(WSServer myServer) {
		this.myServer = myServer;
	}
	
	protected abstract void execute() throws IOException, ClassNotFoundException;
	public abstract boolean handle(final String cmd) throws IOException, ClassNotFoundException;
	protected abstract boolean isValid(final String cmd);

}
