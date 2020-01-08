package KeskoName;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.List;

public class ReadWriteCSV {

    public void run(File file, String company, boolean skipFirst,
                    int country, int name, int code, List<String> excluded) {
        try {
            read(file, company, excluded, skipFirst, country, name, code);
        } catch (Exception e) {
            System.out.println(e + "Failed to read.");
        }
    }

    private void read(File file, String company, List<String> excluded, boolean skipFirst,
                      int countryCol, int nameCol, int codeCol) throws Exception {
        BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        BufferedWriter csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                file.getParent() + File.separator + "Output " + file.getName()), StandardCharsets.UTF_8));
        String row;
        String[] data;
        String name;
        int maxLength;
        int rowNum = 0;
        while ((row = csvReader.readLine()) != null) {
            rowNum++;
            if (!skipFirst || rowNum > 1) {
                data = row.replaceAll(String.valueOf((char) 160), " ").split(",", -1);
                maxLength = 30 - (company.trim().length() + data[countryCol - 1].trim().length() + data[codeCol - 1].trim().length() + 3);
                name = company + "-"
                        + data[countryCol - 1].toLowerCase().trim() + "-"
                        + createName(data[nameCol - 1], maxLength, excluded) + "-"
                        + data[codeCol - 1].toLowerCase().trim();
                data[data.length - 1] = name;
                for (String cell : data) {
                    csvWriter.append(String.join(",", cell));
                    if (!cell.equals(name)) {
                        csvWriter.append(",");
                    }
                }
                csvWriter.append("\n");
            }

        }
        csvReader.close();
        csvWriter.flush();
        csvWriter.close();
    }

    private String createName(String name, int maxLength, List<String> excluded) {
        name = unaccent(name.toLowerCase().trim());
        for (String delete : excluded) {
            delete = delete.toLowerCase();
            name = name.startsWith(delete) ? name.substring(delete.length()) : name;
            name = name.endsWith(delete) ? name.substring(0, name.length() - delete.length()) : name;
        }

        name = name.replaceAll("[^\\p{L}\\p{Nd}]+", "");

        return name.length() > maxLength ? name.substring(0, maxLength) : name;
    }

    private String unaccent(String src) {
        return Normalizer
                .normalize(src, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
}