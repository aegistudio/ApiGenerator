/**
 * WINSEMAPHORE.H - WinAPI Semaphore
 *
 * An implementation utilizing the WinAPI
 * semaphore.
 */

#pragma once

#include "semaphore.h"
#include "winapi.h"

namespace api {

class WinSemaphore : public Semaphore {
	HANDLE semaphore;
public:
	WinSemaphore();

	virtual ~WinSemaphore();

	virtual void proberen();

	virtual void verhogen();
};

};
