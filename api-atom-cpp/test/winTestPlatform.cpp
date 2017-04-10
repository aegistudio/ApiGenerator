#include "testCase.h"

#include "winapi.h"
#include "winPlatform.h"

api::Platform& getPlatform() {
	static api::WinPlatform winPlatform;
	return winPlatform;
}

void sleep(int64_t mills) {
	Sleep(mills);
}
