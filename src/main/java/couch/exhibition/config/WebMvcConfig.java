package couch.exhibition.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.io.IOException;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**/*")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath,
                                                   Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        return requestedResource.exists() && requestedResource.isReadable() ? requestedResource
                                : new ClassPathResource("/static/index.html");
                    }
                });

        registry
                .addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry
                .addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        WebMvcConfigurer.super.addResourceHandlers(registry);
    }


    @Bean
    public Docket swaggerAPI(){
        //Docket : swagger Bean
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(true) //?????? ?????? ????????? ?????? ??????
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)) //swagger?????? ?????? ????????? //.apis(RequestHandlerSelectors.any())->error ?????????????????? ??????.
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Exhibition Service Swagger")
                .description("Exhibition API Service Test")
                .version("1.0")
                .build();
    }
        // ????????? ???????????? ????????? URL ??? ?????? => http://localhost:8080/swagger-ui/index.html
}
