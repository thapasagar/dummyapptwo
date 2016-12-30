package com.dummyapp.two.configs;

import com.dummyapp.two.service.AimClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by sthapa on 12/27/2016.
 */
public class TimeInterceptor extends HandlerInterceptorAdapter {
    private Map<String, String> map;
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeInterceptor.class);

    @Value("${kafka.topic.name}")
    private String kafkaTopic;

    @Inject
    private ObjectMapper mapper;

    @Inject
    private AimClientService aimClientService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception
            ex) throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        String requestId = request.getHeader("requestid");
        String trackingId = requestId != null ? requestId : request.getAttribute("TRACKING_ID") != null ? request
                .getAttribute("TRACKING_ID").toString() : UUID.randomUUID().toString();

        LOGGER.info("=== DummyOne Trackingid === {} ", trackingId);
        String url = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (queryString != null) url += "?" + queryString;
        map = new LinkedHashMap<>();
        map.put("TIME_STAMP", String.valueOf(Instant.now()));
        map.put("IP_ADDRESS", request.getRemoteAddr());
        map.put("USER_ID", request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "UNKNOWN" + " " +
                "" + "" + "" + "USER");
        map.put("URI", request.getRequestURI().replace("/kukuscorner-ecom/REST/DEP/", ""));
        map.put("QUERY_PARAM", queryString != null ? queryString : "NONE");
        map.put("TRACKING_ID", trackingId);
        map.put("PROTOCOL", request.getProtocol());

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        map.put("RESPONSE_CODE", response.getStatus() != 0 ? String.valueOf(response.getStatus()) : "NONE");
        map.put("TIME_TAKEN", String.valueOf(elapsedTime));
        String json = mapper.writeValueAsString(map);
        LOGGER.info(json);
        aimClientService.send(kafkaTopic, json);
    }
}
