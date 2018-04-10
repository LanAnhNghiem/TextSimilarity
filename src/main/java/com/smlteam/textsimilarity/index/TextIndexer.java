package com.smlteam.textsimilarity.index;

import ai.vitk.tok.Tokenizer;
import ai.vitk.type.Token;
import com.smlteam.textsimilarity.constant.Constants;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class TextIndexer {
    public static void main(String[] args){
        tokenizer();
    }
    public static void tokenizer(){
        try {
            Tokenizer tokenizer = new Tokenizer();
            List<String> tokens = new ArrayList<>();
            List<List<Token>> tokenList = Files.lines(Paths.get(Constants.TEST))
                    .map(s-> tokenizer.tokenize(s))
                    .collect(Collectors.toList());
            List<List<String>> documents  =new LinkedList<>();
            for(List<Token> token : tokenList){
                List<String> doc = token.stream().map(t->t.getWord()).collect(Collectors.toList());
                documents.add(doc);
            }
            for(List<String> listStr: documents){
                for(String str: listStr){
                    System.out.print(str+"\n");
                }
                System.out.print(listStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
