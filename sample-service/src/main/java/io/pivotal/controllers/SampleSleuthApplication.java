package io.pivotal.controllers;

import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.EnableAspectJAutoProxy;
        import org.springframework.scheduling.annotation.EnableAsync;
        import org.springframework.web.client.RestTemplate;

/**
 * @author Reshmi Krishna
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
public class SampleSleuthApplication {

    public static final String CLIENT_NAME = "testApp";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SampleController sampleController() {
        return new SampleController();
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleSleuthApplication.class, args);
    }

}
