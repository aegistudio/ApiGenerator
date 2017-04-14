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

# The 'mscl' and 'mslink' are scripts that I wrap up the 
# Visual C++ under linux and put it under /usr/bin. You
# could use wine <PATH-TO-VC>/Bin/CL.EXE and wine <PATH-TO-LINK>
# instead.
COMPILER = mscl
LINKER = mslink
LIBTOOL = mslib
CXXFLAGS = /GX /Op

SOURCE = src
INCLUDE = include

# ******************* NEVER MODIFY UNDER ***************
all: api-atom-cpp.lib

api-atom-cpp.lib: $(lib_targets)
	$(LIBTOOL) /OUT:$@ $^ 

$(lib_targets): %.obj : $(SOURCE)/%.cpp
	$(COMPILER) $(CXXFLAGS) /c $< /Fo$@ /I $(INCLUDE)

clean: 
	rm -rf *.obj
	rm -rf *.lib
