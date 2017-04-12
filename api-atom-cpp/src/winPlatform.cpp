#include "winPlatform.h"

#include "winThread.h"
#include "winSemaphore.h"

using namespace api;

Thread* WinPlatform::newThread(Runnable* runnable, bool ownRunnable) {
	return new WinThread(runnable, ownRunnable);
}

Semaphore* WinPlatform::newSemaphore() {
	return new WinSemaphore();
}
