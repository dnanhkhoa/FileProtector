package structure;

public final class ProcessInfo {

    private int total;
    private int current;
    private int secondLeft;

    public ProcessInfo() {
        reset();
    }

    public void reset() {
        total = 0;
        current = 0;
        secondLeft = 0;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSecondLeft() {
        return secondLeft;
    }

    public void setSecondLeft(int secondLeft) {
        this.secondLeft = secondLeft;
    }

    public int getProgressValue() {
        return total > 0 ? current / total : 0;
    }

}
