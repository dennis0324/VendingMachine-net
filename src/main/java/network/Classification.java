package network;

public class Classification {

    private String[] classification;

    Classification(String CmdData) {
        classification = CmdData.split("\\|");
    }

    public String toString() {
        return String.join("|", classification);
    }

    public void setValue(int index, String value) {
        classification[index] = value;
    }

    public String getValue(int index) {
        return classification[index];
    }
}
