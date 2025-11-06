// src/user/user.entity.ts
import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  UpdateDateColumn,
  DeleteDateColumn,
} from 'typeorm';

@Entity() // 이 클래스가 DB 테이블임을 나타냅니다.
export class User {
  @PrimaryGeneratedColumn() // 기본 키(Primary Key)를 나타냅니다.
  id: number;

  @Column() // 복호화가 가능한 유저의 실명
  name: string;

  @Column() // 복호화가 불가능한 비밀번호
  hashedPassword: string;

  @DeleteDateColumn({ nullable: true }) // 탈퇴시 개인정보 전부 NULL처리 후
  deletedAt: Date;

  @CreateDateColumn()
  createdAt: Date;

  @UpdateDateColumn()
  updatedAt: Date;
}
