#include "bufferStream.h"
#include <string.h>

using namespace api;

BufferInputStream::BufferInputStream(int _frameSize, int8_t* _buffer):
	frameSize(_frameSize), buffer(_buffer), pointer(0)	
	{	}

void BufferInputStream::read(int8_t* _buffer, int size) {
	if(size == 0) return;
	if(remaining() == 0) return;
	int shouldCopy = size;

	if(pointer + size > frameSize)
		shouldCopy = frameSize - pointer;

	memcpy(_buffer, buffer, shouldCopy);
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
