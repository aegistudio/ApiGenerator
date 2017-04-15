/**
 * APIEXCEPTION.H - The ApiException class.
 *
 * ApiException generalize types of exception
 * while calling api, so the call flow could 
 * be interrupted just as interruption between
 * different sub-sytems.
 *
 * Some Api module should be aware of the
 * existence of ApiExceptions and react to
 * them formally.
 */

#pragma once
#include <string>

namespace api {

class ApiException {
	std::string m_message;
public:
	ApiException();

	ApiException(const std::string&);

	const std::string& message() const;
};

};
