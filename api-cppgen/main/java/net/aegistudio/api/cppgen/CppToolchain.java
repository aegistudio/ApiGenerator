package net.aegistudio.api.cppgen;

public enum CppToolchain {
	MINGW32("gcc", "mingw32-g++", "mingw32-ld", "mingw32-ar"),
	GCC("gcc", "g++", "ld", "ar"),
	ENVGCC("gcc", "$(CC)", "$(LD)", "$(AR)"),
	MSVC("msvc", "CL.EXE", "LINK.EXE", "LIB.EXE"),
	WRAPMSVC("msvc", "mscl", "mslink", "mslib"),
	ENVMSVC("msvc", "$(CC)", "$(LD)", "$(AR)");
	
	public final String whichMake;
	public final String compiler;
	public final String linker;
	public final String libtool;
	
	private CppToolchain(String makefile, 
			String compiler, String linker, String libtool) {
		this.whichMake = makefile;
		this.compiler = compiler;
		this.linker = linker;
		this.libtool = libtool;
	}
	
	public static String supported() {
		StringBuilder builder = new StringBuilder();
		CppToolchain[] toolChains = values();
		for(int i = 0; i < toolChains.length; i ++) {
			if(i > 0) builder.append(", ");
			builder.append(toolChains[i].name().toLowerCase());
		}
		return new String(builder);
	}
	
	public static CppToolchain parse(String name) {
		try {
			return valueOf(name.toUpperCase());
		}
		catch(RuntimeException e) {
			throw new IllegalArgumentException("Invalid toolchain name!");
		}
	}
}
