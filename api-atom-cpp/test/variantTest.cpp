#include "testCase.h"
#include <apiVariant>
#include <iostream>
#include <exceptional>

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
	api::variant<float> listValue;
	api::variant<std::string> listStringValue;

	TestValueType(): listValue(0), listStringValue(0) {}
};

api::variant<TestValueType> copyValueType(int size) {
	api::variant<TestValueType> testObject(size);
	int i; for(i = 0; i < size; i ++) {
		testObject[i].integerValue = i;
		testObject[i].doubleValue = i;
		testObject[i].listValue = api::variant<float>(1);
		testObject[i].listValue[0] = i;

		testObject[i].listStringValue = api::variant<std::string>(1);
		testObject[i].listStringValue[0] = "";
		testObject[i].listStringValue[0] += i;
	}
	return testObject;
}

void verifyValueType(int expectedSize, api::variant<TestValueType>& buffer) {
	assertEquals(expectedSize, buffer.length());
	int i; for(i = 0; i < expectedSize; i ++) {
		assertEquals(i, buffer[i].integerValue);
		assertEquals((double)i, buffer[i].doubleValue);
		assertEquals(1, buffer[i].listValue.length());
		assertEquals((float)i, buffer[i].listValue[0]);

		std::string strVerify("");
		strVerify += i;
		assertEquals(strVerify, buffer[i].listStringValue[0]);
	}
}

_EX(api::variant<int>) copyExInteger(int size) {
	return copyInteger(size);
}

_EX(api::variant<TestValueType>) copyExValueType(int size) {
	return copyValueType(size);
}

void test() throw (int) {
	int theCopiedSize = 256;

	api::variant<int> copied = copyInteger(theCopiedSize);
	verifyInteger(theCopiedSize, copied);
	std::cout << "Finished verifying integer." << std::endl;

	_EX(api::variant<int>) exCopied = copyExInteger(theCopiedSize);
	api::variant<int> aExCopiedValue = exCopied.value;
	verifyInteger(theCopiedSize, aExCopiedValue);
	std::cout << "Finished verifying integer (with exceptional)." << std::endl;

	api::variant<TestValueType> copiedValue = copyValueType(theCopiedSize);
	verifyValueType(theCopiedSize, copiedValue);
	std::cout << "Finished verifying value type." << std::endl;

	_EX(api::variant<TestValueType>) exCopiedValue = copyValueType(theCopiedSize);
	api::variant<TestValueType> aExCopiedValueValue = exCopiedValue.value;
	verifyValueType(theCopiedSize, aExCopiedValueValue);
	std::cout << "Finished verifying value type (with exceptional)." << std::endl;
}
