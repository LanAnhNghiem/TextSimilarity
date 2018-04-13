package com.smlteam.textsimilarity.preprocess;

import ai.vitk.tok.Tokenizer;
import ai.vitk.type.Token;
import com.smlteam.textsimilarity.constant.Constants;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.ml.feature.StopWordsRemover;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.collection.JavaConverters;
import scala.tools.cmd.Meta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class Preprocesser {

    public static void main(String[] args){
        SparkSession spark = new SparkSession.Builder().appName("TextSimilarity")
                .master("local").getOrCreate();
//        tokenizerVN(preprocesser(spark), spark);
//        tokenizerEN(preprocesser(spark));
        final long startTime = System.nanoTime();
//        filterStopwordsVN(spark, tokenizerVN(preprocesser(spark), spark));
        filterStopwordsEN(tokenizerEN(preprocesser(spark)));
        System.out.print("\n"+(System.nanoTime() - startTime) / 1e6);
//        filterStopwordsEN(tokenizerEN(preprocesser(spark)));
    }
    public static Dataset<Row> preprocesser(SparkSession spark){

        //create dataset with column named "origin"
        Dataset<Row> rawSet = spark.read().text(Constants.ORIGIN).withColumnRenamed("value", "origin");
        //to lowercase
        rawSet = rawSet.withColumn("origin", functions.lower(new Column("origin")));

        //create table document
        rawSet.createOrReplaceTempView("document");
        //filter all the url
        Dataset<Row> docSet = spark.sql("select REGEXP_REPLACE(origin,'((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\\\\\))+[\\\\w\\\\d:#@%/;$()~_?\\\\+-=\\\\\\\\\\\\.&]*)','') as filterURL from document");
        //filter all the punctuation
        docSet = docSet.withColumn("filterPunc", functions
                .regexp_replace(docSet.col("filterURL"), Constants.PUNCTUATION_REGEX,""));
        docSet = docSet.drop("filterURL");
//        docSet.show();
        return docSet;
    }
    public static Dataset<Row> tokenizerVN(Dataset<Row> docSet, SparkSession spark){
        Tokenizer tokenizer = new Tokenizer();
        String docStr = docSet.as(Encoders.STRING()).first();
        List<Token>tokenList = tokenizer.tokenize(docStr);
        List<String> docList = new LinkedList<>();
        for(Token token: tokenList){
            docList.add(token.getWord().replace(" ","_"));
        }
        List<Row> data = Arrays.asList(RowFactory.create(docList));
        StructType schema = new StructType(new StructField[]{new StructField("tokenized",
                DataTypes.createArrayType(DataTypes.StringType), false, Metadata.empty())});
        Dataset<Row> resultSet = spark.createDataFrame(data, schema);
        resultSet.show(false);
        resultSet.printSchema();
        return resultSet;
    }
    public static Dataset<Row> tokenizerEN(Dataset<Row> docSet){
        org.apache.spark.ml.feature.Tokenizer tokenizer = new org.apache.spark.ml.feature.Tokenizer()
                .setInputCol("filterPunc").setOutputCol("tokenized");
        Dataset<Row> resultSet = tokenizer.transform(docSet).drop("filterPunc");
        resultSet.show(false);
        resultSet.printSchema();
        return resultSet;
    }
    public static void filterStopwordsVN(SparkSession spark, Dataset<Row> tokenizedSet){
        Dataset<Row> stopWords = spark.read().text(Constants.STOPWORDS_VN);
//        stopWords.show();
        tokenizedSet = tokenizedSet.withColumn("tokenized", functions.explode(functions.col("tokenized")));
        tokenizedSet.show(false);
        Dataset<Row> filtedSet = tokenizedSet.except(stopWords).as("filtered");
        filtedSet.show(100,false);
    }
    public static void filterStopwordsEN(Dataset<Row> tokenizedSet){
        StopWordsRemover remover = new StopWordsRemover().setInputCol("tokenized").setOutputCol("filtered");
        Dataset<Row> resultSet = remover.transform(tokenizedSet).drop("tokenized");
        resultSet.show(false);
    }
}
