package com.example.opensource.armeria_demo;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.annotation.Blocking;
import com.linecorp.armeria.server.annotation.Default;
import com.linecorp.armeria.server.annotation.Delete;
import com.linecorp.armeria.server.annotation.ExceptionHandler;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Param;
import com.linecorp.armeria.server.annotation.Post;
import com.linecorp.armeria.server.annotation.ProducesJson;
import com.linecorp.armeria.server.annotation.Put;
import com.linecorp.armeria.server.annotation.RequestConverter;
import com.linecorp.armeria.server.annotation.RequestObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BlogService {

  private final Map<Integer, BlogPost> blogPosts = new ConcurrentHashMap<>();

  @Get("/")
  public HttpResponse index() {
    return HttpResponse.of("welcome to armeria");
  }

  @Post("/blogs")
  @RequestConverter(BlogPostRequestConverter.class) // default: JacksonRequestConverterFunction.class
  public HttpResponse createBlogPost(BlogPost blogPost) {
    blogPosts.put(blogPost.getId(), blogPost);
    return HttpResponse.ofJson(blogPost);
  }

  @Get("/blogs/:id")
  public HttpResponse getBlogPost(@Param int id) {
    final BlogPost blogPost = blogPosts.get(id);
    return HttpResponse.ofJson(blogPost);
  }

  @Get("/blogs")
  @ProducesJson
  public Iterable<BlogPost> getBlogPosts(@Param @Default("true") boolean descending) {
    if (descending) {
      return blogPosts.entrySet()
              .stream()
              .sorted(Collections.reverseOrder(Comparator.comparingInt(Entry::getKey)))
              .map(Entry::getValue).collect(Collectors.toList());
    }
    return new ArrayList<>(blogPosts.values());
  }

  @Put("/blogs/:id")
  public HttpResponse updateBlogPost(@Param int id, @RequestObject BlogPost blogPost) {
    final BlogPost oldBlogPost = blogPosts.get(id);
    if (oldBlogPost == null) {
      return HttpResponse.of(HttpStatus.NOT_FOUND);
    }

    final BlogPost newBlogPost = new BlogPost(id,
                                              blogPost.getTitle(),
                                              blogPost.getContent(),
                                              oldBlogPost.getCreatedAt(),
                                              blogPost.getCreatedAt());
    blogPosts.put(id, newBlogPost);

    return HttpResponse.ofJson(newBlogPost);
  }

  @Blocking
  @Delete("/blogs/:id")
  @ExceptionHandler(BadRequestExceptionHandler.class)
  public HttpResponse deleteBlogPost(@Param int id) {
    if (blogPosts.remove(id) == null) {
      throw new IllegalArgumentException("The blog post does not exist. ID: " + id);
      // Or we can simply return a NOT_FOUND response.
      // return HttpResponse.of(HttpStatus.NOT_FOUND);
    }

    return HttpResponse.of(HttpStatus.NO_CONTENT);
  }
}
