// This test will send packet inside process,
// and see whether the packet serializer matches
// the deserializer.

#include "testCase.h"
#include "apiPacket.h"
#include "packetCall.h"
#include "packetReturn.h"
#include "packetException.h"

#include "stream.h"
#include "bufferStream.h"

#include <iostream>

// --------------------- Parent PacketTest ------------------------------
class PacketTest {
public:
	virtual void prepare(api::OutputStream&, int) = 0;
	virtual void verify(api::InputStream&, int) throw (int) = 0;

	void test(std::string) throw(int);
};


const int packetCount = 300;
void PacketTest::test(std::string name) throw (int) {
	std::cout << "[INFO] Begin " << name << " test." << std::endl;

	api::BufferOutputStream testOutput;
	int32_t i;

	std::cout << "[INFO] Preparing test data..." << std::endl;
	for(i = 0; i < packetCount; i ++) 
		prepare(testOutput, i);

	std::cout << "[INFO] Replicating test data..." << std::endl;
	int8_t* replicate = testOutput.clone();
	api::BufferInputStream testInput(testOutput.size(), replicate);

	std::cout << "[INFO] checking test data..." << std::endl;
	for(i = 0; i < packetCount; i ++) 
		verify(testInput, i);

	assertEquals(0, testInput.remaining());
	std::cout << "[INFO] End " << name << " test." << std::endl;
}


// --------------------- Child PacketCallTest ---------------------------
class PacketCallTest : public PacketTest {
public:
	virtual void prepare(api::OutputStream&, int);

	virtual void verify(api::InputStream&, int) throw (int);
};

void PacketCallTest::prepare(api::OutputStream& testOutput, int i) {
	api::PacketCall testCall;
	testCall.caller = i;
	testCall.callee = i / 2;
	testCall.call = packetCount - i;

	api::BufferOutputStream innerBuilder;
	innerBuilder.writeFloat(i);
	innerBuilder.writeDouble(i);

	testCall.size = innerBuilder.size();
	testCall.parameter = innerBuilder.clone();
	
	testCall.write(testOutput);
}

void PacketCallTest::verify(api::InputStream& testInput, int i) throw (int) {
	api::PacketCall checkCall;
	checkCall.read(testInput);

	assertEquals(i, checkCall.caller);
	assertEquals(i / 2, checkCall.callee);
	assertEquals(packetCount - i, checkCall.call);

	api::BufferInputStream innerReader(
		checkCall.size, checkCall.parameter);

	assertEquals((float)i, innerReader.readFloat());
	assertEquals((double)i, innerReader.readDouble());
	assertEquals(0, innerReader.remaining());
}

// --------------------- Child PacketReturnTest ---------------------------
class PacketReturnTest : public PacketTest {
public:
	virtual void prepare(api::OutputStream&, int);

	virtual void verify(api::InputStream&, int) throw (int);
};

void PacketReturnTest::prepare(api::OutputStream& testOutput, int i) {
	api::PacketReturn testReturn;
	testReturn.caller = i;

	api::BufferOutputStream innerBuilder;
	innerBuilder.writeInt(i * 1234);

	testReturn.size = innerBuilder.size();
	testReturn.result = innerBuilder.clone();
	
	testReturn.write(testOutput);
}

void PacketReturnTest::verify(api::InputStream& testInput, int i) throw (int) {
	api::PacketReturn checkReturn;
	checkReturn.read(testInput);

	assertEquals(i, checkReturn.caller);

	api::BufferInputStream innerReader(
		checkReturn.size, checkReturn.result);

	assertEquals(i * 1234, innerReader.readInt());
	assertEquals(0, innerReader.remaining());
}

// --------------------- Child PacketExceptionTest ---------------------------
#include <sstream>
class PacketExceptionTest : public PacketTest {
public:
	std::string generateExcept(int);

	virtual void prepare(api::OutputStream&, int);

	virtual void verify(api::InputStream&, int) throw (int);
};

std::string PacketExceptionTest::generateExcept(int i ) {
	std::stringstream builder;
	builder << "Test for exception" << i << std::endl;
	return builder.str();
}

void PacketExceptionTest::prepare(api::OutputStream& testOutput, int i) {
	api::PacketException testException;
	testException.caller = i * 5678;
	testException.exception = generateExcept(i);
	
	testException.write(testOutput);
}

void PacketExceptionTest::verify(api::InputStream& testInput, int i) throw (int) {
	api::PacketException checkException;
	checkException.read(testInput);

	assertEquals(i * 5678, checkException.caller);
	assertEquals(generateExcept(i), checkException.exception.message());
}

// ---------------------- Main Test Function ------------------------------
void test() throw (int) {
	PacketCallTest callTest;
	callTest.test("packet call");

	PacketReturnTest returnTest;
	returnTest.test("packet return");

	PacketExceptionTest exceptionTest;
	exceptionTest.test("packet exception");
}
