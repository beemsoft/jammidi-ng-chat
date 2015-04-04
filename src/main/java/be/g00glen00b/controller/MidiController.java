package be.g00glen00b.controller;

import be.g00glen00b.dto.Message;
import be.g00glen00b.dto.MidiEvent;
import be.g00glen00b.dto.OutputMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/")
public class MidiController {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @MessageMapping("/midi")
  @SendTo("/topic/midi")
  public MidiEvent sendMidi(MidiEvent message) {
    logger.info("Midi event sent: " + message.getKey());
    return message;
  }
}
