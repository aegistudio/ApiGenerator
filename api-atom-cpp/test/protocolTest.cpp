#include "testCase.h"
#include "defaultRegistry.h"
#include "defaultProtocol.h"

#include "bufferStream.h"
#include "packetCall.h"
#include "packetReturn.h"
#include "packetException.h"
#include "stringio.h"
#include <sstream>
#include <iostream>

#include <memory>

std::string callPacketStr(int i) {
	std::stringstream innerString;
	innerString << "Test Call Packet #" << i;
	return innerString.str();
}

class TestPart {
public:
	virtual ~TestPart() {}

	virtual int times() = 0;

	virtual api::Packet* transfer(int) = 0;

	virtual void verify(api::Packet*, int) = 0;

	void doTransfer(api::Protocol<api::Packet>& protocol, api::OutputStream& outputStream) {
		int i; for(i = 0; i < times(); i ++) {
			std::auto_ptr<api::Packet> current(transfer(i));
			api::exceptional<void*> transferMonad = protocol.transfer(current.get(), outputStream);
			assertClause(!transferMonad.abnormal, "Error while sending.", 2);
		}
	}

	void doVerify(api::Protocol<api::Packet>& protocol, api::InputStream& inputStream) {
		int i; for(i = 0; i < times(); i ++) {
			api::exceptional<api::Packet*> receiveMonad = protocol.receive(inputStream);
			assertClause(!receiveMonad.abnormal, "Error while receiving.", 1);
			std::auto_ptr<api::Packet> current(receiveMonad.value);
			verify(current.get(), i);
		}
	}
};


const int part1Size = 1000;
class TestPart1Call : public TestPart {
public:
	virtual int times() {
		return part1Size;
	}

	virtual api::Packet* transfer(int i) {
		api::PacketCall* callPacket = new api::PacketCall;
		callPacket -> caller = i;
		callPacket -> callee = i / 2;
		callPacket -> call = part1Size - i;
		
		api::BufferOutputStream innerOutput;
		api::String::write(callPacketStr(i), innerOutput);

		callPacket -> size = innerOutput.size();
		callPacket -> parameter = innerOutput.clone();

		return callPacket;
	}

	virtual void verify(api::Packet* packet, int i) {
		assertEquals(api::PacketType::PacketCall, packet -> id());
		api::PacketCall& theCallPacket 
			= *reinterpret_cast<api::PacketCall*>(packet);

		assertEquals(i, theCallPacket.caller);
		assertEquals(i / 2, theCallPacket.callee);
		assertEquals(part1Size - i, theCallPacket.call);

		api::BufferInputStream innerInput(
			theCallPacket.size, theCallPacket.parameter);
		assertEquals(callPacketStr(i), api::String::read(innerInput));
	}
} testPart1;

const int part2Size = 2000;
class TestPart2Mix : public TestPart {
public:
	virtual int times() {
		return part2Size;
	}

	virtual api::Packet* transfer(int i) {
		switch(i % 3) {
			case 0:
				return new api::PacketCall;
			case 1:
				return new api::PacketReturn;
			default:
				return new api::PacketException;
		}
	}

	virtual void verify(api::Packet* packet, int i) {
		switch(i % 3) {
			case 0:
				assertEquals(api::PacketType::PacketCall, packet -> id());
				return;
			case 1:
				assertEquals(api::PacketType::PacketReturn, packet -> id());
				return;
			default:
				assertEquals(api::PacketType::PacketException, packet -> id());
				return;
		}
	}
} testPart2;


void test() throw (int) {
	int i;
	api::DefaultRegistry registry;
	api::DefaultProtocol protocol(registry);

	api::BufferOutputStream bufferOutput;
	
	// Transfer part 1.
	testPart1.doTransfer(protocol, bufferOutput);
	std::cout << "[INFO] Finished part1 transfer." << std::endl;

	// Transfer part 2.
	testPart2.doTransfer(protocol, bufferOutput);
	std::cout << "[INFO] Finished part2 transfer." << std::endl;

	// Replicate stream.
	int8_t* replicateData = bufferOutput.clone();
	api::BufferInputStream bufferInput(
		bufferOutput.size(), replicateData);
	std::cout << "[INFO] Finished replicate." << std::endl;

	// Receive part 1.
	testPart1.doVerify(protocol, bufferInput);
	std::cout << "[INFO] Finished part1 verifying." << std::endl;

	// Receive part 2.
	testPart2.doVerify(protocol, bufferInput);
	std::cout << "[INFO] Finished part2 verifying." << std::endl;
	
	// Clean up.
	assertEquals(0, bufferInput.remaining());
	delete replicateData;
}
