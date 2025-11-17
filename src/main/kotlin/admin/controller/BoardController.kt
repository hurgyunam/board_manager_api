package com.overtheinfinite.admin.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/board")
class BoardController {
    // addBoard = ONLY ADMIN
    // deleteBoard = ONLY ADMIN OR BOARD MANAGER => CASCADE DELETE POST
    // getBoards = MENU

}