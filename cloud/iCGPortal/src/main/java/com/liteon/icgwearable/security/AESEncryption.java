package com.liteon.icgwearable.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

public class AESEncryption {
	private static final Logger logger = Logger.getLogger(DataEncryption.class);
	// Initialize a 16 character (128 bit) secret key for the encryption.
	private static byte[] key = { 0x2d, 0x2a, 0x2d, 0x42, 0x55, 0x49, 0x4c, 0x44, 0x41, 0x43, 0x4f, 0x44, 0x45, 0x2d,
			0x2a, 0x2d };

	public static String encrypt(String plainText) throws Exception {
		String encryptedString = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF8"));
			encryptedString = new String(Base64.getEncoder().encode(cipherText), "UTF-8");
		} catch (NoSuchAlgorithmException noSuchAlgo) {
			logger.error("\n No Such Algorithm exists: ", noSuchAlgo);
			throw (noSuchAlgo);
		} catch (NoSuchPaddingException noSuchPad) {
			logger.error("\n No Such Padding exists: ", noSuchPad);
			throw (noSuchPad);
		} catch (InvalidKeyException invalidKey) {
			logger.error("\n Invalid Key: ", invalidKey);
			throw (invalidKey);
		} catch (BadPaddingException badPadding) {
			logger.error("\n Bad Padding: ", badPadding);
			throw (badPadding);
		} catch (IllegalBlockSizeException illegalBlockSize) {
			logger.error("\n Illegal Block Size: ", illegalBlockSize);
			throw (illegalBlockSize);
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			logger.error("\n UnSupported Encoding: ", unsupportedEncodingException);
			throw (unsupportedEncodingException);
		}
		return encryptedString;
	}

	public static String decrypt(String encryptedText) throws Exception {
		String decryptedString = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] cipherText = Base64.getDecoder().decode(encryptedText.getBytes("UTF8"));
			decryptedString = new String(cipher.doFinal(cipherText), "UTF-8");
		} catch (IllegalArgumentException illegalArgument) {
			logger.error("\n Illegal Argument: ", illegalArgument);
			throw (illegalArgument);
		} catch (NoSuchAlgorithmException noSuchAlgo) {
			logger.error("\n No Such Algorithm exists: ", noSuchAlgo);
			throw (noSuchAlgo);
		} catch (NoSuchPaddingException noSuchPad) {
			logger.error("\n No Such Padding exists: ", noSuchPad);
			throw (noSuchPad);
		} catch (BadPaddingException badPadding) {
			logger.error("\n Bad Padding: ", badPadding);
			throw (badPadding);
		} catch (IllegalBlockSizeException illegalBlockSize) {
			logger.error("\n Illegal Block Size: ", illegalBlockSize);
			throw (illegalBlockSize);
		}
		return decryptedString;
	}
	
	/*
	 * Password security using PBKDF2WithHmacSHA1 algorithm
	 */
	public static String generatePasswordHash(String password)
			throws Exception {
		String passwordHash = null;
		try{
			int iterations = 1000;
			char[] chars = password.toCharArray();
			byte[] salt = getSalt();

			PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = skf.generateSecret(spec).getEncoded();
			
			passwordHash = iterations + ":" + toHex(salt) + ":" + toHex(hash);
		} catch (NoSuchAlgorithmException noSuchAlgo) {
			logger.error("\n No Such Algorithm exists: ", noSuchAlgo);
			throw (noSuchAlgo);
		} catch (InvalidKeySpecException invalidKeySpecException) {
			logger.error("\n Invalid Key Spec Exception: ", invalidKeySpecException);
			throw (invalidKeySpecException);
		}
		
		return passwordHash;
	}

	private static byte[] getSalt() throws Exception {
		byte[] salt = null;
		try{
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			salt = new byte[16];
			sr.nextBytes(salt);
		} catch (NoSuchAlgorithmException noSuchAlgo) {
			logger.error("\n No Such Algorithm exists: ", noSuchAlgo);
			throw (noSuchAlgo);
		}
		
		return salt;
	}

	private static String toHex(byte[] array) throws NoSuchAlgorithmException {
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length * 2) - hex.length();
		if (paddingLength > 0) {
			return String.format("%0" + paddingLength + "d", 0) + hex;
		} else {
			return hex;
		}
	}

	public static boolean validatePassword(String originalPassword, String storedPassword)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		String[] parts = storedPassword.split(":");
		int iterations = Integer.parseInt(parts[0]);
		byte[] salt = fromHex(parts[1]);
		byte[] hash = fromHex(parts[2]);
		
		if(originalPassword==null)
			return false;

		PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] testHash = skf.generateSecret(spec).getEncoded();

		int diff = hash.length ^ testHash.length;
		for (int i = 0; i < hash.length && i < testHash.length; i++) {
			diff |= hash[i] ^ testHash[i];
		}
		return diff == 0;
	}

	private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}
		public static void main(String args[]) {
		try {
			// Either use secret key defined above or use below method
			// Here we use SHA-1 to generate a hash from your key and trim the
			// result to 128 bit (16 bytes).

			String username = "bob@google.org";
			String password = "superadmin";
			String SALT2 = "deliciously salty";
			key = (SALT2 + username + password).getBytes("UTF-8");
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit

			// End of other method of defining key

			String encrypted = encrypt("Build AES-128 Bit Encryption");
			String decrypted = decrypt(encrypted);

			System.out.println("Encrypted String : " + encrypted);
			System.out.println("Decrypted String : " + decrypted);

			// One way password encryption

			String originalPassword = "password1";
			String generatedSecuredPasswordHash = generatePasswordHash(originalPassword);
			System.out.println("generatedSecuredPasswordHash:" + generatedSecuredPasswordHash);
			//System.out.println("generatedSecuredPasswordHash:" + generatedSecuredPasswordHash);
			boolean matched = validatePassword("password", "1000:19a040e53736a7a21a09bab16e576e59:9ed28c7810fac46b83bc5f96f07d9c9ac466637de97bb5339f2fd1bd7d1ec5f66ae37185fde63bf488cecc65eaf305f11f26da7f3677a713fcea389db885126a");
			System.out.println("validatePassword response: " + matched);

			matched = validatePassword("password1", generatedSecuredPasswordHash);
			System.out.println("validatePassword response: " + matched);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}