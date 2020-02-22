package qlaall.util;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 处理延迟事件工具类，
 * 事件本身可以接受被多次处理，有一定的延迟能力
 * 持久化功能由redis完成，用例：
 * 	val tired = EventDealer("tired", Consumer { s -> println(s) }, redisTemplate)
 * 	tired.add(3,ChronoUnit.SECONDS)
 * 	tired.add(9,ChronoUnit.SECONDS)
 * 	3秒后和9秒后将打印出两个当时的时间。
 */
public class SimpleDelayEventManager {
    private static final Logger logger= LoggerFactory.getLogger(SimpleDelayEventManager.class);
    public static final Map<String, SimpleDelayEventManager> NAMED_EVENT_MANAGER = new HashMap<>();
    //事件名称
    private String eventName;
    //事件处理器
    private Consumer<String> eventHandler;
    private RedisTemplate<String, String> redisTemplate;

    private BlockingQueue<Void> emptyQueue = new LinkedBlockingQueue<>();

    //如果已存在，将会返回旧的
    public SimpleDelayEventManager(String eventName, Consumer<String> o, RedisTemplate<String, String> redisTemplate) {
        SimpleDelayEventManager simpleDelayEventManager = NAMED_EVENT_MANAGER.get(eventName);
        if (simpleDelayEventManager == null) {
            this.eventName = eventName;
            this.eventHandler = o;
            this.redisTemplate = redisTemplate;
            SimpleDelayEventManager.NAMED_EVENT_MANAGER.put(eventName, this);
            Thread thread = new Thread(this::start);
            thread.setDaemon(true);
            thread.start();
        }
    }
    //线程池执行
    public SimpleDelayEventManager(String eventName, Consumer<String> o, RedisTemplate<String, String> redisTemplate, Executor executor) {
        SimpleDelayEventManager simpleDelayEventManager = NAMED_EVENT_MANAGER.get(eventName);
        if (simpleDelayEventManager == null) {
            this.eventName = eventName;
            this.eventHandler = o;
            this.redisTemplate = redisTemplate;
            SimpleDelayEventManager.NAMED_EVENT_MANAGER.put(eventName, this);
            executor.execute(this::start);
        }
    }

    private void start() {
        while (true) {
            try {
                emptyQueue.poll(1, TimeUnit.SECONDS);
                long now = OffsetDateTime.now().toEpochSecond();

                boolean shouldAgain = true;
                while (shouldAgain) {
                    Set<ZSetOperations.TypedTuple<String>> set = redisTemplate.opsForZSet().rangeWithScores(eventName, 0, 5);
                    if (set == null || set.isEmpty()) {
                        continue;
                    }
                    int dealCount = 0;
                    for (ZSetOperations.TypedTuple<String> t : set) {
                        if (t.getScore() < now) {
                            logger.info("处理EVENT:{}\t Score:{}\t Content:{}",eventName,t.getScore(),t.getValue());
                            eventHandler.accept(t.getValue());
                            redisTemplate.opsForZSet().remove(eventName, t.getValue());
                            dealCount++;
                        }else {
                            break;
                        }
                    }
                    if (dealCount == set.size()) {
                        shouldAgain = true;
                    } else {
                        shouldAgain = false;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public @Nullable
    SimpleDelayEventManager get(String schedualName){
        return SimpleDelayEventManager.NAMED_EVENT_MANAGER.get(schedualName);
    }
    public void add(long delayNum, ChronoUnit timeUnit) {
        OffsetDateTime targetTime = OffsetDateTime.now().plus(delayNum, timeUnit);
        long score = targetTime.toEpochSecond();
        redisTemplate.opsForZSet().add(eventName, targetTime.withOffsetSameInstant(ZoneOffset.ofHours(8)).toString(), score);
    }

}
