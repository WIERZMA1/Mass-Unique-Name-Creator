package UniqueID;

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
            System.out.println("Failed to read. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String runSingle(String name, String company, String country, String code, List<String> excluded) {
        return createUniqueId(name, company, country, code, excluded);
    }

    private void read(File file, String company, List<String> excluded, boolean skipFirst,
                      int countryCol, int nameCol, int codeCol) throws Exception {
        BufferedReader csvReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8));
        BufferedWriter csvWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(createFilename(file)), StandardCharsets.UTF_8));
        String row;
        String[] data;
        String name;
        String country = "";
        String code = "";
//        int maxLength;
        int rowNum = 0;
        while ((row = csvReader.readLine()) != null) {
            row = removeBom(row);
            rowNum++;
            if (!skipFirst || rowNum > 1) {
                data = row.replaceAll(String.valueOf((char) 160), " ").split(",", -1);
                if (countryCol >= 0) {
                    country = data[countryCol - 1].trim().equals("") ? "" : data[countryCol - 1].trim().toLowerCase() + "-";
                }
                if (codeCol >= 0) {
                    code = data[codeCol - 1].trim().equals("") ? "" : "-" + data[codeCol - 1].trim().toLowerCase();
                }
                name = createUniqueId(data[nameCol - 1], company, data[countryCol - 1], data[codeCol - 1], excluded);
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

    private String createFilename(File file) {
        return file.getParent() + File.separator + file.getName().substring(0, file.getName().lastIndexOf('.')) + " output.csv";
    }

    private String createName(String name, int maxLength, List<String> excluded) {
        name = unaccent(name.toLowerCase().trim());
        for (String delete : excluded) {
            delete = delete.toLowerCase();
            name = name.startsWith(delete) ? name.substring(delete.length()) : name;
            name = name.endsWith(delete) ? name.substring(0, name.length() - delete.length()) : name;
        }
        name = name.replaceAll("[^\\p{L}\\p{Nd}]+", ""); // Leave only letters and digits
        return name.length() > maxLength ? name.substring(0, maxLength) : name;
    }

    private String createUniqueId(String name, String company, String country, String code, List<String> excluded) {
        company = company.trim().equals("") ? "" : company.trim().toLowerCase() + "-";
        country = country.trim().equals("") ? "" : country.trim().toLowerCase() + "-";
        code = code.trim().equals("") ? "" : "-" + code.trim().toLowerCase();
        int maxLength = 30 - (company.trim().length() + country.length() + code.length());
        return company + country + createName(name, maxLength, excluded) + code;
    }

    private String unaccent(String src) {
        return Normalizer
                .normalize(src, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    private String removeBom(String input) {
        return input.startsWith("\uFEFF") ? input.substring(1) : input;
    }
}