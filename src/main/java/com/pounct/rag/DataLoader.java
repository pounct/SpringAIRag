package com.pounct.rag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {
    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingModel);
        
    }
}
