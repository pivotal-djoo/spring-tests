package com.djoo.springtests.integration;

import com.djoo.springtests.SpringTestsApplication;
import com.djoo.springtests.models.Note;
import com.djoo.springtests.persistence.NotesRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringTestsApplication.class)
@AutoConfigureMockMvc
class NotesIntegrationTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private MockMvc mvc;

    @AfterEach
    void tearDown() {
        notesRepository.deleteAll();
    }

    @Test
    public void getNotes_returnsNotes() throws Exception {
        List<Note> notes = asList(
                Note.builder().text("Pick up bread").build(),
                Note.builder().text("Grab some butter").build()
        );
        notesRepository.saveAll(notes);

        MockHttpServletResponse response = mvc.perform(get("/notes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<Note> actualNote = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<Note>>() {
        });

        assertThat(actualNote).hasSize(2);
        assertThat(actualNote.get(0).getText()).isEqualTo("Pick up bread");
        assertThat(actualNote.get(1).getText()).isEqualTo("Grab some butter");
    }

    @Test
    public void createNote_createsNewNote() throws Exception {
        Note notes = Note.builder().text("Pick up catfood").build();

        mvc.perform(post("/notes")
                .content(objectMapper.writeValueAsString(notes))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());

        List<Note> savedNotes = notesRepository.findAll();
        assertThat(savedNotes.get(0).getText()).isEqualTo(notes.getText());
    }
}
