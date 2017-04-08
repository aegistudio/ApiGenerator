#include "testCase.h"
#include "fileStream.h"

const char* name = "FileTest.file.test";
const char* errorMsg = "Cannot open file";

void test() throw (int) {
	int i;	const int count = 1024;

	FILE* writeBuffer = fopen(name, "w");
	assert(writeBuffer != NULL, errorMsg, 1);

	api::FileOutputStream outputStream(writeBuffer);

	for(i = 0; i < count; i ++) {
		outputStream.writeInt(i);
		outputStream.writeDouble(i);
	}
	fclose(writeBuffer);

	FILE* readBuffer = fopen(name, "r");
	assert(readBuffer != NULL, errorMsg, 1);

	api::FileInputStream inputStream(readBuffer);

	for(i = 0; i < count; i ++) {
		assertEquals(i, inputStream.readInt());
		assertEquals((double)i, inputStream.readDouble());
	}
	fclose(readBuffer);
}
