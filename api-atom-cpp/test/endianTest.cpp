#include "testCase.h"
#include "endian.h"

template<typename T>
void endianTestInt(T testInteger) {
	// Test integer.
	T testIntegerMirror = testInteger;

	api::Endian& intEndian = api::Endian::integer();
	intEndian.network((int8_t*)&testInteger, sizeof(T));
	std::cout << "[INFO] After swapping order, the integer "
		<< testIntegerMirror << " becomes: "
		<< testInteger << std::endl;
	intEndian.host((int8_t*)&testInteger, sizeof(T));

	assertEquals(testIntegerMirror, testInteger, 1);
}

template<typename T>
void endianTestFloat(T testFloat) {
	// Test integer.
	T testFloatMirror = testFloat;

	api::Endian& floatEndian = api::Endian::integer();
	floatEndian.network((int8_t*)&testFloat, sizeof(T));
	std::cout << "[INFO] After swapping order, the floating "
		<< testFloatMirror << " becomes: "
		<< testFloat << std::endl;
	floatEndian.host((int8_t*)&testFloat, sizeof(T));

	assertEquals(testFloatMirror, testFloat, 1);
}

void test() throw (int) {
	endianTestInt((long)12);
	endianTestInt((int)12345678);
	endianTestInt((short)1234);
	endianTestFloat((double)3.141592653);
}
