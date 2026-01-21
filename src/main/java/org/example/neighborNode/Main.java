package org.example.neighborNode;

import org.example.utils.FilesUtils;
import java.util.*;

public class Main {
    ArrayList<RawNode> rawNodes = new ArrayList<>();
    Map<String,Set<NodeSet>> graph = new HashMap<>();
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
                    RawNode rawNode = new RawNode(data[0], data[1], data[3], data[4]);
                    rawNodes.add(rawNode);

                    ingredient.put(data[0], data[1]);
                    ingredient.put(data[3], data[4]);
                }
            }
        }
    }

    public void dataSort(){
        rawNodes.sort(new Comparator<RawNode>() {
            @Override
            public int compare(RawNode s1, RawNode s2) {
                return s1.getStart().compareTo(s2.getStart());
                //return Integer.compare(s1.getStart(), s2.getStart());
            }
        });
    }

    public void nodeGroup(){
        boolean first = true;
        String nodeKey = "";

        for (RawNode raw : rawNodes){
            if(first){
                first = false;
                nodeKey = raw.getStart();

                Set<NodeSet> temp = new HashSet<>();

                NodeSet start = new NodeSet();
                start.setCode(raw.getStart());
                start.setName(raw.getStartNM());

                NodeSet end = new NodeSet();
                end.setCode(raw.getEnd());
                end.setName(raw.getEndNM());

                temp.add(start);
                temp.add(end);

                graph.put(start.getCode(), temp);
            }
            else{
                if(nodeKey.equals(raw.getStart())){
                    NodeSet end = new NodeSet();
                    end.setCode(raw.getEnd());
                    end.setName(raw.getEndNM());
                    graph.get(nodeKey).add(end);
                }
                else {
                    nodeKey = raw.getStart();

                    Set<NodeSet> temp = new HashSet<>();

                    NodeSet start = new NodeSet(raw.getStart(), raw.getStartNM());
                    start.setCode(raw.getStart());
                    start.setName(raw.getStartNM());

                    NodeSet end = new NodeSet(raw.getEnd(), raw.getEndNM());

                    temp.add(start);
                    temp.add(end);
                    graph.put(start.getCode(), temp);
                }
            }
        }
    }

    public void makeGraph(){
        Set<String> ks = graph.keySet();
        String[] keys = ks.toArray(new String[0]);

        for(int i =0; i < keys.length; i++){
            String key = keys[i];
            Set<NodeSet> comp = graph.get(key);

            for(int j = i+1; j < keys.length; j++){
                HashSet<NodeSet> intersection = new HashSet<>(graph.get(keys[j]));  // s1으로 intersection 생성
                intersection.retainAll(comp);
                //System.out.println(graph.get(keys[j]) +":"+comp);

                if(!intersection.isEmpty()){
                    Set<NodeSet> unionSet = new HashSet<>(graph.get(keys[j]));
                    unionSet.addAll(comp);

                    graph.put(keys[j], unionSet);
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
        graph.forEach((k, v) -> {
            System.out.print(k + " [ ");
            for(NodeSet set : v){
                System.out.print(set.getCode() + ":" + set.getName() + " | ");
            }
            System.out.println(" ] ");
        });
        System.out.println("------------------------");
        for (Map.Entry<String, String> entry : ingredient.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            System.out.println(k + ":" + v);
        }
        System.out.println("------------------------");
        Set<String> graphKeys = new HashSet<>(graph.keySet());
        System.out.println("Graph Size : " + graphKeys.size());
        Set<String> ingredientKeys = new HashSet<>(ingredient.keySet());
        System.out.println("Graph Size : " + ingredientKeys.size());
    }

    public void run(){
        long startTime = System.currentTimeMillis();

        String path = "files/ingredient_subset.csv";
        makeData(path);

        dataSort();

        nodeGroup();

        makeGraph();

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