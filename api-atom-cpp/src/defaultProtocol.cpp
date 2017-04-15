#include "defaultProtocol.h"
#include <stdint.h>

using namespace api;

DefaultProtocol::DefaultProtocol(PacketRegistry& _registry):
	registry(_registry) {}

_EX(Packet*) DefaultProtocol::receive(InputStream& inputStream) {
	int8_t packetId = inputStream.readByte();

	tryDeclare(Packet*, packet, 
		registry.newPacket(packetId));
	packet -> read(inputStream);
	return packet;
}

_EX(void*) DefaultProtocol::transfer(Packet* packet, 
	OutputStream& outputStream) {

	tryDeclare(int, packetId, 
		registry.lookPacket(packet));

	outputStream.writeByte(packetId);
	packet -> write(outputStream);
	return NULL;
}
