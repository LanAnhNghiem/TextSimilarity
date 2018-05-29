package com.smlteam.textsimilarity.index;

import ai.vitk.tok.Tokenizer;
import ai.vitk.type.Token;
import com.smlteam.textsimilarity.constant.Constants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author Lan Anh
 */
public class Indexer {

    public void indexer(String originStr, String testStr){
        String indexPath = Constants.INDEX;

        String docsPath = Constants.DOCS;

        boolean create = true;

        if(docsPath == null){
            System.exit(1);
        }

        final Path docDir = Paths.get(docsPath);
        try{
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            if (create) {
                // Create a new index in the directory, removing any
                // previously indexed documents:
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            } else {
                // Add new documents to an existing index:
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            }
            //set RAM size
            iwc.setRAMBufferSizeMB(256.0);

            IndexWriter writer = new IndexWriter(dir, iwc);

            List<String> documents = new LinkedList<>();
            documents.add(originStr);
            documents.add(testStr);
            indexDocs(writer, docDir, documents);
            writer.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void indexer(List<String> lstOrigin, List<String> lstTest){
        String indexPath = Constants.INDEX;

        String docsPath = Constants.DOCS;

        boolean create = true;

        if(docsPath == null){
            System.exit(1);
        }

        final Path docDir = Paths.get(docsPath);
        try{
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            if (create) {
                // Create a new index in the directory, removing any
                // previously indexed documents:
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            } else {
                // Add new documents to an existing index:
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            }
            //set RAM size
            iwc.setRAMBufferSizeMB(256.0);

            IndexWriter writer = new IndexWriter(dir, iwc);

            List<String> documents = new LinkedList<>();

            indexDocs(writer, docDir, documents);
            writer.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    //Tách đoạn văn thành từ hoặc cụm từ
    public static String tokenizer(String originPath){
        List<String> doc = new LinkedList<>();
        try {
            Tokenizer tokenizer = new Tokenizer();
            List<List<Token>> tokenList = Files.lines(Paths.get(originPath))
                    .map(s-> tokenizer.tokenize(s))
                    .collect(Collectors.toList());

            for(List<Token> token : tokenList){
                for(Token term: token){
                    doc.add(term.getWord().replace(" ","_"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc.toString().replace("[","").replace("]","");
    }

    public static String tokenizer2(String path){
        List<String> doc = new LinkedList<>();
        List<Token> tokens = new LinkedList<>();
        BufferedReader br = null;
        FileReader fr = null;
        Tokenizer tokenizer = new Tokenizer();
        final ExecutorService executor = Executors.newFixedThreadPool(5);
        final List<Future<?>> futures = new ArrayList<>();
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                tokens.addAll(tokenizer.tokenize(line));
            }
            for(Token token: tokens){
                Future<?> future = executor.submit(() -> {
                    doc.add(token.getWord().replace(" ","_"));
                });
                futures.add(future);

            }
            try {
                for (Future<?> future : futures) {
                    future.get(); // do anything you need, e.g. isDone(), ...
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(br != null){
                    br.close();
                }
                if(fr != null){
                    fr.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return doc.toString().replace("[","").replace("]","");
    }

    //index một tập các văn bản
    public static void indexDocs(final IndexWriter writer, Path path, List<String> contents) throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        for(String content: contents){
            indexDoc(writer, path, timestamp.getTime(), content);
        }
    }

    //index một văn bản
    public static void indexDoc(IndexWriter writer, Path file, long lastModified, String contents) throws IOException {
        Document doc = new Document();
        Field pathField = new StringField("path", file.toString(), Field.Store.YES);
        doc.add(pathField);
        doc.add(new LongPoint("modified", lastModified));
        FieldType myFieldType = new FieldType(TextField.TYPE_STORED);
        myFieldType.setStoreTermVectors(true);
        doc.add(new Field("contents", contents, myFieldType));
        if(writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE){
            writer.addDocument(doc);
        }else{
            writer.updateDocument(new Term("path", file.toString()), doc);
        }
    }
    public static void convertTime(double seconds){
        double min = seconds/60;
        double sec = seconds-min*60;
        System.out.print("\n"+ min +" minutes "+sec+" seconds");
    }
}
