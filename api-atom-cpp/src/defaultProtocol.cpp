#include "defaultProtocol.h"
#include <stdint.h>

using namespace api;

DefaultProtocol::DefaultProtocol(PacketRegistry& _registry):
	registry(_registry) {}

Packet* DefaultProtocol::receive(InputStream& inputStream) 
	throw (ApiException) {

	int8_t packetId = inputStream.readByte();
	Packet* packet = registry.newPacket(packetId);
	packet -> read(inputStream);
	return packet;
}

void DefaultProtocol::transfer(Packet* packet, 
	OutputStream& outputStream) throw (ApiException) {

	outputStream.writeByte(
		(int8_t)registry.lookPacket(packet));
	packet -> write(outputStream);
}
