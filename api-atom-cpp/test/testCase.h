#pragma once
#include <iostream>
#include <string>

void assert(bool statement, std::string message, int errorCode = 1);

void test() throw(int);
