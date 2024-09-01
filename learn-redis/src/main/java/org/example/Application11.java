package org.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.List;

public class Application11 {
    private static final Log LOG = LogFactory.getLog(Application11.class);

    public static void main(String[] args) {

        List<RedisNode> list = Arrays.asList(new RedisNode("master", 30371), new RedisNode("node1", 30372));
        RedisClusterConfiguration redisConfiguration = new RedisClusterConfiguration();
        redisConfiguration.setClusterNodes(list);
        redisConfiguration.setPassword("123456");
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisConfiguration);
        connectionFactory.afterPropertiesSet();

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(StringRedisSerializer.UTF_8);
        template.afterPropertiesSet();

        template.opsForValue().set("foo", "bar");

        LOG.info("Value at foo:" + template.opsForValue().get("foo"));

        connectionFactory.destroy();
    }
}
