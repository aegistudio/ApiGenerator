#pragma once

#include "thread.h"
#include "winapi.h"

namespace api {

class WinThread : public Thread {
	HANDLE threadHandle;
	bool detached;
public:
	WinThread(Runnable* _runnable, bool _ownRunnable);

	virtual ~WinThread();

	virtual void start();

	virtual void detach();

	virtual void kill();

	virtual void join();

	Runnable* getRunnable() const;

	bool isDetached() const;

	void finish();
};

};
