#include "testCase.h"
#include "pipeStream.h"

void test() throw (int) {
	api::Pipe pipe(getPlatform());
	int i;
	
	class WritingThread : public api::Runnable {
		api::Pipe& pipe;	
	public:
		WritingThread(api::Pipe& _pipe):
			pipe(_pipe) {}

		virtual void run() {
			sleep(500L);
			int j;	for(j = 0; j < 200; j ++) {
				pipe.outputStream().writeFloat(3.14159f);
				pipe.outputStream().writeInt(246810);
				pipe.outputStream().writeByte(16);
			}
		}
	} writing(pipe);
	getPlatform().newThread(&writing) -> detach();

	for(i = 0; i < 200; i ++) {
		assertEquals(3.14159f, pipe.inputStream().readFloat());
		assertEquals(246810, pipe.inputStream().readInt());
		assertEquals((int8_t)16, pipe.inputStream().readByte());
	}
}
