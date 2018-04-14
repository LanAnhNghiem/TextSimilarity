package com.smlteam.textsimilarity.tfidf;

import com.smlteam.textsimilarity.constant.Constants;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CalcTFIDF {
    public static void main(String []args) throws IOException {
        calcTF();
    }
    public static Double calcTF() throws IOException {
        int count = 0;
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(Constants.INDEX).toPath()));
        int num_doc = reader.numDocs();
        for(int i = 0; i<num_doc; i++){
            Document document = reader.document(i);
            document.getField("contents");
            Terms termVector = reader.getTermVector(i,"contents");
            TermsEnum itr = termVector.iterator();
            BytesRef term = null;
            PostingsEnum postings = null;
            int termFreq = 0;

            while((term = itr.next()) != null){
                String termText = term.utf8ToString();
                postings = itr.postings(postings, PostingsEnum.FREQS);
                int freq = postings.nextDoc();
                System.out.println("doc:" + i + ", term: " + termText + ", termFreq = " + freq);
            }
        }



//        for(String word: doc){
//            if(term.equalsIgnoreCase(word)){
//                count++;
//            }
//        }
        return null;//count/ (double)doc.size();
    }

    public Double calcIDF(String term, List<List<String>> docs){
        for(List<String> doc: docs){
            for(String word: doc){

            }
        }
        return null;
    }
}
