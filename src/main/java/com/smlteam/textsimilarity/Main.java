package com.smlteam.textsimilarity;

import com.smlteam.textsimilarity.constant.Constants;
import com.smlteam.textsimilarity.index.Indexer;
import com.smlteam.textsimilarity.preprocess.Preprocesser;

public class Main {
    public static void main(String[] args){
        Preprocesser preprocesser = new Preprocesser(false);
        String originContent = preprocesser.getPureContentFromFile(Constants.ORIGIN);
        String testContent = preprocesser.getPureContentFromFile(Constants.TEST);
        Indexer indexer = new Indexer();
        indexer.indexer(originContent, testContent);
    }
}
