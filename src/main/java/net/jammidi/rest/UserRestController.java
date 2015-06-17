package net.jammidi.rest;

import net.jammidi.dto.Message;
import net.jammidi.dto.OutputMessage;
import net.jammidi.dto.User;
import net.jammidi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    public SimpMessageSendingOperations messagingTemplate;

    @RequestMapping("/api/users")
    public List<User> getAll() {
        return userService.getAll();
    }

    @RequestMapping(value = "/api/users/{username}", method = RequestMethod.GET)
    public User get(@PathVariable String username) {
        return userService.get(username);
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    public void insert(@RequestBody User user) {
        user.setLatestLogon(new Date());
        userService.insert(user);
        Message message = new Message();
        message.setMessage(user.getUsername() + " has registered");
        messagingTemplate.convertAndSend("/topic/message", new OutputMessage(message, new Date()));
    }

    @RequestMapping(value = "/api/users/{username}", method = RequestMethod.PUT)
    public void update(@PathVariable String username, @RequestBody User user) {
        userService.update(username, user);
    }

    @RequestMapping(value = "/api/users/{username}", method = RequestMethod.DELETE)
    public void update(@PathVariable String username) {
        userService.delete(username);
    }
}