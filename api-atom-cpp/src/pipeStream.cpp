#include "pipeStream.h"

using namespace api;

Pipe::Pipe(Platform& platform): pipe(platform.newSemaphore()), 
	input(pipe), output(pipe) {}

PipeInputStream::PipeInputStream(MonitorQueue<int8_t>& _pipe):
	pipe(_pipe) {}

PipeOutputStream::PipeOutputStream(MonitorQueue<int8_t>& _pipe):
	pipe(_pipe) {}

void PipeInputStream::read(void* buffer, int size) {
	if(size == 0) return;
	int8_t* castBuffer = (int8_t*)buffer;
	int8_t data = pipe.remove();
	*castBuffer = data;
	read(castBuffer + 1, size - 1);
}

void PipeOutputStream::write(const void* buffer, int size) {
	if(size == 0) return;
	const int8_t* castBuffer = (const int8_t*) buffer;
	pipe.add(*castBuffer);
	write(castBuffer + 1, size - 1);
}

InputStream& Pipe::inputStream() {
	return input;
}

OutputStream& Pipe::outputStream() {
	return output;
}
