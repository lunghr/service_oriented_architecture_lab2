package com.example.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@EnableWs
@Configuration
@ComponentScan("com.example.endpoint")
public class WebServiceConfig {

    @Bean(name = "hr")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema hrSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("HRPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://example.com/hr-service");
        wsdl.setSchema(hrSchema);
        return wsdl;
    }

    @Bean
    public XsdSchema hrSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/hr-service.xsd"));
    }

    // Добавляем WebClient для общения с worker-service
    @Bean
    public WebClient webClient() throws SSLException {

        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(sslContext));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.example.generated"); // Укажите пакет с JAXB-классами
        return marshaller;
    }
}
