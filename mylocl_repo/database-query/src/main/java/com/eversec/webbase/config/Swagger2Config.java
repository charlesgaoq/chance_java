
package com.eversec.webbase.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
// @EnableSwagger2
//@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class Swagger2Config {
    // public class Swagger2Config extends BaseAppConfig {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    // @Bean
    // public Docket createRestApi() {
    // Docket docket = new Docket(DocumentationType.SWAGGER_2)
    // .apiInfo(apiInfo())
    // .groupName("travel")
    // .genericModelSubstitutes(DeferredResult.class)
    // .useDefaultResponseMessages(false)
    //// .globalResponseMessage(RequestMethod.GET,customerResponseMessage())
    // .forCodeGeneration(true)
    // .select()
    // .apis(RequestHandlerSelectors.basePackage("com.eversec"))
    // .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
    // .paths(PathSelectors.any())
    // .build();
    // logger.debug("swagger docket groupName: {}",docket.getGroupName());
    // return docket;
    // }
    // @Override
    // protected ApiInfo apiInfo() {
    // ApiInfo apiInfo = new ApiInfoBuilder().title("日志留存接口查询服务 RESTful APIs")
    // .description("OTS-NETlOG RESTful APIs")
    // // .contact(new Contact("Ken","",""))
    // .version("1.1.0").license("恒安嘉新").licenseUrl("eversec.com").build();
    // logger.debug("swagger ApiInfo : {},{},{}", apiInfo.getTitle(),
    // apiInfo.getDescription(),
    // apiInfo.getVersion());
    // return apiInfo;
    // }

}
