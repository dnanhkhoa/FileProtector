package structure;

public final class ProgressInfo {

    private int total;
    private int current;
    private int secondLeft;

    public ProgressInfo() {
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
        return total > 0 ? current * 100 / total : 0;
    }

    public String getTimeLeft() {
        int hours = secondLeft / 3600;
        int minutes = (secondLeft % 3600) / 60;
        int seconds = (secondLeft % 3600) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
