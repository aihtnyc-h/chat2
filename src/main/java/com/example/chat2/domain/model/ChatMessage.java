package com.example.chat2.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class ChatMessage {

    public ChatMessage() {
    }

    @Builder
    public ChatMessage(MessageType type, String roomId, String userName, String userProfile, String message, long userCount) {
        this.type = type;
        this.roomId = roomId;
        this.userName = userName;
        this.userProfile = userProfile;
        this.message = message;
        this.userCount = userCount;
    }

    // 메시지 타입 : 입장, 퇴장, 채팅
    public enum MessageType {
        ENTER, QUIT, TALK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private MessageType type; // 메시지 타입

    @Column
    private String roomId; // 방번호

    @Column
    private String userName; // 메시지 보낸사람

    private String userProfile;

    @Column
    private String message; // 메시지

    @Column
    private String timenow;
    private long userCount; // 채팅방 인원수, 채팅방 내에서 메시지가 전달될때 인원수 갱신시 사용
}
