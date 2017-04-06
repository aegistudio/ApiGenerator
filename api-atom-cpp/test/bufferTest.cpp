#include "testCase.h"
#include "bufferStream.h"
#include "stdio.h"

const double testFloating = 1.2345678;

void test() throw (int) {
	int32_t i;

	// Write test first.
	api::BufferOutputStream output;

	for(i = 0; i < 10000; i ++)
		output.writeInt(i);
	assertEquals(output.size(), 
		(int)(10000 * sizeof(int32_t)));
	
	for(i = 0; i < 200; i ++)
		output.writeDouble(testFloating);
	std::cout << "[INFO] Finishes write test." << std::endl;

	// Replicate test.
	int8_t* checkBuffer = output.clone();
	std::cout << "[INFO] Finishes replication." << std::endl;

	std::cout << "[INFO] Showing the first 512 bytes: " << std::endl;
	for(i = 0; i < 512; i ++) {
		char current[32];
		sprintf(current, "%2x", checkBuffer[i]);
		std::cout << current << " ";
		if((i + 1) % 32 == 0)
			std::cout << std::endl;
	}

	// Read integer test.
	api::BufferInputStream input(output.size(), checkBuffer);
	for(i = 0; i < 10000; i ++)
		assertEquals(i, input.readInt());
	std::cout << "[INFO] Finishes read integer test." << std::endl;
	
	// Read floating test.
	for(i = 0; i < 200; i ++)
		assertEquals(testFloating, input.readDouble());
	std::cout << "[INFO] Finishes read floating test." << std::endl;
	
	// End test enclosure.
	assertEquals(0, input.remaining());
	std::cout << "[INFO] Finishes read test." << std::endl;
}
