#include "packetReturn.h"

using namespace api;

PacketReturn::PacketReturn():
	caller(0), size(0), result(NULL) {}

PacketReturn::~PacketReturn() {
	if(result) delete[] result;
}

void PacketReturn::read(InputStream& inputStream) {
	caller = inputStream.readInt();

	size = inputStream.readInt();
	if(size > 0) {
		result = new int8_t[size];
		inputStream.read(result, size);
	}
}

void PacketReturn::write(OutputStream& outputStream) {
	outputStream.writeInt(caller);
	
	outputStream.writeInt(size);
	if(size > 0)
		outputStream.write(result, size);
}
