// src/user/dto/create-user.dto.ts (클라이언트 -> 서버)
import { IsString, MinLength } from 'class-validator';

export class CreateUserDto {
  @IsString()
  @MinLength(2)
  name: string; // 클라이언트가 입력해야 하는 필드만 포함

  @IsString()
  @MinLength(8)
  password: string; // 비밀번호는 해싱되기 전의 원본을 받음
}

// src/user/dto/user.response.dto.ts (서버 -> 클라이언트)
// 비밀번호나 해시를 노출하지 않도록 정의
export class UserResponseDto {
  id: number;
  name: string; // 이미 복호화된 이름
  createdAt: Date;
}
