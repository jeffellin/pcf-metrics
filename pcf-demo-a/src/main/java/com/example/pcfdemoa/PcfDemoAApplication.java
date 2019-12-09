package com.example.pcfdemoa;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.web.client.DefaultRestTemplateExchangeTagsProvider;
import org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTagsProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Random;

@SpringBootApplication
public class PcfDemoAApplication {


	@Bean
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


		@GetMapping("/")
		@Timed(value = "hello.time")
		public Map<String,String> sayHello()   {



			log.info("this is the 1st hop");

			String url = "http://demob-jellin.cfapps.io/hop";

			ParameterizedTypeReference<Map<String, String>> ptr =
					new ParameterizedTypeReference<Map<String, String>>() {
					};

			ResponseEntity<Map<String, String>> responseEntity =
					this.restTemplate.exchange(url, HttpMethod.GET, null, ptr);

			Map m = responseEntity.getBody();
			m.put("hello","world");
			m.put("hop2",Integer.toString(responseEntity.getStatusCodeValue()));

			return m;
		};

	}

}
