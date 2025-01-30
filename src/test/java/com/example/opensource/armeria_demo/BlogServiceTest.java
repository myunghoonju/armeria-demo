package com.example.opensource.armeria_demo;

import com.example.opensource.armeria_demo.rest.BlogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.testing.junit5.server.ServerExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;

class BlogServiceTest {

  @RegisterExtension
  static ServerExtension TEST_SERVER = initServer();

  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void create() {
    WebClient cli = WebClient.of(TEST_SERVER.httpUri());
    HttpRequest req = req(Map.of("title", "My first blog", "content", "Hello world"));
    AggregatedHttpResponse response = cli.execute(req).aggregate().join();

    System.out.println(response.contentUtf8());
  }

  HttpRequest req(Map<String, String> content) {
    try {
      return HttpRequest.builder().post("/blogs").content(MediaType.JSON_UTF_8, objectMapper.writeValueAsString(content)).build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  static ServerExtension initServer() {
    return new ServerExtension() {

      @Override
      protected void configure(ServerBuilder serverBuilder) throws Exception {
        serverBuilder.annotatedService(new BlogService());
      }

    };
  }
}