package com.algorand.algosdk.unit;

import com.algorand.algosdk.unit.utils.ClientMocker;
import com.google.common.io.Files;
import io.cucumber.java.en.Given;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponsesShared {
    public File bodyFile;

    @Given("mock http responses in {string} loaded from {string}")
    public void mock_http_responses_in_loaded_from(String file, String dir) throws Exception {
        this.bodyFile = new File("src/test/resources/com/algorand/algosdk/unit/" + dir + "/" + file);
        assertThat(this.bodyFile).exists();

        // Content type based on file extension.
        String contentType;
        switch (Files.getFileExtension(file)) {
            case "base64":
                contentType = "application/msgpack";
                break;
            case "json":
            default:
                contentType = "application/json";
                break;
        }
        ClientMocker.oneResponse(200, contentType, bodyFile);
    }
}
