package javaapplication11;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author NVTC
 */
public class JavaApplication11 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        final long startTime = System.nanoTime();
        System.out.println("TIME: " + (System.nanoTime() - startTime) / 1e6);
        String path = "Demo.txt";
        String newContent = getPureContentFromFile(path);
        System.out.println(newContent);

    }

    //file's content to list converting function
    public static ArrayList<String> fileToList(String fileName) {
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

    public static String removeSpecialChar(String word) {
        Pattern pt = null;
        try {
            pt = Pattern.compile("[^a-zA-Z]");

            Matcher match = pt.matcher(word);
            while (match.find()) {
                String s = match.group();
                word = word.replaceAll("\\" + s, " ");
            }
        }catch (Exception e) {
            System.out.println("LOI SML");
            return "";
        }
        return word;
    }

    public static String removeUrl(String commentstr) {
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

    public static String cleanWord(String word) {
        word = removeUrl(word);
        word = removeSpecialChar(word);
        return word;
    }

    public static String getPureContentFromFile(String path) {
        ArrayList<String> content = fileToList(path);
        for (int i = 0; i < content.size(); i++) {
            content.set(i, cleanWord(content.get(i)));
        }
        for (String string : new ArrayList<>(content)) {
            if (isStopWord(string)) {
                content.remove(string);
            }
        }
        System.out.println("LENG: " + content.size());
        return String.join(" ", content);
    }

    private static boolean isStopWord(String string) {
        ArrayList<String> lstStopWord = fileToList("stopwords_en.txt");
        for (String word : lstStopWord) {
            if (word.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }
}
