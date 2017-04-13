#include "winThread.h"

using namespace api;

WinThread::WinThread(Runnable* _runnable, bool _ownRunnable):
	Thread(_runnable, _ownRunnable), 
	threadHandle(NULL), detached(false) {}

Runnable* WinThread::getRunnable() const {
	return runnable;
}

bool WinThread::isDetached() const {
	return detached;
}

DWORD WINAPI WinThreadFunction(LPVOID paramWinThread) {
	WinThread* winThread = (WinThread*)paramWinThread;
	Runnable* runnable = winThread -> getRunnable();
	runnable -> run();
	if(winThread -> isDetached()) delete winThread;
	winThread -> finish();
	return 0;
}

WinThread::~WinThread() {
	//kill();
	finish();
}

void WinThread::start() {
	if(threadHandle != NULL) return;	// Already running.
	
	threadHandle = CreateThread(
		NULL, 0, WinThreadFunction, 
		reinterpret_cast<LPVOID>(this), 
		0, NULL);
}

void WinThread::detach() {
	detached = true;
	start();
}

void WinThread::join() {
	if(threadHandle == NULL) return;
	WaitForSingleObject(threadHandle, INFINITE);
	finish();
}

void WinThread::kill() {
	if(threadHandle == NULL) return;
	TerminateThread(threadHandle, 0);
	finish();

	// The guard condition threadHandle
	// guarentees that this code will not
	// be executed twice.
	if(isDetached()) delete this;
}

void WinThread::finish() {
	CloseHandle(threadHandle);
	threadHandle = NULL;
}
