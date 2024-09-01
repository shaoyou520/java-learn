package org.example.controller;

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
     * @return user list
     */
    @GetMapping("find/{userId}")
    public Result<User> edit(@PathVariable("userId") String userId) {
        User user = redisService.get(userId);
        return Result.success(user);
    }

}
