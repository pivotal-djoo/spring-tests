package com.djoo.springtests.clients;

import com.djoo.springtests.models.Note;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class NotesClientMockWebServerTest {
    public static MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private NotesClient subject;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockWebServer.getPort());
        subject = new NotesClient(baseUrl);
    }

    @Test
    void createNote_makesPostRequestToApi() throws JsonProcessingException, InterruptedException {
        Note note = Note.builder()
                .text("note 1")
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(note))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        Note actualNote = subject.createNote(note);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getPath()).isEqualTo("/notes");
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(recordedRequest.getBody().readUtf8()).isEqualTo(objectMapper.writeValueAsString(note));
        assertThat(actualNote).isEqualTo(note);
    }

    @Test
    void getNotes_makesGetRequestToApi() throws JsonProcessingException, InterruptedException {
        List<Note> notes = asList(
                Note.builder().text("notes 1").build(),
                Note.builder().text("notes 2").build());

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(notes))
                .setHeader("Content-Type", "application/json")
        );

        List<Note> actualNotes = subject.getNotes();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/notes");
        assertThat(actualNotes).isEqualTo(notes);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}
