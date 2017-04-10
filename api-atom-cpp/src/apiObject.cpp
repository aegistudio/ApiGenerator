#include "apiObject.h"
#include "apiHost.h"

using namespace api;

ApiObject::~ApiObject() {
	std::set<ApiHost*>::iterator iter;
	for(iter = hosts.begin(); iter != hosts.end(); iter ++)
		(*iter) -> demarshal(this);
}

void ApiObject::remember(ApiHost* host) {
	hosts.insert(host);
}

void ApiObject::forget(ApiHost* host) {
	hosts.erase(host);
}
