package com.dalgim.example.sb.cxf.usernametoken.config;

import com.dalgim.example.sb.cxf.usernametoken.endpoint.FruitCatalog;
import com.dalgim.example.sb.cxf.usernametoken.endpoint.FruitCatalogImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.xml.ws.Endpoint;

/**
 * Created by dalgim on 08.04.2017.
 */
@Configuration
public class WebServiceServerConfig {

    private static final String SERVLET_URL_PATH = "/api";
    private static final String SERVICE_URL_PATH = "/FruitCatalog";

    @Bean
    public ServletRegistrationBean cxfServlet() {
        return new ServletRegistrationBean(new CXFServlet(), SERVLET_URL_PATH + "/*");
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public FruitCatalog fruitService() {
        return new FruitCatalogImpl();
    }

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), fruitService());
        endpoint.publish(SERVICE_URL_PATH);
        endpoint.getProperties().put(Message.EXCEPTION_MESSAGE_CAUSE_ENABLED, "true");
        endpoint.getProperties().put(Message.FAULT_STACKTRACE_ENABLED, "true");
        endpoint.getInInterceptors().add(loggingInInterceptor());
        endpoint.getOutInterceptors().add(loggingOutInterceptor());
        return endpoint;
    }

    private LoggingInInterceptor loggingInInterceptor() {
        LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
        loggingInInterceptor.setPrettyLogging(true);
        return loggingInInterceptor;
    }

    private LoggingOutInterceptor loggingOutInterceptor() {
        LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
        loggingOutInterceptor.setPrettyLogging(true);
        return loggingOutInterceptor;
    }

}
