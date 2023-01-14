package bepo.textsafe.textsafe.util;

import javafx.collections.ObservableList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Serialization {
    private static final String cryptKey = "UEZEOEOR75";
    private static String dataKey = "";

    //All relevant paths
    private static final String savePath = "application-files/Data.ser";
    private static final String namePath = "application-files/Info.ser";
    private static final String authPath = "application-files/Auth.ser";
    private static final String tempPath = "application-files/Temp.ser";

    public static ArrayList<String> deserializeData() throws Exception {
        ArrayList<String> data = new ArrayList<>();
        ArrayList<byte[]> encrypted = new ArrayList<>();

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(savePath))) {
            encrypted = (ArrayList<byte[]>) in.readObject();
        } catch(Exception e) {
            return data;
        }

        if(dataKey != null && !dataKey.isEmpty()) {
            deserializeDataKey();
        }

        assert dataKey != null;
        for (byte[] byteArr : encrypted) {
            data.add(decrypt(byteArr, dataKey));
        }

        System.out.println("Data: Deserialize Complete");

        return data;
    }

    public static void serializeData(ObservableList<String> data) throws Exception {
        if(dataKey != null && !dataKey.isEmpty()) {
            deserializeDataKey();
        }

        assert dataKey != null;
        ArrayList<byte []> encrypted = new ArrayList<>();

        for (String text : data) {
            encrypted.add(encrypt(text, dataKey));
        }

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savePath))) {
            out.writeObject(encrypted);
        }

        System.out.println("Data: Serialize Complete");
    }

    public static ArrayList<String> deserializeName() throws Exception {
        ArrayList<String> name = new ArrayList<>();
        ArrayList<byte[]> encrypted = new ArrayList<>();

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(namePath))) {
            encrypted = (ArrayList<byte[]>) in.readObject();
        } catch(Exception e) {
            return name;
        }

        if(dataKey != null && !dataKey.isEmpty()) {
            deserializeDataKey();
        }

        assert dataKey != null;
        for (byte[] byteArr : encrypted) {
            name.add(decrypt(byteArr, dataKey));
        }

        System.out.println("Name: Deserialize Complete");

        return name;
    }

    public static void serializeName(ObservableList<String> name) throws Exception {
        if(dataKey != null && !dataKey.isEmpty()) {
            deserializeDataKey();
        }

        assert dataKey != null;
        ArrayList<byte []> encrypted = new ArrayList<>();

        for (String text : name) {
            encrypted.add(encrypt(text, dataKey));
        }

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(namePath))) {
            out.writeObject(encrypted);
        }

        System.out.println("Name: Serialize Complete");
    }

    public static String deserializePin() throws Exception {
        ArrayList<byte[]> encrypted;

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(authPath))) {
            encrypted = (ArrayList<byte[]>) in.readObject();
        } catch(Exception e) {
            return null;
        }

        String pin = decrypt(encrypted.get(0), cryptKey);
        dataKey = decrypt(encrypted.get(1), cryptKey);

        serializeDataKey();

        System.out.println("Pin: Deserialize Complete");

        return pin;
    }

    public static void serializePin(String pin) throws Exception {
        ArrayList<byte[]> encrypted = new ArrayList<>();
        encrypted.add(encrypt(pin, cryptKey));

        if(dataKey == null || dataKey.isEmpty()) {
            try {
                deserializeDataKey();
            } catch (Exception e) {
                System.err.println("No key saved " + e);
                keyGen();
            }

            serializeDataKey();
        }

         encrypted.add(encrypt(dataKey, cryptKey));

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(authPath))) {
            out.writeObject(encrypted);
        }

        System.out.println("Pin: Serialize Complete");
    }

    private static void keyGen() throws Exception {
        System.out.println("Entered Keygen because no key could be found");

        boolean valid = false;
        Random random = new Random();
        String alphabet = "0123456789QWERTZUIOPASDFGHJKLYXCVBNM";
        String key = "";

        while(!valid) {
            for (int i = 0; i <= 10; i++) {
                key += (alphabet.charAt(random.nextInt(alphabet.length())));
            }

            try {
                byte[] temp = encrypt("sampleData", key);
                decrypt(temp, key);
                valid = true;

            } catch(BadPaddingException e) {
                System.err.println("Generated key invalid, trying again...");
            }
        }

        dataKey = key;
    }

    private static void deserializeDataKey() throws IOException, ClassNotFoundException {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(tempPath))) {
            dataKey = (String) in.readObject();
            System.out.println("Read Data Key");
        } catch (EOFException e) {
            System.err.println("temp file is empty");
            throw new IOException();
        }
    }

    private static void serializeDataKey() {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempPath))) {
            out.writeObject(dataKey);
            System.out.println("Wrote Data Key");
        } catch(Exception a) {
            System.err.println(a);
        }
    }

    //Uses triple DES encryption to encrypt each String of data
    private static byte[] encrypt(String data, String keyToUse) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest(keyToUse.getBytes(StandardCharsets.UTF_8));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        final byte[] plainTextBytes = data.getBytes(StandardCharsets.UTF_8);

        return cipher.doFinal(plainTextBytes);
    }

    //Decrypts Triple DES encryption for each byte[] of data
    private static String decrypt(byte[] data, String keyToUse) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest(keyToUse.getBytes(StandardCharsets.UTF_8));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        decipher.init(Cipher.DECRYPT_MODE, key, iv);

        final byte[] plainText = decipher.doFinal(data);

        return new String(plainText, StandardCharsets.UTF_8);
    }
}