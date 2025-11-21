package com.overtheinfinite.board.controller

import com.overtheinfinite.board.dto.*
import com.overtheinfinite.board.service.PostService
import com.overtheinfinite.common.BasicRes
import com.overtheinfinite.security.JwtTokenProvider
import com.overtheinfinite.user.domain.RoleType
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException

@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postService: PostService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @Value("\${file.upload.dir}") // YAML 경로(file.upload.dir)와 일치해야 합니다.
    private val uploadDir: String? = null

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
    fun addPost(@RequestBody addPostRequest: AddPostRequest, @CookieValue("accessToken") accessToken: String): BasicRes<PostResponse> {
        val user = jwtTokenProvider.getTokenUserResponse(accessToken)

        val res = postService.addPost(addPostRequest, user.userId)

        return BasicRes(result=true, data=res)
    }

    // 새로운 게시글의 이미지
    @PostMapping("/image")
    fun uploadImage(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, Any>> {
        if (file.isEmpty) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "result" to false,
                "message" to "Please select a file to upload."
            ));
        }
        try {
            // 2. 파일 이름 가져오기
            val fileName = file.originalFilename
                ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                    "result" to false,
                    "message" to "Invalid file name."
                ))


            // 3. 서버에 저장할 파일 객체 생성 (저장 경로 + 파일 이름)
            val dest = File(uploadDir + fileName)


            // UPLOAD_DIR 경로가 없으면 생성 (선택 사항)
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs()
            }

            // 4. 파일 저장
            // 실제로 파일을 지정된 서버 경로에 전송 (저장)합니다.
            file.transferTo(dest)

            // 5. 성공 응답
            return ResponseEntity.ok(mapOf(
                "result" to true,
                "message" to "File uploaded successfully: $fileName",
                "fileName" to fileName
            ))
        } catch (e: IOException) {
            // 파일 저장 중 발생할 수 있는 오류 처리
            e.printStackTrace()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf(
                    "result" to false,
                    "message" to "Failed to upload the file: " + e.message
                ))
        }

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