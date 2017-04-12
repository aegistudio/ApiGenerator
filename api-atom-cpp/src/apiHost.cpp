#include "apiHost.h"
#include "apiObject.h"
#include "apiTransaction.h"
#include "packetCall.h"

#include <sstream>

using namespace api;

ApiHost::ApiHost(ConnectionFactory& _factory, Platform& _platform):
	platform(_platform) {

	connection = _factory.newConnection((*this));
}

ApiHost::~ApiHost() {
	std::map<int32_t, ApiObject*>::iterator iter;
	for(iter = objects.begin(); iter != objects.end(); iter ++) 
		(*iter).second -> forget(this);
	
	if(connection) delete connection;
}

void ApiHost::start() {
	connection -> start();
}

void ApiHost::close() {
	connection -> close();
}

// ------ Marshal & Demarshal Management -------------
int32_t ApiHost::marshal(ApiObject* apiObject) {
	if(ids.count(apiObject)) return ids[apiObject];
	
	int32_t pointerValue = reinterpret_cast<int32_t>(apiObject);
	while(objects.count(pointerValue) > 0)
		pointerValue ++;
	
	objects[pointerValue] = apiObject;
	apiObject -> remember(this);
	return pointerValue;
}

void ApiHost::demarshal(ApiObject* apiObject) {
	if(ids.count(apiObject)) {
		int32_t pointerValue = ids[apiObject];
		objects.erase(pointerValue);
		ids.erase(apiObject);
	}
}

ApiObject* ApiHost::search(int32_t value) throw (ApiException) {
	if(value == 0) return this;
	if(objects.count(value)) return objects[value];
	else {
		std::stringstream messageBuilder;
		messageBuilder << "Api Object #" << value + " does not exist.";
		throw ApiException(messageBuilder.str());
	}
}

// ------ Call / Return Management --------------------
variant<int8_t> ApiHost::call(int32_t calleeId, int32_t callId, 
	variant<int8_t>& callData) throw (ApiException) {

	ApiTransaction callTransaction(platform.newSemaphore());

	// Construct call packet and send.
	PacketCall* callPacket = new PacketCall();
	callPacket -> caller = marshal(&callTransaction);
	callPacket -> callee = calleeId;
	callPacket -> call = callId;
	callPacket -> size = callData.length;
	callPacket -> parameter = *callData;
	connection -> send(callPacket);

	// Wait for result, if an exception is generated,
	// the call stack will be retraced.
	callTransaction.call();

	// Notice: data owns the result data now.
	variant<int8_t> data(
		callTransaction.resultSize(), 
		callTransaction.resultData());
	return data;
}

void ApiHost::handle(Packet* packet) {

}
