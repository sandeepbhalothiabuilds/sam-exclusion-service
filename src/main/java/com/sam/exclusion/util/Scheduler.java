package com.sam.exclusion.util;

import com.sam.exclusion.service.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class Scheduler {

    @Autowired
    CSVParser csvParser;

    @Scheduled(cron = "0 16 17 * * ?") // Run at 3:15 PM every day
    public void myTask() throws IOException {
        // Your task logic here
        System.out.println("Task executed at 3:15 PM!");
        downloadFile();
    }


    public void downloadFile() throws IOException {

        deleteFiles("data/files");

        Date date = new Date();
        // Format the date into Julian format (YYDDD)
        SimpleDateFormat julianFormat = new SimpleDateFormat("yyDDD");
        String julianDate = julianFormat.format(date);
        System.out.println("Julian date: " + julianDate);
        String zipFileName = "SAM_Exclusions_Public_Extract_V2_"+julianDate+".ZIP";
        String url = "https://sam.gov/api/prod/fileextractservices/v1/api/download/Exclusions/Public%20V2/SAM_Exclusions_Public_Extract_V2_" + julianDate + ".ZIP?privacy=Public";
        String path = "data/files/"+zipFileName;
        downloadZipFile(url, path);
        //downloadZipFile2(url , path);
        System.out.println("File Downloaded at path: " + path);

        File file = new File(path);

        FileInputStream inputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(
                file.getName(),
                file.getName(),
                "application/octet-stream",
                inputStream
        );

        unzip(multipartFile, "data/files/");

        //send the file for processing.
          File finalFile = new File("data/files/"+zipFileName.replace("ZIP", "CSV"));
          csvParser.extractExcelData(finalFile);
    }

    private void downloadZipFile(String url, String path) throws IOException {
        URL zipurl = new URL(url);
        try (ReadableByteChannel readableByteChannel = Channels.newChannel(zipurl.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(path)) {

            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
    }

    public void unzip(MultipartFile multipartFile, String destDir) throws IOException {
        File tempFile = File.createTempFile("temp", ".zip");
        multipartFile.transferTo(tempFile);

        try (ZipFile zipFile = new ZipFile(tempFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(destDir, entry.getName());

                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    try (InputStream in = zipFile.getInputStream(entry);
                         FileOutputStream out = new FileOutputStream(entryDestination)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error :" + e);
        } finally {
            tempFile.delete();
        }
    }

    private static void deleteFiles(String directoryPath) throws IOException {
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            System.out.println("Directory does not exist: " + directoryPath);
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            System.out.println("No files found in directory: " + directoryPath);
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                if (!file.delete()) {
                    System.err.println("Failed to delete file: " + file.getAbsolutePath());
                }
            }
        }
    }

    /*private void downloadZipFile2(String url, String path) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println("Error: "+e);
        }
    }*/

}

