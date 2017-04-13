#include "defaultRegistry.h"

#include "packetCall.h"
#include "packetReturn.h"
#include "packetException.h"

#include <memory>
#include <sstream>

using namespace api;


class CallFactory : public PacketFactory {
public:
	virtual Packet* newPacket() {
		return new PacketCall();
	}
} packetCallFactory;

class ReturnFactory : public PacketFactory {
public:
	virtual Packet* newPacket() {
		return new PacketReturn();
	}
} packetReturnFactory;

class ExceptionFactory : public PacketFactory {
public:
	virtual Packet* newPacket() {
		return new PacketException();
	}
} packetExceptionFactory;

DefaultRegistry::DefaultRegistry() {
	int i = 0; for(; i < MAX_PACKET; i ++)
		registry[i] = NULL;
	insert(&packetCallFactory);
	insert(&packetReturnFactory);
	insert(&packetExceptionFactory);
}

void DefaultRegistry::insert(PacketFactory* factory) {
	Packet* packetNew = factory -> newPacket();
	if(packetNew) {
		registry[packetNew -> id()] = factory;
		delete packetNew;
	}
}

Packet* DefaultRegistry::newPacket(int packetId) 
	throw (ApiException) {
	
	PacketFactory* factory = registry[packetId];
	if(factory) return factory -> newPacket();
	else {
		std::stringstream message;
		message << "Packet #" << packetId << " does not exists.";
		throw ApiException(message.str());
	}
}

int DefaultRegistry::lookPacket(Packet* packet) 
	throw (ApiException) {

	int result = packet -> id();
	if(registry[result] == NULL) {
		std::stringstream message;
		message << "Packet #" << result << " does not exists.";
		throw ApiException(message.str());
	}
	return result;
}
