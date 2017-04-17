#include "apiRemote.h"

using namespace api;

ApiRemote::ApiRemote():
	host(NULL), handle(0) { }

bool ApiRemote::nullPointer() const { 
	return handle == 0; 
}

ApiRemote::ApiRemote(const ApiRemote& _copy):
	host(_copy.host), handle(_copy.handle) {

}

_EX(variant<int8_t>) ApiRemote::call(
	int32_t call, variant<int8_t> parameter) {

	if(nullPointer()) throwException(
		"Invoke on null pointer!");
	return host -> call(handle, call, parameter);
}

void ApiRemote::read(ApiHost& _host, 
	InputStream& _inputStream) {

	host = &_host;
	handle = _inputStream.readInt();
}

void ApiRemote::write(ApiHost& _host,
	OutputStream& _outputStream) {

	if(host != &_host)
		_outputStream.writeInt(0);
	else _outputStream.writeInt(handle);
}
