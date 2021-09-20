package ru.gb.antonov.j71.beans.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig
{
/*  Как-бы описание способа обращения к кэшу : ключами будут строки, а значениями — JSON-ны.
*/  @Bean
    public RedisTemplate<String, Object> redisTemplate ()
    {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setKeySerializer (new StringRedisSerializer());    //< стандартн. Redis-сериализатор для строк.
        template.setValueSerializer (new GenericJackson2JsonRedisSerializer()); //< Стандартн.сериализатор для JSON-нов.
        template.setConnectionFactory (jedisConnectionFactory());
        return template;
    }

/*  Здесь ничего не настраиваем, если используем только Memurai//Redis, без Докеров и др.
*/  @Bean
    public JedisConnectionFactory jedisConnectionFactory ()
    {
        return new JedisConnectionFactory();
    }
}
