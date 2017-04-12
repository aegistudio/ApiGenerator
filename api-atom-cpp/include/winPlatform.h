#pragma once

#include "platform.h"

namespace api {

class WinPlatform : public Platform {
public:
	virtual ~WinPlatform() {}

	virtual Thread* newThread(Runnable*, bool);

	virtual Semaphore* newSemaphore();
};

};
