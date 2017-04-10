#include "winPlatform.h"

#include "winThread.h"
#include "winSemaphore.h"

using namespace api;

Thread* WinPlatform::newThread(Runnable* runnable) {
	return new WinThread(runnable);
}

Semaphore* WinPlatform::newSemaphore() {
	return new WinSemaphore();
}
