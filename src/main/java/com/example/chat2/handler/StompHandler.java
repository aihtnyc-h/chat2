package com.example.chat2.handler;

import com.example.chat2.service.ChatRoomService;
import com.example.chat2.security.JwtTokenProvider;
import com.example.chat2.domain.repository.UserRepository;
import com.example.chat2.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final UserRepository userRepository;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println("웹소켓에 신호 들어옴");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청
            System.out.println("웹소켓 연결 요청");
            String jwtToken = accessor.getFirstNativeHeader("token");
            System.out.println("토큰 확인 토큰 값:"+ jwtToken);
            System.out.println("연결 요청");
            log.info("CONNECT {}", jwtToken);
            System.out.println("토큰 유효성 검증");
            // Header의 jwt token 검증 -> 유효하지 않다면 websocket 연결을 하지 않음
            jwtTokenProvider.validateToken(jwtToken);
            System.out.println("완료");
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
            // header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
            System.out.println("구독 요청");
            String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
            System.out.println("해당 룸 ID:" +roomId);
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            System.out.println("요청한 session ID :"+ sessionId);
            System.out.println("입장 요청, 유저정보 셋팅 요청");
            chatRoomService.setUserEnterInfo(sessionId, roomId);
            // 채팅방의 인원수를 +1한다.
            System.out.println("인원수 +1");
            chatRoomService.plusUserCount(roomId);
            System.out.println("입장 메세지 발송 요청 진입");
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
//            chatService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.ENTER).roomId(roomId).sender(name).build());
            System.out.println("발송 요청");
            log.info("SUBSCRIBED {}, {}", name, roomId);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
            System.out.println("연결 종료 단계");
            // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            System.out.println("연결이 종료된 세션 : "+sessionId);
            String roomId = chatRoomService.getUserEnterRoomId(sessionId);
            System.out.println("룸 아이디 확인 : "+ roomId);
            // 채팅방의 인원수를 -1한다.
            System.out.println("인원수 -1 ");
            chatRoomService.minusUserCount(roomId);
            // 클라이언트 퇴장 메시지를 채팅방에 발송한다.(redis publish)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
//            chatService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.QUIT).roomId(roomId).sender(name).build());
            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatRoomService.removeUserEnterInfo(sessionId);
            System.out.println("맵핑 정보 삭제");
            log.info("DISCONNECTED {}, {}", sessionId, roomId);
        }
        return message;
    }
}
