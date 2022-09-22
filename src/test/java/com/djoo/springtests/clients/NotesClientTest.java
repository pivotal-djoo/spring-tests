package com.djoo.springtests.clients;

import com.djoo.springtests.models.Note;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest()
class NotesClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private WireMock wireMock;
    private NotesClient subject;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wireMockRuntimeInfo) {
        wireMock = wireMockRuntimeInfo.getWireMock();
        subject = new NotesClient(wireMockRuntimeInfo.getHttpBaseUrl());
    }

    @Test
    void createNote_makesPostRequestToApi() throws JsonProcessingException {
        Note note = Note.builder()
                .text("note 1")
                .build();

        stubFor(post(anyUrl())
                .willReturn(okForJson(note)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(201)));

        Note actualNote = subject.createNote(note);

        wireMock.verifyThat(postRequestedFor(urlPathEqualTo("/notes"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(note))));

        assertThat(actualNote).isEqualTo(note);
    }

    @Test
    void getNotes_makesGetRequestToApi() {
        List<Note> notes = asList(
                Note.builder().text("notes 1").build(),
                Note.builder().text("notes 2").build());

        stubFor(get(anyUrl())
                .willReturn(okForJson(notes)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)));

        List<Note> actualNotes = subject.getNotes();

        wireMock.verifyThat(getRequestedFor(urlPathEqualTo("/notes")));

        assertThat(actualNotes).isEqualTo(notes);
    }
}
