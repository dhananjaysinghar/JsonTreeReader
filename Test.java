package com.example.demo.utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) throws Exception {

        URL url = Test.class.getResource("/input.json");
        String content = Files.lines(Paths.get(url.toURI()))
                .collect(Collectors.joining(System.lineSeparator()));

        List<List<Map<String, String>>> dataList = getDataList(content);
        Map<String, Map<String, List<Map<String, String>>>> result = getResults(dataList);


        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result));

    }

    private static List<List<Map<String, String>>> getDataList(String content) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(content);
        List<List<Map<String, String>>> dataList = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> itr = tree.fields();
        while (itr.hasNext()) {
            List<Map<String, String>> list = new ArrayList<>();
            Map.Entry<String, JsonNode> entry1 = itr.next();
            List<Map<?, ?>> list1 = mapper.readValue(entry1.getValue().toPrettyString(), List.class);
            for (Map<?, ?> obj : list1) {
                Map<String, String> map = new LinkedHashMap<>();
                Map<String, String> keyData = getKey(entry1.getKey());
                if (obj.containsKey(keyData.get(entry1.getKey()))) {
                    map.put(entry1.getKey(), obj.get(keyData.get(entry1.getKey())).toString());
                    list.add(map);
                }
            }
            dataList.add(list);
        }
        return dataList;
    }

    private static Map<String, Map<String, List<Map<String, String>>>> getResults(List<List<Map<String, String>>> list) {
        Map<String, Map<String, List<Map<String, String>>>> result = new LinkedHashMap<>();
        Map<String, List<Map<String, String>>> resultMap = new LinkedHashMap<>();
        result.put("results", resultMap);
        List<Map<String, String>> resultRow = new ArrayList<>();
        int resultRowSize = list.get(0).size();
        int count = 0;
        while (count < resultRowSize) {
            Map<String, String> data = new LinkedHashMap<>();
            for (List<Map<String, String>> l : list) {
                for (Map<String, String> ignored : l) {
                    Map<String, String> dataMap = l.get(count);
                    data.putAll(dataMap);
                    break;
                }
            }
            count++;
            resultRow.add(data);
        }
        resultMap.put("resultRow", resultRow);
        return result;
    }


    private static Map<String, String> getKey(String str) {
        Map<String, String> map = new HashMap<>();
        map.put("account-group", "title");
        map.put("market-value", "amount");
        map.put("category-id", "title");

        return map;
    }
}