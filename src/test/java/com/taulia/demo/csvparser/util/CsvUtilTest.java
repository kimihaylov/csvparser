package com.taulia.demo.csvparser.util;

import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CsvUtilTest {

    @Test
    void testHasCSVFormat() {
        MockMultipartFile validCsvFile = new MockMultipartFile("file", "test.csv", "text/csv", "buyer,field1,field2\nBuyer1,value11,value12\nBuyer2,value21,value22".getBytes());
        MockMultipartFile invalidCsvFile = new MockMultipartFile("file", "test.txt", "text/plain", "buyer,field1,field2\nBuyer1,value11,value12\nBuyer2,value21,value22".getBytes());

        assertTrue(CsvUtil.hasCSVFormat(validCsvFile));
        assertFalse(CsvUtil.hasCSVFormat(invalidCsvFile));
    }

    @Test
    void testParseCSV() throws IOException {
        String csvContent = "buyer,field1,field2\nBuyer1,value11,value12\nBuyer2,value21,value22";
        MockMultipartFile inputFile = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        Map<String, List<CSVRecord>> recordsByBuyer = CsvUtil.parseCSV(inputFile);

        assertNotNull(recordsByBuyer);
        assertEquals(2, recordsByBuyer.size());

        CSVRecord buyer1Record = recordsByBuyer.get("Buyer1").get(0);
        CSVRecord buyer2Record = recordsByBuyer.get("Buyer2").get(0);

        assertEquals("Buyer1", buyer1Record.get("buyer"));
        assertEquals("value11", buyer1Record.get("field1"));
        assertEquals("value12", buyer1Record.get("field2"));

        assertEquals("Buyer2", buyer2Record.get("buyer"));
        assertEquals("value21", buyer2Record.get("field1"));
        assertEquals("value22", buyer2Record.get("field2"));
    }

    @Test
    void testGetHeader() throws IOException {
        String csvContent = "buyer,field1,field2\nBuyer1,value11,value12\nBuyer2,value21,value22";
        MockMultipartFile inputFile = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        String[] headers = CsvUtil.getHeader(inputFile);

        assertNotNull(headers);
        assertEquals(3, headers.length);
        assertArrayEquals(new String[]{"buyer", "field1", "field2"}, headers);
    }
}
