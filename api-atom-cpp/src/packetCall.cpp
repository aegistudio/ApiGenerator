#include "packetCall.h"

using namespace api;

PacketCall::~PacketCall() {
	if(parameter) delete[] parameter;
}

void PacketCall::read(InputStream& inputStream) {
	caller = inputStream.readInt();
	callee = inputStream.readInt();
	call = inputStream.readInt();

	size = inputStream.readInt();
	parameter = new int8_t[size];
	inputStream.read(parameter, size);
}

void PacketCall::write(OutputStream& outputStream) {
	outputStream.writeInt(caller);
	outputStream.writeInt(callee);
	outputStream.writeInt(call);
	
	outputStream.writeInt(size);
	outputStream.write(parameter, size);
}
