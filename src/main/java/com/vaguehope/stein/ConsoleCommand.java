package com.vaguehope.stein;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.SessionAware;
import org.apache.sshd.server.session.ServerSession;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.Terminal;

public class ConsoleCommand implements Command, SessionAware {

	private static final AtomicInteger COUNTER = new AtomicInteger(0);

	private final ScheduledExecutorService schEx;

	private InputStream in;
	private OutputStream out;
	private ExitCallback callback;

	private volatile DesuTerm term = null;

	public ConsoleCommand (ScheduledExecutorService schEx) {
		this.schEx = schEx;
	}

	@Override
	public void setInputStream (InputStream in) {
		this.in = in;
	}

	@Override
	public void setOutputStream (OutputStream out) {
		this.out = out;
	}

	@Override
	public void setErrorStream (OutputStream err) {/* Unused. */}

	@Override
	public void setExitCallback (ExitCallback callback) {
		this.callback = callback;
	}

	@Override
	public void setSession (ServerSession session) {/* Unused. */}

	@Override
	public void start (Environment env) throws IOException {
		Terminal terminal = TerminalFacade.createTextTerminal(this.in, this.out, Charset.forName("UTF8"));
		this.term = new DesuTerm("desuTerm" + COUNTER.getAndIncrement(), env, terminal, this.callback);
		this.term.schedule(this.schEx);
	}

	@Override
	public void destroy () {
		if (this.term == null) throw new IllegalStateException();
		this.term.stopAndJoin();
	}

}
