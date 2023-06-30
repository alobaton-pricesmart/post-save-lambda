package org.com.lambda.repository;

import org.com.lambda.models.Post;

public interface PostRepository {
  Post getItem(String pk, String sk);
  Post putItem(Post post);
}
