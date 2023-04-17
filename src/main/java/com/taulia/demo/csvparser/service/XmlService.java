package com.taulia.demo.csvparser.service;

import com.taulia.demo.csvparser.util.CsvUtil;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class XmlService {

    private static final Logger logger = LogManager.getLogger(XmlService.class);

    @Value("${xml.output.path}")
    private String outputPath;

    public void writeXmlOutput(MultipartFile inputFile) throws IOException {


        Map<String, List<CSVRecord>> records = CsvUtil.parseCSV(inputFile);
        File outputDir = new File(outputPath);
        outputDir.mkdirs();

        for (String buyer : records.keySet()) {
            File buyerDir = new File(outputDir, buyer);
            buyerDir.mkdirs();

            File invoiceImagesDir = new File(buyerDir, "invoice_images");
            invoiceImagesDir.mkdirs();

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("invoices");
                doc.appendChild(rootElement);

                for (CSVRecord record : records.get(buyer)) {
                    Element invoice = doc.createElement("invoice");
                    rootElement.appendChild(invoice);

                    for (Map.Entry<String, String> entry : record.toMap().entrySet()) {
                        if (!entry.getKey().equals("invoice_image")) {
                            Element field = doc.createElement(entry.getKey());
                            field.appendChild(doc.createTextNode(entry.getValue()));
                            invoice.appendChild(field);
                        }
                    }

                    if (!record.get("invoice_image").isEmpty()) {
                        extractAndSaveInvoiceImage(record.get("invoice_image"), record.get("image_name"), invoiceImagesDir);
                    }
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                DOMSource source = new DOMSource(doc);

                File xmlOutputFile = new File(buyerDir, buyer + ".xml");
                StreamResult result = new StreamResult(xmlOutputFile);
                transformer.transform(source, result);

                logger.info("XML output created: {}", xmlOutputFile);
            } catch (ParserConfigurationException | TransformerException e) {
                logger.error("Error creating XML output for buyer: {}", buyer, e);
            }
        }
    }

    static void extractAndSaveInvoiceImage(String base64Image, String imageName, File outputDir) {
        try {

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            File outputFile = new File(outputDir, imageName);
            Files.write(outputFile.toPath(), imageBytes);

            logger.info("Invoice image extracted: {}", outputFile);
        } catch (IOException e) {
            logger.error("Error extracting and saving invoice image", e);
        }
    }
}
