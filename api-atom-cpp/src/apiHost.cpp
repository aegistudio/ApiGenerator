#include "apiHost.h"
#include "apiObject.h"

using namespace api;

ApiHost::ApiHost(Platform& _platform):
	platform(_platform) {}

ApiHost::~ApiHost() {
	std::map<int32_t, ApiObject*>::iterator iter;
	for(iter = objects.begin(); iter != objects.end(); iter ++) 
		(*iter).second -> forget(this);
}

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
	else return NULL;
}
