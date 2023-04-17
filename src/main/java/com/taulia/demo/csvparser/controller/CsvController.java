package com.taulia.demo.csvparser.controller;

import com.taulia.demo.csvparser.domain.CsvConstants;
import com.taulia.demo.csvparser.service.CsvService;
import com.taulia.demo.csvparser.service.XmlService;
import com.taulia.demo.csvparser.util.CsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/api/csv")
public class CsvController {

    @Autowired
    CsvService csvService;

    @Autowired
    XmlService xmlService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("operation-type")CsvConstants.OutputFormat outputFormat) {
        String message = "";

        if (CsvUtil.hasCSVFormat(file)) {

            try {
                if(outputFormat == CsvConstants.OutputFormat.XML){
                    xmlService.writeXmlOutput(file);
                } else if (outputFormat == CsvConstants.OutputFormat.CSV) {
                    csvService.writeCsvOutput(file);
                }

                return new ResponseEntity<>(HttpStatus.OK);

            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
            }
        }

        message = "Please upload a csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

}