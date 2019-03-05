import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class GenerateKey {
 
 
 public static String generateKeyHash(String schoolPhoneNum)
   throws Exception {
  String keyHash = null;
  try{
   int iterations = 1000;
   char[] chars = schoolPhoneNum.toCharArray();
   byte[] salt = getSalt();

   PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
   SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
   byte[] hash = skf.generateSecret(spec).getEncoded();
   
   keyHash = iterations + ":" + toHex(salt) + ":" + toHex(hash);
  } catch (NoSuchAlgorithmException noSuchAlgo) {
   System.out.println("\n No Such Algorithm exists: "+ noSuchAlgo);
   throw (noSuchAlgo);
  } catch (InvalidKeySpecException invalidKeySpecException) {
   System.out.println("\n Invalid Key Spec Exception: "+ invalidKeySpecException);
   throw (invalidKeySpecException);
  }
  
  return keyHash;
 }

 private static byte[] getSalt() throws Exception {
  byte[] salt = null;
  try{
   SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
   salt = new byte[16];
   sr.nextBytes(salt);
  } catch (NoSuchAlgorithmException noSuchAlgo) {
   System.out.println("\n No Such Algorithm exists: "+ noSuchAlgo);
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

 
 
 public static void main(String args[]) {
    
   String accesskey = null;
   
        //Creating Scanner instance to scan console for User input
        Scanner console = new Scanner(System.in);
    
        System.out.println("System is ready to accept input, please enter school phonenumber : ");
        String schoolPhoneNum = console.nextLine();
         System.out.println(" entered phone number is:"+  schoolPhoneNum);
      if( schoolPhoneNum.length() < 10){
        System.out.println(" enter proper phonenumber with 10 digits");
        schoolPhoneNum = console.nextLine();
       }
       
       try {
    accesskey = generateKeyHash(schoolPhoneNum);
    System.out.println(" Generated accesskey is=  " +accesskey);
   } catch (Exception e) {
    e.printStackTrace();
   }

 }

}
