package com.djoo.springtests.persistence;

import com.djoo.springtests.SpringTestsApplication;
import com.djoo.springtests.models.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = SpringTestsApplication.class)
public class NotesRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotesRepository subject;

    @Test
    public void findAll_returnsAllNotes() {
        Note note1 = Note.builder().text("note 1").build();
        Note note2 = Note.builder().text("note 2").build();
        entityManager.persistAndFlush(note1);
        entityManager.persistAndFlush(note2);

        List<Note> actualNotes = subject.findAll();
        assertThat(actualNotes.size()).isEqualTo(2);
        assertThat(actualNotes.get(0).getText()).isEqualTo(note1.getText());
        assertThat(actualNotes.get(1).getText()).isEqualTo(note2.getText());
    }

    @Test
    public void save_savesThenReturnsNote() {
        Note newNote = Note.builder().text("note 1").build();
        Note savedNote = subject.save(newNote);

        assertThat(savedNote.getText()).isEqualTo(newNote.getText());
        assertThat(entityManager.find(Note.class, savedNote.getId())).isEqualTo(savedNote);
    }
}
