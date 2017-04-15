#include "testCase.h"
#include "apiHost.h"
#include "bufferStream.h"
#include "pipeStream.h"
#include "streamConnection.h"
#include "defaultProtocol.h"
#include "defaultRegistry.h"

class ApiClient : public api::ApiHost {
public:
	ApiClient(api::ConnectionFactory& factory):
		api::ApiHost(factory, getPlatform()) {}

	api::exceptional<float> threeSum(float _a1, float _a2, float _a3) {

		api::BufferOutputStream output;
		output.writeFloat(_a1);
		output.writeFloat(_a2);
		output.writeFloat(_a3);

		api::variant<int8_t> callData(
			output.size(), output.clone());

		tryDeclare(api::variant<int8_t>, callResult,
			this -> call(0, 0, callData));

		std::cout << "[INFO] Client call finishes." << std::endl;

		api::BufferInputStream input(
			callResult.length, *callResult);
		return input.readFloat();
	}
};

class ApiServer : public api::ApiHost {
public:
	ApiServer(api::ConnectionFactory& factory):
		api::ApiHost(factory, getPlatform()) {}
	
	api::exceptional<float> threeSum(float _a1, float _a2, float _a3) 
		throw (api::ApiException) {
		// Deliberately wait some time.
		sleep(300L);

		return _a1 + _a2 + _a3;
	}

private:
	api::exceptional<void*> invokeThreeSum(api::InputStream& inputStream, 
		api::OutputStream& outputStream) throw (api::ApiException) {

		float _a1 = inputStream.readFloat();
		float _a2 = inputStream.readFloat();
		float _a3 = inputStream.readFloat();

		std::cout << "[INFO] Invoke on threeSum: " 
			<< _a1 << " " << _a2 << " " << _a3 << std::endl;

		tryDeclare(float, result, threeSum(_a1, _a2, _a3));

		outputStream.writeFloat(result);
		std::cout << "[INFO] Result: " << result << " prepared." << std::endl;
		return NULL;
	}
public:
	virtual api::exceptional<void*> invoke(int32_t callId, api::InputStream& inputStream, 
		api::OutputStream& outputStream) {

		switch(callId) {
			case 0:
				invokeThreeSum(inputStream, outputStream);
				return NULL;
			break;
			default:
				return NULL;
		}
	}
};

void test() throw (int) {
	api::Pipe serverInput(getPlatform());
	api::Pipe serverOutput(getPlatform());
	api::DefaultRegistry registry;
	api::DefaultProtocol protocol(registry);

	api::StreamFactory clientConnection(
		serverOutput.inputStream(), serverInput.outputStream(),
		getPlatform(), protocol);
	api::StreamFactory serverConnection(
		serverInput.inputStream(), serverOutput.outputStream(),
		getPlatform(), protocol);
	ApiClient client(clientConnection);
	ApiServer server(serverConnection);


	client.start();
	server.start();

	assertEquals(server.threeSum(1.0f, 2.0f, 3.0f).value,
		client.threeSum(1.0f, 2.0f, 3.0f).value);
	std::cout << "[INFO] Result 1 correct." << std::endl;

	assertEquals(server.threeSum(0.0f, -12.0f, 2.0f).value,
		client.threeSum(0.0f, -12.0f, 2.0f).value);
	std::cout << "[INFO] Result 2 correct." << std::endl;

	assertEquals(server.threeSum(3.141592f, -2.7182818f, 1.7320508f).value,
		client.threeSum(3.141592f, -2.7182818f, 1.7320508f).value);
	std::cout << "[INFO] Result 3 correct." << std::endl;

	assertEquals(server.threeSum(400.0f, 3.0f, 2.0f).value,
		client.threeSum(400.0f, 3.0f, 2.0f).value);
	std::cout << "[INFO] Result 4 correct." << std::endl;

	std::cout << "[INFO] Finished pseudo apiHost test." << std::endl;

	client.close();
	server.close();
}
