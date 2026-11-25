package com.example.chatbot.logic;

import com.example.chatbot.model.NewsItem;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LuceneSearchService {
    private final Analyzer analyzer = new RussianAnalyzer();
    private final Directory directory = new ByteBuffersDirectory();
    private final SearchHelper searchHelper;

    public LuceneSearchService(SearchHelper searchHelper) {
        this.searchHelper = searchHelper;
    }
    public void indexNews(List<NewsItem> newsList) {
        try {
            IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer));
            writer.deleteAll();

            for (NewsItem item : newsList) {
                Document doc = new Document();
                doc.add(new TextField("title", item.title(), Field.Store.YES));
                doc.add(new TextField("text", item.text(), Field.Store.YES));
                doc.add(new TextField("url", item.url(), Field.Store.YES));
                writer.addDocument(doc);
            }
            writer.close();

        } catch (Exception e) {
            System.out.println("Ошибка при индексации новостей: " + e.getMessage());
        }
    }

    public List<NewsItem> search(String keyword) {
        List<NewsItem> results = new ArrayList<>();

        try {
            DirectoryReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            List<String> searchWords = searchHelper.getSynonyms(keyword);

            // Создаем парсеры для разных полей
            QueryParser titleParser = new QueryParser("title", analyzer);
            QueryParser textParser = new QueryParser("text", analyzer);

            BooleanQuery.Builder builder = new BooleanQuery.Builder();

            for (String word : searchWords) {
                builder.add(new BooleanClause(titleParser.parse(word), BooleanClause.Occur.SHOULD));
                builder.add(new BooleanClause(textParser.parse(word), BooleanClause.Occur.SHOULD));
            }
            Query query = builder.build();

            TopDocs topDocs = searcher.search(query, 5);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.storedFields().document(scoreDoc.doc);
                results.add(new NewsItem(
                        doc.get("title"),
                        doc.get("text"),
                        "Lucene",
                        doc.get("url"),
                        LocalDateTime.now()
                ));
            }
            reader.close();

        } catch (Exception e) {
            System.out.println("Ошибка при поиске в Lucene: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }
}