/**
 * SAFECOUNTER.H - Transaction Safe Counter
 *
 * This is a counter to ensure all transactions
 * should be done when you are to end the handler.
 *
 * When a transaction comes, it will increase the
 * counter. And reduce the counter whe the task
 * ends.
 */

#pragma once
#include "platform.h"
#include "semaphore.h"

namespace api {

class SafeCounter {
	int activities;
	Semaphore *mutex, *safe;
public:
	SafeCounter(Platform& platform);

	~SafeCounter();

	void begin();

	void end();

	void wait();
};

};
