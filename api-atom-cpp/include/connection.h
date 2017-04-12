/**
 * CONNECTION.H - Substitutable Connection
 * 
 * Connections are substitutable lower level
 * of the API connector. A connection should
 * be capable of sending and receiving packets.
 * 
 * And a connection hides the detail of how to
 * send and receive packets inside as their
 * concrete implementation.
 */

#pragma once
#include "apiPacket.h"

namespace api {

class PacketHandler {
public:
	virtual void handle(Packet&) = 0;
};

class Connection {
public:
	// Warning: sending packet will transfer
	// ownership to the connection, please 
	// be aware of double-free corruption
	// caused by improper ownership.
	virtual void send(Packet*) = 0;

	// Run into eternal loop of waiting
	// for packet, block until close called.
	virtual void start() = 0;

	// Exit the eternal waiting loop.
	virtual void close() = 0;
};

class ConnectionFactory {
public:
	// @@@ Memory Leak Warning @@@
	// Invocation to this method is equivalent
	// to invocation to constructor denoted by
	// this factory.
	virtual Connection* newConnection(PacketHandler&);
};

};
