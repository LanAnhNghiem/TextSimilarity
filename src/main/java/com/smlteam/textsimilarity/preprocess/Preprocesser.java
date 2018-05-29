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
    private ArrayList<String> lstStopwordEN = new ArrayList<>();
    private ArrayList<String> lstStopwordVN = new ArrayList<>();
    private List<List<String>> lstSentences = new ArrayList<>();
    public Preprocesser(boolean isEN){
        this.isEN = isEN;
        if(isEN){
            lstStopwordEN = readFile(Constants.STOPWORDS_EN);
        }else{
            lstStopwordVN = readFile(Constants.STOPWORDS_VN);
        }
    }

    public List<List<String>> getLstSentences() {
        return lstSentences;
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
                String result = removeUrl(line);
                result = removeSpecialChar(result);
                lstSentences.add(Arrays.asList(result.split("\\.")));
                String[] lineArray = result.replace(".","").split(" ");
                List<String> lineContent = Arrays.asList(lineArray);
                arrContent.addAll(lineContent);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                String result = removeUrl(line);
                result = removeSpecialChar(result);
                lstSentences.add(Arrays.asList(result.split("\\.")));
                tokenList.addAll(tokenizer.tokenize(result));
            }
            for(Token token: tokenList){
                //ignore punctuation such as "-" or "."
//                if(token.getLemma().equalsIgnoreCase("PUNCT"))
//                    continue;
                arrContent.add(token.getWord().replace(" ","_"));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return arrContent;
    }

    public ArrayList<String> readFile(String fileName){
        ArrayList<String> arrContent = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                arrContent.add(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return arrContent;
    }
    public String removeSpecialChar(String word) {
        word = word.toLowerCase();
        Pattern pt = null;
        try {
            String reg = "[,/?|\\[\\](){}\\\\^([0-9]+)^!@#$%^&*()`~<>:;+=|\"]";
            pt = Pattern.compile(reg);

            Matcher match = pt.matcher(word);
            while (match.find()) {
                String s = match.group();
                word = word.replace(s,"");
                //remove ...
                word = word.replaceAll("\\.{3,}","");
            }
        }catch (Exception e) {
            e.printStackTrace();
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

    public String getPureContentFromFile(String path){
        ArrayList<String> content = new ArrayList<>();
        if(isEN){
            content = fileToList(path);
            content.removeAll(lstStopwordEN);
        }else{
            content = fileToListVN(path);
            content.removeAll(lstStopwordVN);
        }
//        System.out.println("LENG: " + content.size());
        return String.join(" ", content);
    }


}
