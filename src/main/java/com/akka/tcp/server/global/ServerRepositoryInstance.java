package com.akka.tcp.server.global;


import com.akka.tcp.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/** 전체 JPA Repository 설정**/
@Component
public class ServerRepositoryInstance {

    public ServerRepositoryInstance() throws Throwable
    {}

    private static UserRepository userRepository;

    public static UserRepository getUserRepository() {
        return userRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

