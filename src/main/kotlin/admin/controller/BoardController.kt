package com.overtheinfinite.admin.controller

import com.overtheinfinite.admin.dto.AddBoardRequest
import com.overtheinfinite.admin.dto.BoardResponse
import com.overtheinfinite.admin.service.BoardService
import com.overtheinfinite.common.BasicRes
import com.overtheinfinite.security.JwtTokenProvider
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/board")
class BoardController(
    private val boardService: BoardService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    // addBoard = ONLY ADMIN
    // deleteBoard = ONLY ADMIN OR BOARD MANAGER => CASCADE DELETE POST
    // getBoards = MENU

    @GetMapping()
    fun getBoards(): BasicRes<List<BoardResponse>> {
        val boards = boardService.getBoards()

        return BasicRes(result=true, data=boards)
    }

    @PostMapping()
    fun addBoard(@RequestBody addBoardRequest: AddBoardRequest, @CookieValue("accessToken") accessToken: String): BasicRes<Long> {
        val res = jwtTokenProvider.getTokenUserResponse(accessToken)

        val newBoardId = boardService.addBoard(addBoardRequest.boardName)

        return BasicRes(result=true, data=newBoardId)
    }
}