#include "fileStream.h"

using namespace api;

// Only used under win32, to turn the
// file mode into binary.
#ifdef _WIN32
#include "fcntl.h"
#include "io.h"
#endif

inline void binarizeFile(FILE* file) {
#ifdef _WIN32
	_setmode(_fileno(file), _O_BINARY);
#endif
}

FileInputStream::FileInputStream(FILE* _file):
	file(_file) {
	
	binarizeFile(file);
}

void FileInputStream::read(void* buffer, int size) {
	fread(buffer, size, 1, file);
}

FileOutputStream::FileOutputStream(FILE* _file):
	file(_file) {

	binarizeFile(file);
}

void FileOutputStream::write(const void* buffer, int size) {
	fwrite(buffer, size, 1, file);
}
