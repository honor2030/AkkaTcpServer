package com.akka.tcp.server.extension;

import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Spring을 통해 Akka에서 bean을 생성하는 클래스
 */
@Component
public class SpringExtension implements Extension {

    private ApplicationContext applicationContext;

    /**
     * Spring application context 초기화
     */
    public void initialize(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 명시된 actor의 bean명을 통해 SpringActorProducer 클래스로 Props 생성하기
     */
    public Props props(String actorBeanName, Object... args) {
        return (args != null && args.length > 0) ?
                Props.create(SpringActorProducer.class,
                        applicationContext,
                        actorBeanName, args) :
                Props.create(SpringActorProducer.class,
                        applicationContext,
                        actorBeanName);
    }
}
