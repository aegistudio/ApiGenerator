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
#include "connection.h"
#include <apiVariant>

namespace api {

class ApiObject;

class ApiHost : public ApiLocal, PacketHandler {
	std::map<int32_t, ApiObject*> objects;
	std::map<ApiObject*, int32_t> ids;
protected:
	Platform& platform;
	Connection* connection;

	virtual void handleCall(Packet*);

	virtual void handleReturn(Packet*);

	virtual void handleException(Packet*);

	virtual void generalExcept(ApiException);
public:	
	ApiHost(ConnectionFactory&, Platform&);

	virtual ~ApiHost();

	virtual void start();

	virtual void close();

	int32_t marshal(ApiObject*);

	void demarshal(ApiObject*);

	ApiObject* search(int32_t) throw (ApiException);

	variant<int8_t> call(int32_t, int32_t, variant<int8_t>&) 
		throw (ApiException);

	virtual void invoke(int32_t, InputStream&, OutputStream&) 
		throw (ApiException) {}
	
	virtual void handle(Packet* packet);
};

};
