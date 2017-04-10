#pragma once

#include "platform.h"

namespace api {

class WinPlatform : public Platform {
public:
	virtual ~WinPlatform() {}

	virtual Thread* newThread(Runnable*);

	virtual Semaphore* newSemaphore();
};

};
