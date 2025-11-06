// src/user/user.service.ts
import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User } from './user.entity';
import * as bcrypt from 'bcrypt';
import { decrypt, encrypt } from '@app/common/utils/encryption.util';
import { CreateUserDto } from './dto/create-user.dto';

// 보안을 위해 비밀번호 해싱에 사용할 솔트 라운드 수
const SALT_ROUNDS = 10;

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User) // TypeORM Repository 주입
    private usersRepository: Repository<User>,
  ) {}

  findAll(): Promise<User[]> {
    return this.usersRepository.find();
  }

  async findOne(id: number): Promise<User | null> {
    // 해당 ID의 사용자가 없으면 null을 반환합니다.
    return this.usersRepository.findOneBy({ id });
  }

  /**
   * 🌟 새로운 인증 함수: 사용자 이름과 비밀번호를 검증합니다.
   * @param username 로그인에 사용될 사용자 이름 (여기서는 name 필드 사용 가정)
   * @param pass 사용자가 입력한 원본 비밀번호
   * @returns 인증 성공 시 User 객체 (비밀번호 제거), 실패 시 null
   */
  async validateUser(
    username: string,
    pass: string,
  ): Promise<Omit<User, 'hashedPassword'> | null> {
    // 1. 이름 암호화 (DB에 암호화된 이름으로 저장되어 있으므로)
    const encryptedUsername = encrypt(username); // 이전에 만든 암호화 유틸리티 사용

    // 2. 암호화된 이름으로 DB에서 사용자 조회
    // 🚨 주의: 유니크하지 않은 필드로 조회하면 오류가 발생할 수 있습니다.
    // 실제로는 email이나 username 같은 유니크한 필드를 사용해야 합니다.
    const user = await this.usersRepository.findOne({
      where: { name: encryptedUsername },
      // DB에서 hashedPassword 필드를 가져와야 비교가 가능합니다.
      select: ['id', 'name', 'hashedPassword', 'createdAt', 'updatedAt'],
    });

    if (!user) {
      return null; // 사용자 없음
    }

    // 3. 비밀번호 비교
    const isPasswordValid = await bcrypt.compare(pass, user.hashedPassword);

    if (!isPasswordValid) {
      return null; // 비밀번호 불일치
    }

    // 4. 보안: 반환 객체에서 hashedPassword 필드 제거
    // TypeORM의 toJSON() 메서드나 아래와 같이 객체 분해 할당을 사용할 수 있습니다.
    const { hashedPassword, ...result } = user;

    // 5. 복호화된 이름으로 사용자 객체 반환
    // 필요하다면 반환 직전에 이름을 복호화하여 클라이언트에게 전달합니다.
    result.name = decrypt(result.name);

    return result;
  }
  /**
   * 새로운 사용자를 생성하고 저장합니다.
   * - 비밀번호를 해싱합니다.
   * - 이름을 암호화합니다.
   * @param userDTO 사용자의 정보 (비밀번호와 이름 포함)
   */
  async create(userDTO: CreateUserDto): Promise<User> {
    const { name, password } = userDTO;

    // 1. 비밀번호 해싱
    // DTO의 password를 사용하여 엔티티 필드인 hashedPassword를 만듭니다.
    const hashedPassword = await bcrypt.hash(password, SALT_ROUNDS);

    // 2. 이름 암호화
    // 암호화 유틸리티를 사용하여 이름(name)을 암호화합니다.
    const encryptedName = encrypt(name);

    // 3. User 엔티티 생성에 필요한 최종 데이터 객체 준비
    // DTO에 없는 필드(id, createdAt 등)는 repository.create가 무시합니다.
    const userToSave = {
      name: encryptedName, // 엔티티 필드명에 맞게 암호화된 값 할당
      hashedPassword: hashedPassword, // 엔티티 필드명에 맞게 해시된 값 할당
      // 다른 필드가 있다면 여기에 추가합니다 (예: email 등)
    };

    // 3. User 엔티티 생성
    const newUser = this.usersRepository.create(userToSave);

    // 4. DB에 저장
    return this.usersRepository.save(newUser);
  }

  async remove(id: number): Promise<void> {
    await this.usersRepository.delete(id);
  }
}
