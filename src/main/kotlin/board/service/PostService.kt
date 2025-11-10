package com.overtheinfinite.board.service

import com.overtheinfinite.board.domain.Post
import com.overtheinfinite.board.dto.*
import com.overtheinfinite.board.repository.BoardRepository
import com.overtheinfinite.board.repository.PostRepository
import com.overtheinfinite.board.repository.PostRepositoryCustom
import com.overtheinfinite.user.domain.RoleType
import com.overtheinfinite.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import kotlin.NoSuchElementException

@Service
class PostService(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val postRepositoryCustom: PostRepositoryCustom,
) {

    fun getPost(postId: Long): PostResponse {
        return PostResponse(postRepository.findById(postId).orElseThrow {
            // 게시판이 존재하지 않으면 예외 발생 (404 Not Found에 해당)
            NoSuchElementException("Post not found with ID: ${postId}")
        })
    }
    fun getPosts(request: SearchPostRequest): Page<PostResponse> {
        return postRepositoryCustom.search(request)
    }
    @Transactional
    fun addPost(request: AddPostRequest, userId: Long): PostResponse {
        val board = boardRepository.findById(request.boardId).orElseThrow {
            // 게시판이 존재하지 않으면 예외 발생 (404 Not Found에 해당)
            NoSuchElementException("Board not found with ID: ${request.boardId}")
        }
        val user = userRepository.findById(userId).orElseThrow {
            // 사용자 ID가 유효하지 않으면 예외 발생 (401 Unauthorized 또는 404 Not Found에 해당)
            NoSuchElementException("User not found with ID: $userId")
        }

        val post = Post(title=request.title, content=request.content, board=board, writer=user, isBan=false)

        val newPost = postRepository.save(post)

        return PostResponse(newPost)
    }
    @Transactional
    fun updatePost(request: UpdatePostRequest, userId: Long): PostResponse? {
        val post = postRepository.findById(request.id).orElseThrow() {
            NoSuchElementException("Board not found with ID: ${request.id}")
        }

        if(post.writer.id == userId) {
            post.title = request.title
            post.content = request.content

            val newPost = postRepository.save(post)

            return PostResponse(newPost)
        } else {
            return null
        }
    }

    @Transactional
    fun deletePost(request: DeletePostRequest, userId: Long): Boolean {
        val post = postRepository.findById(request.id).orElseThrow() {
            NoSuchElementException("Post not found with ID: ${request.id}")
        }

        if(post.writer.id == userId) {
            postRepository.delete(post)

            return true
        } else {
            return false
        }
    }

    @Transactional
    fun banPost(request: DeletePostRequest, userId: Long): Boolean {
        val user = userRepository.findById(userId).orElseThrow() {
            NoSuchElementException("User not found with ID: $userId")
        }

        if(user.role == RoleType.ADMIN) {
            val post = postRepository.findById(request.id).orElseThrow() {
                NoSuchElementException("Post not found with ID: ${request.id}")
            }

            post.isBan = true

            postRepository.save(post)

            return true
        } else {
            return false
        }
    }
}