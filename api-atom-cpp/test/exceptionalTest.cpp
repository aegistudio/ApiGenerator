#include "testCase.h"

#include <exceptional>
#include <apiVariant>

using namespace api;

exceptional< int > nonNegativeInt(int someValue) {
	if(someValue >= 0) return someValue;
	else throwException("Value should be positive.");
}

exceptional< float > cascadeCast(int someValue) {
	tryDeclare(int, raw, nonNegativeInt(
		someValue));
	return (float)raw;
}

exceptional< variant<int> > countDown(int someValue) {
	tryDeclare(int, value, nonNegativeInt(someValue));

	variant<int> result(value);
	int i; for(i = 0; i < result.length; i ++) 
		tryAssign(int, result[i], result,
			nonNegativeInt(value - i));
	return result;
}

void test() throw (int) {
	exceptional< int > test1 = nonNegativeInt(10);
	assertClause(!test1.abnormal, "No exception this case.");
	assertEquals(10, test1.value);

	exceptional< int > test2 = nonNegativeInt(-1);
	assertClause(test2.abnormal, "Should except this case.");

	exceptional< float > test3 = cascadeCast(20);
	assertClause(!test3.abnormal, "No exception this case.");
	assertEquals((float)20, test3.value);

	exceptional< float > test4 = cascadeCast(-2);
	assertClause(test4.abnormal, "Should except this case.");

	exceptional< variant<int> > test5 = countDown(40);
	assertClause(!test5.abnormal, "No exception this case.");
	const variant<int>& test5List = test5.value;
	assertEquals(test5List.length, 40);
	int i; for(i = 0; i < test5List.length; i ++)
		assertEquals(test5List.length - i, test5List[i]);

}
