cmake_minimum_required(VERSION 2.6)
# ======================================== #
# *            API-ATOM-CPP              * #
# * Atom interface defined for CPP as is * #
# * designed in API Generator.           * #
# ======================================== #

project(api-atom-cpp)

set(CMAKE_CXX_COMPILER_VERSION c99)
set(CMAKE_CXX_FLAGS "-std=c99 -g")

include_directories(include)
add_subdirectory(src bin)


