package com.overtheinfinite.admin.service

import com.overtheinfinite.admin.domain.Board
import com.overtheinfinite.admin.dto.BoardResponse
import com.overtheinfinite.admin.repository.BoardRepository
import org.springframework.stereotype.Service

@Service
class BoardService(
    private val boardRepository: BoardRepository
) {
    fun addBoard(boardName: String): Long {
        val board = Board(name=boardName)

        val savedBoard = boardRepository.save(board)

        if(savedBoard.id != null) {
            return savedBoard.id
        } else {
            throw RuntimeException("Board를 저장한 값을 가져왔는데 ID가 없대.. 이게 말이 되나?")
        }
    }

    fun getBoards(): List<BoardResponse> {
        val boards = boardRepository.findAll()

        return boards.
            map { board ->
                board.id?.let {
                    BoardResponse(
                        id = it,
                        name = board.name,
                    )
                }!!
        }
    }

}