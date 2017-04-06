#pragma once
#include <iostream>
#include <string>
#include <sstream>

void assert(bool statement, std::string message, int errorCode = 1);

template<typename T>
void assertEquals(T expected, T get, int errorCode = 2) {
	std::stringstream output;
	output << "Assert equals fail: ";
	output << "Expected: " << expected << ", ";
	output << "Get: " << get << ".";
	assert(expected == get, output.str(), errorCode);
}

void test() throw(int);