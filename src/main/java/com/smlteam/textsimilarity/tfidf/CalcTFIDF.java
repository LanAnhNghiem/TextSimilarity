package com.smlteam.textsimilarity.tfidf;

import com.smlteam.textsimilarity.constant.Constants;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CalcTFIDF {

    public HashMap<String, Double> calcTFIDF(IndexReader reader, int docNum) throws IOException {
        HashMap<String, Double> vector = new HashMap<>();
//        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(Constants.INDEX).toPath()));
        Document document = reader.document(docNum);
        Terms termVector = reader.getTermVector(docNum, "contents");
        Fields field = reader.getTermVectors(docNum);
        TermsEnum itr = termVector.iterator();
        BytesRef term = null;


        while((term = itr.next())!=null){
            PostingsEnum docEnum = MultiFields.getTermDocsEnum(reader,"contents", term);
            docEnum.freq();
            String termText = term.utf8ToString();
            Term termInstance = new Term("contents", term);
            long termFreq = reader.totalTermFreq(termInstance);
            long docCount = reader.docFreq(termInstance);
            itr.postings(docEnum).nextPosition();
            double tfidf = ((double)termFreq/ termVector.size())*(Math.log((double)reader.numDocs()/docCount));
            vector.put(termText, tfidf);
            System.out.println("term: "+termText+", termFreq = "+termFreq+", docCount = "+docCount);
        }

        return vector;
    }
    public List<HashMap<String, Double>>calcAllTFIDF() throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(Constants.INDEX).toPath()));
        List<HashMap<String, Double>> vectorList = new LinkedList<>();
        for(int i=0 ; i< reader.numDocs(); i++){
            vectorList.add(calcTFIDF(reader, i));
        }
        reader.close();
        return vectorList;
    }
}
