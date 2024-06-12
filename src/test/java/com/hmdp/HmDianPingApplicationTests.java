package com.hmdp;

import com.hmdp.entity.Shop;
import com.hmdp.service.IShopService;
import com.hmdp.service.impl.ShopServiceImpl;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

@SpringBootTest
class HmDianPingApplicationTests {
    @Resource
    private ShopServiceImpl shopService;
    @Resource
    private CacheClient cacheClient;
    @Resource
    private RedisIdWorker redisIdWorker;
    private ExecutorService es = Executors.newFixedThreadPool(500);


    @Test
    void JustTest() {
        UUID uuid = UUID.randomUUID();
        String string = uuid.toString();
        cn.hutool.core.lang.UUID uuid1 = cn.hutool.core.lang.UUID.randomUUID();
        String string1 = uuid1.toString(true);
        System.out.println(" ");
    }

    @Test
    void IdWorkerTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(300);
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                long id = redisIdWorker.nextId("order");
                System.out.println("id = " + id);
            }
            latch.countDown();
        };
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            es.submit(task);
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("time = " + (end - begin));
    }

    @Test
    void saveTest() throws InterruptedException {
        Shop shop = shopService.getById(1L);
        cacheClient.setWithLogicalExpire(CACHE_SHOP_KEY, shop, 10L, TimeUnit.SECONDS);
    }

    @Test
    void getTime() {
        LocalDateTime time = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        long beginTime = time.toEpochSecond(ZoneOffset.UTC);
        System.out.println("beginTime = " + beginTime);
    }

    @Test
    void timeStampTest() {
        long l = redisIdWorker.nextId("shop");
    }
}
