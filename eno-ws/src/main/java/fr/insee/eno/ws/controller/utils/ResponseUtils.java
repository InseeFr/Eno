package fr.insee.eno.ws.controller.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResponseUtils {

	private ResponseUtils() {}
	
	public static ResponseEntity<StreamingResponseBody> generateResponseFromOutputStream(ByteArrayOutputStream outputStream, String fileName) throws IOException {

		byte[] output = outputStream.toByteArray();
		outputStream.close();

		StreamingResponseBody stream = out -> out.write(output);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, HeaderUtils.headersAttachment(fileName))
				.body(stream);
	}

	public static ResponseEntity<StreamingResponseBody> generateResponseFromInputStream(InputStream inputStream, String fileName) throws IOException {

		byte[] output = inputStream.readAllBytes();
		inputStream.close();

		StreamingResponseBody stream = out -> out.write(output);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, HeaderUtils.headersAttachment(fileName))
				.body(stream);
	}

}
