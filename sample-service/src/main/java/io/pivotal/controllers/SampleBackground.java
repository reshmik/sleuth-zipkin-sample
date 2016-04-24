
package io.pivotal.controllers;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author Reshmi Krishna
 */
@Component
public class SampleBackground {

    @Autowired
    private Tracer tracer;

    @Async
    public void background() throws InterruptedException {
        final Random random = new Random();
        int millis = random.nextInt(1000);
        Thread.sleep(millis);
        this.tracer.addTag("background-sleep-millis", String.valueOf(millis));
    }

}
