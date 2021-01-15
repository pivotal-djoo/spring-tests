package com.djoo.springtests.messaging;

import com.djoo.springtests.NotesService;
import com.djoo.springtests.models.Note;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@AllArgsConstructor
@Component("NotesEventListener")
class NotesEventListener implements Consumer<Note> {
    NotesService notesService;

    @Override
    public void accept(Note note) {
        notesService.createNote(note);
    }
}
