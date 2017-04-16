package net.aegistudio.api.cppgen;

import java.io.IOException;

import net.aegistudio.api.Document;
import net.aegistudio.api.gen.CommonGenerator;
import net.aegistudio.api.gen.Context;

public class CppPerspectGenerator<Perspect> extends CommonGenerator {
	public final boolean clientSide;
	public final CppReadSerializer readSerializer;
	public final CppWriteSerializer writeSerializer;
	
	public CppPerspectGenerator(boolean clientSide) {
		super(new CppTypeTable(clientSide));
		this.clientSide = clientSide;
		this.readSerializer = new CppReadSerializer();
		this.writeSerializer = new CppWriteSerializer();
	}

	@Override
	public void generate(Context context, Document dom) throws IOException {
		
	}

}
