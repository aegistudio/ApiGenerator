#include "packetReturn.h"

using namespace api;

PacketReturn::~PacketReturn() {
	if(result) delete[] result;
}

void PacketReturn::read(InputStream& inputStream) {
	caller = inputStream.readInt();

	size = inputStream.readInt();
	result = new int8_t[size];
	inputStream.read(result, size);
}

void PacketReturn::write(OutputStream& outputStream) {
	outputStream.writeInt(caller);
	
	outputStream.writeInt(size);
	outputStream.write(result, size);
}
