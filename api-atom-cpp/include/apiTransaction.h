/**
 * APITRANSACTION.H - Api Transaction
 *
 * Api Transaction repreesnts a receiver
 * from a single call of this side.
 *
 * It would block the current thread until
 * some result or exception has come from
 * another side.
 */

#pragma once

#include "apiObject.h"
#include "apiException.h"
#include "semaphore.h"
#include <exceptional>
#include <stdint.h>

namespace api {

class ApiTransaction : public ApiObject {
	ApiException exception;
	Semaphore* semaphore;
	int32_t size; int8_t* response;
	bool abnormal;
public:
	ApiTransaction(Semaphore*);

	virtual ~ApiTransaction();

	virtual _EX(void*) call();

	int32_t resultSize();

	// @@@ Memory Leak Warning @@@
	// This method will transfer the ownership
	// of the call data out of this object, so
	// the caller of this method takes the
	// responsibility of managing memory.
	int8_t* resultData();

	virtual void result(int32_t, int8_t*);

	virtual void except(ApiException);
};

};
