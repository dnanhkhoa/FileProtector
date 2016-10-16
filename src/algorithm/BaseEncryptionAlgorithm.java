package algorithm;

import java.util.Map;

import enums.ModeOfOperationEnum;
import enums.PaddingModeEnum;

public abstract class BaseEncryptionAlgorithm {

    public BaseEncryptionAlgorithm() {
        // TODO Auto-generated constructor stub
    }

    public final String getName() {
        return getClass().getSimpleName();
    }

    public abstract Map<String, Object> buildConfig(byte[] key, ModeOfOperationEnum modeOfOperation,
            PaddingModeEnum paddingMode);

    public abstract byte[] encrypt(byte[] data);

    public abstract byte[] decrypt(byte[] data);

}
