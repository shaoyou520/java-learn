package org.example;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
        syncCommands.set("cc", "dd");
        connection.close();
        clusterClient.shutdown();

    }
}
