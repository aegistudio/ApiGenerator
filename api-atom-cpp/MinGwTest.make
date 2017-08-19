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
CXXFLAGS = -O3 -Wall
LDFLAGS = -lgcc -lstdc++ -lntdll -lmingw32 -lkernel32 -lmingwex

TEST = test
INCLUDE = include

# ******************* NEVER MODIFY UNDER ***************
all: $(test_targets)

$(test_targets): %.test : $(TEST)/%.cpp testMain.obj winTestPlatform.obj api-atom-cpp.lib
# TODO: If some one knows what $(COMPILER) links with, please help making it into a compile
# command with a link command.
#	$(COMPILER) $(CXXFLAGS) -c $< -o $@.obj -I $(INCLUDE)
#	$(LINKER) $(LDFLAGS) $@.obj testMain.obj winTestPlatform.obj api-atom-cpp.lib -o $@.exe 
	$(COMPILER) $(CXXFLAGS) $< testMain.obj winTestPlatform.obj api-atom-cpp.lib -o $@.exe -I $(INCLUDE)
	@echo "[WINETEST] Running test $@:"
	@wine $@.exe && mv $@.exe $@
	@echo "[WINETEST] Test $@ succeed."

winTestPlatform.obj: $(TEST)/winTestPlatform.cpp
	$(COMPILER) $(CXXFLAGS) -I $(INCLUDE) -c $< -o $@

testMain.obj: $(TEST)/testMain.cpp
	$(COMPILER) $(CXXFLAGS) -I $(INCLUDE) -c $< -o $@

api-atom-cpp.lib:
	$(MAKEFILE) MinGwBuild.make

clean: 
	rm -rf *.test
	rm -rf *.lib
	rm -rf *.exe
	rm -rf *.obj
	rm -rf *.ilk
	rm -rf *.pdb
