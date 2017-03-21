package net.aegistudio.api.javagen;

import net.aegistudio.api.gen.TypeTable;

public interface FilteredSerializer extends Serializer {
	public boolean accept(TypeTable.Result type);
}
