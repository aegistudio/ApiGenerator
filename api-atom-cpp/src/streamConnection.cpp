#include "streamConnection.h"

#include <readerThread>
#include <writerThread>
#include <monitorQueue>
#include <memory>

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
			packetHandle.handle(*packet);
	}

	virtual void handleError(ApiException exception) {
		// Ignore.
	}
};

class StreamWriteHandler : public WriterHandler<Packet> {
public:
	virtual void handleError(Packet* packet, ApiException e) {
		// Ignore.
	}
};

class StreamConnection : public Connection {
	InputStream& inputStream;
	OutputStream& outputStream;
	PacketHandler& packetHandle;
	Platform& platform;
	Protocol<Packet>& protocol;

	// Reader scope.
	StreamReaderHandler readHandler;
	ReaderThread<Packet> readerThread;
	std::auto_ptr<Thread> readerThreadHandle;

	// Writer scope.
	MonitorQueue<Packet*> monitorQueue;
	StreamWriteHandler writeHandler;
	WriterThread<Packet> writerThread;
	std::auto_ptr<Thread> writerThreadHandle;

public:
	StreamConnection(InputStream&, OutputStream&, 
		PacketHandler&, Platform&, Protocol<Packet>&);

	virtual void send(Packet*);

	virtual void start();

	virtual void close();
};


StreamConnection::StreamConnection(
	InputStream& _i, OutputStream& _o, PacketHandler& _p, 
	Platform& _pt, Protocol<Packet>& _ptcl):
	inputStream(_i), outputStream(_o), packetHandle(_p), 
	platform(_pt), protocol(_ptcl),

	// Reader handler.
	readHandler(packetHandle), 
	readerThread(_pt, _i, _ptcl, readHandler),
	readerThreadHandle(_pt.newThread(&readerThread)),
	
	// Writer handler.
	writeHandler(), monitorQueue(_pt.newSemaphore()),
	writerThread(_ptcl, _o, writeHandler, monitorQueue),
	writerThreadHandle(_pt.newThread(&writerThread)) {
}

void StreamConnection::start() {
	readerThreadHandle -> start();
	

	// We just need to wait for the stop of
	// reader thread.
	readerThreadHandle -> join();
}

void StreamConnection::close() {
	readerThreadHandle -> kill();
}

void StreamConnection::send(Packet* packet) {
	monitorQueue.add(packet);
}

Connection* StreamFactory::newConnection(PacketHandler& handler) {
	return new StreamConnection(inputStream, outputStream, 
		handler, platform, protocol);
}
