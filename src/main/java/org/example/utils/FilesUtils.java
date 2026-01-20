package org.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FilesUtils {
    private static final Logger logger = LoggerFactory.getLogger(FilesUtils.class);

    public String getFilePath(String fileName){
        URL resource;
        String filePath = "";

        try {
            resource = getClass().getClassLoader().getResource(fileName);
            assert resource != null;
            filePath = resource.getFile();
        }
        catch (NullPointerException npe){
            logger.info(npe.toString());
        }

        return filePath;
    }

    public List<String> getFileContentByStream(String fileName, boolean includeTitle){
        String filePath = getFilePath(fileName);

        List<String> content = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "euc-kr"))) {
            String line;

            while ((line = br.readLine()) != null) {
                if(includeTitle){
                    content.add(line);
                }
                else{
                    includeTitle = true;
                }
            }
        }
        catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }

        return content;
    }

    public List<String> getFileContentByReader(String fileName, boolean includeTitle) {
        String filePath = getFilePath(fileName);

        List<String> fileContents = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (includeTitle) {
                    fileContents.add(line);
                }
                else {
                    includeTitle = true;
                }

            }
        } catch (IOException ioe) {
            logger.info(ioe.toString());
        }

        return fileContents;
    }
}
