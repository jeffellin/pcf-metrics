package com.example.pcfdemoa;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.util.Random;

@SpringBootApplication
@EnableDiscoveryClient
public class PcfDemoBApplication {


	public static void main(String[] args) {
		SpringApplication.run(PcfDemoBApplication.class, args);
	}

	@RestController
	@Timed
	public class RestDemo {

		@Value("${app.zone:na}")
		String zone;

		private Log log = LogFactory.getLog(getClass());

		@Autowired
		MeterRegistry registry;


		@GetMapping("/")
		@Timed(value = "hello.time")
		public String sayHello() throws InterruptedException {




			Counter counter = registry.counter("hello.count");
			counter.increment();


				Random rn = new Random();
				int answer = rn.nextInt(5) + 1;

				Thread.sleep(answer*1000);
				return "hello world:" + answer;


		}

		@GetMapping("/hop")
		@Timed(value = "hop.time")
		Map<String, String> message(HttpServletRequest httpRequest) {


			Collections.list(httpRequest.getHeaderNames()).stream().forEach(h->log.info(h+"="+httpRequest.getHeader(h)));

			log.info("this is the hop");

			String key = "message";

			Map<String, String> response = new HashMap<>();

				String value = "Hi, from a REST Zone:" + zone + " endpoint: " + System.currentTimeMillis();

				response.put(key, value);

				return response;
		}

	}

}
