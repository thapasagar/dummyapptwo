package com.dummyapp.two.configs;

import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

/**
 * Created by SThapa on 10/28/2016.
 */
@WebFilter
public class MDCFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        try {
            Principal principal = ((HttpServletRequest) request).getUserPrincipal();
            String user = principal != null ? principal.getName() : ((HttpServletRequest) request).getRemoteUser();
            Integer status = ((HttpServletResponse) response).getStatus();
            String requestId = ((HttpServletRequest) request).getHeader("trackingid");
            String trackingId = requestId != null ? requestId : request.getAttribute("TRACKING_ID") != null ? request
                    .getAttribute("TRACKING_ID").toString() : UUID.randomUUID().toString();

            request.setAttribute("TRACKING_ID", trackingId);
            MDC.put("IP", request.getRemoteAddr());
            MDC.put("USER", user != null ? user.toUpperCase() : "UNKNOWN USER");
            MDC.put("URI", ((HttpServletRequest) request).getRequestURI());
            MDC.put("TRACKING_ID", trackingId);
            MDC.put("RESPONSE_CODE", status != null ? status.toString() : "-");
            MDC.put("PROTOCOL", request.getProtocol());

            chain.doFilter(request, response);
        } finally {
            MDC.remove("USER");
            MDC.remove("IP");
            MDC.remove("URI");
            MDC.remove("RESPONSE_CODE");
            MDC.remove("TRACKING_ID");
            MDC.remove("PROTOCOL");
        }
    }

    @Override
    public void destroy() {

    }
}
