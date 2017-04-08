#include "stream.h"

using namespace api;

int8_t InputStream::readByte() {
	int8_t result;
	read(&result, 1);
	return result;
}

#define READ_INTEGER(name, type)\
type InputStream::read##name() {\
	type result;\
	read(&result, sizeof(type));\
	api::Endian::integer().host((byte*)&result, sizeof(type));\
	return result;\
}

#define READ_FLOATING(name, type)\
type InputStream::read##name() {\
	type result;\
	read(&result, sizeof(type));\
	api::Endian::floating().host((byte*)&result, sizeof(type));\
	return result;\
}

READ_INTEGER(Short, int16_t)
READ_INTEGER(Int, int32_t)
READ_INTEGER(Long, int64_t)

READ_FLOATING(Float, float)
READ_FLOATING(Double, double)
