package com.taulia.demo.csvparser.util;

import com.taulia.demo.csvparser.domain.CsvConstants;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

public class CsvUtil {
    public static boolean hasCSVFormat(MultipartFile file) {
        return CsvConstants.TYPE.equals(file.getContentType());
    }
    public static Map<String, List<CSVRecord>> parseCSV(MultipartFile inputFile) throws IOException {
        try (InputStream inputStream = inputFile.getInputStream();
             Reader in = new InputStreamReader(inputStream);
             CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in)) {

            Map<String, List<CSVRecord>> recordsByBuyer = new LinkedHashMap<>();

            for (CSVRecord record : parser) {
                String buyer = record.get("buyer");
                recordsByBuyer.computeIfAbsent(buyer, k -> new ArrayList<>()).add(record);
            }
            return recordsByBuyer;
        }
    }

    public static  String[] getHeader(MultipartFile inputFile) throws IOException {
        try (InputStream inputStream = inputFile.getInputStream();
                Reader in = new InputStreamReader(inputStream);
             CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in)) {
           return parser.getHeaderMap().keySet().toArray(new String[0]);
        }
    }
}
