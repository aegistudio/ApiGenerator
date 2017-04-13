/**
 * APIOBJECT.H - Api Object Definition
 *
 * Defines what an ApiObject should be,
 * any ApiObject should be capable of
 * being marshalled into the ApiHost.
 *
 * Commonly there're three kinds of ApiObjects:
 * - ApiLocal: the most common object when
 * this side act as the skeleton side and is 
 * implementing interfaces, or the stub side
 * and implementing callbacks.
 *
 * - ApiTransaction: another common object when
 * an invocation is created and then a call is
 * sent to the server side.
 *
 * - ApiHost: the api host would be marshaled
 * into 0, and an ApiHost is alive among the
 * whole lifecycle of an ApiConnection.
 */

#pragma once

#include <set>

namespace api {

class ApiHost;

class ApiObject {
	bool valid;

	std::set<ApiHost*> hosts;
public:	
	ApiObject();

	virtual ~ApiObject();

	void remember(ApiHost*);

	void forget(ApiHost*);

	virtual bool callable() { return false; }
};

};
