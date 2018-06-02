package com.packtpub.yummy.reverseproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
public class ErrorPageController extends AbstractErrorController {
    private static final Logger LOG = LoggerFactory.getLogger("error");
    private static final String PATH = "/error";

    public ErrorPageController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
    }

    @RequestMapping(value = PATH)
    public void error(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> errorAttributes = this.getErrorAttributes(request, false);
        LOG.info("ERROR-PAGE:{}, IP:{}:{}, MESSAGE: {}, QUERY:{}", response.getStatus(), request.getRemoteAddr(), request.getRemotePort(), errorAttributes.get("message"), request.getAttribute("javax.servlet.error.request_uri"));
        response.sendError(501, String.valueOf(errorAttributes.get("message")));
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

}
