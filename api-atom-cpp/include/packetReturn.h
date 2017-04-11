/**
 * PACKETCALL.H
 *
 * This packet is sent when a call present.
 *
 * The layout of this packet is:
 * +-------------------+----------------+
 * | CallerId          | Int32          |
 * +-------------------+----------------+
 * | sizeof(Result)    | Int32          |
 * +-------------------+----------------+
 * | Result            | Variant        |
 * +-------------------+----------------+
 */
#pragma once

#include "apiPacket.h"
#include "stream.h"

namespace api {

namespace PacketType {
const int PacketReturn = 0x01;
};

class PacketReturn : public Packet {
public:
	int caller;
	int size;	int8_t* result;

	PacketReturn();

	virtual ~PacketReturn();

	inline virtual int id() { return PacketType::PacketReturn; }

	virtual void read(InputStream& inputStream);

	virtual void write(OutputStream& outputStream);
};

};
