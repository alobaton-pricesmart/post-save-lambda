package org.com.lambda.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.com.lambda.models.Post;
import org.com.lambda.repository.PostRepository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@ApplicationScoped
public class PostRepositoryDynamoDbImpl  implements PostRepository {

  protected final DynamoDbTable<Post> dynamoDbTable;

  @Inject
  PostRepositoryDynamoDbImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
    dynamoDbTable =
        dynamoDbEnhancedClient.table(
            getTableName(), TableSchema.fromClass(Post.class));
  }

  public String getTableName() {
    return "post";
  }

  @Override
  public Post getItem(String pk, String sk) {
    return dynamoDbTable.getItem(Key.builder().partitionValue(pk).sortValue(sk).build());
  }

  @Override
  public Post putItem(Post post) {
    dynamoDbTable.putItem(post);
    return post;
  }
}
