package com.top.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.xml.stream.events.Comment;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Qna extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qno;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1500, nullable = false)
    private String content;

    // 28 Oct
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 50, nullable = false)
    private String writer;


    // title modify
    public void changeTitle(String title){
        this.title = title;
    }

    // content modify
    public void changeContent(String content){
        this.content = content;
    }

    @OneToMany(mappedBy = "qna", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @OrderBy("id asc")
    private List<Answer> answer;


    public boolean hasAnswers() {
        return answer != null && !answer.isEmpty();
    }


}
