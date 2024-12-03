/**
 * Copyright (C) 2016, GIAYBAC
 * <p>
 * Released under the MIT license
 */
package com.nyc.properties.service;

import com.nyc.properties.entity.Table;
import com.google.common.primitives.Ints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PDFParser {

    @Autowired
    NycRcuListingsService nycRcuListingsService;

    public void extractTables(File file) {
        try {
            String filePath = file.getName().substring(0, file.getName().indexOf(".pdf"));
            String out = System.getProperty("user.dir")+"\\_Docs\\result\\"+filePath+".html";

            Map<String, String> boroughAndIdMap = getBoroughNameAndCode(filePath);

            PDFTableExtractor extractor = (new PDFTableExtractor())
                    .setSource(file);

            List<Integer> exceptLineIdxes = Arrays.asList(0, -1);

            extractor.exceptLine(Ints.toArray(exceptLineIdxes));

            List<Table> tables = extractor.extract();
            nycRcuListingsService.persistNycRcuRecord(tables, boroughAndIdMap);
            Writer writer = new OutputStreamWriter(new FileOutputStream(out), "UTF-8");
            try {
                for (Table table : tables) {
                    writer.write("Page: " + (table.getPageIdx() + 1) + "\n");
                    writer.write(table.toHtml());


                }

            } finally {
                try {
                    writer.close();
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }


    }

    private Map<String, String> getBoroughNameAndCode(String filePath) {
        Map<String, String> boroughAndIdMap = new HashMap<>();
        if(filePath.toLowerCase().contains("manhattan")){
            boroughAndIdMap.put("MN", "10");
        } else if (filePath.toLowerCase().contains("brooklyn")) {
            boroughAndIdMap.put("BK", "30");
        } else if (filePath.toLowerCase().contains("bronx")) {
            boroughAndIdMap.put("BX", "20");
        }  else if (filePath.toLowerCase().contains("queens")) {
            boroughAndIdMap.put("QN", "40");
        } else if (filePath.toLowerCase().contains("staten-island")) {
            boroughAndIdMap.put("SI", "50");
        }
        return boroughAndIdMap;
    }


}
