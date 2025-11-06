import { Body, Controller, HttpCode, HttpStatus, Post } from '@nestjs/common';
import { UserService } from './user.service';
import { CreateUserDto } from './dto/create-user.dto';

@Controller('/api/v1')
export class UserController {
  constructor(private readonly userService: UserService) {}

  @Post('/register')
  @HttpCode(HttpStatus.CREATED)
  async registerUser(@Body() userDTO: CreateUserDto) {
    const user = await this.userService.create(userDTO);

    return { id: user.id, message: 'User successfully registered' };
  }
}
