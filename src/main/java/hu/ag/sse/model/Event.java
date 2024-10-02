package hu.ag.sse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Event implements Serializable {

    //private final LocalDateTime dateTime = LocalDateTime.now();
    private EventType type;
    private String registrationId;
    private String message;

}
