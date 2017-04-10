#include "testCase.h"

#include "winSemaphore.h"
#include "winThread.h"
#include <monitorQueue>
#include <iostream>

const int maxValue = 20;

void test() throw (int) {
	api::MonitorQueue<int> monitorQueue(new api::WinSemaphore);

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
				Sleep(10L);
			}
		}

	} producer(monitorQueue);
	api::WinThread* producerThread = new api::WinThread(&producer);

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
	api::WinThread consumerThread(&consumer);

	consumerThread.start();
	Sleep(1000L);
	producerThread -> detach();

	consumerThread.join();
}
