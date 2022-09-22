package com.djoo.springtests.apis;


import com.djoo.springtests.NotesService;
import com.djoo.springtests.models.Note;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotesController.class)
public class NotesControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private NotesService notesService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void getNotes_returnsNotes() throws Exception {
        List<Note> notes = asList(Note.builder().text("note 1").build(),
                Note.builder().text("note 2").build());
        when(notesService.getAllNotes()).thenReturn(notes);

        MockHttpServletResponse response = mvc.perform(get("/notes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<Note> actualNotes = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<Note>>() {
        });

        assertThat(actualNotes).hasSize(2);
        assertThat(actualNotes.get(0).getText()).isEqualTo("note 1");
        assertThat(actualNotes.get(1).getText()).isEqualTo("note 2");
    }

    @Test
    public void createNote_createsNewNote() throws Exception {
        Note note = Note.builder().text("note 1").build();

        when(notesService.createNote(note)).thenReturn(note);

        mvc.perform(post("/notes")
                        .content(objectMapper.writeValueAsString(note))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":null,\"text\":\"note 1\"}"));

        verify(notesService).createNote(note);
    }
}
