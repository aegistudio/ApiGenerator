#include "bufferStream.h"
#include <string.h>
#include <stddef.h>

using namespace api;

BufferOutputStream::BufferOutputStream(int _frameSize): 
	frameSize(_frameSize), writing(absoluteFirst), 
	index(0), pointer(0) {}

int BufferOutputStream::size() const {
	if(index == 0) return pointer;
	else return STACK_SIZE 
		+ (index - 1) * frameSize + pointer;
}

BufferOutputStream::~BufferOutputStream() {
	int i; for(i = 0; i < index; i ++)
		if(buffers[i]) delete buffers[i];
}

int BufferOutputStream::inframeRemaining() const {
	if(index == 0) return STACK_SIZE - pointer;
	return frameSize - pointer;
}

void BufferOutputStream::write(int8_t* input, int size) {
	if(size == 0) return;
	int remaining = inframeRemaining();
	if(size < remaining) {
		memcpy(writing + pointer, input, size);
		pointer = pointer + size;
	}
	else {
		memcpy(writing + pointer, input, remaining);
		pointer = 0;
		writing = new int8_t[frameSize];
		buffers[index] = writing;
		index ++;
		write(input + remaining, size - remaining);
	}
}

void BufferOutputStream::copy(int8_t* copying) const {
	if(index == 0) 
		memcpy(copying, absoluteFirst, pointer);
	else {
		memcpy(copying, absoluteFirst, STACK_SIZE);
		copying += STACK_SIZE;

		for(int i = 0; i < index - 1; i ++) {
			memcpy(copying, buffers[i], frameSize);
			copying += frameSize;
		}
		memcpy(copying, writing, pointer);
	}
}

int8_t* BufferOutputStream::clone() const {
	int8_t* output = new int8_t[size()];
	if(output) copy(output);
	return output;
}
