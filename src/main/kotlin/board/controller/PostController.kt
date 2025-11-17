package com.overtheinfinite.board.controller

import com.overtheinfinite.board.dto.*
import com.overtheinfinite.board.service.PostService
import com.overtheinfinite.common.BasicRes
import com.overtheinfinite.security.JwtTokenProvider
import com.overtheinfinite.user.domain.RoleType
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postService: PostService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @GetMapping()
    fun getPosts(request: SearchPostRequest): BasicRes<Page<PostResponse>> {
        val content = postService.getPosts(request);

        return BasicRes(result=true, data=content)
    }

    @GetMapping("/{postId}")
    fun getPost(@PathVariable("postId") postId: Long): BasicRes<PostResponse> {
        val content = postService.getPost(postId)

        return BasicRes(result=true, data=content)
    }

    @PostMapping
    fun addPost(@RequestBody addPostRequest: AddPostRequest, @RequestHeader("Authorization") authorizationHeader: String): BasicRes<PostResponse> {
        val user = jwtTokenProvider.getTokenUserResponse(authorizationHeader)

        val res = postService.addPost(addPostRequest, user.userId)

        return BasicRes(result=true, data=res)
    }

    @PostMapping("/update")
    fun updatePost(@RequestBody updatePostRequest: UpdatePostRequest, @RequestHeader("Authorization") authorizationHeader: String): BasicRes<PostResponse?> {
        val user = jwtTokenProvider.getTokenUserResponse(authorizationHeader)

        val res = postService.updatePost(updatePostRequest, user.userId)

        return BasicRes(result=true, data=res)
    }

    @PostMapping("/ban")
    fun banPost(@RequestBody deletePostRequest: DeletePostRequest, @RequestHeader("Authorization") authorizationHeader: String): BasicRes<Boolean?> {
        val user = jwtTokenProvider.getTokenUserResponse(authorizationHeader)

        if(user.role == RoleType.ADMIN){
            val res = postService.banPost(deletePostRequest, user.userId)

            return BasicRes(result=true, data=res)
        }
        else {
            return BasicRes(result=false, data=null)
        }
    }

    @DeleteMapping()
    fun deletePost(@RequestBody deletePostRequest: DeletePostRequest, @RequestHeader("Authorization") authorizationHeader: String): BasicRes<Boolean> {
        val user = jwtTokenProvider.getTokenUserResponse(authorizationHeader)

        val res = postService.deletePost(deletePostRequest, user.userId)

        return BasicRes(result=res, data=res)
    }

    @GetMapping("/comment")
    fun getPostComments(getPostCommentRequest: GetPostCommentRequest):
            BasicRes<Page<PostCommentResponse>> {
        val res = postService.getPostComments(getPostCommentRequest)

        return BasicRes(result=true, data=res)
    }

    @PostMapping("/comment")
    fun addPostComment(@RequestBody addPostCommentRequest: AddPostCommentRequest, @RequestHeader("Authorization") authorizationHeader: String):
            BasicRes<PostCommentResponse> {
        val user = jwtTokenProvider.getTokenUserResponse(authorizationHeader)

        val res = postService.addPostComment(addPostCommentRequest, user.userId)

        return BasicRes(result=true, data=res)
    }
}