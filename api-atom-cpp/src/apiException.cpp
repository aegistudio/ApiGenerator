#include "apiException.h"

using namespace api;

ApiException::ApiException(const std::string& _message):
	m_message(_message) {}

const std::string& ApiException::message() {
	return m_message;
}
