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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class TextIndexer {
    public static void main(String[] args){
        final long startTime = System.nanoTime();
        String indexPath = Constants.INDEX;
        String docsPath = Constants.DOCS;
        boolean create = true;

        if(docsPath == null){
            System.exit(1);
        }

        final Path docDir = Paths.get(docsPath);
//        if (!Files.isReadable(docDir)) {
//            System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
//            System.exit(1);
//        }

        Date start = new Date();
        try{
            System.out.println("Indexing to directory '" + indexPath + "'...");
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
            documents.add(tokenizer(Constants.ORIGIN));
            documents.add(tokenizer(Constants.TEST));
            indexDocs(writer, docDir, documents);

            writer.close();

        }catch(IOException e){
            e.printStackTrace();
        }
        final long duration = System.nanoTime() - startTime;
        convertTime(TimeUnit.NANOSECONDS.toSeconds(duration));
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
        doc.add(new TextField("contents", contents, Field.Store.YES));
        if(writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE){
            System.out.print("Adding file "+ file.toString());
            writer.addDocument(doc);
        }else{
            System.out.print("Updating file "+ file.toString());
            writer.updateDocument(new Term("path", file.toString()), doc);
        }
    }
    public static void convertTime(long seconds){
        long min = seconds/60;
        long sec = seconds-min*60;
        System.out.print("\n"+ min +" minutes "+sec+" seconds");
    }
}
