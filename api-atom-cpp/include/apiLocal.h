/**
 * APILOCAL.H - Api Local Object
 *
 * Local objects are those who who are
 * able to receive invocations and generate
 * concrete results.
 */

#pragma once

#include <stdint.h>
#include <exceptional>

#include "stream.h"
#include "apiObject.h"
#include "apiException.h"

namespace api {

class ApiLocal : public ApiObject {
public:
	virtual _EX(void*) invoke(int32_t callId, 
		InputStream& request, OutputStream& response) {

		throwException("No call declared!");
	}

	virtual bool callable() { return true; }
};

};
