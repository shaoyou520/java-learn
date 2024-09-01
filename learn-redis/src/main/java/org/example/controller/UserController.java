package org.example.controller;

import jakarta.websocket.server.PathParam;
import org.example.entry.Message;
import org.example.entry.User;
import org.example.service.IRedisService;
import org.example.respond.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IRedisService<User> redisService;

    @Autowired
    private IRedisService<Message> msgRedisService;

    /**
     * @param user user param
     * @return user
     */
    @PostMapping("add")
    public Result<User> add(@RequestBody User user) {
        redisService.set(String.valueOf(user.getId()), user);
        user = redisService.get(String.valueOf(user.getId()));
        return Result.success(user);
    }

    /**
     *
     * @param key
     * @param msg
     * @return
     */
    @PostMapping("addset/{key}")
    public Result<Message> addSet(@PathVariable("key") String key, @RequestBody Message msg) {
        msgRedisService.addSet(key, msg, (double)msg.getScore());
        return Result.success(msg);
    }


    /**
     * @return user list
     */
    @GetMapping("find/{userId}")
    public Result<User> edit(@PathVariable("userId") String userId) {
        User user = redisService.get(userId);
        return Result.success(user);
    }

}
