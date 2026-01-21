package org.example.neighborNode;

import org.example.utils.FilesUtils;
import java.util.*;

public class Main {
    ArrayList<Node> nodes = new ArrayList<>();
    Map<String,Set<String>> graph = new HashMap<>();
    Map<String, String> ingredient = new HashMap<>();

    public String[] getRawDataFromCSV(String path, boolean includeTitle){
        FilesUtils filesUtils = new FilesUtils();
        List<String> fileContent = filesUtils.getFileContentByStream(path, includeTitle);

        String [] rawData = new String[fileContent.size()];

        int idx = 0;
        for(String line : fileContent){
            rawData[idx] = line;
            idx++;
        }
        return rawData;
    }

    public void makeData(String path){
        String[] rawData = getRawDataFromCSV(path, false);

        for (String rawDatum : rawData) {
            if (rawDatum != null && !rawDatum.isEmpty()){
                String[] data = rawDatum.split(",", -1);
                if(data[8].equals("동일")) {
                    Node node = new Node(data[0], data[3]);
                    nodes.add(node);

                    ingredient.put(data[0], data[1]);
                    ingredient.put(data[3], data[4]);
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
                temp.add(start);
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
                    temp.add(start);
                    temp.add(node.getEnd());
                    graph.put(start, temp);
                }
            }
        }
    }

    public void makeGraph(){
        Set<String> ks = graph.keySet();
        String[] keys = ks.toArray(new String[0]);

        for(int i =0; i < keys.length; i++){
            String key = keys[i];
            Set<String> comp = graph.get(key);

            for(int j = i+1; j < keys.length; j++){
                HashSet<String> intersection = new HashSet<>(graph.get(keys[j]));  // s1으로 intersection 생성
                intersection.retainAll(comp);

                if(!intersection.isEmpty()){
                    graph.get(keys[j]).addAll(comp);
                    graph.get(key).clear();
                }
            }
        }

        cleanEmptyNode();
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
        Set<String> keys = new HashSet<>(graph.keySet());
        System.out.println("Graph Size : " + keys.size());
    }

    public void run(){
        long startTime = System.currentTimeMillis();

        String path = "files/ingredient_subset.csv";
        makeData(path);

        dataSort();

        nodeGroup();

        makeGraph();

//        graph.forEach((k, v) -> {
//            System.out.println(k + ":" + v);
//        });

//        ingredient.forEach((k, v) -> {
//            System.out.println(k + ":" + v);
//        });
//        System.out.println("Ingredient Count : "+ ingredient.size()); // 649

        validate();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("실행 시간: " + duration + "ms");
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }
}
