package com.dummyapp.two.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by SThapa on 12/15/2016.
 */
@Service
public class AimClientService {

    private static Logger LOGGER = LoggerFactory.getLogger(AimClientService.class);

    @Value("${aim.url.host}")
    private String aimUrl;
    @Value("${aim.url.port}")
    private String aimPort;
    @Value("${aim.kafka.request.path}")
    private String aimRequestPath;

    @Autowired
    private ObjectMapper mapper;

    private HttpClient client;

    @Async
    public void send(final String topic, final String message) {
        String url = "http://" + aimUrl + ":" + aimPort + "/" + aimRequestPath + "/" + topic;
        client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");

        try {
            StringEntity postingString = new StringEntity(message);
            post.setEntity(postingString);
            client.execute(post);
            LOGGER.info("=== Message === {} Sent at == topic: {} ", message, topic);
        } catch (UnsupportedEncodingException uee) {
            LOGGER.info("=== Error converting to StringEntity === UnsupportedEncodingException: {} ", uee.getMessage());
        } catch (IOException ioe) {
            LOGGER.info("=== Error executing post request === IOException: {} ", ioe.getMessage());
        } catch (Exception e) {
            LOGGER.info("=== Error Sending Request === Exception: {} ", e.getMessage());
        }
    }

}
