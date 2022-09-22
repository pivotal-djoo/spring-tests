package com.djoo.springtests.clients;

import com.djoo.springtests.models.Note;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

public class NotesClient {

    private WebClient webClient;
    private String notesApiBaseUrl;

    public NotesClient(@Value("${server.baseUrl}") String baseUrl) {
        webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.notesApiBaseUrl = baseUrl;
    }

    public Note createNote(Note note) {
        return webClient
                .post()
                .uri(UriComponentsBuilder
                        .fromHttpUrl(notesApiBaseUrl)
                        .path("/notes")
                        .build()
                        .toUri())
                .bodyValue(note)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(Note.class)
                .block();
    }

    public List<Note> getNotes() {
        return webClient
                .get()
                .uri(UriComponentsBuilder
                        .fromHttpUrl(notesApiBaseUrl)
                        .path("/notes")
                        .build()
                        .toUri())
                .retrieve()
                .bodyToFlux(Note.class)
                .collectList()
                .block();
    }

}
