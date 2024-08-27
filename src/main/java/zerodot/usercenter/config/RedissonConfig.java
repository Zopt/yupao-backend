package zerodot.usercenter.config;


import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redission 配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;
    private String port;

    @Bean
    public RedissonClient redissonClient(){
        // 1. Create config object  创建配置
        Config config = new Config();
        String RedisAdress = String.format("redis://%s:%s",host,port);
        config.useSingleServer().setAddress(RedisAdress).setDatabase(3);
        //创建一个实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
