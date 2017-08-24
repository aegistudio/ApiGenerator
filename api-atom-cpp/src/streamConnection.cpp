#include "streamConnection.h"

#include "safeCounter.h"
#include <readerThread>
#include <writerThread>
#include <monitorQueue>
#include <memory>
#include "packetException.h"
#include "packetCall.h"

using namespace api;

StreamFactory::StreamFactory(
	InputStream& _i, OutputStream& _o, Platform& _p, Protocol<Packet>& _pt):
	inputStream(_i), outputStream(_o), platform(_p), protocol(_pt) {}


class StreamReaderHandler : public ReaderHandler<Packet> {
	PacketHandler& packetHandle;
public:
	StreamReaderHandler(PacketHandler& _handle):
		packetHandle(_handle) {}
	virtual void handleData(Packet* packet) {
		if(packet != NULL) 
			packetHandle.handle(packet);
	}

	virtual void handleError(ApiException exception) {
		PacketException readErrorPacket;
		readErrorPacket.caller = 0;
		readErrorPacket.exception = exception;
		packetHandle.handle(&readErrorPacket);
	}
};

class StreamWriteHandler : public WriterHandler<Packet> {
	PacketHandler& packetHandle;
public:
	StreamWriteHandler(PacketHandler& _handle):
		packetHandle(_handle) {}

	virtual void handleError(Packet* packet, ApiException e) {
		if(packet != NULL && (packet -> id() == PacketType::PacketCall)) {
			PacketCall* call = reinterpret_cast<PacketCall*>(packet);
			PacketException writeErrorPacket;
			writeErrorPacket.caller = call -> caller;
			writeErrorPacket.exception = e;
			packetHandle.handle(&writeErrorPacket);
		}
	}
};

class StreamConnection : public Connection {
	InputStream& inputStream;
	OutputStream& outputStream;
	PacketHandler& packetHandle;
	Platform& platform;
	Protocol<Packet>& protocol;
	SafeCounter safeCounter;

	// Reader scope.
	StreamReaderHandler readHandler;
	ReaderThread<Packet> readerThread;
	std::unique_ptr<Thread> readerThreadHandle;

	// Writer scope.
	MonitorQueue<Packet*> monitorQueue;
	StreamWriteHandler writeHandler;
	WriterThread<Packet> writerThread;
	std::unique_ptr<Thread> writerThreadHandle;

public:
	StreamConnection(InputStream&, OutputStream&, 
		PacketHandler&, Platform&, Protocol<Packet>&);
	
	virtual ~StreamConnection() {}

	virtual void send(Packet*);

	virtual void start();

	virtual void close();
};


StreamConnection::StreamConnection(
	InputStream& _i, OutputStream& _o, PacketHandler& _p, 
	Platform& _pt, Protocol<Packet>& _ptcl):
	inputStream(_i), outputStream(_o), packetHandle(_p), 
	platform(_pt), protocol(_ptcl), safeCounter(_pt),

	// Reader handler.
	readHandler(packetHandle), 
	readerThread(_pt, _i, _ptcl, readHandler, safeCounter),
	readerThreadHandle(_pt.newThread(&readerThread)),
	
	// Writer handler.
	monitorQueue(_pt, NULL), writeHandler(packetHandle), 
	writerThread(_ptcl, _o, writeHandler, monitorQueue),
	writerThreadHandle(_pt.newThread(&writerThread)) {
}

void StreamConnection::start() {
	readerThreadHandle -> start();
	writerThreadHandle -> start();
}

void StreamConnection::close() {
	readerThreadHandle -> kill();
	safeCounter.wait();
	monitorQueue.close();
	writerThreadHandle -> join();
}

void StreamConnection::send(Packet* packet) {
	monitorQueue.add(packet);
}

Connection* api::StreamFactory::newConnection(PacketHandler& handler) {
	return new StreamConnection(inputStream, outputStream, 
		handler, platform, protocol);
}
