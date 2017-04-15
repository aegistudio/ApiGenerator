/**
 * APIREMOTE.H - Api Remote Object
 *
 * Remote objects are mirror of api host
 * or local objects in opposite side.
 * 
 * You could refer to them via the host
 * and the actual handle.
 */

#pragma once

#include <stdint.h>

#include "apiHost.h"
#include "apiException.h"
#include <exceptional>

namespace api {

class ApiRemote {
	ApiHost* host;
	const int32_t handle;
protected:
	exceptional< variant<int8_t> >
		call(int32_t, variant<int8_t>);
public:
	ApiRemote(ApiHost*, int32_t);

	ApiRemote(const ApiRemote&);

	bool nullPointer() const;
};

};
