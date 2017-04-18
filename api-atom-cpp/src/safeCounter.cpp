#include "safeCounter.h"

using namespace api;

SafeCounter::SafeCounter(Platform& platform):
	activities(0),
	mutex(platform.newSemaphore()),
	safe(platform.newSemaphore()) {

	mutex -> verhogen();
	safe -> verhogen();
}

SafeCounter::~SafeCounter() {
	delete mutex;
	delete safe;
}

void SafeCounter::begin() {
	mutex -> proberen();
	if(activities == 0)
		safe -> proberen();
	activities ++;
	mutex -> verhogen();
}

void SafeCounter::end() {
	mutex -> proberen();
	activities --;
	if(activities == 0)
		safe -> verhogen();
	mutex -> verhogen();
}

void SafeCounter::wait() {
	safe -> proberen();
}
