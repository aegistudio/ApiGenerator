/**
 * THREAD.H - Api-Adapted Thread
 *
 * This class is a abstraction for multithreading
 * under different platform. Please pickup the 
 * platform dependent implementation while using
 * this class.
 */

#pragma once

namespace api {

class Runnable {
public:
	virtual ~Runnable() {}

	virtual void run() = 0;
};

class Thread {
protected:
	bool ownRunnable;
	Runnable* runnable;
public:
	Thread(Runnable* _runnable, bool _ownRunnable):
		runnable(_runnable), ownRunnable(_ownRunnable) {}

	virtual ~Thread() {
		if(ownRunnable && runnable)
			delete runnable;
	}

	virtual void start() = 0;

	virtual void detach() = 0;

	virtual void join() = 0;

	virtual void kill() = 0;
};

};
