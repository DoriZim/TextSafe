package bepo.textsafe.textsafe.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Serialization {
    private static final String cryptKey = "UEZEOEOR75";

    //All relevant paths
    private static final String savePath = "application-files/Data.ser";
    private static final String authPath = "application-files/Auth.ser";


    public static String deserializeData() throws Exception {
        String data = "";
        byte[] encrypted;

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(savePath))) {
            encrypted = (byte[]) in.readObject();
        } catch(Exception e) {
            return data;
        }

        data = decrypt(encrypted);

        System.out.println("Data: Deserialize Complete");

        return data;
    }

    public static void serializeData(String data) throws Exception {
        byte [] encrypted = encrypt(data);

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savePath))) {
            out.writeObject(encrypted);
        }

        System.out.println("Data: Serialize Complete");
    }

    public static String deserializePin() throws Exception {
        String pin = "";
        byte[] encrypted;

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(authPath))) {
            encrypted = (byte[]) in.readObject();
        } catch(Exception e) {
            return pin;
        }

        pin = decrypt(encrypted);

        System.out.println("Pin: Deserialize Complete");

        return pin;
    }

    public static void serializePin(String pin) throws Exception {
        byte [] encrypted = encrypt(pin);

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(authPath))) {
            out.writeObject(encrypted);
        }

        System.out.println("Pin: Serialize Complete");
    }


    //Uses triple DES encryption to encrypt each String of data
    private static byte[] encrypt(String data) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest(cryptKey.getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        final byte[] plainTextBytes = data.getBytes("utf-8");
        final byte[] cipherText = cipher.doFinal(plainTextBytes);

        return cipherText;
    }

    //Decrypts Triple DES encryption for each byte[] of data
    private static String decrypt(byte[] data) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest(cryptKey.getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        decipher.init(Cipher.DECRYPT_MODE, key, iv);

        final byte[] plainText = decipher.doFinal(data);

        return new String(plainText, "UTF-8");
    }
}