package net.jammidi.controller;

import net.jammidi.dto.MidiEvent;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class EventComparator implements Comparator<MidiEvent> {

    @Override
    public int compare(MidiEvent o1, MidiEvent o2) {
        return o1.getIndex() - o2.getIndex();
    }
}
