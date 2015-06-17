package net.jammidi.controller;

import net.jammidi.dto.MidiEvent;
import net.jammidi.dto.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/")
public class MidiController {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Resource
  private MongoOperations mongoTemplate;

  @Autowired
  public SimpMessageSendingOperations messagingTemplate;

  @Autowired
  private EventComparator eventComparator;

  private boolean isReplaying = false;
  private int replayVersion;

  @MessageMapping("/midi")
  public MidiEvent sendMidi(@Payload MidiEvent midiEvent) {
    logger.info("Midi event sent: " + midiEvent.getKey());

    if (isReplaying) {
      midiEvent.setVersion(replayVersion + 1);
    } else {
      messagingTemplate.convertAndSend( "/topic/midi", midiEvent);
    }

    mongoTemplate.save(midiEvent);

    return midiEvent;
  }

  @MessageMapping("/replay")
  public void replay(@Payload Song song) throws InterruptedException {
    isReplaying = true;
    replayVersion = song.getVersion();
    Query query = new Query();
    query.addCriteria(Criteria.where("songTitle").is(song.getTitle()).and("version").is(song.getVersion()));
    List<MidiEvent> events = mongoTemplate.find(query, MidiEvent.class);

    for (MidiEvent event : events) {
      Thread.sleep(event.getInterval());
      messagingTemplate.convertAndSend( "/topic/midi", event);
    }
    isReplaying = false;
  }

  @MessageMapping("/replayAll")
  public void replayAll(@Payload String songTitle) throws InterruptedException {
    if (isReplaying) {
      return;
    }
    isReplaying = true;
    replayVersion = 3;
    Query query = new Query();
    query.addCriteria(Criteria.where("songTitle").is(songTitle).and("version").is(replayVersion - 1));
    List<MidiEvent> events1 = mongoTemplate.find(query, MidiEvent.class);

    List<MidiEvent> eventIndex = new ArrayList<>();
    int index = 0;
    for (MidiEvent event : events1) {
      index = index + event.getInterval();
      event.setIndex(index);
      eventIndex.add(event);
    }
    query = new Query();
    query.addCriteria(Criteria.where("songTitle").is(songTitle).and("version").is(replayVersion - 2));
    List<MidiEvent> events2 = mongoTemplate.find(query, MidiEvent.class);
    index = 0;
    for (MidiEvent event : events2) {
      index = index + event.getInterval();
      event.setIndex(index);
      eventIndex.add(event);
    }

    Collections.sort(eventIndex, eventComparator);
    MidiEvent prevEvent = null;
    for (MidiEvent event: eventIndex) {
      if (prevEvent != null) {
        event.setInterval(event.getIndex() - prevEvent.getIndex());
      }
      prevEvent = event;
    }

    for (MidiEvent event : eventIndex) {
      Thread.sleep(event.getInterval());
      messagingTemplate.convertAndSend( "/topic/midi", event);
    }
    isReplaying = false;
  }

  @MessageMapping("/clear")
  public void clear(@Payload Song song) throws InterruptedException {
    logger.info("Clear events");

    Query query = new Query();
    query.addCriteria(Criteria.where("songTitle").is(song.getTitle()).and("version").is(song.getVersion()));
    mongoTemplate.remove(query, MidiEvent.class);
  }
}
