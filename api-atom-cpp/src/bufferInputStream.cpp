#include "bufferStream.h"
#include <string.h>

using namespace api;

BufferInputStream::BufferInputStream(int _frameSize, int8_t* _buffer):
	frameSize(_frameSize), pointer(0), buffer(_buffer) 
	{	}

void BufferInputStream::read(void* _buffer, int size) {
	if(size == 0) return;
	if(remaining() == 0) return;
	int shouldCopy = size;

	if(pointer + size > frameSize)
		shouldCopy = frameSize - pointer;

	memcpy(_buffer, buffer + pointer, shouldCopy);
	pointer = pointer + shouldCopy;
}

int BufferInputStream::remaining() const {
	return frameSize - pointer;
}

void BufferInputStream::rewind(OutputStream& outputStream) {
	int remainingValue = remaining();
	if(remainingValue == 0) return;
	outputStream.write(buffer + pointer, remainingValue);
	pointer += remainingValue;
}
