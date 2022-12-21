package com.sparta.spartahomework.service;

import com.sparta.spartahomework.dto.CommentResponseDto;
import com.sparta.spartahomework.dto.PostRequestDto;
import com.sparta.spartahomework.dto.PostResponseDto;
import com.sparta.spartahomework.entity.Comment;
import com.sparta.spartahomework.entity.Post;
import com.sparta.spartahomework.entity.User;
import com.sparta.spartahomework.entity.UserRoleEnum;
import com.sparta.spartahomework.exception.CustomException;
import com.sparta.spartahomework.jwt.JwtUtil;
import com.sparta.spartahomework.repository.PostRepository;
import com.sparta.spartahomework.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.sparta.spartahomework.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts() {
        List<PostResponseDto> posts = postRepository.findAllByOrderByModifiedAtDesc().stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());

        return posts;
    }

    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우만 글 쓰기 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new CustomException(INVALID_TOKEN);
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new CustomException(USER_NOT_FOUND)
            );

            Post post = postRepository.saveAndFlush(new Post(requestDto, user));

            PostResponseDto postResponseDto = new PostResponseDto(post);

            return postResponseDto;
        } else {
            return null;
        }
    }
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new CustomException(POST_NOT_FOUND)
        );

        PostResponseDto postResponseDto = new PostResponseDto(post);
        return postResponseDto;
    }
    @Transactional
    public PostResponseDto update(Long id, PostRequestDto postRequestDto, HttpServletRequest request) {

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

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new CustomException(USER_NOT_FOUND)
            );

            // 해당하는 게시글 가져오기
            Post post = postRepository.findById(id).orElseThrow(
                    () -> new CustomException(POST_NOT_FOUND)
            );

            // 작성자와 사용자가 같은 지 확인
            if (user.getRole() == UserRoleEnum.ADMIN || post.getAuthor().equals(user.getUsername())) {
                post.update(postRequestDto);
                PostResponseDto postResponseDto = new PostResponseDto(post);
                return postResponseDto;
            } else {
                throw new CustomException(INVALID_USER);
            }
        } else {
            return null;
        }
    }

    @Transactional
    public Long deletePost(Long id, HttpServletRequest request) {
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

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new CustomException(USER_NOT_FOUND)
            );

            // 해당하는 게시글 가져오기
            Post post = postRepository.findById(id).orElseThrow(
                    () -> new CustomException(POST_NOT_FOUND)
            );

            // 작성자와 사용자가 같은 지 확인
            if (user.getRole() == UserRoleEnum.ADMIN || post.getAuthor().equals(user.getUsername())) {
                postRepository.deleteById(id);
                return id;
            } else {
                throw new CustomException(INVALID_USER);
            }
        } else {
            return null;
        }
    }
}
