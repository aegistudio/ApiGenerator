/**
 * PLATFORM.H - Platform Factory
 *
 * Defines platform dependent abstract factory
 * for creating Thread and Semaphore.
 *
 * For example, one possible platform is WinPlatform
 * which is utilizing WinAPI, another platform is 
 * PosixPlatform which utilizing pthread and mutex
 * utilities.
 */

#pragma once

#include "thread.h"
#include "semaphore.h"

namespace api {

// @@@ Memory Leak Warning @@@
// Invocation to all new* methods has the
// same effect of invocation to new with
// constructor to every instances, and may
// cause memory leak if not handled properly.
class Platform {
public:
	virtual ~Platform() {}

	virtual Thread* newThread(Runnable*) = 0;

	virtual Semaphore* newSemaphore() = 0;	
};

};
