package com.smlteam.textsimilarity.tfidf;

import java.util.List;

public class TFIDF {
    private double calTF(String term, List<String> doc){
        int count = 0;
        for(String word : doc){
            if(term.equalsIgnoreCase(word)){
                count++;
            }
        }
        return count / doc.size();
    }

    private double calIDF(String term, List<List<String>> docs){
        int count = 0;
        for(List<String> doc: docs){
            for(String word: doc){
                if(word.equalsIgnoreCase(term)){
                    count++;
                    break;
                }
            }
        }
        return docs.size() / count;
    }
    public double calTFIDF(String term, List<String> doc, List<List<String>> docs){
        return calTF(term, doc) * calIDF(term, docs);
    }
}
