package com.djoo.springtests.apis;

import com.djoo.springtests.NotesService;
import com.djoo.springtests.models.Note;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class NotesControllerWebTestClientTest {

    @MockBean
    NotesService notesService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void createNote_createsNewNote() {
        Note note = Note.builder().text("Pick up some milk").build();
        when(notesService.createNote(note)).thenReturn(note);

        webTestClient.post().uri("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(note), Note.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.text").isNotEmpty()
                .jsonPath("$.text").isEqualTo("Pick up some milk");
    }

    @Test
    public void getNotes_returnsAllNotes() {
        List<Note> notes = asList(Note.builder().text("note 1").build(),
                Note.builder().text("note 2").build());
        when(notesService.getAllNotes()).thenReturn(notes);

        FluxExchangeResult<Note> result = webTestClient.get().uri("/notes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Note.class);

        List<Note> actualNotes = result.getResponseBody().collectList().block();
        assertThat(actualNotes).hasSize(2);
        assertThat(actualNotes.get(0).getText()).isEqualTo("note 1");
        assertThat(actualNotes.get(1).getText()).isEqualTo("note 2");
    }
}
