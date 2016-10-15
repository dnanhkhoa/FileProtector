package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import algorithm.BaseEncryptionAlgorithm;
import structure.ProcessInfo;

public final class Processor {

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

    protected void encryptFile(File inFile, File outFile) throws FileNotFoundException, IOException {
        try (FileInputStream inFileStream = new FileInputStream(inFile)) {
            try (FileOutputStream outFileStream = new FileOutputStream(outFile)) {

            }
        }
    }

    protected void decryptFile(File inFile, File outFile) throws FileNotFoundException, IOException {
        try (FileInputStream inFileStream = new FileInputStream(inFile)) {
            try (FileOutputStream outFileStream = new FileOutputStream(outFile)) {

            }
        }
    }

    public void process(File inFile, File outFile) {

    }

}
