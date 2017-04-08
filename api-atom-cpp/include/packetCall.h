/**
 * PACKETCALL.H
 *
 * This packet is sent when a call present.
 *
 * The layout of this packet is:
 * +-------------------+----------------+
 * | CallerId          | Int32          |
 * +-------------------+----------------+
 * | CalleeId          | Int32          |
 * +-------------------+----------------+
 * | Call              | Int32          |
 * +-------------------+----------------+
 * | sizeof(Parameter) | Int32          |
 * +-------------------+----------------+
 * | Parameter         | variant        |
 * +-------------------+----------------+
 */
#pragma once

#include "apiPacket.h"
#include "stream.h"

namespace api {

namespace PacketType {
const int PacketCall = 0x00;
};

class PacketCall : public Packet {
public:
	int caller;
	int callee;
	int call;

	int size;	int8_t* parameter;

	virtual ~PacketCall();

	inline virtual int id() { return PacketType::PacketCall; }

	virtual void read(InputStream& inputStream);

	virtual void write(OutputStream& outputStream);
};

};
