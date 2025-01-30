package com.example.opensource.armeria_demo.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost {

  private int id;

  private String title;

  private String content;

  private long createdAt;

  private long modifiedAt;

  @JsonCreator
  BlogPost(@JsonProperty("id") int id,
           @JsonProperty("title") String title,
           @JsonProperty("content") String content) {
    this(id, title, content, System.currentTimeMillis());
  }

  BlogPost(int id, String title, String content, long createdAt) {
    this(id, title, content, createdAt, createdAt);
  }

}
