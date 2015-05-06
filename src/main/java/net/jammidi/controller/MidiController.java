package net.jammidi.controller;

import net.jammidi.dto.MidiEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/")
public class MidiController {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Resource
  private MongoOperations mongoTemplate;

  @Autowired
  public SimpMessageSendingOperations messagingTemplate;

  @MessageMapping("/midi")
  @SendTo("/topic/midi")
  public MidiEvent sendMidi(MidiEvent message) {
    logger.info("Midi event sent: " + message.getKey());

    mongoTemplate.save(message);

    return message;
  }

  @MessageMapping("/replay")
  public void replay() throws InterruptedException {
    List<MidiEvent> events = mongoTemplate.findAll(MidiEvent.class);
    for (MidiEvent event : events) {
      Thread.sleep(event.getInterval());
      messagingTemplate.convertAndSend( "/topic/midi", event);
    }
    mongoTemplate.dropCollection("events");
  }
}
