package com.taulia.demo.csvparser.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class XmlServiceTest {

    @InjectMocks
    private XmlService xmlService;

    @Test
    void testWriteXmlOutput(@TempDir Path tempDir) throws IOException {
        ReflectionTestUtils.setField(xmlService, "outputPath", tempDir.toString());

        String csvContent = "buyer,field1,field2,invoice_image,image_name\nBuyer1,value11,value12,,\nBuyer2,value21,value22,,";
        MockMultipartFile inputFile = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        xmlService.writeXmlOutput(inputFile);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        for (String buyer : List.of("Buyer1", "Buyer2")) {

            Path buyerDir = tempDir.resolve(buyer);
            assertTrue(Files.exists(buyerDir));

            Path invoiceImagesDir = buyerDir.resolve("invoice_images");
            assertTrue(Files.exists(invoiceImagesDir));

            Path xmlOutputFile = buyerDir.resolve(buyer + ".xml");
            assertTrue(Files.exists(xmlOutputFile));
            try {
                Document doc = dbFactory.newDocumentBuilder().parse(xmlOutputFile.toFile());
                NodeList invoiceNodes = doc.getElementsByTagName("invoice");
                assertEquals(1, invoiceNodes.getLength());

                Element invoice = (Element) invoiceNodes.item(0);
                assertEquals("value" + buyer.charAt(5) + "1", invoice.getElementsByTagName("field1").item(0).getTextContent());
                assertEquals("value" + buyer.charAt(5) + "2", invoice.getElementsByTagName("field2").item(0).getTextContent());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Test
    void testExtractAndSaveInvoiceImage(@TempDir Path tempDir) {
        String base64Image = "Base64EncodedImageData";
        String imageName = "test_invoice_image.png";
        File outputDir = tempDir.toFile();

        XmlService.extractAndSaveInvoiceImage(base64Image, imageName, outputDir);

        Path outputFile = tempDir.resolve(imageName);
        assertTrue(Files.exists(outputFile));
    }
}

