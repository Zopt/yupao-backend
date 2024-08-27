package zerodot.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * 自定义Swagger接口文档的配置
 */
@Configuration
@EnableSwagger2
@Profile({"dev","test"})
@ComponentScan(basePackages = {"zerodot.usercenter.controller"})
public class SwaggerConfig{
    @Bean
    public Docket customDocket(){
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        return docket.apiInfo(apiInfo()).
                pathMapping("/")
                .select() // 选择那些路径和api会生成document
                .apis(RequestHandlerSelectors.any())// 对所有api进行监控
                //不显示错误的接口地址
//                .paths(Predicates.not(PathSelectors.regex("/error.*")))//错误路径不监控
                .paths(PathSelectors.regex("/.*"))// 对根下所有路径进行监控
                .build().protocols(Stream.of("http", "https").collect(toSet()));
    }

    private ApiInfo apiInfo(){
        Contact contact = new Contact("zerodot","https://github.com/zerodot/","3300745564@qq.com");
        return new ApiInfoBuilder().
                title("zerodot用户中心").
                description("用户中心接口说明文档").
                termsOfServiceUrl("https://www.cqinfo.top").
                contact(contact).
                version("1.0.0").
                build();
    }

}