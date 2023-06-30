package org.com.lambda;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.com.lambda.models.Post;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class LambdaHandlerTest {

    private final ObjectMapper objectMapper = objectMapper();

    @Test
    public void testSimpleLambdaSuccess() throws Exception {
        Post post = new Post();
        post.setMessage("Hello world!");
        post.setAuthor("alobaton");
        post.setLikes(1000000);
        post.setTags(List.of("quarkus", "is", "amazing"));

        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody(objectMapper.writeValueAsString(post));

        SQSEvent in = new SQSEvent();
        in.setRecords(List.of(message));

        given()
            .contentType("application/json")
            .accept("application/json")
            .body(in)
            .when()
            .post()
            .then()
            .statusCode(200)
            .body(containsString("event processed successfully"));
    }

    private ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).disable(SerializationFeature.FAIL_ON_EMPTY_BEANS).setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(module);
    }

}
