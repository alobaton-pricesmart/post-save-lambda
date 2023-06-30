package org.com.lambda.models;

import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@DynamoDbBean
public abstract class Model {
  @Getter(onMethod = @__({@DynamoDbPartitionKey}))
  private String pk;

  @Getter(onMethod = @__({@DynamoDbSortKey}))
  private String sk;
}