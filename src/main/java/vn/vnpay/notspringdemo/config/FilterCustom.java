package vn.vnpay.notspringdemo.config;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;
@Component
public class FilterCustom  implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(FilterCustom.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        UUID uuid = UUID.randomUUID();
        ThreadContext.put("token", uuid.toString());
        logger.info("Create token request in Thread context: {} ", uuid.toString());
        chain.doFilter(request,response);
    }

}
