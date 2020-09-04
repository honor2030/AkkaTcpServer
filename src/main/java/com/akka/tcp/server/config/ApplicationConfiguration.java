package com.akka.tcp.server.config;

import akka.actor.ActorSystem;
import com.akka.tcp.server.extension.SpringExtension;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@Configuration
@Lazy
@ComponentScan(basePackages = {
        "com.akka.tcp.server.services",
        "com.akka.tcp.server.actors",
        "com.akka.tcp.server.extension" })
public class ApplicationConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SpringExtension springExtension;


    @Autowired
    private Environment env;

    @Bean
    public ActorSystem actorSystem() {


        ActorSystem system = ActorSystem
                .create("AkkTcpServer", ConfigFactory.load());
        springExtension.initialize(applicationContext);
        return system;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
    }

}
