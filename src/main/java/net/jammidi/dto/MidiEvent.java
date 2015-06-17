package net.jammidi.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection="events")
public class MidiEvent {

    @Id
    private int id;
    private int interval;
    private int index;
    private String a;
    private int key;
    private String b;
    private String user;
    private int version;
    private String desc;
    private String songTitle;

}
