// src/user/user.entity.ts
import { Entity, Column, PrimaryGeneratedColumn } from 'typeorm';

@Entity() // 이 클래스가 DB 테이블임을 나타냅니다.
export class User {
  @PrimaryGeneratedColumn() // 기본 키(Primary Key)를 나타냅니다.
  id: number;

  @Column() // 일반 컬럼을 나타냅니다.
  firstName: string;

  @Column()
  lastName: string;

  @Column({ default: true })
  isActive: boolean;
}
