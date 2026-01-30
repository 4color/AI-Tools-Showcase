import CryptoJS from 'crypto-js'

/**
 * 使用 SHA256 加密密码
 * @param password 原始密码
 * @returns 加密后的密码（十六进制字符串）
 */
export function encryptPassword(password: string): string {
  return CryptoJS.SHA256(password).toString(CryptoJS.enc.Hex)
}
