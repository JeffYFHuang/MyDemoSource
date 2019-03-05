package com.liteon.icgwearable.security;

import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Logger;

public final class DataCompression {
	private static final Logger logger = Logger.getLogger(DataCompression.class);

	/*public static void main(String[] arg) throws Exception {
		String plainText = "Walking on water and developing software from a specification are easy if both are frozen. Do you gree?";

		byte[] compressedData = compress(plainText);
		decompress(compressedData);
	}*/

	public static byte[] compress(String plainText) throws Exception {
		byte[] output = null;
		if (plainText == null || plainText.length() == 0) {
			return output;
		}
		try {
			byte[] data = plainText.getBytes("UTF-8");
			output = new byte[100];
			// Compresses the data
			Deflater compresser = new Deflater();
			compresser.setInput(data);
			compresser.finish();
			int bytesAfterdeflate = compresser.deflate(output);

			String compressOP = "Compressed byte number:" + bytesAfterdeflate;
			logger.debug(compressOP);
			logger.info(compressOP);
		} catch (UnsupportedEncodingException uee) {
			logger.error("\n UnsupportedEncodingException ", uee);
			logger.error(" UnsupportedEncodingException " + uee);
			throw (uee);
		}
		return output;
	}

	public static String decompress(byte[] compressedData) throws Exception {
		String decompressText = "";
		if (compressedData == null) {
			return decompressText;
		}
		try {
			// Decompresses the data
			int bytesAfterdeflate = compressedData.length;
			Inflater decompresser = new Inflater();
			decompresser.setInput(compressedData, 0, bytesAfterdeflate);
			byte[] result = new byte[100];
			int resultLength = decompresser.inflate(result);
			decompresser.end();
			decompressText = new String(result, 0, resultLength, "UTF-8");

			String decompressOP = "Decompressed data: " + decompressText;
			logger.debug(decompressOP);
			logger.info(decompressOP);
		} catch (DataFormatException dfe) {
			logger.error("\n DataFormatException ", dfe);
			logger.error(" DataFormatException " + dfe);
			throw (dfe);
		}
		return decompressText;
	}
}