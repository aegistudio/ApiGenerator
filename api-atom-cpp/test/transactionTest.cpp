#include "testCase.h"

#include "apiTransaction.h"
#include "thread.h"
#include "stream.h"
#include "bufferStream.h"

#include "stringio.h"
#include <string>

const std::string valueFeedString = "Value Feed Test";

void test() throw (int) {
	api::Platform& platform = getPlatform();
	api::ApiTransaction valueTransaction(platform.newSemaphore());

	class ValueFeedRunnable : public api::Runnable {
		api::ApiTransaction& transaction;
	public:
		ValueFeedRunnable(api::ApiTransaction& transRef):
			transaction(transRef) {}

		virtual void run() {
			api::BufferOutputStream resultBuilder;
			api::String::write(valueFeedString, resultBuilder);
			transaction.result(resultBuilder.size(), 
				resultBuilder.clone());
		}
	} valueFeed(valueTransaction);

	platform.newThread(&valueFeed) -> detach();

	// Analog for calling inside the class body.
	valueTransaction.call();
	int32_t size = valueTransaction.resultSize();
	int8_t* data = valueTransaction.resultData();
	api::BufferInputStream resultReader(size, data);
	assertEquals(valueFeedString, api::String::read(resultReader));
	delete[] data;
}
