package com.akka.tcp.server.actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import akka.util.ByteString;
import com.akka.tcp.server.extension.SpringExtension;
import com.akka.tcp.server.services.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.Option;
import static com.akka.tcp.server.constants.CommonConstants.sendMessage;
import static com.akka.tcp.server.constants.ServerErrorCode.error;

/**
 * Title : TcpServerHandlerActor
 * Description : Tcp 서버 핸들러 액터
**/


@Component
@Scope("prototype")
public class TcpServerHandlerActor extends UntypedActor {

    private ApplicationContext context = null;
    private LoggingAdapter log;
    private SpringExtension ext;
    private ActorSystem system;


    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {

        //  에러메시지 정의(별도 정의 필요). 여기서는 간단히 Error 리턴
        byte[] sendMessageBytes = error.getBytes();

        //  클라이언트에 에러 메시지 전송
        getSender().tell(TcpMessage.write(ByteString.fromArray(sendMessageBytes)), getSelf());

    }


    @Override
    public void preStart() throws Exception {

        context = ApplicationContextProvider.getApplicationContext();

        log = Logging.getLogger(getContext().system(), this);

        ext = context.getBean(SpringExtension.class);

        system = context.getBean(ActorSystem.class);
    }


    @Override
    public void onReceive(Object msg) throws Exception {

        //  Client로부터 메세지를 받았을 경우
        if (msg instanceof Received) {

            //  전달받은 데이터 확인
            final String data = ((Received) msg).data().utf8String();
            log.info("TcpServerHandlerActor - Received message : " + data);

            /**  비즈니스 로직 처리 **/
            //  클라이언트 요청 데이터 파싱
            //  파싱한 데이터에 따른 DB 입출력 등의 로직 처리


            //  전송할 메시지
            byte[] sendMessageBytes = sendMessage.getBytes();

            //  클라이언트에 응답 전송
            getSender().tell(TcpMessage.write(ByteString.fromArray(sendMessageBytes)), getSelf());

            //  보낸 메세지 출력
            log.info("Result : " + sendMessage);

            //  서버 예외처리시(예제)
            //  throw new ServerException(error);

        //  Client와의 연결종료시(소켓 연결 종료)
        } else if (msg instanceof ConnectionClosed) {

            //  연결종료
            log.info("TcpServerHandlerActor - Connection close : " + msg);

            //  현재 context 종료
            getContext().stop(getSelf());
        }
    }
}
