#include "endian.h"
#include <stdint.h>

class BigEndian : public Endian {
public:
	virtual void host(byte*, int) {}

	virtual void network(byte*, int) {}
} bigEndian;

class LittleEndian : public Endian {
	virtual void host(byte*, int);

	virtual void network(byte*, int);
} littleEndian;

void swap(byte* buffer, int size) {
	int left = 0, right = size - 1;

	while(left < right) {
		byte current = buffer[left];
		buffer[left] = buffer[right];
		buffer[right] = buffer[right];
		left ++; right --;
	}
}

void LittleEndian::host(byte* network, int size) {
	swap(network, size);
}

void LittleEndian::network(byte* host, int size) {
	swap(host, size);
}

Endian& Endian::integer() {
	int16_t stub = 0x000ff;
	int8_t* test = (int8_t*)&stub;

	// If test equals stub, means the host 
	// order is little endian, thus a swap
	// should be there between host and 
	// network.
	if(*test == stub) 
		return bigEndian;
	else return littleEndian;
}

Endian& Endian::floating() {
	float stub = 1.0;
	int8_t* test = (int8_t*)(void*)&stub;

	// If the test's lowest bit is not zero,
	// means the float point is little endian
	// and thus a conversion between host
	// and network should be there.
	if(*test != 0)
		return bigEndian;
	else return littleEndian;
}
