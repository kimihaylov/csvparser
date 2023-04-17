package com.taulia.demo.csvparser.controller;

import com.taulia.demo.csvparser.domain.CsvConstants;
import com.taulia.demo.csvparser.service.CsvService;
import com.taulia.demo.csvparser.service.XmlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CsvController.class)
class CsvControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private XmlService xmlService;

    @MockBean
    private CsvService csvService;

    @Test
    void testUploadFile_XML() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", CsvConstants.TYPE, "buyer,field1,field2\nBuyer1,value11,value12\nBuyer2,value21,value22".getBytes());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/api/csv/upload").file(file).param("operation-type", CsvConstants.OutputFormat.XML.name()))
                .andExpect(status().isOk());
    }
    @Test
    void testUploadFile_CSV() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", CsvConstants.TYPE, "buyer,field1,field2\nBuyer1,value11,value12\nBuyer2,value21,value22".getBytes());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/api/csv/upload").file(file).param("operation-type", CsvConstants.OutputFormat.CSV.name()))
                .andExpect(status().isOk());
    }
    @Test
    void testUploadFile_InvalidFileFormat() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, "buyer,field1,field2\nBuyer1,value11,value12\nBuyer2,value21,value22".getBytes());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/api/csv/upload").file(file).param("operation-type", CsvConstants.OutputFormat.CSV.name()))
                .andExpect(status().isBadRequest());
    }


}