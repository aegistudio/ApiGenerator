#include "stringio.h"

#include <iostream>

void api::String::write(const std::string& string, OutputStream& outputStream) {
	int size = string.size();
	outputStream.writeInt(size);
	outputStream.write(string.data(), size);
}

std::string api::String::read(InputStream& inputStream) {
	int size = inputStream.readInt();
	int8_t* buffer = new int8_t[size + sizeof(char)];
	buffer[size] = '\0';
	inputStream.read(buffer, size);
	
	std::string result = (char*)buffer;
	delete[] buffer;
	return result;
}
