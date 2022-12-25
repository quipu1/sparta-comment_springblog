package com.sparta.spartahomework.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.spartahomework.dto.CommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Post_ID", nullable = false)
    private Post post;

    public Comment(CommentRequestDto requestDto, Post post, User user) {
        this.author = user.getUsername();
        this.content = requestDto.getContent();
        this.post = post;
        this.user = user;

    }

    public boolean check_author(String username) {
        if (this.author.equals(username)) {
            return true;
        }
        return  false;
    }

    public void update(CommentRequestDto requestDto) {
        this.content = requestDto.getContent();
    }
}
