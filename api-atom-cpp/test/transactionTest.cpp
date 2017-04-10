#include "testCase.h"

#include "apiTransaction.h"
#include "thread.h"
#include "stream.h"
#include "bufferStream.h"

#include "stringio.h"
#include <string>
#include <iostream>

const std::string valueFeedString = "Value Feed Test";
const std::string exceptString = "Exception Feed Test";

void test() throw (int) {
	api::Platform& platform = getPlatform();

	// Test when a value output.
	api::ApiTransaction valueTransaction(platform.newSemaphore());

	class ValueFeedRunnable : public api::Runnable {
		api::ApiTransaction& transaction;
	public:
		ValueFeedRunnable(api::ApiTransaction& transRef):
			transaction(transRef) {}

		virtual void run() {
			sleep(500L);
			api::BufferOutputStream resultBuilder;
			api::String::write(valueFeedString, resultBuilder);
			transaction.result(resultBuilder.size(), 
				resultBuilder.clone());
		}
	} valueFeed(valueTransaction);

	platform.newThread(&valueFeed) -> detach();

	valueTransaction.call();
	int32_t size = valueTransaction.resultSize();
	int8_t* data = valueTransaction.resultData();
	api::BufferInputStream resultReader(size, data);
	std::string resultString = api::String::read(resultReader);
	std::cout << "[INFO] Get result string: " << resultString << std::endl;
	assertEquals(valueFeedString, resultString);
	delete[] data;

	std::cout << "[INFO] Value output works." << std::endl;

	// Test when an exception occurs.
	api::ApiTransaction exceptTransaction(platform.newSemaphore());

	class ExceptFeedRunnable : public api::Runnable {
		api::ApiTransaction& transaction;
	public:
		ExceptFeedRunnable(api::ApiTransaction& transRef):
			transaction(transRef) {}

		virtual void run() {
			sleep(500L);
			api::ApiException testException(exceptString);
			transaction.except(testException);
		}
	} exceptFeed(exceptTransaction);

	platform.newThread(&exceptFeed) -> detach();

	try {
		exceptTransaction.call();
		assert(false, "No exception comes.", 1);
	}
	catch(api::ApiException e) {
		std::cout << "[INFO] Get exception message: "
			<< e.message() << std::endl;
		assertEquals(exceptString, e.message());
	}

	std::cout << "[INFO] Exception output works." << std::endl;
}
