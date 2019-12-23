package com.example.pcfdemoa;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.web.client.DefaultRestTemplateExchangeTagsProvider;
import org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTagsProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class PcfDemoAApplication {

	private static Log log = LogFactory.getLog(getClass());

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {

		return restTemplateBuilder.build();

	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("GithubLookup-");
		executor.initialize();
		return executor;
	}


	@Bean
	public RestTemplateExchangeTagsProvider provider() {

		return new DefaultRestTemplateExchangeTagsProvider();

	}

	public static void main(String[] args) {

		ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(PcfDemoAApplication.class, args);

		log.info("Starting multiple threads");

		RestDemo restDemo = configurableApplicationContext.getBean(RestDemo.class);

		restDemo.sayHello();
		restDemo.sayHello();
		restDemo.sayHello();
		restDemo.sayHello();
		restDemo.sayHello();

	}

	@Component
	public class RestDemo {

		private Log log = LogFactory.getLog(getClass());


		@Autowired
		RestTemplate restTemplate;

		@Autowired
		MeterRegistry registry;


		@Value("${loadgen.url}")
		String url;


		@Async
		@SneakyThrows
		public void sayHello() {


			log.info("Starting the run");

			while (true) {



				ParameterizedTypeReference<Map<String, String>> ptr =
						new ParameterizedTypeReference<Map<String, String>>() {
						};

				ResponseEntity<Map<String, String>> responseEntity =
						this.restTemplate.exchange(url, HttpMethod.GET, null, ptr);

				Map m = responseEntity.getBody();
				m.put("hello", "world");
				m.put("hop2", Integer.toString(responseEntity.getStatusCodeValue()));

				log.info(m);

				Thread.sleep(500);

			}

		}

	}
}
