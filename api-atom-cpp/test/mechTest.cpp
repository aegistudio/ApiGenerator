#include "testCase.h"

/**
 * Ensure that the test works properly.
 */

void test() throw (int) {
	std::cout << "[INFO] Running test base." << std::endl;
	assertClause(true, "This should not be false.");
}
