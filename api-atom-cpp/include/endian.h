/**
 * ENDIAN.H - Network Endian Converter
 * 
 * The class "Endian" should be capable of
 * converting code between host endian and
 * network endian.
 *
 * The host endian depends on machine's actual 
 * architecture while the network endian is
 * always in big-endian.
 */

#pragma once
#include <stdint.h>

namespace api {

class Endian {
public:
	virtual void network(int8_t*, int) = 0;

	virtual void host(int8_t*, int) = 0;

	static Endian& integer();

	static Endian& floating();
};

};
