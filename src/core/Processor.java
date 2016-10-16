package core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import algorithm.BaseEncryptionAlgorithm;
import structure.HeaderInfo;
import structure.ProcessInfo;

public final class Processor {

    private final static String                        SIGNATURE  = "ENC";
    private final static int                           BLOCK_SIZE = 512;
    private final Map<String, BaseEncryptionAlgorithm> algorithmMap;
    private final ProcessInfo                          processInfo;
    private final Observable                           observable;

    public Processor() {
        this.algorithmMap = new HashMap<>();
        this.processInfo = new ProcessInfo();
        this.observable = new Observable();
    }

    public void registerObserver(Observer observer) {
        this.observable.addObserver(observer);
    }

    public void registerAlgorithm(BaseEncryptionAlgorithm algorithm) {
        this.algorithmMap.put(algorithm.getName(), algorithm);
    }

    public void encryptFile(File inFile, File outFile) {

    }

    public void decryptFile(File inFile, File outFile) {

    }

    public void process(File inFile, File outFile) throws FileNotFoundException, IOException {
        try (InputStream inputStream = new FileInputStream(inFile)) {
            try (DataInputStream dataInputStream = new DataInputStream(inputStream)) {
                // Code here

                /*
                 * try (OutputStream outputStream = new
                 * FileOutputStream(outFile)) { try (DataOutputStream
                 * dataOutputStream = new DataOutputStream(outputStream)) { //
                 * Code here } }
                 */
            }
        }
    }

    public boolean isEncryptedFile(File inFile) {
        try (InputStream inputStream = new FileInputStream(inFile)) {
            try (DataInputStream dataInputStream = new DataInputStream(inputStream)) {
                int headerSize = dataInputStream.readInt();
                byte[] headerData = new byte[headerSize];

                if (dataInputStream.read(headerData) != headerSize) {
                    return false;
                }

                try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(headerData)) {
                    try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                        HeaderInfo headerInfo = (HeaderInfo) objectInputStream.readObject();
                        if (!headerInfo.getSignature().equals(SIGNATURE)) {
                            return false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }
        return true;
    }

}
