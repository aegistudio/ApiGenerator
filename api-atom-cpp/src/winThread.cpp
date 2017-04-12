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
	return 0;
}

WinThread::~WinThread() {
	kill();
}

void WinThread::start() {
	if(threadHandle) return;	// Already running.
	
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
	if(!threadHandle) return;
	WaitForSingleObject(threadHandle, INFINITE);
	CloseHandle(threadHandle);
	threadHandle = NULL;
}

void WinThread::kill() {
	if(!threadHandle) return;
	TerminateThread(threadHandle, 0);
	CloseHandle(threadHandle);
	threadHandle = NULL;

	// The guard condition threadHandle
	// guarentees that this code will not
	// be executed twice.
	if(isDetached()) delete this;
}
