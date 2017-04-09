/**
 * SEMAPHORE.H - Semaphore
 * 
 * A semaphore is a guard object with a counter
 * and a queue of processes or threads. 
 *
 * These processes or threads are waiting for 
 * the resource whose count is recorded in
 * the counter. When the count of resource
 * is less than zero, it would block the thread,
 * until some resource is available.
 */

#pragma once

namespace api {

class Semaphore {
public:
	virtual ~Semaphore() {}

	virtual void proberen() = 0;

	virtual void verhogen() = 0;
};

};
