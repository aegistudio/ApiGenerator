/**
 * DEFAULTREGISTRY - Default Packet Registry
 *
 * The default packet registry contains three
 * basic packets: 0x00 for PacketCall, 0x01
 * for PacketReturn and 0x02 for PacketException.
 */

#pragma once

#include "apiPacket.h"
#define MAX_PACKET 256

namespace api {

class PacketFactory {
public:
	virtual ~PacketFactory() {}

	virtual Packet* newPacket() = 0;
};

class DefaultRegistry : public PacketRegistry {
	PacketFactory* registry[MAX_PACKET];
public:
	DefaultRegistry();

	void insert(PacketFactory*);

	virtual _EX(Packet*) newPacket(int packetid); 

	virtual _EX(int) lookPacket(Packet* packet);
};

};
