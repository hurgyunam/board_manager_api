// src/common/utils/encryption.util.ts

import * as crypto from 'crypto';

// 환경 변수에서 키와 IV를 가져옵니다.
// 실제 환경에서는 설정 파일을 통해 안전하게 불러와야 합니다.
// (여기서는 NestJS 환경을 가정하고 process.env를 사용했습니다.)

// 32바이트 (256비트) 키
const ENCRYPTION_KEY = process.env.AES_ENCRYPTION_KEY;

// 16바이트 (128비트) 초기화 벡터
const IV = process.env.AES_IV;

// 사용할 암호화 알고리즘
const ALGORITHM = 'aes-256-cbc';

/**
 * 데이터를 암호화합니다.
 * @param text 암호화할 원본 문자열
 * @returns 암호화된 문자열 (Hex 형식)
 */
export function encrypt(text: string): string {
  if (!ENCRYPTION_KEY || !IV) {
    throw new Error('Encryption key or IV not set in environment variables.');
  }

  const cipher = crypto.createCipheriv(
    ALGORITHM,
    Buffer.from(ENCRYPTION_KEY, 'utf8'),
    Buffer.from(IV, 'utf8'),
  );

  let encrypted = cipher.update(text, 'utf8', 'hex');
  encrypted += cipher.final('hex');

  // IV를 암호화된 데이터와 함께 저장하지 않고 환경 변수를 사용하도록 단순화했습니다.
  // 실제로는 암호문 앞에 IV를 붙여 저장하는 방식도 많이 사용됩니다.
  return encrypted;
}

/**
 * 암호화된 데이터를 복호화합니다.
 * @param encryptedText 복호화할 암호화된 문자열 (Hex 형식)
 * @returns 복호화된 원본 문자열
 */
export function decrypt(encryptedText: string): string {
  if (!ENCRYPTION_KEY || !IV) {
    throw new Error('Encryption key or IV not set in environment variables.');
  }

  const decipher = crypto.createDecipheriv(
    ALGORITHM,
    Buffer.from(ENCRYPTION_KEY, 'utf8'),
    Buffer.from(IV, 'utf8'),
  );

  try {
    let decrypted = decipher.update(encryptedText, 'hex', 'utf8');
    decrypted += decipher.final('utf8');
    return decrypted;
  } catch (error) {
    // 복호화 실패 시 (예: 잘못된 키, IV, 또는 암호문 변조)
    console.error('Decryption failed:', error);
    throw new Error('Invalid encrypted data.');
  }
}
