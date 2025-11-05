import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { ConfigModule, ConfigService } from '@nestjs/config'; // 👈 ConfigModule 임포트import { AppService } from './app.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UserModule } from './user/user.module';
import { AppService } from './app.service';

@Module({
  imports: [
    // 1. ConfigModule 설정: 프로젝트 전체에서 환경 변수를 사용할 수 있게 함
    ConfigModule.forRoot({
      isGlobal: true, // 모든 모듈에서 ConfigService를 사용할 수 있게 전역 설정
      envFilePath: '.env', // .env 파일 경로 지정 (기본값)
    }),

    // 2. TypeOrmModule 동적 설정 (for RootAsync)
    TypeOrmModule.forRootAsync({
      imports: [ConfigModule], // ConfigService를 사용하기 위해 ConfigModule 임포트
      useFactory: (configService: ConfigService) => ({
        type: configService.get<any>('DB_TYPE'), // 'mysql' (MariaDB 사용)
        host: configService.get<string>('DB_HOST'),
        port: configService.get<number>('DB_PORT'), // 👈 문자열을 숫자로 변환
        username: configService.get<string>('DB_USERNAME'),
        password: configService.get<string>('DB_PASSWORD'),
        database: configService.get<string>('DB_DATABASE'),

        // 엔티티 경로는 환경 변수와 상관없이 고정
        entities: [__dirname + '/**/*.entity{.ts,.js}'],
        synchronize: true, // 개발 환경 설정
        logging: true,
      }),
      inject: [ConfigService], // useFactory에 ConfigService를 주입
    }),
    UserModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
