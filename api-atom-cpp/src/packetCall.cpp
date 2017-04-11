#include "packetCall.h"

using namespace api;

PacketCall::PacketCall():
	caller(0), callee(0), call(0),
	size(0), parameter(NULL) {}

PacketCall::~PacketCall() {
	if(parameter) delete[] parameter;
}

void PacketCall::read(InputStream& inputStream) {
	caller = inputStream.readInt();
	callee = inputStream.readInt();
	call = inputStream.readInt();

	size = inputStream.readInt();
	if(size > 0) {
		parameter = new int8_t[size];
		inputStream.read(parameter, size);
	}
}

void PacketCall::write(OutputStream& outputStream) {
	outputStream.writeInt(caller);
	outputStream.writeInt(callee);
	outputStream.writeInt(call);
	
	outputStream.writeInt(size);
	if(size > 0) 
		outputStream.write(parameter, size);
}
