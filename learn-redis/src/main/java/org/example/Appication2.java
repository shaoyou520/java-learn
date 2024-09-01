package org.example;

import io.lettuce.core.RedisURI;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Appication2 {
    private static final Log LOG = LogFactory.getLog(Application11.class);

    public static void main(String[] args) {
        RedisURI node1 = RedisURI.Builder.redis("master")
                .withPort(30370)
                .withPassword("123456")
                .withDatabase(16)
                .build();
        RedisURI node2 = RedisURI.Builder.redis("master")
                .withPort(30371)
                .withPassword("123456")
                .withDatabase(16)
                .build();
        RedisClusterClient clusterClient = RedisClusterClient.create(Arrays.asList(node1, node2));

        StatefulRedisClusterConnection<String, String> connection = clusterClient.connect();
        RedisAdvancedClusterCommands<String, String> syncCommands = connection.sync();
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
        Long ss = syncCommands.eval(script.getBytes(StandardCharsets.UTF_8), ScriptOutputType.INTEGER, new String[]{"key_xx"}, "key_xx", "30");
        System.out.println(ss);
        connection.close();
        clusterClient.shutdown();

    }
}
