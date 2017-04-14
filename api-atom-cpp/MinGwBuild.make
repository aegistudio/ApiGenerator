# This Makefile is used under condition that you
# are to build an EXE (PE) under linux. As it is tested
# that objcopy-ing an ELF format to a PE format doesn't
# make it works with wine.

# The system for using this Makefile should be under
# linux and have wine installed. Then you should install
# a Microsoft Visual C++ compiler with you wine.

# Finally you should set up environments and this script
# could get it done.

# ******************** CAUTION *********************
# You should use the CMakeLists.txt under the same directory
# while building APIs for linux under linux or windows
# under windows, never use this one.

COMPILER = mingw32-g++
LINKER = mingw32-ld
LIBTOOL = mingw32-ar
CXXFLAGS = -O3 

SOURCE = src
INCLUDE = include

# ******************* NEVER MODIFY UNDER ***************
all: api-atom-cpp.lib

api-atom-cpp.lib: $(lib_targets)
	$(LIBTOOL) rv $@ $^ 

$(lib_targets): %.obj : $(SOURCE)/%.cpp
	$(COMPILER) $(CXXFLAGS) -c $< -o $@ -I $(INCLUDE)

clean: 
	rm -rf *.obj
	rm -rf *.lib
