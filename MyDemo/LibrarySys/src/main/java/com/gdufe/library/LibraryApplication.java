package com.gdufe.library;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    //修改tomcat参数支持请求特殊符号
    @Bean
    public TomcatServletWebServerFactory webServerFactory() {

        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();

        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {

            @Override

            public void customize(Connector connector) {

                connector.setProperty("relaxedPathChars", "\"<>[\\]^`{|}");

                connector.setProperty("relaxedQueryChars", "\"<>[\\]^`{|}");

            }

        });

        return factory;

    }
}
