package com.top.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notice extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nno;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1500, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 50, nullable = false)
    private String writer;

    @Column(nullable = false)
    private boolean pinned; //고정여부

    // title modify
    public void changeTitle(String title){
        this.title = title;
    }

    // content modify
    public void changeContent(String content){
        this.content = content;
    }

    // pinned 상태 변경 메서드
    public void togglePinned() {
        this.pinned = !this.pinned;
    }
}
