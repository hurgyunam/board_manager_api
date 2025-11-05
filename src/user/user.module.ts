import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from './user.entity'; // 위에서 생성한 엔티티 임포트
import { UserService } from './user.service';

@Module({
  imports: [
    TypeOrmModule.forFeature([User]), // 해당 모듈에서 사용할 엔티티 등록
  ],
  providers: [UserService],
  exports: [TypeOrmModule],
})
export class UserModule {}
