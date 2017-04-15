/**
 * PROTOCOL.H - Protocol Definition
 *
 * Protocol defines how we transfer and
 * receive message on a messaging channel
 * (represented by InputStream and 
 * OutputStream), where stream is an 
 * abstraction of data flow.
 */

#pragma once

#include "apiException.h"
#include "stream.h"
#include <exceptional>

namespace api {

template<typename T>
class Protocol {
public:
	virtual ~Protocol() {}

	// @@@ Memory Leak Warning @@@
	// This method would deliberately
	// new an instance, and should be
	// aware of potential memory leak.
	virtual exceptional< T* > receive(InputStream&) = 0;

	virtual exceptional<void*> transfer(T*, OutputStream&) = 0;
};

};
