#include "stream.h"

using namespace api;

void OutputStream::writeByte(int8_t param) {
	write(&param, 1);
}

#define INT_METHOD(name, type)\
void OutputStream::write##name(type param) {\
	api::Endian::integer().network((byte*)&param, sizeof(type));\
	write((int8_t*)&param, sizeof(type));\
}

INT_METHOD(Short, int16_t)
INT_METHOD(Int, int32_t)
INT_METHOD(Long, int64_t)

#define FLT_METHOD(name, type)\
void OutputStream::write##name(type param) {\
	api::Endian::floating().network((byte*)&param, sizeof(type));\
	write((int8_t*)&param, sizeof(type));\
}

FLT_METHOD(Float, float)
FLT_METHOD(Double, double)
