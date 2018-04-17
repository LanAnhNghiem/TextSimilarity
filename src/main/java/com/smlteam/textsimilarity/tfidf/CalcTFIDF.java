package com.smlteam.textsimilarity.tfidf;

import com.smlteam.textsimilarity.constant.Constants;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CalcTFIDF {

    public HashMap<String, Double> calcTFIDF(IndexReader reader, int docNum) throws IOException {
        HashMap<String, Double> vector = new HashMap<>();
        Document document = reader.document(docNum);
        Terms termVector = reader.getTermVector(docNum, "contents");
        TermsEnum itr = termVector.iterator();
        BytesRef term = null;

        while((term = itr.next())!=null){
            String termText = term.utf8ToString();
            Term termInstance = new Term("contents", term);
            long termFreq = itr.totalTermFreq();
            long docCount = reader.docFreq(termInstance);
            double tfidf = (double)termFreq/ termVector.size();
            vector.put(termText, tfidf);
//            System.out.println("term: "+termText+", termFreq = "+termFreq+", docCount = "+docCount);
        }

        return vector;
    }
    public List<HashMap<String, Double>>calcAllTFIDF() {
        IndexReader reader = null;
        List<HashMap<String, Double>> vectorList = new LinkedList<>();
        try {
            reader = DirectoryReader.open(FSDirectory.open(new File(Constants.INDEX).toPath()));

            for(int i=0 ; i< reader.numDocs(); i++){
                vectorList.add(calcTFIDF(reader, i));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vectorList;
    }

}
