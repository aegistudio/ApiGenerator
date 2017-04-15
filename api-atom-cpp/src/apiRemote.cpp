#include "apiRemote.h"

using namespace api;

ApiRemote::ApiRemote(ApiHost* _host, int32_t _handle):
	host(_host), handle(_handle) {	

}

bool ApiRemote::nullPointer() const { 
	return handle == 0; 
}

ApiRemote::ApiRemote(const ApiRemote& _copy):
	host(_copy.host), handle(_copy.handle) {

}

_EX(variant<int8_t>) ApiRemote::call(
	int32_t call, variant<int8_t> parameter) {

	return host -> call(handle, call, parameter);
}
