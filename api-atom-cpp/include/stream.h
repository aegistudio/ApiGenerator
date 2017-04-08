/**
 * STREAM.H - Api-Adapted Stream
 *
 * The class "InputStream" and "OutputStream" 
 * should be capable of reading or writing
 * something from or to a virtual device, 
 * either a piece of memory, or a file/pipe,
 * depending on the actual implementation.
 */

#pragma once

#include <stdint.h>
#include "endian.h"

namespace api {

class InputStream {
public:
	virtual ~InputStream() {}

	virtual void read(void*, int) = 0;

	int8_t	readByte();

	int16_t	readShort();

	int32_t	readInt();

	int64_t	readLong();

	float	readFloat();

	double	readDouble();
};

class OutputStream {
public:
	virtual ~OutputStream() {}

	virtual void write(const void*, int) = 0;

	void	writeByte(int8_t);

	void	writeShort(int16_t);

	void	writeInt(int32_t);

	void	writeLong(int64_t);

	void	writeFloat(float);

	void	writeDouble(double);
};

};
