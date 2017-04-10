/**
 * APIHOST.H - Api Host Definition
 *
 * Defines what an api host should be
 * capable of. Commonly an api host
 * should be capable of establishing
 * connection, perform a call, marshal
 * api objects, etc.
 */

#pragma once

#include <map>
#include <stdint.h>

#include "apiException.h"
#include "apiLocal.h"
#include "platform.h"

namespace api {

class ApiObject;

class ApiHost : public ApiLocal {
	std::map<int32_t, ApiObject*> objects;
	std::map<ApiObject*, int32_t> ids;
protected:
	Platform& platform;
public:	
	ApiHost(Platform&);

	virtual ~ApiHost();

	int32_t marshal(ApiObject*);

	void demarshal(ApiObject*);

	ApiObject* search(int32_t) throw (ApiException);

	int8_t* call(int32_t, int32_t, int8_t*) throw (ApiException);

	virtual void invoke(int32_t, InputStream&, OutputStream&) 
		throw (ApiException) {}
};

};
