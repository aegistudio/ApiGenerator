#include "apiHost.h"
#include "apiObject.h"
#include "apiTransaction.h"

#include "packetCall.h"
#include "packetReturn.h"
#include "packetException.h"

#include "bufferStream.h"

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

void ApiHost::detach() {
	connection -> detach();
}

// ------ Marshal & Demarshal Management -------------
#include <iostream>
int32_t ApiHost::marshal(ApiObject* apiObject) {
	if(ids.count(apiObject)) return ids[apiObject];
	
	int32_t pointerValue = reinterpret_cast<int32_t>(apiObject);
	while(objects.count(pointerValue) > 0)
		pointerValue ++;
	
	objects[pointerValue] = apiObject;
	ids[apiObject] = pointerValue;
	apiObject -> remember(this);
	return pointerValue;
}

void ApiHost::demarshal(ApiObject* apiObject) {
	if(ids.count(apiObject)) {
		int32_t pointerValue = ids[apiObject];
		objects.erase(pointerValue);
		ids.erase(apiObject);
		apiObject -> forget(this);
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
	callPacket -> parameter = callData.transfer();
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
	if(packet == NULL) return;
	switch(packet -> id()) {
		// packet instanceof api::PacketCall
		case PacketType::PacketCall:
			handleCall(packet);
		break;

		// packet instanceof api::PacketReturn
		case PacketType::PacketReturn:
			handleReturn(packet);
		break;

		// packet instanceof api::PacketException
		case PacketType::PacketException:
			handleException(packet);
		break;
		
		// Unknown instance, will do nothing.
		default:
		break;
	}
}

void ApiHost::handleCall(Packet* packet) {
	PacketCall* packetCall = reinterpret_cast<PacketCall*>(packet);
	try {
		ApiObject* target = search(packetCall -> callee);
		if(!(target -> callable()))
			throw ApiException("Target not callable!");
		else {
			ApiLocal* callable = reinterpret_cast<ApiLocal*>(target);

			BufferInputStream inputStream(
				packetCall -> size, 
				packetCall -> parameter);

			BufferOutputStream outputStream;

			callable -> invoke(packetCall -> call, 
				inputStream, outputStream);

			PacketReturn* callResult = new PacketReturn;
			callResult -> caller = packetCall -> caller;
			callResult -> size = outputStream.size();
			callResult -> result = outputStream.clone();
			connection -> send(callResult);
		}
	}
	catch(ApiException e) {
		PacketException* callError = new PacketException;
		callError -> caller = packetCall -> caller;
		callError -> exception = e;
		connection -> send(callError);
	}
}

void ApiHost::handleReturn(Packet* packet) {
	PacketReturn* packetReturn = reinterpret_cast<PacketReturn*>(packet);
	try {
		ApiObject* target = search(packetReturn -> caller);
		if(target -> callable()) 
			throw ApiException("Not a transaction.");
		else {
			ApiTransaction* transaction 
				= reinterpret_cast<ApiTransaction*>(target);
			transaction -> result(
				packetReturn -> size, packetReturn -> result);
		}
	}
	catch(ApiException e) {
		generalExcept(e);
	}
}

void ApiHost::handleException(Packet* packet) {
	PacketException* packetException = reinterpret_cast<PacketException*>(packet);
	if(packetException -> caller == 0)
		generalExcept(packetException -> exception);
	else {
		try {
			ApiObject* target = search(packetException -> caller);
			if(target -> callable()) 
				throw ApiException("Not a transaction.");
			else {
				ApiTransaction* transaction 
					= reinterpret_cast<ApiTransaction*>(target);
				transaction -> except(packetException -> exception);
			}
		}
		catch(ApiException e) {
			generalExcept(e);
		}
	}
}

void ApiHost::generalExcept(ApiException e) {

}
