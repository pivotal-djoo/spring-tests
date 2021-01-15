package com.djoo.springtests;

import com.djoo.springtests.messaging.NotesEventPublisher;
import com.djoo.springtests.models.Note;
import com.djoo.springtests.persistence.NotesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotesService {
    private NotesRepository notesRepository;
    private NotesEventPublisher notesEventPublisher;

    public List<Note> getAllNotes() {
        return notesRepository.findAll();
    }

    public Note createNote(Note newNote) {
        Note note = notesRepository.save(newNote);
        notesEventPublisher.publish(newNote);
        return note;
    }
}
