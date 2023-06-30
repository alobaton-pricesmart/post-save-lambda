package org.com.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.com.lambda.models.Post;
import org.com.lambda.repository.PostRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class PostLambda implements RequestHandler<SQSEvent, String> {

  private final ObjectMapper objectMapper = objectMapper();

  @Inject
  PostRepository postRepository;

  @Override
  public String handleRequest(SQSEvent input, Context context) {
    if (input == null || input.getRecords() == null || input.getRecords().isEmpty()) {
      return "empty event";
    }

    input.getRecords().stream().forEach(this::processMessage);
    return "event processed successfully";
  }

  private void processMessage(SQSEvent.SQSMessage sqsMessage) {
    String body = sqsMessage.getBody();
    Post post = null;
    try {
      post = objectMapper.readValue(body, Post.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    UUID uuid = UUID.randomUUID();
    String pk = uuid.toString();
    post.setPk(pk);

    LocalDateTime now = LocalDateTime.now();
    String sk = String.valueOf(now.toEpochSecond(ZoneOffset.UTC));
    post.setSk(sk);

    post.setCreationTime(now);

    postRepository.putItem(post);
    Log.debug("post saved!");

    post = postRepository.getItem(pk, sk);
    Log.debug("post checked!");
  }

  public ObjectMapper objectMapper() {
    JavaTimeModule module = new JavaTimeModule();
    return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).disable(SerializationFeature.FAIL_ON_EMPTY_BEANS).setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(module);
  }
}