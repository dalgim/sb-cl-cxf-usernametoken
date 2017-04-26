package com.dalgim.example.sb.cxf.usernametoken.config;

import com.dalgim.example.sb.cxf.usernametoken.endpoint.FruitCatalog;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

import static org.apache.wss4j.common.ConfigurationConstants.ACTION;
import static org.apache.wss4j.common.ConfigurationConstants.PASSWORD_TYPE;
import static org.apache.wss4j.common.ConfigurationConstants.PW_CALLBACK_CLASS;
import static org.apache.wss4j.common.ConfigurationConstants.USER;

/**
 * Created by dalgim on 09.04.2017.
 */
@Configuration
public class WebServiceClientConfig {

    @Bean
    public FruitCatalog jaxWsProxyFactoryBean(@Value("${fruitService.address}") String address) {
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(FruitCatalog.class);
        jaxWsProxyFactoryBean.setAddress(address);
        FruitCatalog fruitCatalog = (FruitCatalog) jaxWsProxyFactoryBean.create();
        Client client = ClientProxy.getClient(fruitCatalog);
        configureEndpoint(client.getEndpoint());
        return fruitCatalog;
    }

    private void configureEndpoint(Endpoint endpoint) {
        endpoint.getInInterceptors().add(loggingInInterceptor());
        endpoint.getInInterceptors().add(wss4JInInterceptor());
        endpoint.getOutInterceptors().add(loggingOutInterceptor());
        endpoint.getOutInterceptors().add(wss4JOutInterceptor());
    }

    private WSS4JOutInterceptor wss4JOutInterceptor() {
        Map<String, Object> securityConfig = new HashMap<>();
        securityConfig.put(ACTION, "UsernameToken");
        securityConfig.put(PASSWORD_TYPE, "PasswordDigest");
        securityConfig.put(USER, "client");
        securityConfig.put(PW_CALLBACK_CLASS, CertificatePasswordHandler.class.getName());
        return new WSS4JOutInterceptor(securityConfig);
    }

    private WSS4JInInterceptor wss4JInInterceptor() {
        Map<String, Object> securityConfig = new HashMap<>();
        securityConfig.put(ACTION, "UsernameToken");
        securityConfig.put(PASSWORD_TYPE, "PasswordText");
        securityConfig.put(PW_CALLBACK_CLASS, CertificatePasswordHandler.class.getName());
        return new WSS4JInInterceptor(securityConfig);
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
