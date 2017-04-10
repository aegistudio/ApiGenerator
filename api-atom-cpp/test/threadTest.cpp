#include "testCase.h"
#include <iostream>

void testJoin() throw (int);

void test() throw (int) {
	testJoin();
};

void testJoin() throw (int) {
	const int theCounter = 10000000;
	class JoinRunnable : public api::Runnable {
	private:
		const int maxCounter;
	public:
		int loopCounter;

		JoinRunnable(int _maxCounter):
			maxCounter(_maxCounter),
			loopCounter(0) {}


		virtual void run() {
			while(loopCounter < maxCounter)
				loopCounter ++;
		}

	} loopRunnable(theCounter);

	assertEquals(0, loopRunnable.loopCounter);

	api::Thread* theThread = getPlatform()
		.newThread(&loopRunnable);
	theThread -> start();
	theThread -> join();
	delete theThread;

	assertEquals(theCounter, loopRunnable.loopCounter);

	std::cout << "Assigned counter: " << theCounter << std::endl;
	std::cout << "Thread counter: " << loopRunnable.loopCounter << std::endl;
}
