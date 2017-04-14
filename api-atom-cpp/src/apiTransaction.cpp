#include "apiTransaction.h"

#include <stddef.h>
#include <string.h>

using namespace api;

ApiTransaction::ApiTransaction(Semaphore* _semaphore):
	semaphore(_semaphore), response(NULL), abnormal(false) {}

ApiTransaction::~ApiTransaction() {
	delete semaphore;
}

void ApiTransaction::result(int32_t blockSize, int8_t* block) {
	size = blockSize;
	if(blockSize > 0) {
		response = new int8_t[blockSize];
		memcpy(response, block, blockSize);
	}
	semaphore -> verhogen();
}

void ApiTransaction::except(ApiException apiExcept) {
	exception = apiExcept;
	abnormal = true;
	semaphore -> verhogen();
}

void ApiTransaction::call() throw (ApiException) {
	semaphore -> proberen();
	if(abnormal) throw exception;
}

int32_t ApiTransaction::resultSize() {
	return size;
}

int8_t* ApiTransaction::resultData() {
	return response;
}
