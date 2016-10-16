package algorithm;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import enums.ModeOfOperationEnum;
import enums.PaddingModeEnum;

public abstract class BaseEncryptionAlgorithm {

    public BaseEncryptionAlgorithm() {
        // TODO Auto-generated constructor stub
    }

    public final String getName() {
        return getClass().getSimpleName();
    }

    public abstract Cipher buildCipher(byte[] key, ModeOfOperationEnum modeOfOperation, PaddingModeEnum paddingMode,
            byte[] iv, boolean isEncryptMode) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException;

    public abstract byte[] doFinal(byte[] data, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException;

}
