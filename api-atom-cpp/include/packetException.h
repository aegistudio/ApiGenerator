/**
 * PACKETEXCEPTION.H
 *
 * This packet is sent when a call present.
 *
 * The layout of this packet is:
 * +-------------------+----------------+
 * | CallerId          | Int32          |
 * +-------------------+----------------+
 * | sizeof(Exception) | Int32          |
 * +-------------------+----------------+
 * | Exception         | Variant        |
 * +-------------------+----------------+
 */
#pragma once

#include "packet.h"
#include "apiException.h"
#include "stream.h"

namespace api {

namespace PacketType {
const int PacketException = 0x02;
};

class PacketException : public Packet {
public:
	int caller;
	ApiException exception;

	virtual ~PacketException();

	inline virtual int id() { return PacketType::PacketException; }

	virtual void read(InputStream& inputStream);

	virtual void write(OutputStream& outputStream);
};

};
