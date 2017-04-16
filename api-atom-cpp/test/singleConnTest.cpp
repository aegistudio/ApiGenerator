#include "testCase.h"

#include "pipeStream.h"
#include "streamConnection.h"

#include "defaultRegistry.h"
#include "defaultProtocol.h"

#include "packetCall.h"

#include <iostream>
#include <stdio.h>

const int numPackets = 699;

void test() throw(int) {
	api::Pipe pipe(getPlatform());
	api::DefaultRegistry registry;
	api::DefaultProtocol protocol(registry);

	api::StreamFactory factory(
		pipe.inputStream(), pipe.outputStream(), 
		getPlatform(), protocol);


	api::Semaphore* waitSemaphore = getPlatform()
		.newSemaphore();

	api::Semaphore* printMutex = getPlatform()
		.newSemaphore();
	printMutex -> verhogen();

	class TestHandler : public api::PacketHandler {
		api::Semaphore* theSemaphore;
		api::Semaphore* theMutex;
	public:
		TestHandler(api::Semaphore* semaphore, api::Semaphore* printMutex):
			theSemaphore(semaphore), theMutex(printMutex) {}

		virtual void handle(api::Packet* packet) {
			assertEquals(packet -> id(), api::PacketType::PacketCall);
			api::PacketCall* theCall = reinterpret_cast<api::PacketCall*>(packet);
			assertEquals(1234, theCall -> caller);

			theMutex -> proberen();
			printf("[INFO] Packet %d (#%lx) has verified.\n", 
				theCall -> callee, theCall);
			theMutex -> verhogen();
			if(theCall -> callee == numPackets - 1) 
				theSemaphore -> verhogen();
		}
	} testHandler(waitSemaphore, printMutex);

	api::Connection* connection = factory.newConnection(testHandler);

	printMutex -> proberen();
	std::cout << "[INFO] Wait 1 second." << std::endl;
	printMutex -> verhogen();

	connection -> start();
	//sleep(1000L);

	int i; for(i = 0; i < numPackets; i ++) {
		api::PacketCall* callPacket = new api::PacketCall;
		callPacket -> caller = 1234;
		callPacket -> callee = i;
		connection -> send(callPacket);
		printMutex -> proberen();
		printf("[INFO] Packet %d (#%lx) has sent.\n", i, callPacket);
		printMutex -> verhogen();
	}

	waitSemaphore -> proberen();

	delete waitSemaphore;

	connection -> close();
	delete connection;
}
