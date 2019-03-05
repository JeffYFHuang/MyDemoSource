package com.liteon.icgwearable.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;

public final class DataEncryption {
	private static final Logger logger = Logger.getLogger(DataEncryption.class);

	/*public static void main(String[] args) throws Exception {
		String text = "Walking on water and developing software from a specification are easy if both are frozen. Do you gree?";
		String key = "LITEON";
		String encodedText = encrypt(text, key);
		decrypt(encodedText, key);
	}*/

	/**
	 * Following method is used to encrypt the string Step 1: Generate a DES key
	 * using defined KEY String Step 2: Build the initialization vector Step 3:
	 * Create a Cipher by specifying the following parameters a. Algorithm name
	 * - here it is DES b. Mode - here it is CBC c. Padding - PKCS5Padding Step
	 * 4: Initialize the Cipher for Encryption Step 5: Encrypt the Data 1.
	 * Convert the Input String to Bytes 2. Encrypt the bytes using doFinal
	 * method Step 6: Encode encrypted data to base64 for saving to database
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String encrypt(String plainText, String key) throws Exception {
		String cipherText = "";
		if (plainText == null || plainText.length() == 0) {
			return cipherText;
		}
		try {
			final MessageDigest md = MessageDigest.getInstance("md5");
			final byte[] digestOfPassword = md.digest(key.getBytes("utf-8"));
			final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 8);

			final SecretKey secretKey = new SecretKeySpec(keyBytes, "DES");
			final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
			final Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

			byte[] plainTextBytes = plainText.getBytes("utf-8");
			byte[] cipherByte = cipher.doFinal(plainTextBytes);

			cipherByte = BASE64EncoderStream.encode(cipherByte);

			cipherText = new String(cipherByte);

			String encodedOP = "Input text encrypted format to transfer over network: " + cipherText;
			logger.debug(encodedOP);
			logger.info(encodedOP);

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
		} catch (InvalidAlgorithmParameterException invalidParam) {
			logger.error("\n Invalid Parameter: ", invalidParam);
			throw (invalidParam);
		}

		return cipherText;
	}

	/**
	 * Following method is used to Decrypt the Data 1. Initialize the Cipher for
	 * Decryption 2. Decode with base64 to get bytes 3. Decrypt the cipher bytes
	 * using doFinal method
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String decrypt(String cipherText, String key) throws Exception {
		String decodedText = "";
		if (cipherText == null || cipherText.length() == 0) {
			return decodedText;
		}

		try {
			final MessageDigest md = MessageDigest.getInstance("md5");
			final byte[] digestOfPassword = md.digest(key.getBytes("utf-8"));
			final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 8);

			final SecretKey secretKey = new SecretKeySpec(keyBytes, "DES");
			final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
			final Cipher decipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			decipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

			byte[] dec = BASE64DecoderStream.decode(cipherText.getBytes());

			final byte[] plainText = decipher.doFinal(dec);
			decodedText = new String(plainText, "utf-8");

			String decodedOP = "Input text decrypted format with encrypted text received: " + decodedText;
			logger.debug(decodedOP);
			logger.info(decodedOP);
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

		return decodedText;
	}

}