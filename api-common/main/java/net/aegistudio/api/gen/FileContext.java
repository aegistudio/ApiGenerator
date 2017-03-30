package net.aegistudio.api.gen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileContext implements Context {
	protected final File root;
	
	public FileContext(String root) {
		this(new File(root));
	}
	
	public FileContext(File root) {
		this.root = root;
		if(!root.exists()) root.mkdirs();
	}
	
	public FileContext() {
		this(".");
	}
	
	private File into(String path, boolean lastFolder) throws IOException {
		String[] fullPath = path.split("[/]");
		File current = root;
		for(int i = 0; i < fullPath.length; i ++) {
			if(!current.exists()) current.mkdir();
			current = new File(current, fullPath[i]);
		}
		
		if(!current.exists()) {
			if(lastFolder) current.mkdir(); 
			else current.createNewFile();
		}
		return current;
	}
	
	@Override
	public OutputStream file(String path) throws IOException {
		return new FileOutputStream(into(path, false));
	}

	@Override
	public Context step(String path) throws IOException {
		return new FileContext(into(path, true));
	}
}
