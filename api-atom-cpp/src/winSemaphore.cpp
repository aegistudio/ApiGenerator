#include "winSemaphore.h"

#define MAX_SEM_COUNT 0x7fffffff

using namespace api;

WinSemaphore::WinSemaphore() {
	semaphore = CreateSemaphore(NULL, 0, MAX_SEM_COUNT, NULL);
}

WinSemaphore::~WinSemaphore() {
	CloseHandle(semaphore);
}

void WinSemaphore::proberen() {
	WaitForSingleObject(semaphore, INFINITE);
}

void WinSemaphore::verhogen() {
	ReleaseSemaphore(semaphore, 1, NULL);
}
