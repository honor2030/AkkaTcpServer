package com.akka.tcp.server.actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.Tcp.Bound;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.Tcp.ConnectionClosed;
import akka.io.TcpMessage;
import akka.japi.Function;
import akka.util.ByteString;
import com.akka.tcp.server.extension.SpringExtension;
import com.akka.tcp.server.services.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.Option;
import scala.concurrent.duration.Duration;
import java.net.InetSocketAddress;
import static akka.actor.SupervisorStrategy.*;
import static com.akka.tcp.server.constants.ServerErrorCode.error;

/**
 * Title : TCP Server Actor
 * Description : 클라이언트 요청을 받아서 핸들러로 넘겨주는 액터
 **/

@Component
@Scope("prototype")
public class TcpServerActor extends UntypedActor {

    private ActorRef tcpActor;

    private LoggingAdapter log;

    private SpringExtension ext;

    private ActorSystem system;

    private ApplicationContext context = null;

    private String serverIP = "0.0.0.0";

    private int serverPort = 50000;


    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {

        //  에러메시지 정의(별도 정의 필요). 여기서는 간단히 Error 리턴
        byte[] sendMessageBytes = error.getBytes();

        //  클라이언트에 에러 메시지 전송
        getSender().tell(TcpMessage.write(ByteString.fromArray(sendMessageBytes)), getSelf());
    }

    //  액터 시작전 초기화
    @Override
    public void preStart() throws Exception {

        context = ApplicationContextProvider.getApplicationContext();

        log = Logging.getLogger(getContext().system(), this);

        ext = context.getBean(SpringExtension.class);

        system = context.getBean(ActorSystem.class);

        tcpActor = Tcp.get(getContext().system()).manager();

        tcpActor.tell(TcpMessage.bind(getSelf(),
                new InetSocketAddress(serverIP, serverPort), 100), getSelf());

    }

    @Override
    public void onReceive(Object msg) throws Exception {

        //  소켓 리스닝 상태
        if (msg instanceof Bound) {
            log.info("Listening on " + serverIP + ":" + serverPort);

        //  OS 버퍼가 가득차 있어 쓰기 실패시
        } else if (msg instanceof CommandFailed) {
            getContext().stop(getSelf());

        //  클라이언트 연결시
        } else if (msg instanceof Connected) {
            final Connected conn = (Connected) msg;

            //  연결 정보 출력
            log.info("TcpServer - received message: connected from "
                    + conn.remoteAddress().getAddress() + ":"
                    + conn.remoteAddress().getPort() + " to "
                    + conn.localAddress().getAddress() + ":"
                    + conn.localAddress().getPort());


            //  TCP 핸들러 Actor 생성
            final ActorRef handler = system.actorOf(
                    ext.props("tcpServerHandlerActor"));

            //  Handler Actor에게 메세지 전달
            getSender().tell(TcpMessage.register(handler), getSelf());

        //  클라이언트 연결 종료시
        } else if (msg instanceof ConnectionClosed) {
            log.info("TcpServer - received message: disconnected");
        }
    }


    /**
     *  자식 actor에 대한 예외처리.
     *  OneForOneStrategy : 오류가 난 자식 Actor에 한해 작동
     */
    private static SupervisorStrategy strategy =
            new OneForOneStrategy(5, Duration.create("1 minute"),
                    new Function<Throwable, Directive>() {

                        @Override
                        public Directive apply(Throwable throwable) throws Exception {

                            if (throwable instanceof Exception) {
                                return resume();
                            } else {
                                return escalate();
                            }
                        }
                    });

    @Override
    public SupervisorStrategy supervisorStrategy() {

        return strategy;
    }
}
