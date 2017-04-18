/**
 * FILESTREAM.H - Api-Adapted Stream
 *
 * The class "FileInputStream" and "FileOutputStream"
 * should be capable of reading or writing data(s)
 * from or to a C File, utilizing the I/O functions.
 */
#pragma once

#include "stream.h"
#include "stdio.h"

namespace api {

class FileInputStream : public InputStream {
	FILE* file;
public:
	FileInputStream(FILE*);

	virtual ~FileInputStream() {}

	virtual void read(void*, int);
};

class FileOutputStream : public OutputStream {
	FILE* file;
public:
	FileOutputStream(FILE*);

	virtual ~FileOutputStream() {}

	virtual void write(const void*, int);

	virtual void flush();
};

};
