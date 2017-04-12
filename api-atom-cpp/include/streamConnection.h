/**
 * STREAMCONNECTION.H - Stream-based Connection
 *
 * A stream based connection will read and write
 * from and to a stream, regardless of what the
 * underlying content is the stream.
 */

#pragma once

#include "stream.h"
#include "connection.h"
#include "platform.h"
#include "protocol.h"

namespace api {

class StreamFactory : public ConnectionFactory {
	InputStream& inputStream;
	OutputStream& outputStream;
	Platform& platform;
	Protocol<Packet>& protocol;
public:
	StreamFactory(InputStream&, OutputStream&, Platform&, Protocol<Packet>&);

	virtual ~StreamFactory() {}

	virtual Connection* newConnection(PacketHandler&);
};

};
