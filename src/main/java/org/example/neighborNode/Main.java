package org.example.neighborNode;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    ArrayList<Node> nodes = new ArrayList<>();
    Map<String,Set<String>> graph = new HashMap<>();

    public String[] getRawData(String path){
        String[] rawData = new String[20];
        String d1 = "M1,M2";
        String d2 = "M1,M3";
        String d3 = "M5,M7";
        String d4 = "M8,M9";
        String d5 = "M7,M8";
        String d6 = "M3,M8";
        String d7 = "M2,M5";
        String d8 = "M1,M7";
        String d9 = "M6,M7";
        String d10 = "M10,M12";
        String d11 = "M12,M13";
        String d12 = "M12,M10";
        String d13 = "M15,M16";
        String d14 = "M16,M17";
        String d15 = "M17,M15";

        rawData[0] = d1;
        rawData[1] = d2;
        rawData[2] = d3;
        rawData[3] = d4;
        rawData[4] = d5;
        rawData[5] = d6;
        rawData[6] = d7;
        rawData[7] = d8;
        rawData[8] = d9;
        rawData[9] = d10;
        rawData[10] = d11;
        rawData[11] = d12;
        rawData[12] = d13;
        rawData[13] = d14;
        rawData[14] = d15;
        return rawData;
    }

    public String getFilePath(String fileName){
        URL resource;
        String filePath = "";

        try{
            resource = getClass().getClassLoader().getResource(fileName);
            assert resource != null;
            filePath = resource.getPath();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return filePath;
    }

    public List<String> getFileContent(String fileName, boolean includeTitle){
        String filePath = getFilePath(fileName);

        List<String> content = new ArrayList<>();
        //try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
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

    public String[] getRawDataFromCSV(String path, boolean includeTitle){
        List<String> fileContent = getFileContent(path, includeTitle);

        String [] rawData = new String[fileContent.size()];

        int idx = 0;
        for(String line : fileContent){
            rawData[idx] = line;
            idx++;
        }
        return rawData;
    }

    public void makeData(String path){
        //String[] rawData = getRawData(path);
        String[] rawData = getRawDataFromCSV(path, false);

        for (String rawDatum : rawData) {
            if (rawDatum != null && !rawDatum.isEmpty()){
                String[] data = rawDatum.split(",", -1);
                if(data[8].equals("동일")){
                    Node node = new Node(data[0], data[3]);
                    nodes.add(node);
                }
            }
        }
    }

    public void dataSort(){
        nodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node s1, Node s2) {
                return s1.getStart().compareTo(s2.getStart());
                //return Integer.compare(s1.getStart(), s2.getStart());
            }
        });
    }

    public void nodeGroup(){
        boolean first = true;
        String start = "";
        for (Node node : nodes){
            if(first){
                first = false;
                Set<String> temp = new HashSet<>();

                start = node.getStart();
                temp.add(node.getEnd());
                graph.put(start, temp);
            }
            else{
                if(start.equals(node.getStart())){
                    graph.get(start).add(node.getEnd());
                }
                else {
                    Set<String> temp = new HashSet<>();

                    start = node.getStart();
                    temp.add(node.getEnd());
                    graph.put(start, temp);
                }
            }
        }
    }

    public void makeGraph(){
        makeGraphByKey();

        makeGraphByValue();

        graph.forEach((k, v) -> {
            System.out.println(k + ":" + v);
        });

        System.out.println("OUTPUT Count : " + graph.size());
    }

    public void makeGraphByKey(){
        Set<String> ks = graph.keySet();
        String[] keys = ks.toArray(new String[0]);

        int idx = 1;
        for(int i = keys.length-1; i > 0; i--){
            String key = keys[i];
            Set<String> comp = new HashSet<>();
            comp.add(key);

            idx = mergeNode(keys, idx, key, comp);
        }

        cleanEmptyNode();
    }

    public void makeGraphByValue(){
        Set<String> ks = graph.keySet();
        String[] keys = ks.toArray(new String[0]);

        int idx = 1;
        for(int i = keys.length-1; i > 0; i--){
            String key = keys[i];
            Set<String> comp = graph.get(key);

            idx = mergeNode(keys, idx, key, comp);
        }

        cleanEmptyNode();
    }

    private int mergeNode(String[] keys, int idx, String key, Set<String> comp) {
        for(int j = 0; j < keys.length-idx; j++){
            if(graph.get(keys[j]).containsAll(comp)){
                String[] temp = graph.get(key).toArray(
                        String[]::new
                );

                for(String s : temp){
                    graph.get(keys[j]).add(s);
                }

                graph.get(key).clear();
            };
        }
        idx++;

        return idx;
    }

    public void cleanEmptyNode(){
        ArrayList<String> rk = new ArrayList<>();

        graph.forEach((k, v) -> {
            if(graph.get(k).isEmpty()){
                rk.add(k);
            }
        });
        rk.forEach(
                k -> graph.remove(k)
        );
    }

    public void validate(){
        Set<String> keys = new HashSet<>();

        for (String key : graph.keySet()) {
            //Set<String> value = graph.get(key);
            keys.add(key);
        }
        System.out.println(keys.size());
    }

    public void writeFile(){
        List<String> lines = Arrays.asList("Line 1: Hello", "Line 2: World", "Line 3: Java Files");
        Path file = Paths.get("output.txt");

        try {
            // Writes all lines to the file, creating it if it doesn't exist.
            // If the file already exists, it will be overwritten by default.
            Files.write(file, lines);
            System.out.println("Successfully wrote list to file using Files.write()");
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run(){
        String path = "files/ingredient_subset.csv";
        makeData(path);

        dataSort();

        nodeGroup();

        makeGraph();

        //writeFile();

        validate();
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }
}
