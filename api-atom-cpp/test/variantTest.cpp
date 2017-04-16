#include "testCase.h"
#include <apiVariant>
#include <iostream>

api::variant<int> copyInteger(int size) {
	api::variant<int> testObject(size);
	int i; for(i = 0; i < size; i ++)
		testObject[i] = i;
	return testObject;
}

void verifyInteger(int expectedSize, api::variant<int>& buffer) {
	assertEquals(expectedSize, buffer.length());
	int* innerBuffer = *buffer;
	int i; for(i = 0; i < expectedSize; i ++) {
		assertEquals(i, buffer[i]);
		assertEquals(i, innerBuffer[i]);
	}
}

class TestValueType {
public:
	int integerValue;
	double doubleValue;
};

api::variant<TestValueType> copyValueType(int size) {
	api::variant<TestValueType> testObject(size);
	int i; for(i = 0; i < size; i ++) {
		testObject[i].integerValue = i;
		testObject[i].doubleValue = i;
	}
	return testObject;
}

void verifyValueType(int expectedSize, api::variant<TestValueType>& buffer) {
	assertEquals(expectedSize, buffer.length());
	int i; for(i = 0; i < expectedSize; i ++) {
		assertEquals(i, buffer[i].integerValue);
		assertEquals((double)i, buffer[i].doubleValue);
	}
}

void test() throw (int) {
	int theCopiedSize = 256;

	api::variant<int> copied = copyInteger(theCopiedSize);
	verifyInteger(theCopiedSize, copied);
	std::cout << "Finished verifying integer." << std::endl;

	api::variant<TestValueType> copiedValue = copyValueType(theCopiedSize);
	verifyValueType(theCopiedSize, copiedValue);
	std::cout << "Finished verifying value type." << std::endl;
}
