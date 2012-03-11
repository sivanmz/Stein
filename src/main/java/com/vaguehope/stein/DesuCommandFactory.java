package com.vaguehope.stein;

import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;

public class DesuCommandFactory implements Factory<Command> {

	@Override
	public Command create () {
		return new DesuCommand();
	}

}
