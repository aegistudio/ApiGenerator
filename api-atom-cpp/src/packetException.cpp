#include "packetException.h"
#include "stringio.h"

using namespace api;

PacketException::~PacketException() {}

void PacketException::read(InputStream& inputStream) {
	caller = inputStream.readInt();
	exception = api::String::read(inputStream);
}

void PacketException::write(OutputStream& outputStream) {
	outputStream.writeInt(caller);
	api::String::write(exception.message(), outputStream);
}
