package com.top.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "answer")
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "QNA_ID")
    private Qna qna;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private  Member member;

    // Content Update
    public void update(String content){
        this.content = content;
    }
}
