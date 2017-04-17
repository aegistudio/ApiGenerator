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

#include <sstream>

namespace api {

class ApiLocal : public ApiObject {
public:
	virtual _EX(void*) invoke(int32_t callId, ApiHost& host,
		InputStream& request, OutputStream& response) {

		std::stringstream message;
		message << "Call #" << callId << " is not declared!";
		throwException(message.str());
	}

	virtual bool callable() { return true; }
};

};
