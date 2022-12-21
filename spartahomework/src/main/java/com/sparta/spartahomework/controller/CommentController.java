package com.sparta.spartahomework.controller;

import com.sparta.spartahomework.dto.CommentRequestDto;
import com.sparta.spartahomework.dto.CommentResponseDto;
import com.sparta.spartahomework.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/comment")
    public CommentResponseDto createComment(@RequestParam Long post_id, @RequestBody CommentRequestDto requestDto, HttpServletRequest request) {
        return commentService.createComment(post_id, requestDto, request);
    }

    // 댓글 수정
    @PutMapping("/comment")
    public CommentResponseDto updateComment(@RequestParam Long comment_id, @RequestBody CommentRequestDto requestDto, HttpServletRequest request) {
        return commentService.update(comment_id, requestDto, request);
    }

    // 댓글 삭제
    @DeleteMapping("/comment")
    public Long deleteComment(@RequestParam Long comment_id, HttpServletRequest request) {
        return commentService.deleteComment(comment_id, request);
    }
}
