package com.djoo.springtests.messaging;

import com.djoo.springtests.models.Note;
import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotesEventPublisher {

    private final StreamBridge streamBridge;

    public void publish(Note note) {
        streamBridge.send("NotesEventPublisher-out-0", note);
    }

}
