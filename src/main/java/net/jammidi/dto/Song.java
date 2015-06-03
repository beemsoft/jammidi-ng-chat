package net.jammidi.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection="songs")
public class Song {

    private String title;
    private String description;
    private Date startDate;
    private User author;
    private List<User> contributors;
    private int version;

}
