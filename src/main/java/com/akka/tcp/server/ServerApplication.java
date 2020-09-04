package com.akka.tcp.server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.TcpMessage;
import com.akka.tcp.server.extension.SpringExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ServerApplication {

    private static ApplicationContext context = null;

    public static void main(String[] args) {

        //  Springboot context 가져오기
        context = SpringApplication.run(ServerApplication.class, args);

        //  액터 시스템 가져오기
        ActorSystem system = context.getBean(ActorSystem.class);

        //  로그 시스템 세팅
        final LoggingAdapter log = Logging.getLogger(system, "AkkaTcpServer");

        //  actor 생성시 필요
        SpringExtension ext = context.getBean(SpringExtension.class);

        //  서버 시작
        ActorRef TcpServerActor = system.actorOf(ext.props("tcpServerActor"));

    }

}
