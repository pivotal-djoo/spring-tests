package com.djoo.springtests;

import com.djoo.springtests.messaging.NotesEventPublisher;
import com.djoo.springtests.models.Note;
import com.djoo.springtests.persistence.NotesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotesServiceTest {

    @Mock
    private NotesRepository notesRepository;

    @Mock
    private NotesEventPublisher notesEventPublisher;

    @InjectMocks
    private NotesService subject;

    @Test
    public void getAllNotes_returnsAllNotes() {
        List<Note> notes = asList(
                Note.builder().text("note 1").build(),
                Note.builder().text("note 2").build()
        );
        when(notesRepository.findAll()).thenReturn(notes);

        List<Note> actualNotes = subject.getAllNotes();
        assertThat(actualNotes.size()).isEqualTo(2);
        assertThat(actualNotes.get(0).getText()).isEqualTo(notes.get(0).getText());
        assertThat(actualNotes.get(1).getText()).isEqualTo(notes.get(1).getText());
    }

    @Test
    public void newNote_savesNewNoteToRepository() {
        Note newNote = Note.builder().text("note 1").build();
        Note expectedSavedNote = Note.builder().id(1L).text(newNote.getText()).build();
        when(notesRepository.save(newNote)).thenReturn(expectedSavedNote);

        Note savedNote = subject.createNote(newNote);
        assertThat(savedNote).isEqualTo(expectedSavedNote);
    }

    @Test
    void newNote_publishesNewNoteToAMQP() {
        Note newNote = Note.builder().text("note 1").build();
        subject.createNote(newNote);
        verify(notesEventPublisher).publish(newNote);
    }
}
