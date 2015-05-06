package net.jammidi.controller;

import net.jammidi.dto.MidiEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("/")
public class MidiController {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Resource
  private MongoOperations mongoTemplate;

  @MessageMapping("/midi")
  @SendTo("/topic/midi")
  public MidiEvent sendMidi(MidiEvent message) {
    logger.info("Midi event sent: " + message.getKey());

    mongoTemplate.save(message);

    return message;
  }
}
