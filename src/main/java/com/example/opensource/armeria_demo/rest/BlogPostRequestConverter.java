package com.example.opensource.armeria_demo.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.armeria.common.AggregatedHttpRequest;
import com.linecorp.armeria.common.annotation.Nullable;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.RequestConverterFunction;

import java.lang.reflect.ParameterizedType;
import java.util.concurrent.atomic.AtomicInteger;

final class BlogPostRequestConverter implements RequestConverterFunction {

  private static final ObjectMapper mapper = new ObjectMapper();

  private final AtomicInteger idGenerator = new AtomicInteger();

  @Override
  public Object convertRequest(ServiceRequestContext ctx,
                               AggregatedHttpRequest request,
                               Class<?> expectedResultType,
                               @Nullable ParameterizedType expectedParameterizedResultType)
          throws Exception {
    if (expectedResultType == BlogPost.class) {
      JsonNode jsonNode = mapper.readTree(request.contentUtf8());
      int id = idGenerator.getAndIncrement();
      String title = stringValue(jsonNode, "title");
      String content = stringValue(jsonNode, "content");

      return new BlogPost(id, title, content);
    }

    return RequestConverterFunction.fallthrough();
  }

  static String stringValue(JsonNode jsonNode, String field) {
    final JsonNode value = jsonNode.get(field);
    if (value == null) {
      throw new IllegalArgumentException(field + " is missing!");
    }
    return value.textValue();
  }
}