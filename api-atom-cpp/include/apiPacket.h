/**
 * APIPACKET.H - Api Packets
 *
 * Packets are datagrams that could
 * be switcted between different API
 * systems.
 *
 * Every packet is labelled with their
 * packet id.
 */

#pragma once

#include "apiException.h"
#include <exceptional>
#include "stream.h"

namespace api {

// Please define all packet types here, so that we could
// refer to them more easily.
namespace PacketType {};

class Packet {
public:
	virtual ~Packet() {}

	virtual int id() = 0;

	virtual void read(InputStream& inputStream) = 0;

	virtual void write(OutputStream& outputStream) = 0;
};

class PacketRegistry {
public:
	virtual ~PacketRegistry() {}

	virtual _EX(Packet*) newPacket(int packetId) = 0;

	virtual _EX(int) lookPacket(Packet* packet) = 0;
};

};
