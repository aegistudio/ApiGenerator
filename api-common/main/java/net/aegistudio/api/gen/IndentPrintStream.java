package net.aegistudio.api.gen;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;

public class IndentPrintStream extends PrintStream {
	protected int currentIndent = 0;
	public String indent = "\t";
	
	public IndentPrintStream(PrintStream parent) throws FileNotFoundException {
		super(parent);
	}

	public IndentPrintStream(OutputStream parent) throws FileNotFoundException {
		super(parent);
	}
	
	public void println(String line) {
		for(int i = 0; i < currentIndent; i ++)
			super.print(indent);
		super.println(line);
	}
	
	public void push() {
		currentIndent ++;
	}
	
	public void pop() {
		currentIndent --;
	}
}
