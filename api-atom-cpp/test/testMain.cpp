#include "testCase.h"

void assertClause(bool statement, std::string message, int errorCode) {
	if(!(statement)) {
		std::cerr << message << std::endl;
		throw (errorCode);
	}
}

int main() {
	try {
		test();
		return 0;
	}
	catch(int errorCode) {
		return errorCode;
	}
}
