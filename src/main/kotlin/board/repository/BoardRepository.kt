package com.overtheinfinite.board.repository

import com.overtheinfinite.board.domain.Board
import org.springframework.data.jpa.repository.JpaRepository

interface BoardRepository: JpaRepository<Board, Long> {
}