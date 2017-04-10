#include "testCase.h"

#include "winPlatform.h"

api::Platform& getPlatform() {
	static api::WinPlatform winPlatform;
	return winPlatform;
}
