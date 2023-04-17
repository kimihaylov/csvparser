package com.taulia.demo.csvparser.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CsvServiceTest {

    @InjectMocks
    private CsvService csvService;

    @Test
    void testWriteCsvOutput(@TempDir Path tempDir) throws IOException {
        ReflectionTestUtils.setField(csvService, "outputPath", tempDir.toString());

        String csvContent = "buyer,field1,field2\nBuyer1,value11,value12\nBuyer1,value111,value112\nBuyer2,value21,value22";
        MockMultipartFile inputFile = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        csvService.writeCsvOutput(inputFile);

        Path xmlOutputFile = tempDir.resolve("Buyer1" + ".csv");
        assertTrue(xmlOutputFile.toFile().exists());

        List<String> lines = Files.readAllLines(xmlOutputFile);
        assertEquals(3, lines.size());

        Path xmlOutputFile2 = tempDir.resolve("Buyer2" + ".csv");
        assertTrue(xmlOutputFile2.toFile().exists());

        List<String> lines2 = Files.readAllLines(xmlOutputFile2);
        assertEquals(2, lines2.size());
    }
}