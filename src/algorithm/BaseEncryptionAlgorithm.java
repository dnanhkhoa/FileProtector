package algorithm;

public abstract class BaseEncryptionAlgorithm {

    private final String name;
    
    public BaseEncryptionAlgorithm() {
        this.name = "None";
    }

    public String getName() {
        return name;
    }
}
