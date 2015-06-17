package net.jammidi.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Date;

@Data
@Document(collection="users")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class User {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Date latestLogon;

}