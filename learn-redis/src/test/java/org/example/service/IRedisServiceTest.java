package org.example.service;

import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.example.entry.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
class IRedisServiceTest {

//    两者的关系是StringRedisTemplate继承RedisTemplate。
//    两者的数据是不共通的；也就是说StringRedisTemplate只能管理StringRedisTemplate里面的数据，RedisTemplate只能管理RedisTemplate中的数据。
//    SDR默认采用的序列化策略有两种，一种是String的序列化策略，一种是JDK的序列化策略。
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    void testExecute() {
        Integer num = 12;
        Integer result = (Integer) redisTemplate.execute((RedisCallback<Integer>) connection -> {
            RedisStringCommands commands = connection.stringCommands();
            int n = 0;
            for (int i = 0; i < num; i++) {
                // 写入缓存
                commands.set(("execute_" + i ).getBytes(), ("hello world " + i).getBytes());

                // 从缓存获取值
                byte[] value = commands.get("execute_key".getBytes());
                System.out.println(new String(value));
                n++;
            }
            return n;
        });
        System.out.println(result);
    }

    @Test
    public void testRedisScript() {
        // 使用 lua 脚本实现获取锁
        // ARGV[1] = lock-key
        // ARGV[2] = 30
        String script = "local key = ARGV[1];" +
                "local expiration = ARGV[2];" +
                "local value = 1;" +
                "if redis.call('EXISTS', key) == 1 then " +
                "  return -1; " + // 如果键存在，则返回-1
                "else " +
                "  redis.call('SET', key, value);" + // 如果键不存在，则设置键和值
                "  redis.call('EXPIRE', key, expiration);" + // 为键设置过期时间
                "  return 1;" + // 返回1，表示锁获取成功
                "end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        // 其中，lock-key 表示锁的键，30 表示过期时间为30秒
        Long val = (Long) stringRedisTemplate.execute(redisScript, Arrays.asList("lock-key"), "lock-key", "30");
        System.out.println("val = " + val);

    }

    @Test
    public void testSet() {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        redisTemplate.opsForZSet();
        Message message = new Message();
        message.setId("1");
        message.setContent("11111111");
        message.setApprovalNum(1);
        message.setScore(8.0);
        zSetOperations.add("set1mm", message, message.getScore());
        message.setScore(4.0);
        message.setId("2");
        zSetOperations.add("set1mm", message, message.getScore());
        message.setScore(-7.0);
        message.setId("9");
        zSetOperations.add("set1mm", message, message.getScore());
        message.setScore(5.0);
        message.setId("7");
        zSetOperations.add("set1mm", message, message.getScore());
        message.setScore(5.0);
        message.setId("3");
        zSetOperations.add("set1mm", message, message.getScore());

        Set<ZSetOperations.TypedTuple<Message>> set = zSetOperations.rangeByScoreWithScores("set1mm", 0.0, 5.0);
        Set<Message> messages = set.stream().map(item -> item.getValue()).collect(Collectors.toSet());
        messages.forEach(item -> System.out.println(item));
    }
}