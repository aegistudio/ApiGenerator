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
#include <memory>

namespace api {

class SafeCounter {
	int activities;
	std::unique_ptr<Semaphore> mutex;
	std::unique_ptr<Semaphore> safe;
public:
	SafeCounter(Platform& platform);

	void begin();

	void end();

	void wait();
};

};
