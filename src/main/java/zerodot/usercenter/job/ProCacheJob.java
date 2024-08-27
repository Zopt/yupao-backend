package zerodot.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zerodot.usercenter.modol.domain.User;
import zerodot.usercenter.service.UserService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class ProCacheJob {

    @Resource
    private UserService userService;

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    RedissonClient redissonClient;

    private List<Long> mainUserId = Arrays.asList(3L);

    @Scheduled(cron = "0 44 12 * * ? ")  //每天上午10点15触发执行
    public void run() {
        RLock lock = redissonClient.getLock("yupao:precacheJob:doCache:lock");

        try {
            if (lock.tryLock(0,30000, TimeUnit.MILLISECONDS)) {
                System.out.println("加锁线程Id"+Thread.currentThread().getName());
                for (Long userId : mainUserId) {
                    //无缓存查询数据库
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1,20),queryWrapper);
                    String redisKey = String.format("yupao:user:recommend:`%s`", userId);

                    //如果有缓存 直接读缓存
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    try {
                        valueOperations.set(redisKey, userPage,5, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        log.error("User key error", e);
                    }
                }
            }

        } catch (InterruptedException e) {
            log.error("redissonClientError" + e);
        }finally {
            //只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                System.out.println("释放锁线程Id"+Thread.currentThread().getName());
                lock.unlock();
            }
        }

    }
}
