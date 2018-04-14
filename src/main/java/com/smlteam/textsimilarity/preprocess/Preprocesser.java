package com.smlteam.textsimilarity.preprocess;

import ai.vitk.tok.Tokenizer;
import ai.vitk.type.Token;
import com.smlteam.textsimilarity.constant.Constants;
import org.apache.lucene.queryparser.classic.ParseException;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author NVTC
 */
public class Preprocesser {
    private static boolean isEN = false;
    public Preprocesser(boolean isEN){
        this.isEN = isEN;
    }
//    public static void main(String[] args) throws IOException, ParseException {
//        // TODO code application logic here
//        final long startTime = System.nanoTime();
//        System.out.println("TIME: " + (System.nanoTime() - startTime) / 1e6);
//        String path = "/home/lana/IdeaProjects/TextSimilarity/src/main/resources/Demo.txt";
//        String newContent = getPureContentFromFile(path);//vietnamese
//        System.out.println(newContent);
//
//    }

    //file's content to list converting function
    public ArrayList<String> fileToList(String fileName) {
        ArrayList<String> arrContent = new ArrayList();
        try {
            //Tạo luồng và liên kết luồng
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = br.readLine()) != null) {
                String[] lineArray = line.split(" ");
                List<String> lineContent = Arrays.asList(lineArray);
                arrContent.addAll(lineContent);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return arrContent;
    }

    public ArrayList<String> fileToListVN(String fileName){
        ArrayList<String> arrContent = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            Tokenizer tokenizer = new Tokenizer();
            List<Token> tokenList = new LinkedList<>();
            while ((line = br.readLine()) != null) {
                tokenList.addAll(tokenizer.tokenize(line));
            }
            for(Token token: tokenList){
                arrContent.add(token.getWord().replace(" ","_"));
            }
        }catch (IOException e){

        }
        return arrContent;
    }

    public String removeSpecialChar(String word) {
        word = word.toLowerCase();
        Pattern pt = null;
        try {
            String reg = "[,./?|\\[\\](){}\\\\^([0-9]+)^!@#$%^&*()`~<>:;+=|\"]";
            pt = Pattern.compile(reg);

            Matcher match = pt.matcher(word);
            while (match.find()) {
                String s = match.group();
                word = word.replace(s,"");
            }
        }catch (Exception e) {
            System.out.println("LOI SML");
            return "";
        }
        return word;
    }

    public String removeUrl(String commentstr) {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i), "").trim();
            i++;
        }
        return commentstr;
    }

    public String cleanWord(String word) {
        word = removeUrl(word);
        word = removeSpecialChar(word);
        return word;
    }

    public String getPureContentFromFile(String path){
        ArrayList<String> content = new ArrayList<>();
        if(isEN){
            content = fileToList(path);
            for (int i = 0; i < content.size(); i++) {
                content.set(i, cleanWord(content.get(i)));
            }
            for (String string : new ArrayList<>(content)) {
                if (isStopWordEN(string)) {
                    content.remove(string);
                }
            }
        }else{
            content = fileToListVN(path);
            for (int i = 0; i < content.size(); i++) {
                content.set(i, cleanWord(content.get(i)));
            }
            for (String string : new ArrayList<>(content)) {
                if (isStopWordVN(string) || string.isEmpty() || string == null) {
                    content.remove(string);
                }

            }
        }
        System.out.println("LENG: " + content.size());
        return String.join(" ", content);
    }

    private boolean isStopWordEN(String string) {
        ArrayList<String> lstStopWord = fileToList(Constants.STOPWORDS_EN);
        for (String word : lstStopWord) {
            if (word.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStopWordVN(String string) {
        ArrayList<String> lstStopWord = fileToList(Constants.STOPWORDS_VN);
        for (String word : lstStopWord) {
            if (word.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }
}
