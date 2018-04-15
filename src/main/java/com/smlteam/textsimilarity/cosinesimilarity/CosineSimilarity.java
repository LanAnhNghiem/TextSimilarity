package com.smlteam.textsimilarity.cosinesimilarity;

import com.smlteam.textsimilarity.tfidf.CalcTFIDF;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CosineSimilarity {
    public static void main(String[] args) throws IOException {
        CalcTFIDF calcTFIDF = new CalcTFIDF();
        List<HashMap<String, Double>> listVector = calcTFIDF.calcAllTFIDF();
        calcCosine(listVector.get(0), listVector.get(1));
    }
    public static Double calcCosine(HashMap<String, Double> leftVec, HashMap<String, Double> rightVec){
        Double result = 0.0;
        Set<String> intersection = new HashSet<>();
        intersection.addAll(leftVec.keySet());
        intersection.retainAll(rightVec.keySet());
        double dotProduct = 0.0, d1 = 0.0, d2 = 0.0;
        for(String key: intersection){
            dotProduct += leftVec.get(key) * rightVec.get(key);
        }
        for(Double value : leftVec.values()){
            d1 += Math.pow(value, 2);
        }
        for(Double value: rightVec.values()){
            d2 += Math.pow(value, 2);
        }

        return dotProduct/(Math.sqrt(d1)*Math.sqrt(d2));
    }
}
