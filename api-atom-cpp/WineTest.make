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
CXXFLAGS = /Ot /GX

TEST = test
INCLUDE = include

targets = mechTest.test endianTest.test bufferTest.test\
	packetTest.test variantTest.test

# ******************* NEVER MODIFY UNDER ***************
all: $(targets)

$(targets): %.test : $(TEST)/%.cpp testMain.obj api-atom-cpp.lib
	$(COMPILER) $(CXXFLAGS) /c $< /Fo$@.obj /I $(INCLUDE)
	$(LINKER) $@.obj testMain.obj api-atom-cpp.lib /OUT:$@.exe
	@echo "[WINETEST] Running test $@:"
	@wine $@.exe && mv $@.exe $@
	@echo "[WINETEST] Test $@ succeed."

testMain.obj: $(TEST)/testMain.cpp
	$(COMPILER) $(CXXFLAGS) /c $< /O $@

api-atom-cpp.lib:
	make -f WineBuild.make

clean: 
	rm -rf *.test
	rm -rf *.lib
	rm -rf *.exe
	rm -rf *.obj
