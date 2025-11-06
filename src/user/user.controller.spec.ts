import { Test, TestingModule } from '@nestjs/testing';
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { CreateUserDto } from './dto/create-user.dto';
import { HttpStatus } from '@nestjs/common';

// 1. Mock UserService 구현
const mockUserService = {
  create: jest.fn(),
};

describe('UserController', () => {
  let controller: UserController;
  let service: UserService;

  beforeEach(async () => {
    // 2. TestingModule 구성
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UserController],
      providers: [
        // 실제 UserService 대신 Mock 객체를 사용하도록 설정
        {
          provide: UserService,
          useValue: mockUserService,
        },
      ],
    }).compile();

    controller = module.get<UserController>(UserController);
    service = module.get<UserService>(UserService);
  });

  // 3. 테스트 시작
  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('registerUser', () => {
    it('should successfully register a user and return the ID', async () => {
      // 테스트에 사용할 입력 및 기대 출력 정의
      const createUserDto: CreateUserDto = {
        password: 'password123',
        name: 'Test User',
      };
      const mockUser = {
        id: 1,
        ...createUserDto,
      };

      // Mock 함수 설정: service.create가 호출되면 mockUser 객체를 반환하도록 설정
      (service.create as jest.Mock).mockResolvedValue(mockUser);

      // 컨트롤러 메서드 호출
      const result = await controller.registerUser(createUserDto);

      // 1. 서비스가 올바른 인수로 호출되었는지 검증
      expect(service.create).toHaveBeenCalledWith(createUserDto);

      // 2. 컨트롤러가 기대하는 형식과 값을 반환했는지 검증
      expect(result).toEqual({
        id: mockUser.id,
        message: 'User successfully registered',
      });
    });
  });
});
