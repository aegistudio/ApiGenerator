#pragma once

#include "thread.h"
#include "winapi.h"

namespace api {

class WinThread : public Thread {
	HANDLE threadHandle;
public:
	WinThread(Runnable* _runnable);

	virtual ~WinThread();

	virtual void start();

	virtual void kill();

	virtual void join();
};

};
