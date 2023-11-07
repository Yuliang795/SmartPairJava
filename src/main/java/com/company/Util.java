package com.company;

import java.lang.reflect.Array;
import java.io.*;
import java.util.*;
import java.util.stream.*;
//import java.util.stream.Collectors;
import java.io.IOException;
public class Util {
    public static String writeClause(int weight, int... numbers) {
        StringBuilder result = new StringBuilder();

        for (int number : numbers) {
            result.append(number).append(" ");
        }
        return weight+" "+result.toString()+"0";
    }

    public static int[] sortTwoInt(int a, int b) {
        if (a > b) {
            return new int[]{b, a};
        } else {
            return new int[]{a, b};
        }
    }

    public static Integer[] checkCCIndex(Integer key1, Integer key2) {
//        Main.connectedComponentTab.putIfAbsent(key1, 0);
//        Main.connectedComponentTab.putIfAbsent(key2, 0);
        return new Integer[]{Main.connectedComponentTab.get(key1), Main.connectedComponentTab.get(key2)};
    }

    public static int[][] createMatrix(int startValue, int rows, int cols) {
        int[][] matrix = new int[rows][cols];
        int value = startValue;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = value;
                value++;
            }
        }
//        System.out.println("matrix index size --- " + matrix[0][0] + ' ' + matrix[matrix.length-1][matrix[0].length-1] );
        return matrix;
    }

    public static List<Integer> createList(int startIndex, int length) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add(startIndex + i);
        }
//        System.out.println("index size --- " + list.get(0) + ' ' + list.get(list.size()-1) +"size:" +list.size());
        return list;
    }


    public double getEuclideanDistance(int a, int b) {
        return Math.abs(a - b);
    }

    private static double calcPointEucDistance(List<Double> point1, List<Double> point2) {
        double sum = 0;
        for (int i = 0; i < point1.size(); i++) {
            sum += Math.pow(point1.get(i) - point2.get(i), 2);
        }
        return Math.sqrt(sum);
    }

    public static List<List<Integer>> sortPairsByEuclideanDistance(List<List<Integer>> pairs, String order) {
        List<List<Integer>> sortedPairs = pairs.stream()
                .sorted(Comparator.comparingDouble(pair -> calcPointEucDistance(Main.X.get(pair.get(0)), Main.X.get(pair.get(1)))))
                .collect(Collectors.toList());
        if ("descending".equals(order)){
            Collections.reverse(sortedPairs);
        }
        return sortedPairs;
    }

//    public static List<List<Integer>> sortPairsByEuclideanDistance(List<List<Integer>> pairList, String order) {
//        List<List<Integer>> sortedPairs = pairList.stream()
//                .sorted(Comparator.comparingDouble(pair -> Math.abs(pair.get(0) - pair.get(1))))
//                .collect(Collectors.toList());
//        if ("descending".equals(order)) {
//            Collections.reverse(sortedPairs);
//        }
//        return sortedPairs;
//    }



    public static boolean checkInnerPair( Integer value1, Integer value2) {

        if (value1 == null || value2 == null) {
            return false;
        }
        return Objects.equals(value1, value2);
    }

    public static boolean checkCrossingPair(Integer value1, Integer value2) {

        if (value1 == null || value2 == null || value1==value2) {
            return false;
        }
        int minConnCompIndex = Math.min(value1, value2);
        int maxConnCompIndex = Math.max(value1, value2);
        if (Main.negEdgeTab.get(minConnCompIndex)!=null && Main.negEdgeTab.get(minConnCompIndex).contains(maxConnCompIndex)){
            return true;
        }
//        if (Main.negEdgeTab.get(maxConnCompIndex)!=null && Main.negEdgeTab.get(maxConnCompIndex).contains(minConnCompIndex)){
//            return true;
//        }
        return false;
    }


    public static void updateConnComp(int key1, Integer value1, int key2, Integer value2) {

        if (value1 == null && value2 == null) {
            Main.connectedComponentTab.put(key1, Main.connCompIndex);
            Main.connectedComponentTab.put(key2, Main.connCompIndex);
//            add to connected component list
            Main.connectedComponentList.get(Main.connCompIndex).addAll(Arrays.asList(key1, key2));
            Main.connCompIndex++;
        } else if (value1 == null && value2 != null) {
            Main.connectedComponentTab.put(key1, value2);
            Main.connectedComponentList.get(value2).add(key1);
        } else if (value2 == null && value1 != null) {
            Main.connectedComponentTab.put(key2, value1);
            Main.connectedComponentList.get(value1).add(key2);
        } else {
            int minConnCompIndex = Math.min(value1, value2);
            int maxConnCompIndex = Math.max(value1, value2);
//            assign the larger values (CC index) to small value in the hash table
            for (Integer key:Main.connectedComponentList.get(maxConnCompIndex)){
                Main.connectedComponentTab.put(key, minConnCompIndex);
            }
//            merge cc list with larger index to cc list with small index and remove the larger one
            Main.connectedComponentList.get(minConnCompIndex).addAll(Main.connectedComponentList.get(maxConnCompIndex));
            Main.negEdgeTab.get(minConnCompIndex).addAll(Main.negEdgeTab.get(maxConnCompIndex));
            Main.negEdgeTab.remove(maxConnCompIndex);
            Main.connectedComponentList.remove(maxConnCompIndex);
            Main.connCompIndex--;
            // update pos edge
            // all the values (CC index) larger than the maxConnCompIndex must minus one to match the index in CC list
            for (Map.Entry<Integer, Integer> entry : Main.connectedComponentTab.entrySet()) {
                if (entry.getValue()!= null && entry.getValue()> maxConnCompIndex) {
                    entry.setValue(entry.getValue() - 1);
                }
            }
            // update neg edge
            Set<Integer> keys = new HashSet<>(Main.negEdgeTab.keySet());

            for (Integer key : keys) {
                Boolean redirect = false;
                Set<Integer> value = Main.negEdgeTab.get(key);
                Set<Integer> newValue = new HashSet<>();
                for (Integer val : value) {
                    if (val > maxConnCompIndex) {
                        newValue.add(val - 1);
                    } else if (val < maxConnCompIndex) {
                        newValue.add(val);
                    }else{
                        // method 1, add minConnCompIndex to current key value
                        // this would require negEdgeTable min check for max, and max check for min
//                        newValue.add(minConnCompIndex);
                        // method2, redirect (current key/minConnCompIndex) to (minConnCompIndex/current key)
                        // if current key has value equals maxConnCompIndex, current key < maxConnCompIndex
                        // hence the key doesn't have to change, and can be added to the value of minConnCompIndex
                        redirect=true;
                    }
                }
                if (key>maxConnCompIndex){
                    Main.negEdgeTab.remove(key);
                    Main.negEdgeTab.put(key-1, newValue);
                    if (redirect==true){
                        Main.negEdgeTab.get(Math.min(minConnCompIndex, key-1)).add(Math.max(minConnCompIndex, key-1));
                    }
                }else{
                    Main.negEdgeTab.put(key, newValue);
                    if (redirect==true){
                        Main.negEdgeTab.get(Math.min(minConnCompIndex, key)).add(Math.max(minConnCompIndex, key));
                    }
                }
            }
        }
    }

    public static void updateNegEdge(int key1, Integer value1, int key2, Integer value2) {
        if (value1==null && value2==null){
            // update cc table, cc list, and neg edge table for point 1
            Main.connectedComponentTab.put(key1, Main.connCompIndex);
            Main.connectedComponentList.get(Main.connCompIndex).add(key1);
            // add new key(edge of small CC ind) and value (large CC ind) to the neg edge tab
            Main.negEdgeTab.get(Main.connCompIndex).add(++Main.connCompIndex);
            // update cc table and cc list for point 2
            Main.connectedComponentTab.put(key2, Main.connCompIndex);
            Main.connectedComponentList.get(Main.connCompIndex++).add(key2);
        } else if (value1==null && value2!=null) {
            // add key1 (point) and value to connected component table and list (pos edge)
            Main.connectedComponentTab.put(key1, Main.connCompIndex);
            Main.connectedComponentList.get(Main.connCompIndex).add(key1);
            // add new value (large CC ind) to key(edge of small CC ind) in the neg edge tab
            Main.negEdgeTab.get(value2).add(Main.connCompIndex);
            Main.connCompIndex++;
        } else if (value2==null && value1!=null) {
            Main.connectedComponentTab.put(key2, Main.connCompIndex);
            Main.connectedComponentList.get(Main.connCompIndex).add(key2);
            Main.negEdgeTab.get(value1).add(Main.connCompIndex);
            Main.connCompIndex++;
        }else {
            int minConnCompIndex = Math.min(value1, value2);
            int maxConnCompIndex = Math.max(value1, value2);
            Main.negEdgeTab.get(minConnCompIndex).add(maxConnCompIndex);
        }
    }


    public static void minMaxScaleDataframe() {
        for (int j = 0; j < Main.X.get(0).size(); j++) { // For each column
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;

            // Find min and max values of the column
            for (List<Double> row : Main.X) {
                double value = row.get(j);
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
            }
            // Check if max and min are the same
            if (max - min == 0) {
                continue; // Skip this column
            }

            // Scale values in the column
            for (List<Double> row : Main.X) {
                double value = row.get(j);
                double scaledValue = ((value - min) / (max - min))*100;
                row.set(j, scaledValue);
            }
        }
    }

    public static void readFile(String filePath) {
        try {
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            // Read the first four lines df_length, numFeature, numLabel
            Main.labelColInd = br.readLine();
            Main.df_length = Integer.parseInt(br.readLine());
            Main.numFeature = Integer.parseInt(br.readLine());
            Main.numLabel = Integer.parseInt(br.readLine());

            // Read the rest of the file starting from the 5th line
            while ((line = br.readLine()) != null) {
                List<String> dataRowStr = Arrays.asList(line.split("\\s+"));
                List<Double> dataRowDbl = new ArrayList<>();
                for (String str : dataRowStr) {
                    dataRowDbl.add(Double.parseDouble(str));
                }
                if (Main.labelColInd.equals("flags: l")) {
                    // If label is in the last column, remove and store it
                    Main.y.add(dataRowDbl.remove(dataRowDbl.size() - 1));
                } else {
                    // If label is in the first column, remove and store it
                    Main.y.add(dataRowDbl.remove(0));
                }
                Main.X.add(dataRowDbl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<List<Double>>> getDistanceClass(){
        List<List<List<Double>>> distanceClasses = new ArrayList<>();
        List<double[]> pairEucDistList = new ArrayList<>();
        // Generate all combinations of pairs of instances
        for (int i = 0; i < Main.X.size(); i++) {
            for (int j = i + 1; j < Main.X.size(); j++) {
                double distance = 0;
                for (int k = 0; k < Main.X.get(i).size(); k++) {
                    distance += Math.pow(Main.X.get(i).get(k) - Main.X.get(j).get(k), 2);
                }
                distance = Math.sqrt(distance);
                pairEucDistList.add(new double[]{distance, i, j});
            }
        }
        // Sort the list of pairs in ascending order
//        pairEucDistList.sort(Comparator.comparingDouble(a -> a[0]));
        pairEucDistList.sort((a, b) -> {
            if (Double.compare(a[0], b[0]) != 0) {
                return Double.compare(a[0], b[0]);
            } else {
                return Double.compare(a[1], b[1]);
            }
        });

        // Create distance classes
        for (double[] pair : pairEucDistList) {
            if (distanceClasses.isEmpty() || Math.abs(pair[0] - distanceClasses.get(distanceClasses.size() - 1).get(0).get(0)) >= 0.1) {
                // Create a new distance class
                List<List<Double>> newClass = new ArrayList<>();
                newClass.add(Arrays.asList(pair[0], pair[1], pair[2]));
                distanceClasses.add(newClass);
            } else {
                // Add to the current distance class
                distanceClasses.get(distanceClasses.size() - 1).add(Arrays.asList(pair[0], pair[1], pair[2]));
            }
        }
        return distanceClasses;
    }


    public static List<List<List<Integer>>> readConstsPairs(String filePath) {
        List<List<Integer>> mlPairs = new ArrayList<>();
        List<List<Integer>> clPairs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isClPair = false;
            while ((line = br.readLine()) != null) {
                if (line.trim().equals("*")) {
                    isClPair = true;
                    continue;
                }
                String[] pair = line.split(" ");
                List<Integer> pairList = Arrays.asList(Integer.parseInt(pair[0]), Integer.parseInt(pair[1]));
                if (isClPair) {
                    clPairs.add(pairList);
                } else {
                    mlPairs.add(pairList);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.asList(mlPairs, clPairs);
    }

    public static void writeListToFile(List<String> list, BufferedWriter bw) throws IOException {
        if (!list.isEmpty()){
            String content = list.stream().collect(Collectors.joining("\n"));
            bw.write(content);
            bw.newLine();
        }
    }

    public static void checkForEmptyString(List<String> list, String listName) {
        if (list.contains("")) {
            System.out.println(listName + " contains an empty string.");
        } else {
            System.out.println(listName + " does not contain an empty string.");
        }
        System.out.println("The size of " + listName + " is: " + list.size());
    }


    public static List<List<List<Double>>> getDistClass(String filename) throws IOException {
        List<List<List<Double>>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<List<Double>> pairsInLine = new ArrayList<>();
                String[] pairs = line.split("-");
                for (String pair : pairs) {
                    pair = pair.replaceAll("\\[|\\]|\\s|-", "").trim();
//                    pair = pair.replaceAll("\\\\[|\\\\]|\\s|-", "").trim();
                    String[] parts = pair.split(",");
                    if (parts.length == 2) {
                        List<Double> pairList = Arrays.asList(Double.parseDouble("0"), Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                        pairsInLine.add(pairList);
                    }
                }
                data.add(pairsInLine);
            }
        }
        return data;
    }





}
