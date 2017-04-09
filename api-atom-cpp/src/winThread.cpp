#include "winThread.h"

using namespace api;

WinThread::WinThread(Runnable* _runnable):
	Thread(_runnable), threadHandle(NULL) {}

DWORD WINAPI WinThreadFunction(LPVOID paramRunnable) {
	Runnable* runnable = (Runnable*)paramRunnable;
	runnable -> run();
	return 0;
}

WinThread::~WinThread() {
	kill();
}

void WinThread::start() {
	if(threadHandle) return;	// Already running.
	
	threadHandle = CreateThread(
		NULL, 0, WinThreadFunction, 
		reinterpret_cast<LPVOID>(runnable), 
		0, NULL);
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
}
