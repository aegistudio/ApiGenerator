#include "apiObject.h"
#include "apiHost.h"

using namespace api;

ApiObject::ApiObject():
	valid(true) {}

ApiObject::~ApiObject() {
	valid = false;
	std::set<ApiHost*>::iterator iter;
	for(iter = hosts.begin(); iter != hosts.end(); ++ iter)
		if(*iter) (*iter) -> demarshal(this);
}

void ApiObject::remember(ApiHost* host) {
	if(valid) hosts.insert(host);
}

void ApiObject::forget(ApiHost* host) {
	if(valid) hosts.erase(host);
}
