/**
 * BUFFERSTREAM.H - Api-Adapted Stream
 *
 * The class "BufferStream" is capable of
 * using a pieces of memory as input or
 * output stream. Similar to ByteBuffer
 * Streams in java.
 *
 * Please notice that BufferInputStream
 * DOES NOT manage the buffer memory, you
 * ought to manage the memory yourself.
 *
 * While the BufferOutputStream manages
 * and allocate every memories for you,
 * and if you want to retain the memory,
 * you should copy it out.
 */

#pragma once

#include "stream.h"
#include <vector>

namespace api {

class BufferInputStream : public InputStream {
	const int frameSize;
	int pointer;
	int8_t *buffer;
public:
	BufferInputStream(int _frameSize, int8_t* _buffer);

	virtual void read(int8_t*, int);

	int remaining() const;

	void rewind(OutputStream&);
};

#define STACK_SIZE 128
class BufferOutputStream : public OutputStream {
	const int frameSize;
	std::vector<int8_t*> buffers;
	int8_t* writing;
	int index; int pointer;
	int8_t absoluteFirst[STACK_SIZE];

	int inframeRemaining() const;
public:
	BufferOutputStream(int = 4096);

	virtual ~BufferOutputStream();

	virtual void write(int8_t*, int) = 0;

	int size() const;
	
	void copy(int8_t*) const;

	// @@@@@Memory Leak Warning@@@@@@
	// Should manage the memory allocated by this method.
	int8_t* clone() const;
};

};
