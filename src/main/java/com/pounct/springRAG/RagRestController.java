package com.pounct.springRAG;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class RagRestController {
    private final ChatClient chatClient;
    @Value("classpath:/prompts/prompt.st")
    private Resource promptResource;
    // on injecte VectorStore pour recuperer le context
    private final VectorStore vectorStore;

    public RagRestController(ChatClient.Builder builder, VectorStore vectorStore) {

        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    // localgost:.../ask?question=donne moi les sens et cas d'utilisation de "palabra"
    // pour le json /ask?question=donne moi au format json les sens et cas d'utilisation de "palabra"
    @GetMapping(value = "/ask", produces = MediaType.TEXT_PLAIN_VALUE) // on peut ajouter MediaType
    public String ask(String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("La pregunta no puede estar vacía");
        }
        // avant envoye de requette
        // on abesoin du prompt prompt.st
        PromptTemplate promptTemplate = new PromptTemplate(promptResource);
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.query(question).withTopK(4)); // k=4 default value
        // List<Document> documents = vectorStore.similaritySearch(question);
        List<String> context = documents.stream().map(Document::getContent).toList();
        // creer un prompt
        Prompt prompt = promptTemplate.create(Map.of("context", context, "question", question));
        // envoye directe : return chatClient.prompt().user(question).call().content();
        return chatClient.prompt(prompt).call().content();
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public String handleInvalidQuestion(IllegalArgumentException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public String handleServerError(Exception e) {
        return "Ocurrió un error al procesar la solicitud: " + e.getMessage();
    }
}
