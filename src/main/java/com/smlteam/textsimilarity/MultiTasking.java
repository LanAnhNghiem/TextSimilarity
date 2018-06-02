package com.smlteam.textsimilarity;

import com.smlteam.textsimilarity.constant.Constants;
import com.smlteam.textsimilarity.cosinesimilarity.CosineSimilarity;
import com.smlteam.textsimilarity.index.Indexer;
import com.smlteam.textsimilarity.preprocess.Preprocesser;
import com.smlteam.textsimilarity.tfidf.CalcTFIDF;

import java.util.HashMap;
import java.util.List;

public class MultiTasking implements Runnable{
    private int startIndex, endIndex;
    private List<String> originContent, testContent;
    public MultiTasking(int start, int end, List<String> originContent, List<String> testContent){
        this.startIndex = start;
        this.endIndex = end;
        this.originContent = originContent;
        this.testContent = testContent;
    }
    @Override
    public void run() {
        final long startTime = System.nanoTime();
        Indexer indexer = new Indexer();
        CalcTFIDF calcTFIDF = new CalcTFIDF();
        int tmp = 0;
        for(int i = startIndex; i < endIndex; i++){
            int origin = 0;
            for(String docO: originContent){
                indexer.indexer(docO, testContent.get(i),String.valueOf(i));
                List<HashMap<String, Double>> listVector = calcTFIDF.calcAllTFIDF(String.valueOf(i));
                CosineSimilarity cs = new CosineSimilarity();
                Double result = cs.calcCosine(listVector.get(0), listVector.get(1));
//                System.out.print("Test: "+i+", Origin: "+origin +", % plagiarism: "+result +"\n");
                origin ++;
            }
        }
        System.out.println("TIME: " + (System.nanoTime() - startTime) / 1e6);
    }
}
