/**
 * DEFAULTPROTOCOL.H - Default Protocol
 *
 * The default protocol will just transfer
 * the packet with one byte packet id 
 * followed by the packet data.
 */
#pragma once

#include "protocol.h"
#include "apiPacket.h"

namespace api {

class DefaultProtocol : public Protocol<Packet> {
	PacketRegistry& registry;
public:
	DefaultProtocol(PacketRegistry&);

	virtual ~DefaultProtocol() {}

	virtual Packet* receive(InputStream&)
		throw (ApiException);

	virtual void transfer(Packet*, OutputStream&)
		throw (ApiException);
};

};
