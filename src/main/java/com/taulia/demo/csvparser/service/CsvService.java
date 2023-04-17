package com.taulia.demo.csvparser.service;

import com.taulia.demo.csvparser.util.CsvUtil;
import org.apache.commons.csv.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class CsvService {

    private static final Logger logger = LogManager.getLogger(CsvService.class);

    @Value("${csv.output.path}")
    private String outputPath;

    public void writeCsvOutput(MultipartFile inputFile) throws IOException {

        Map<String, List<CSVRecord>> records = CsvUtil.parseCSV(inputFile);
        String[] headers = CsvUtil.getHeader(inputFile);
        File outputDir = new File(outputPath);
        outputDir.mkdirs();

        for (String buyer : records.keySet()) {

            File outputFile = new File(outputDir, buyer + ".csv");
            try (Writer out = new FileWriter(outputFile);
                 CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers))) {
                for (CSVRecord record : records.get(buyer)) {
                    printer.printRecord(record.toMap().values());
                }
                logger.info("CSV output created: {}", outputFile);
            } catch (IOException e) {
                logger.error("Error writing CSV output for buyer: {}", buyer, e);
            }
        }
    }
}

