package RAF.KiDSDomaci1.model;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class DataForOutput {

    private Future<ConcurrentHashMap<String,Long>> wordOccurrences;
    private String filename;
    private boolean poison;


    public DataForOutput(Future<ConcurrentHashMap<String,Long>> wordOccurrences, String filename) {
        this.wordOccurrences = wordOccurrences;
        this.filename = filename;
        this.poison = false;

    }

    public DataForOutput(boolean poison) {
        this.poison = poison;
    }

    public Future<ConcurrentHashMap<String, Long>> getWordOccurrences() {
        return wordOccurrences;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isPoison() {
        return poison;
    }
}
