#include "testCase.h"
#include "pipeStream.h"

void test() throw (int) {
	api::Pipe pipe(getPlatform());
	pipe.outputStream().writeFloat(3.14159f);
	pipe.outputStream().writeInt(246810);
	pipe.outputStream().writeByte(16);

	assertEquals(3.14159f, pipe.inputStream().readFloat());
	assertEquals(246810, pipe.inputStream().readInt());
	assertEquals((int8_t)16, pipe.inputStream().readByte());
}
