package com.example.pcfdemoa;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.web.client.DefaultRestTemplateExchangeTagsProvider;
import org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTagsProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.ServiceInstance;
import org.apache.commons.lang.builder.ToStringBuilder;


import java.util.Map;
import java.util.Random;

@SpringBootApplication
@EnableDiscoveryClient
public class PcfDemoAApplication {


	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder.build();
	}


	@Bean
	public RestTemplateExchangeTagsProvider provider(){
		return new DefaultRestTemplateExchangeTagsProvider();

	}
	public static void main(String[] args) {
		SpringApplication.run(PcfDemoAApplication.class, args);
	}

	@RestController
	@Timed
	public class RestDemo {

		private Log log = LogFactory.getLog(getClass());

		@Autowired
		RestTemplate restTemplate;

		@Autowired
		MeterRegistry registry;


		@Autowired
		private DiscoveryClient discoveryClient;

		@Value("${vcap.services.my-db-mine.credentials.password:none}")
		String password;

		@Value("${application.foo:none}")
		String foo;

		@GetMapping("/")
		@Timed(value = "hello.time")
		public Map<String,String> sayHello()   {


			//discoveryClient.getInstances("hop2").forEach((ServiceInstance s) -> {
		//		log.info(ToStringBuilder.reflectionToString(s));
		//	});
			discoveryClient.getInstances("hop2").forEach((ServiceInstance s) -> {
				log.info(ToStringBuilder.reflectionToString(s));
				log.info(s.getMetadata());
			});


			log.info("this is the 1st hop");

			String url = "http://hop2/hop";

			ParameterizedTypeReference<Map<String, String>> ptr =
					new ParameterizedTypeReference<Map<String, String>>() {
					};

			ResponseEntity<Map<String, String>> responseEntity =
					this.restTemplate.exchange(url, HttpMethod.GET, null, ptr);

			Map<String,String> m = responseEntity.getBody();
			m.put("hello","world");
			m.put("hop2",Integer.toString(responseEntity.getStatusCodeValue()));
			m.put("password",password);
			m.put("foo",foo);

			return m;
		};

	}

}
