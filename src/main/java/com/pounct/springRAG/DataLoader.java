package com.pounct.springRAG;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
// import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Component
public class DataLoader {
    private static Logger log = LoggerFactory.getLogger(DataLoader.class);
    //@Value("classpath:/pdfs/quran-simple.pdf")
    //private Resource pdfFile;
    @Value("classpath:/texts/quran-simple.txt")
    private Resource txtFile;
    @Value("vsdemo.json")
    private String vectorStoreName;

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {

        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingModel);
        String path = Path.of("src", "main", "resources", "vectorStore").toFile().getAbsolutePath() + "/" + vectorStoreName;
        File fileVectorStore = new File(path);

        if (fileVectorStore.exists()) {
            log.info("fileVectorStore exists " + path);
            simpleVectorStore.load(fileVectorStore);
        } else {
            try {
                // Traiter le fichier PDF
                // PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(pdfFile);
                // List<Document> documents = pagePdfDocumentReader.get();

                // Traiter le fichier texte et ajouter les chunks
                List<Document> textDocuments = loadTextFile(txtFile);
                // documents.addAll(textDocuments);
                // Diviser les documents en chunks si nécessaire
                TextSplitter textSplitter = new TokenTextSplitter();
                List<Document> chunks = textSplitter.split(textDocuments);

                log.info(String.valueOf(chunks.get(0)));
                // Ajouter les chunks au vector store
                simpleVectorStore.add(chunks);
                simpleVectorStore.save(fileVectorStore);
            } catch (NonTransientAiException e) {
                if (e.getMessage().contains("You exceeded your current quota")) { // insufficient_quota
                    log.error("Quota exceeded. Please check your plan and billing details.");
                    // Vous pouvez décider d'arrêter le traitement ici ou de notifier l'utilisateur
                } else {
                    throw e; // Rethrow l'exception si ce n'est pas une erreur de quota
                }
            } catch (Exception e) {
                log.error("An error occurred while processing documents", e);
                // Gérer d'autres types d'erreurs
            }
        }
        return simpleVectorStore;
    }

    private List<Document> loadTextFile(Resource txtFile) {
        try {
            // Lire le contenu du fichier texte
            String content = new String(txtFile.getInputStream().readAllBytes());

            // Créer un document avec le contenu entier
            Document document = new Document(content);

            // Utiliser un TextSplitter pour diviser le contenu en plusieurs documents
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> chunks = textSplitter.split(List.of(document));

            return chunks;
        } catch (Exception e) {
            log.error("Failed to load text file", e);
            return List.of();
        }
    }

}
