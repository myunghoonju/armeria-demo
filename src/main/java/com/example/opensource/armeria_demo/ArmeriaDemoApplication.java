package com.example.opensource.armeria_demo;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.docs.DocService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArmeriaDemoApplication {

	public static void main(String[] args) {
		Server server = newServer(8080);
		server.closeOnJvmShutdown();
		server.start().join();
		log.info("Armeria Demo Server started");
	}

	static Server newServer(int port) {
		ServerBuilder sb = Server.builder();
		DocService docService = DocService.builder()
										  .exampleRequests(BlogService.class,
														   "createBlogPost",
														   "{\"title\":\"My first blog\", \"content\":\"Hello Armeria!\"}")
									      .build();

		return sb.http(port)
				 .annotatedService(new BlogService())
				 .service("/", (ctx, req) -> HttpResponse.of("Hello Armeria!") )
				 .serviceUnder("/docs", docService).build();
	}
}
