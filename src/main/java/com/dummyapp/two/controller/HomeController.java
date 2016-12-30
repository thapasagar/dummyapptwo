package com.dummyapp.two.controller;

import com.dummyapp.two.service.AimClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by sthapa on 12/27/2016.
 */
@Controller
@RequestMapping("/")
public class HomeController {

    private Random rnd = new Random();
    private static Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Inject
    private AimClientService aimClient;

    @RequestMapping(value = "/ping", method = GET)
    public ResponseEntity ping() {
        return new ResponseEntity<String>("DummyTwo Online : " + Instant.now(), HttpStatus.OK);
    }

    @RequestMapping(value = "/hoptwo", method = GET)
    public ResponseEntity getMe() {
        Long number = ThreadLocalRandom.current().nextLong(500, 15000);
        try {
            Thread.sleep(number);
            LOGGER.info("I slept for {} ", number);
        } catch (InterruptedException e) {
            LOGGER.info("=== Why was I awaken? === ");
        }
        return new ResponseEntity<String>("DummyTwo Response :- Completed at " + Instant.now(), HttpStatus.OK);
    }

    @RequestMapping(value = "/callDummy2", method = GET)
    public ResponseEntity getMeToo() {
        try {
                Thread.sleep(10000);
        } catch (InterruptedException e) {
            //catch exception e
        }
        return new ResponseEntity<String>("Checked", HttpStatus.OK);
    }
}
