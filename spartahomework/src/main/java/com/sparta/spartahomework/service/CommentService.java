package com.sparta.spartahomework.service;

import com.sparta.spartahomework.dto.CommentRequestDto;
import com.sparta.spartahomework.dto.CommentResponseDto;
import com.sparta.spartahomework.entity.Comment;
import com.sparta.spartahomework.entity.Post;
import com.sparta.spartahomework.entity.User;
import com.sparta.spartahomework.entity.UserRoleEnum;
import com.sparta.spartahomework.exception.CustomException;
import com.sparta.spartahomework.jwt.JwtUtil;
import com.sparta.spartahomework.repository.CommentRepository;
import com.sparta.spartahomework.repository.PostRepository;
import com.sparta.spartahomework.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import static com.sparta.spartahomework.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponseDto createComment(Long post_id, CommentRequestDto requestDto, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우만 댓글 쓰기 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new CustomException(INVALID_TOKEN);
            }

            // 토큰에서 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new CustomException(USER_NOT_FOUND)
            );

            // post_id를 사용하여 게시글 존재 확인
            Post post = postRepository.findById(post_id).orElseThrow(
                    () -> new CustomException(POST_NOT_FOUND)
            );

            // 인증된 사용자는 댓글 쓰기
            Comment comment = commentRepository.saveAndFlush(new Comment(requestDto, post, user));

            return new CommentResponseDto(comment);
        } else {
            return null;
        }
    }
    @Transactional
    public CommentResponseDto update(Long comment_id, CommentRequestDto requestDto, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우만 댓글 수정 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new CustomException(INVALID_TOKEN);
            }

            // 토큰에서 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new CustomException(USER_NOT_FOUND)
            );

            // 해당하는 댓글 가져오기
            Comment comment = commentRepository.findById(comment_id).orElseThrow(
                    () -> new CustomException(COMMENT_NOT_FOUND)
            );

            // 댓글 작성자와 사용자가 같은 지 확인
            if (user.getRole() == UserRoleEnum.ADMIN || comment.check_author(user.getUsername())) {
                comment.update(requestDto);
                CommentResponseDto responseDto = new CommentResponseDto(comment);
                return responseDto;
            } else {
                throw new CustomException(INVALID_USER);
            }

        } else {
            return null;
        }
    }
    @Transactional
    public Long deleteComment(Long comment_id, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우만 글 수정 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new CustomException(INVALID_TOKEN);
            }

            // 토큰에서 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new CustomException(USER_NOT_FOUND)
            );

            // 해당하는 댓글 가져오기
            Comment comment = commentRepository.findById(comment_id).orElseThrow(
                    () -> new CustomException(COMMENT_NOT_FOUND)
            );

            // 댓글 작성자와 사용자가 같은 지 확인
            if (user.getRole() == UserRoleEnum.ADMIN || comment.check_author(user.getUsername())) {
                commentRepository.deleteById(comment_id);
                return comment_id;
            } else {
                throw new CustomException(INVALID_USER);
            }
        } else {
            return null;
        }
    }
}
