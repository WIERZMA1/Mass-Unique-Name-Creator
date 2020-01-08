package KeskoName;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadWriteExcluded {

    public static final String DATA_FILE = "Excluded.txt";

    public void write(String excluded) {
        List<String> input = Arrays.asList(excluded.split("\n"));
        try {
            Files.write(new File(DATA_FILE).toPath(), input);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<String> read() {
        List<String> output = new ArrayList<>();
        try {
            if (new File(DATA_FILE).isFile()) {
                output = Files.readAllLines(new File(DATA_FILE).toPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}