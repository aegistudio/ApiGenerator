package net.aegistudio.api.gen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PseudoContext implements Context {
	protected final OutputStream outputStream;
	protected final String parent;
	
	public PseudoContext(OutputStream outputStream, String parent) {
		this.outputStream = outputStream;
		this.parent = parent;
	}

	public PseudoContext(OutputStream outputStream) {
		this(outputStream, "");
	}

	@Override
	public OutputStream file(String path) throws IOException {
		return new ByteArrayOutputStream() {
			public void close() throws IOException {
				super.close();
				
				String fileQualifier = "========== " + parent + "/" + path + "\n";
				outputStream.write(fileQualifier.getBytes());
				outputStream.write(super.toByteArray());
				outputStream.write("\n\n".getBytes());
				outputStream.flush();
			}
		};
	}

	@Override
	public Context step(String path) throws IOException {
		return new PseudoContext(outputStream, parent + "/" + path);
	}
}
