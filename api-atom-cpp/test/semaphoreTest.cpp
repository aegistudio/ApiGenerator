#include "testCase.h"

#include <monitorQueue>
#include <iostream>

const int maxValue = 20;

void test() throw (int) {
	api::Platform& platform = getPlatform();
	api::MonitorQueue<int> monitorQueue(platform.newSemaphore());

	class ProducerThread : public api::Runnable {
		int counter;
		api::MonitorQueue<int>& queue;
	public:
		ProducerThread(api::MonitorQueue<int>& _queue):
			queue(_queue), counter(0) {}

		virtual void run() {
			for(; counter <= maxValue; counter ++) {
				std::cout << "Provider sent: "
					<< counter << std::endl;
				queue.add(counter);
				sleep(10L);
			}
		}

	} producer(monitorQueue);
	api::Thread* producerThread = platform.newThread(&producer);	
	
	class ConsumerThread : public api::Runnable {
		api::MonitorQueue<int>& queue;
	public:
		ConsumerThread(api::MonitorQueue<int>& _queue):
			queue(_queue) {}

		virtual void run() {
			int value;
			do {
				value = queue.remove();
				std::cout << "Consumer received: " 
					<< value << std::endl;
			}
			while(value < maxValue);
		}
	} consumer(monitorQueue);
	api::Thread* consumerThread = platform.newThread(&consumer);

	consumerThread -> start();
	sleep(500L);
	producerThread -> detach();

	consumerThread -> join();
	delete consumerThread;
}
