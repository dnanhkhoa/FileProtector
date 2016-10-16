package core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import algorithm.BaseEncryptionAlgorithm;
import enums.AlgorithmEnum;
import enums.ModeOfOperationEnum;
import enums.PaddingModeEnum;
import exception.ExceptionInfo;
import structure.ObservableModel;
import structure.ProgressInfo;

public final class Processor {

    private final static String                        SIGNATURE  = "DKENC";
    private final static int                           BLOCK_SIZE = 512;

    private final Map<String, BaseEncryptionAlgorithm> algorithmMap;
    private final ProgressInfo                         progressInfo;
    private final ObservableModel                      observable;

    public Processor() {
        this.algorithmMap = new HashMap<>();
        this.progressInfo = new ProgressInfo();
        this.observable = new ObservableModel();
    }

    public void registerObserver(Observer observer) {
        this.observable.addObserver(observer);
    }

    public void registerAlgorithm(BaseEncryptionAlgorithm algorithm) {
        algorithmMap.put(algorithm.getName(), algorithm);
    }

    protected void encryptFile(DataInputStream dataInputStream, DataOutputStream dataOutputStream, String password,
            AlgorithmEnum algorithm, ModeOfOperationEnum modeOfOperation, PaddingModeEnum paddingMode)
            throws ExceptionInfo, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException {

        BaseEncryptionAlgorithm baseEncryptionAlgorithm = algorithmMap.get(algorithm.toString());
        if (baseEncryptionAlgorithm == null) {
            throw new ExceptionInfo("Algorithm does not exist!");
        }

        Cipher cipher = baseEncryptionAlgorithm.buildCipher(password.getBytes("UTF-8"), modeOfOperation, paddingMode,
                null, true);

        dataOutputStream.write(SIGNATURE.getBytes());

        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("alg", algorithm);
        headerMap.put("moo", modeOfOperation);
        headerMap.put("pm", paddingMode);
        headerMap.put("iv", cipher.getIV());
        headerMap.put("np", progressInfo.getTotal());

        byte[] headerData = null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                objectOutputStream.writeObject(headerMap);
                objectOutputStream.flush();
                headerData = byteArrayOutputStream.toByteArray();
            }
        }

        if (headerData == null) {
            throw new ExceptionInfo("Can not encrypt this file!");
        }

        dataOutputStream.writeInt(headerData.length);
        dataOutputStream.write(headerData);

        long startTime = System.nanoTime();

        byte[] data = new byte[BLOCK_SIZE];
        int bytesRead;
        while ((bytesRead = dataInputStream.read(data)) == BLOCK_SIZE) {
            byte[] encryptedData = cipher.doFinal(data);
            dataOutputStream.writeInt(encryptedData.length);
            dataOutputStream.write(encryptedData);

            progressInfo.setSecondLeft((System.nanoTime() - startTime)
                    * (progressInfo.getTotal() - progressInfo.getCurrent()) / 1000000000);
            progressInfo.setCurrent(progressInfo.getCurrent() + 1);

            observable.setChanged();
            observable.notifyObservers(progressInfo);

            startTime = System.nanoTime();
        }
        if (bytesRead > 0) {
            byte[] encryptedData = cipher.doFinal(Arrays.copyOf(data, bytesRead));
            dataOutputStream.writeInt(encryptedData.length);
            dataOutputStream.write(encryptedData);

            progressInfo.setSecondLeft(0);
            progressInfo.setCurrent(progressInfo.getTotal());

            observable.setChanged();
            observable.notifyObservers(progressInfo);
        }
    }

    protected void decryptFile(DataInputStream dataInputStream, DataOutputStream dataOutputStream, String password,
            Map<String, Object> headerMap)
            throws ExceptionInfo, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {

        AlgorithmEnum algorithm = (AlgorithmEnum) headerMap.get("alg");
        ModeOfOperationEnum modeOfOperation = (ModeOfOperationEnum) headerMap.get("moo");
        PaddingModeEnum paddingMode = (PaddingModeEnum) headerMap.get("pm");

        byte[] iv = (byte[]) headerMap.get("iv");

        BaseEncryptionAlgorithm baseEncryptionAlgorithm = algorithmMap.get(algorithm.toString());
        if (baseEncryptionAlgorithm == null) {
            throw new ExceptionInfo("Algorithm does not exist!");
        }

        Cipher cipher = baseEncryptionAlgorithm.buildCipher(password.getBytes("UTF-8"), modeOfOperation, paddingMode,
                iv, false);

        try {
            long startTime = System.nanoTime();

            int dataLength = dataInputStream.readInt();
            byte[] data = new byte[dataLength];
            while (true) {
                dataInputStream.read(data);
                dataOutputStream.write(cipher.doFinal(data));

                progressInfo.setSecondLeft((System.nanoTime() - startTime)
                        * (progressInfo.getTotal() - progressInfo.getCurrent()) / 1000000000);
                progressInfo.setCurrent(progressInfo.getCurrent() + 1);

                observable.setChanged();
                observable.notifyObservers(progressInfo);

                dataLength = dataInputStream.readInt();
                data = new byte[dataLength];

                startTime = System.nanoTime();
            }
        } catch (EOFException e) {
        }

    }

    public void process(File inFile, File outFile, String password, AlgorithmEnum algorithm,
            ModeOfOperationEnum modeOfOperation, PaddingModeEnum paddingMode) throws FileNotFoundException, IOException,
            InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            ExceptionInfo, IllegalBlockSizeException, BadPaddingException {

        boolean isEncryptedFile = false;
        Map<String, Object> headerMap = null;
        int skipLength = SIGNATURE.length() + 4;

        try (InputStream inputStream = new FileInputStream(inFile)) {
            try (DataInputStream dataInputStream = new DataInputStream(inputStream)) {
                byte[] signatureData = new byte[SIGNATURE.length()];

                if (dataInputStream.read(signatureData) == SIGNATURE.length()
                        && SIGNATURE.equals(new String(signatureData))) {

                    int headerSize = dataInputStream.readInt();
                    skipLength += headerSize;

                    byte[] headerData = new byte[headerSize];

                    if (dataInputStream.read(headerData) == headerSize) {
                        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(headerData)) {
                            try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                                headerMap = (Map<String, Object>) objectInputStream.readObject();
                                isEncryptedFile = true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }

        try (InputStream inputStream = new FileInputStream(inFile)) {
            try (DataInputStream dataInputStream = new DataInputStream(inputStream)) {
                try (OutputStream outputStream = new FileOutputStream(outFile)) {
                    try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
                        if (isEncryptedFile) {
                            progressInfo.reset();
                            progressInfo.setTotal((long) headerMap.get("np"));
                            observable.setChanged();
                            observable.notifyObservers(progressInfo);

                            dataInputStream.skip(skipLength);
                            decryptFile(dataInputStream, dataOutputStream, password, headerMap);
                        } else {
                            progressInfo.reset();
                            progressInfo.setTotal((inFile.length() - 1) / BLOCK_SIZE + 1);
                            observable.setChanged();
                            observable.notifyObservers(progressInfo);

                            encryptFile(dataInputStream, dataOutputStream, password, algorithm, modeOfOperation,
                                    paddingMode);
                        }
                    }
                } catch (Exception e) {
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    throw e;
                }
            }
        }
    }

    public boolean isEncryptedFile(File inFile) {
        try (InputStream inputStream = new FileInputStream(inFile)) {
            try (DataInputStream dataInputStream = new DataInputStream(inputStream)) {
                byte[] signatureData = new byte[SIGNATURE.length()];
                if (dataInputStream.read(signatureData) == SIGNATURE.length()
                        && SIGNATURE.equals(new String(signatureData))) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

}
