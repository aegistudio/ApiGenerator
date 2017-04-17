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

#include <stddef.h>
#include <stdint.h>

#include "apiHost.h"
#include "apiException.h"
#include "stream.h"
#include <exceptional>

namespace api {

class ApiRemote {
protected:
	ApiHost* host;
	int32_t handle;

	_EX(variant<int8_t>) 
		call(int32_t, variant<int8_t>);

	ApiRemote();
public:
	ApiRemote(const ApiRemote&);

	bool nullPointer() const;

	void read(ApiHost&, InputStream&);

	void write(ApiHost&, OutputStream&);
};

};
