package io.pivotal.controllers;


import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanAccessor;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Spencer Gibb
 */
@RestController
public class SampleController
        implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {
    private static final Log log = LogFactory.getLog(SampleController.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Tracer tracer;
    @Autowired
    private SpanAccessor accessor;
    @Autowired
    private SampleBackground controller;

    private Random random = new Random();
    private int port;
    @Value("${service1.url}")
    private String service1;

    @Value("${service2.url}")
    private String service2;
    @RequestMapping("/")
    public String hi() throws InterruptedException {
        Thread.sleep(this.random.nextInt(1000));

        String s = this.restTemplate
                .getForObject("http://localhost:" + this.port + "/hi2", String.class);
        return "hi/" + s;
    }

    @RequestMapping("/call")
    public Callable<String> call() {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                int millis = SampleController.this.random.nextInt(1000);
                Thread.sleep(millis);
                SampleController.this.tracer.addTag("callable-sleep-millis",
                        String.valueOf(millis));
                Span currentSpan = SampleController.this.accessor.getCurrentSpan();
                return "async hi: " + currentSpan;
            }
        };
    }

    @RequestMapping("/async")
    public String async() throws InterruptedException {
        this.controller.background();
        return "ho";
    }

    @RequestMapping("/hi2")
    public String hi2() throws InterruptedException {
        int millis = this.random.nextInt(1000);
        Thread.sleep(millis);
        this.tracer.addTag("random-sleep-millis", String.valueOf(millis));
        return "hi2";
    }

    @RequestMapping("/traced")
    public String traced() throws InterruptedException {
        Span span = this.tracer.createSpan("http:customTraceEndpoint",
                new AlwaysSampler());
        int millis = this.random.nextInt(1000);
        log.info(String.format("Sleeping for [%d] millis", millis));
        Thread.sleep(millis);
        this.tracer.addTag("random-sleep-millis", String.valueOf(millis));

        String s = this.restTemplate
                .getForObject("http://localhost:" + this.port + "/call", String.class);
        this.tracer.close(span);
        return "traced/" + s;
    }

    @RequestMapping("/start")
    public String start() throws InterruptedException {
        int millis = this.random.nextInt(1000);
        log.info(String.format("Sleeping for [%d] millis", millis));
        Thread.sleep(millis);
        this.tracer.addTag("random-sleep-millis", String.valueOf(millis));

        String s = this.restTemplate
                .getForObject("http://localhost:" + this.port + "/call", String.class);
        return "start/" + s;
    }

    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
        this.port = event.getEmbeddedServletContainer().getPort();
    }
//    @Autowired
//    private RestTemplate client;



    @RequestMapping(value = "/compose", method = RequestMethod.GET)
    public String composeData() throws Exception{
        //System.out.printf(service1);
        String val1 = restTemplate.getForEntity(service1,String.class).getBody();
        String val2 = restTemplate.getForEntity(service2,String.class).getBody();
        return val1 +" " + val2;
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public ResponseEntity<String> getData(@RequestHeader Map headers) throws Exception{
        Random r = new Random();
        //System.out.println(headers);
        Thread.sleep((long)r.nextDouble()*1000);
        return new ResponseEntity<String>(r.nextDouble()+"", HttpStatus.OK);
    }
}
