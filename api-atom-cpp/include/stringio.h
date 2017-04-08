#pragma once

#include "stream.h"
#include <string>

namespace api { namespace String {
	void write(const std::string& string, OutputStream& outputStream);

	std::string read(InputStream& inputStream);
} };
