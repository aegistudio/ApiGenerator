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
	virtual void run() = 0;
};

class Thread {
protected:
	Runnable* runnable;
public:
	Thread(Runnable* _runnable):
		runnable(_runnable) {}

	virtual ~Thread() {}

	virtual void start() = 0;

	virtual void detach() = 0;

	virtual void join() = 0;

	virtual void kill() = 0;
};

};
