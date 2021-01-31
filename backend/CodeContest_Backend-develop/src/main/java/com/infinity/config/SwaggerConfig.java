package com.infinity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().paths(PathSelectors.any()).build();
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Infinity API").description("Infinity 홧팅~")
				.termsOfServiceUrl("https://edu.ssafy.com").contact("infinity@infinity.com").license("Infinity License")
				.licenseUrl("infinity@infinity.com").version("1.0").build();
	}
}
