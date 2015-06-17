package net.jammidi.service;

import net.jammidi.dto.User;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class UserService {

    @Resource
    private MongoOperations mongoTemplate;

    public List<User> getAll() {
        List<User> events = mongoTemplate.findAll(User.class);
        return events;
    }

    public void insert(User user) {
        mongoTemplate.save(user);
    }

    public User get(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        return mongoTemplate.findOne(query, User.class);
    }

    public void update(String username, User user) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        Update update = new Update();
        update.set(username, user);
        mongoTemplate.upsert(query, update, User.class);
    }

    public void delete(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        mongoTemplate.remove(query, User.class);
    }
}
