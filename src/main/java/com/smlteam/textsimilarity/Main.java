package com.smlteam.textsimilarity;

import com.smlteam.textsimilarity.constant.Constants;
import com.smlteam.textsimilarity.cosinesimilarity.CosineSimilarity;
import com.smlteam.textsimilarity.index.Indexer;
import com.smlteam.textsimilarity.preprocess.Preprocesser;
import com.smlteam.textsimilarity.tfidf.CalcTFIDF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args){
        final long startTime = System.nanoTime();
        Preprocesser preprocesser = new Preprocesser(false);
        String originContent = preprocesser.getPureContentFromFile(Constants.ORIGIN);
        String testContent = preprocesser.getPureContentFromFile(Constants.TEST);
        System.out.println("TIME: " + (System.nanoTime() - startTime) / 1e6);
        Indexer indexer = new Indexer();
        indexer.indexer(originContent, testContent);
        CalcTFIDF calcTFIDF = new CalcTFIDF();
        List<HashMap<String, Double>> listVector = calcTFIDF.calcAllTFIDF();
        CosineSimilarity cs = new CosineSimilarity();
        Double result = cs.calcCosine(listVector.get(0), listVector.get(1));
        System.out.print("% plagiarism: "+result +"\n");
        System.out.println("TIME: " + (System.nanoTime() - startTime) / 1e6);
    }
}
