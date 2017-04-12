/**
 * PIPESTREAM.H - Piped Stream
 *
 * Warning: this class is just intended
 * for test purpose, and it might be
 * extremely slow!
 */

#pragma once

#include "stream.h"
#include "platform.h"

#include <monitorQueue>
#include <stdint.h>

namespace api {

class PipeInputStream : public InputStream {
	MonitorQueue<int8_t>& pipe;
public:
	PipeInputStream(MonitorQueue<int8_t>&);

	virtual void read(void*, int);
};

class PipeOutputStream : public OutputStream {
	MonitorQueue<int8_t>& pipe;
public:
	PipeOutputStream(MonitorQueue<int8_t>&);

	virtual void write(const void*, int);
};

class Pipe {
	MonitorQueue<int8_t> pipe;
	PipeInputStream input;
	PipeOutputStream output;
public:
	Pipe(Platform& platform);

	virtual ~Pipe() {}

	virtual InputStream& inputStream();

	virtual OutputStream& outputStream();
};

};
