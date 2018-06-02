package com.smlteam.textsimilarity;

import com.smlteam.textsimilarity.constant.Constants;
import com.smlteam.textsimilarity.preprocess.Preprocesser;
import java.util.List;

public class Main {
    public static void main(String[] args){

        Preprocesser preprocesser = new Preprocesser(false);
        List<String> originContent = preprocesser.getPureContentFromFile(Constants.ORIGIN);
        List<String> testContent = preprocesser.getPureContentFromFile(Constants.TEST);
        int start = 0, end = 0;
        int threadNum = 4;
        for(int i = 0; i< threadNum; i++)
        {
            start = end;
            if(i != threadNum - 1)
                end = (testContent.size()/ threadNum) + start;
            else
                end = testContent.size();
            Thread thread = new Thread(new MultiTasking(start, end, originContent, testContent));
            thread.start();
//            System.out.print("thread "+ i+1 + "started.");
        }

    }
}
