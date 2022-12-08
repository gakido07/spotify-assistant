package kara.spotifyassistant.security;

import kara.spotifyassistant.Models.EncryptedData;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

@Configuration
public class SecurityUtil {

  @Value("${spotify.client.id}")
  private String spotifyClientId;

  @Value("${spotify.client.secret}")
  private String spotifyClientSecret;
  @Value("${encryption.secret}")
  private String encryptionSecret;
  private final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";

  public PasswordEncoder BcryptEncoder(){
    return new BCryptPasswordEncoder();
  }

  public String extractClientIdFromRoute(String route) {
    return route.split("/")[1];
  }

  public String getEncodedAuthHeader() {
    return Base64.getEncoder().encodeToString((spotifyClientId + ":" + spotifyClientSecret).getBytes());
  }

  private SecretKey getKeyFromPassword(String password) throws Exception {
    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec spec = new PBEKeySpec(password.toCharArray(), "12345678".getBytes(), 65536, 256);
    return new SecretKeySpec(secretKeyFactory.generateSecret(spec)
        .getEncoded(), "AES");
  }

  public EncryptedData encrypt(String rawText) throws Exception {
    final String initVector = RandomStringUtils.random(16, true, false);
    Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
    cipher.init(Cipher.ENCRYPT_MODE, getKeyFromPassword(encryptionSecret), ivParameterSpec);
    byte[] cipherText = cipher.doFinal(rawText.getBytes());
    return new EncryptedData(initVector, Base64.getEncoder().encodeToString(cipherText));
  }

  public String decrypt(EncryptedData encryptedData) throws Exception {
    Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(encryptedData.getIv().getBytes(StandardCharsets.UTF_8));
    cipher.init(Cipher.DECRYPT_MODE, getKeyFromPassword(encryptionSecret), ivParameterSpec);
    byte[] rawtext = cipher.doFinal(Base64.getDecoder().decode(encryptedData.getData()));
    return new String(rawtext);
  }
}
