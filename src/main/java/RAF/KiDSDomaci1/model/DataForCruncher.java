package RAF.KiDSDomaci1.model;

public class DataForCruncher {
    private String filename;
    private String text;
    private boolean poison;
    private String[] splitText;

    public DataForCruncher(String filename, String text) {
        this.filename = filename;
        this.text = text;
        this.poison = false;
    }

    public DataForCruncher(String filename, String[] splitText) {
        this.filename = filename;
        this.splitText = splitText;
        this.poison = false;
    }

    public DataForCruncher(boolean poison) {
        this.filename = null;
        this.text = null;
        this.poison = true;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isPoison() {
        return poison;
    }

    public void setPoison(boolean poison) {
        this.poison = poison;
    }

    public String[] getSplitText() {
        return splitText;
    }
}
