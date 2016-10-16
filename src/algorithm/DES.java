package algorithm;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import enums.ModeOfOperationEnum;
import enums.PaddingModeEnum;

public final class DES extends BaseEncryptionAlgorithm {

    public DES() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Cipher buildCipher(byte[] key, ModeOfOperationEnum modeOfOperation, PaddingModeEnum paddingMode, byte[] iv,
            boolean isEncryptMode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException {

        Cipher cipher = Cipher.getInstance(String.format("%s/%s/%s", getName(), modeOfOperation, paddingMode));

        if (iv == null) {
            iv = new byte[cipher.getBlockSize()];
            SecureRandom randomSecureRandom = new SecureRandom();
            randomSecureRandom.nextBytes(iv);
        }

        if (isEncryptMode) {
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, getName()), new IvParameterSpec(iv));
        } else {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, getName()), new IvParameterSpec(iv));
        }

        return cipher;
    }

    @Override
    public byte[] doFinal(byte[] data, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(data);
    }

}
