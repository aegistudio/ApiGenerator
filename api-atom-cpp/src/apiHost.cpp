#include "apiHost.h"
#include "apiObject.h"
#include "apiTransaction.h"

#include "packetCall.h"
#include "packetReturn.h"
#include "packetException.h"

#include "bufferStream.h"

#include <sstream>

#define TRY_SERVEREX(T, id, expression)\
	tryCatchException(T, id, expression, {\
		serverExcept(id.exception);\
		return;\
	})

using namespace api;

ApiHost::ApiHost(ConnectionFactory& _factory, Platform& _platform):
	platform(_platform) {

	connection = _factory.newConnection((*this));
}

ApiHost::~ApiHost() {
	if(connection) delete connection;

	std::map<int32_t, ApiObject*>::iterator iter;
	for(iter = objects.begin(); iter != objects.end(); iter ++) 
		(*iter).second -> forget(this);
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

_EX(ApiObject*) ApiHost::search(int32_t value) {
	if(value == 0) return this;
	if(objects.count(value)) return objects[value];
	else {
		std::stringstream messageBuilder;
		messageBuilder << "Api Object #" << value + " does not exist.";
		throwException(messageBuilder.str());
	}
}

// ------ Call / Return Management --------------------
_EX(variant<int8_t>) ApiHost::call(
	int32_t calleeId, int32_t callId, 
	variant<int8_t>& callData) {

	ApiTransaction callTransaction(platform.newSemaphore());

	// Construct call packet and send.
	PacketCall* callPacket = new PacketCall();
	callPacket -> caller = marshal(&callTransaction);
	callPacket -> callee = calleeId;
	callPacket -> call = callId;
	callPacket -> size = callData.length();
	callPacket -> parameter = callData.transfer();
	connection -> send(callPacket);

	// Wait for result, if an exception is generated,
	// the call stack will be retraced.
	tryException(void*, callStatus,
		callTransaction.call());

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

	_EX(ApiObject*) callMonad = search(packetCall -> callee);
	if(callMonad.abnormal) {
		clientExcept(packetCall -> caller, callMonad.exception);
		return;
	}
	else {
		ApiObject* target = callMonad.value;
		if(!(target -> callable())) {
			clientExcept(packetCall -> caller, 
				ApiException("Target not callable!"));
			return;
		}
		else {
			ApiLocal* callable = reinterpret_cast<ApiLocal*>(target);

			BufferInputStream inputStream(
				packetCall -> size, 
				packetCall -> parameter);

			BufferOutputStream outputStream;

			_EX(void*) resultMonad = callable -> invoke(
				packetCall -> call, *this, 
				inputStream, outputStream);

			if(resultMonad.abnormal) 
				clientExcept(packetCall -> caller, 
					resultMonad.exception);
			else {
				PacketReturn* callResult = new PacketReturn;
				callResult -> caller = packetCall -> caller;
				callResult -> size = outputStream.size();
				callResult -> result = outputStream.clone();
				connection -> send(callResult);
			}
		}
	}
}

void ApiHost::handleReturn(Packet* packet) {
	PacketReturn* packetReturn = reinterpret_cast<PacketReturn*>(packet);

	TRY_SERVEREX(ApiObject*, returnMonad,
		search(packetReturn -> caller));

	ApiObject* target = returnMonad.value;

	if(target -> callable()) {
		serverExcept(ApiException("Not a transaction."));
		return;
	}
	else {
		ApiTransaction* transaction 
			= reinterpret_cast<ApiTransaction*>(target);
		transaction -> result(
			packetReturn -> size, packetReturn -> result);
	}
}

void ApiHost::handleException(Packet* packet) {
	PacketException* packetException = reinterpret_cast<PacketException*>(packet);
	if(packetException -> caller == 0)
		serverExcept(packetException -> exception);
	else {
		TRY_SERVEREX(ApiObject*, exceptMonad,
			search(packetException -> caller));
			
		ApiObject* target = exceptMonad.value;
		if(target -> callable()) {
			serverExcept(ApiException("Not a transaction."));
			return;
		}

		ApiTransaction* transaction 
			= reinterpret_cast<ApiTransaction*>(target);
		transaction -> except(packetException -> exception);
	}
}

#include <iostream>
void ApiHost::serverExcept(ApiException e) {
	std::cerr << e.message() << std::endl;
}

void ApiHost::clientExcept(int32_t caller, ApiException e) {
	PacketException* callError = new PacketException;
	callError -> caller = caller;
	callError -> exception = e;
	connection -> send(callError);
}
