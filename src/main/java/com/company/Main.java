package com.company;

import java.util.*;

import java.io.IOException;

//write to clauses
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.stream.Collectors;
//import java.util.List;



// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
//    ML CL
    static List<List<Integer>> mlList = new ArrayList<>();
    static String labelColInd;
    static int df_length, numFeature, numLabel;
    static List<List<Double>> X = new ArrayList<>();
    static List<Double> y = new ArrayList<>();
//    static int df_length=10;
    public static Map<Integer, Integer> connectedComponentTab = new HashMap<>();
    public static List<List<Integer>> connectedComponentList = new ArrayList<>();
    static Map<Integer, Set<Integer>> negEdgeTab = new HashMap<>();

    static int connCompIndex = 0;





    static List<String> clauseList_22_23 = new ArrayList<>();
    static List<String> clauseList_24 = new ArrayList<>();
    static List<String> clauseList_25_26 = new ArrayList<>();
    static List<String> clauseList_27_28 = new ArrayList<>();
    static List<String> clauseList_29 = new ArrayList<>();
    static List<String> clauseList_30_31 = new ArrayList<>();
    static List<String> clauseList_b0_b1 = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.println("========= Smart Pair started =========");
//        System.out.println(Util.joinWithComma(0, 1, 2, 3, 4, 5));


        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("dataPath", System.getProperty("dataPath"));
        paramMap.put("constsPath", System.getProperty("constsPath"));
        paramMap.put("distanceClassPath", System.getProperty("distanceClassPath"));
        paramMap.put("xStartInd", System.getProperty("xStartInd"));
        paramMap.put("b0StartInd", System.getProperty("b0StartInd"));
        paramMap.put("hardClauseWeight", System.getProperty("hardClauseWeight"));
        paramMap.put("outFileName", System.getProperty("outFileName"));
        paramMap.put("obj_", System.getProperty("obj_"));

        for (String param : Arrays.asList("dataPath", "xStartInd", "b0StartInd", "hardClauseWeight")) {
            if (paramMap.get(param) == null) {
                System.out.println(param + " is missing -ending");
                return;
            }
        }
        String dataPath = paramMap.get("dataPath").toString();
        String outFileName = paramMap.get("outFileName").toString();
        int xStartInd = Integer.parseInt(paramMap.get("xStartInd").toString());
        int b0StartInd = Integer.parseInt(paramMap.get("b0StartInd").toString());
        int hardClauseWeight = Integer.parseInt(paramMap.get("hardClauseWeight").toString());
        //
        String constsPath = null;
        String distanceClassPath = null;
        String obj_ = paramMap.get("obj_").toString(); //md_ms or md
        //
        if (paramMap.get("constsPath") == null) {
            System.out.println("No constsPath provided -Implementing Smart Pair without ML CL Constraints");
        }else {
            constsPath = paramMap.get("constsPath").toString();
        }

        List<List<List<Double>>> distanceClasseList = new ArrayList<>();
        if (paramMap.get("distanceClassPath") == null) {
            System.out.println("NoDCPath provided -calculating DC based on dataset");
            distanceClasseList = Util.getDistanceClass();
        }else{
            distanceClassPath = paramMap.get("distanceClassPath").toString();
            System.out.println("extracting DC from " + distanceClassPath);
            distanceClasseList = Util.getDistClass(distanceClassPath);
        }


//        String dataPath = "./content/instance_iris";
//        int xStartInd = 2303;
//        int b0StartInd = 2753;
////        String constsPath = "./consts/consts_k0.1_s1732/iris_0.1_mc0.1_s1732";
//        String constsPath = "./consts/consts_k0.5_s2352/iris_0.1_mc0.5_s2352";
////
//        String distanceClassPath = "./DC";
//        String outFileName = "./test.txt";
//        int hardClauseWeight = 2439;
//////         read data -> minmaxsclae -> distance class

        Util.readFile(dataPath);
        Util.minMaxScaleDataframe();
        // distance class list
//        List<List<List<Double>>> distanceClasseList = Util.getDistanceClass(); //no distance class
//        List<List<List<Double>>> distanceClasseList = Util.getDistClass(distanceClassPath); // use existing distance class


        System.out.printf("df length: %d, num label: %d, num DC: %d\n",df_length, numLabel, distanceClasseList.size());



        // initiate variables


        int[][] x = Util.createMatrix(xStartInd, df_length, numLabel);
        List<Integer> b0 = Util.createList(b0StartInd, distanceClasseList.size());
        List<Integer> b1 = Util.createList(b0StartInd + b0.size(), distanceClasseList.size());
//        System.out.println(b1);
        System.out.printf("x: %d-%d, b0: %d, b1: %d\n",x.length, x[0].length, b0.size(), b1.size());
//        System.out.println(Util.writeClause(1,x[1][2], -x[2][0], x[df_length-1][numLabel-1]));

        // init empty list for connected component
        for (int i = 0; i < df_length; i++) {
            connectedComponentList.add(new ArrayList<>());
        }
//        init empty hash table for the positive edges
        for (int i = 0; i < df_length; i++) {
            connectedComponentTab.put(i, null);
            negEdgeTab.put(i, new HashSet<>());
        }




        List<List<Integer>> mlPairs = new ArrayList<>();
        List<List<Integer>> clPairs = new ArrayList<>();
        if (constsPath!=null) {
            List<List<List<Integer>>> pairsMlCl = Util.readConstsPairs(constsPath);
            mlPairs = Util.sortPairsByEuclideanDistance(pairsMlCl.get(0), "ascending");
            clPairs = Util.sortPairsByEuclideanDistance(pairsMlCl.get(1), "descending");
        }

        System.out.printf("%s, %d, %d, %d\n",labelColInd, df_length, numFeature, numLabel);
        System.out.printf("distance class size : %d \n", distanceClasseList.size());
        System.out.printf("consts size ml: %d, cl: %d\n",mlPairs.size(), clPairs.size() );

//        System.out.println(mlPairs);
//        System.out.println(clPairs);








//        List<List<Integer>> mlPairs = Arrays.asList(Arrays.asList(0, 1), Arrays.asList(3, 4), Arrays.asList(5, 9));
//
//        List<List<Integer>> clPairs = Arrays.asList(Arrays.asList(2,5), Arrays.asList(1,2), Arrays.asList(6,7));



        smartPair:
        for (int i = 0; i < 1; i++) {
            for (List<Integer> mlPair : mlPairs) {
//                System.out.println(mlPair);
                int mlPair0 = mlPair.get(0);
                int mlPair1 = mlPair.get(1);
                Integer mlP0CCIndex = connectedComponentTab.get(mlPair0);
                Integer mlP1CCIndex = connectedComponentTab.get(mlPair1);
                if (!Util.checkInnerPair(mlP0CCIndex, mlP1CCIndex)) {
                    Util.updateConnComp(mlPair0, mlP0CCIndex, mlPair1, mlP1CCIndex);
//                    clauseList_25_26
                    for (int c = 0; c < x[0].length-1; c++) {
                        clauseList_25_26.add(Util.writeClause(hardClauseWeight, -x[mlPair0][c], x[mlPair1][c]));
                        clauseList_25_26.add(Util.writeClause(hardClauseWeight, x[mlPair0][c], -x[mlPair1][c]));
                    }
                }
            }

            for (List<Integer> clPair : clPairs) {
//                System.out.println(clPair);
                int clPair0 = clPair.get(0);
                int clPair1 = clPair.get(1);
                Integer clP0CCIndex = connectedComponentTab.get(clPair0);
                Integer clP1CCIndex = connectedComponentTab.get(clPair1);
                // check for inner pair
                if (Util.checkInnerPair(clP0CCIndex, clP1CCIndex)) {
                    System.out.printf("infeasible solution -inner pair: %d - %d\n", clP0CCIndex, clP1CCIndex);
                    break smartPair;
                }

//                if (clPair0==146 & clPair1==56 ){
//                    System.out.println("crossing: " + Util.checkCrossingPair(clP0CCIndex, clP1CCIndex));
//                    System.out.println(clP0CCIndex+":" +clP1CCIndex+ " | " );
//                    System.out.println(negEdgeTab.get(31));
//                    System.out.println(negEdgeTab.get(45));
//                    System.out.println("===");
//                }
                // check for crossing pair
                if (!Util.checkCrossingPair(clP0CCIndex, clP1CCIndex)) {
                    Util.updateNegEdge(clPair0, clP0CCIndex, clPair1, clP1CCIndex);
                    // 22 23
                    clauseList_22_23.add(Util.writeClause(hardClauseWeight, x[clPair0][0], x[clPair1][0]));
                    clauseList_22_23.add(Util.writeClause(hardClauseWeight, -x[clPair0][x[0].length-2], -x[clPair1][x[0].length-2]));
                    // 24
                    for (int c = 0; c < x[0].length-2; c++) {
                        clauseList_24.add(Util.writeClause(hardClauseWeight, -x[clPair0][c], -x[clPair1][c], x[clPair0][c+1], x[clPair1][c+1]));
                    }
                }
            }
//            System.out.println(clauseList_22_23);
//            System.out.println(clauseList_24);
//            System.out.println(connectedComponentTab.get(146));
//            System.out.println(connectedComponentList.get(31));
//            System.out.println(negEdgeTab.get(31));
//            System.out.println(connectedComponentTab.get(56));
//            System.out.println(connectedComponentList.get(45));
//            System.out.println(negEdgeTab.get(45));
//            System.out.println("1: "+connectedComponentList.get(1));
//            System.out.println("4: "+connectedComponentList.get(4));



            // 12-20
            Map<Integer, Integer> hat_connectedComponentTab = new HashMap<>(connectedComponentTab);
            List<List<Integer>> hat_connectedComponentList = new ArrayList<>(connectedComponentList);
            Map<Integer, Set<Integer>> hat_negEdgeTab = new HashMap<>(negEdgeTab);
            int hat_connCompIndex = connCompIndex;
            loop12_20:
            for (int w = 0; w < distanceClasseList.size(); w++) {
                // if obj is md, then skip this part as 30,31 irrelevant
                if (obj_.equals("md")){
                    System.out.println("*opt for md only, skipping 12-20");
                    break loop12_20;
                }
                List<List<Double>> distanceClass = distanceClasseList.get(w);
                //            System.out.println("Distance class " + w + ":");
                for (List<Double> pair : distanceClass) {
                    //                System.out.println(pair.get(1).intValue()+"--"+ pair.get(2).intValue());
                    int indPair0 = pair.get(1).intValue();
                    int indPair1 = pair.get(2).intValue();
                    Integer indPair0CCIndex = connectedComponentTab.get(indPair0);
                    Integer indPair1CCIndex = connectedComponentTab.get(indPair1);
                    //(indPair0CCIndex!=null && indPair0CCIndex==31) | (indPair0CCIndex!=null && indPair0CCIndex==45)
//                    if (indPair0==68 & indPair1==119){//indPair0==40 & indPair1==130 indPair0==68 & indPair1==119
//                        System.out.println("dc" + b1.get(w) +"|pair: " + indPair0 + ":" + indPair1  + "|cc: " + indPair0CCIndex + ":" + indPair1CCIndex);
//                        System.out.println(Util.checkCrossingPair(indPair0CCIndex, indPair1CCIndex));
//
//                        System.out.println(indPair0CCIndex+": "+connectedComponentList.get(indPair0CCIndex));
//                        System.out.println(indPair1CCIndex+": "+connectedComponentList.get(indPair1CCIndex));
////                            System.out.println(connectedComponentList.get(1));
////                            System.out.println(connectedComponentList.get(2));
////                            System.out.println(connectedComponentList.get(3));
//                        System.out.println("0 neg edge: "+negEdgeTab.get(0));
//                        System.out.println(indPair0CCIndex+" neg edge: "+negEdgeTab.get(indPair0CCIndex));
//                        System.out.println("2 neg edge: "+negEdgeTab.get(2));
//                        System.out.println("3 neg edge: "+negEdgeTab.get(3));
//                        System.out.println(indPair1CCIndex+" neg edge: "+negEdgeTab.get(indPair1CCIndex));
//                    }
//                    System.out.println(negEdgeTab);

                    if (Util.checkCrossingPair(indPair0CCIndex, indPair1CCIndex)) {
                        //String t1 = indPair0 + ":" + indPair0CCIndex + "|" + indPair1 + ":" + indPair1CCIndex;
                        // clauses b+ -line16
                        clauseList_b0_b1.add(Util.writeClause(hardClauseWeight, -b1.get(w)));
                        System.out.println("12-20 crossing break" + clauseList_b0_b1 + " |pairInd: " + indPair0 + ":" + indPair1  + " |cc: " + indPair0CCIndex + ":" + indPair1CCIndex);
                        break loop12_20;
                    }
                    if (!Util.checkInnerPair(indPair0CCIndex, indPair1CCIndex)) {
                        Util.updateConnComp(indPair0, indPair0CCIndex, indPair1, indPair1CCIndex);
                        for (int c = 0; c < x[0].length-1; c++) {
                            clauseList_30_31.add(Util.writeClause(hardClauseWeight, -b1.get(w),-x[indPair0][c], x[indPair1][c]));
                            clauseList_30_31.add(Util.writeClause(hardClauseWeight, -b1.get(w),x[indPair0][c], -x[indPair1][c]));
                        }
                    }
                }
            }

            // 21-29
            connectedComponentTab = hat_connectedComponentTab;
            connectedComponentList = hat_connectedComponentList;
            negEdgeTab = hat_negEdgeTab;
            connCompIndex = hat_connCompIndex;
            // sort the pairs in the distance class descending
            List<List<List<Double>>> distanceClassListDESC = new ArrayList<>(distanceClasseList);
            for (List<List<Double>> distanceClass : distanceClassListDESC) {
                Collections.reverse(distanceClass);
            }
            loop21_29:
            for (int w = distanceClassListDESC.size()-1; w >=0 ; w--) {
                List<List<Double>> distanceClass = distanceClassListDESC.get(w);
                //            System.out.println("Distance class " + w + ":");
                for (List<Double> pair : distanceClass) {
                    int indPair0 = pair.get(1).intValue();
                    int indPair1 = pair.get(2).intValue();
                    Integer indPair0CCIndex = connectedComponentTab.get(indPair0);
                    Integer indPair1CCIndex = connectedComponentTab.get(indPair1);

                    if (Util.checkInnerPair(indPair0CCIndex, indPair1CCIndex)) {
                        // clauses b+ -line25
                        clauseList_b0_b1.add(Util.writeClause(hardClauseWeight, b0.get(w)));
                        System.out.println("21-29 crossing break" + clauseList_b0_b1 + " |pairInd: " + indPair0 + ":" + indPair1  + " |cc: " + indPair0CCIndex + ":" + indPair1CCIndex);
                        break loop21_29;
                    }

                    if (!Util.checkCrossingPair(indPair0CCIndex, indPair1CCIndex)) {
                        Util.updateNegEdge(indPair0, indPair0CCIndex, indPair1, indPair1CCIndex);
                        // clauses 27,28,29
                        clauseList_27_28.add(Util.writeClause(hardClauseWeight,b0.get(w), x[indPair0][0], x[indPair1][0]));
                        clauseList_27_28.add(Util.writeClause(hardClauseWeight,b0.get(w), -x[indPair0][x[0].length-2], -x[indPair1][x[0].length-2]));
                        for (int c = 0; c < x[0].length-2; c++) {
                            clauseList_29.add(Util.writeClause(hardClauseWeight, b0.get(w), -x[indPair0][c], -x[indPair1][c], x[indPair0][c+1], x[indPair1][c+1]));
                        }
                    }
                }
            }
        }


//        Util.checkForEmptyString(clauseList_22_23, "clauseList_22_23");
//        Util.checkForEmptyString(clauseList_24, "clauseList_24");
//        Util.checkForEmptyString(clauseList_25_26, "clauseList_25_26");
//        Util.checkForEmptyString(clauseList_27_28, "clauseList_27_28");
//        Util.checkForEmptyString(clauseList_29, "clauseList_29");
//        Util.checkForEmptyString(clauseList_30_31, "clauseList_30_31");
//        Util.checkForEmptyString(clauseList_b0_b1, "clauseList_b0_b1");

        // write clauses to file
//        for (String str1 : clauseList_22_23){
//            System.out.println(str1);
//        }
//        String content = clauseList_24.stream().collect(Collectors.joining("\n"));
//        System.out.println(content + clauseList_24.size());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName, true))) {
            Util.writeListToFile(clauseList_22_23, bw);
            Util.writeListToFile(clauseList_24, bw);
            Util.writeListToFile(clauseList_25_26, bw);
            Util.writeListToFile(clauseList_27_28, bw);
            Util.writeListToFile(clauseList_29, bw);
            // clauses for md, if opt for md, 12-20 skipped, 30_31 will be empty, b0b1 only contains b0
            Util.writeListToFile(clauseList_30_31, bw);
            Util.writeListToFile(clauseList_b0_b1, bw);


            System.out.printf("SmartPair Finished, %d clauses write to %s\n",
                    clauseList_22_23.size()
                    +clauseList_24.size()
                    +clauseList_25_26.size()
                    +clauseList_27_28.size()
                    +clauseList_29.size()
                    +clauseList_30_31.size()
                    +clauseList_b0_b1.size(),outFileName);
            System.out.println("clauseList_22_23: " + clauseList_22_23.size());
            System.out.println("clauseList_24: " + clauseList_24.size());
            System.out.println("clauseList_25_26: " + clauseList_25_26.size());
            System.out.println("clauseList_27_28: " + clauseList_27_28.size());
            System.out.println("clauseList_29: " + clauseList_29.size());
            System.out.println("clauseList_30_31: " + clauseList_30_31.size());
            System.out.println("clauseList_b0_b1: " + clauseList_b0_b1.size());

            System.out.println("========= Smart Pair Finished =========");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}