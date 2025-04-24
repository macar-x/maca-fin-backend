package com.macacloud.fin.util;

import lombok.extern.slf4j.Slf4j;

/**
 * UUID Generator with SnowFlake.
 * <a href="https://www.jianshu.com/p/54a87a7c3622"> Reference </a>
 * 1bit 占位符 + 63bit 有效位数 ( 41bit 时间戳, 10bit 工作机器, 12bit 序列号)
 *
 * @author Emmett
 * @since 2024/06/13
 */
@Slf4j
public class SnowFlakeUtil {

    /**
     * 工作机器标识位数
     */
    private static final long WORKER_ID_BITS = 10L;
    /**
     * 毫秒内自增位数（序列）
     */
    private static final long SEQUENCE_BITS = 12L;
    /**
     * 工作机器 ID 偏左移 12 位
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /**
     * 时间毫秒左移 22 位
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    /**
     * 序列掩码，验证序列是否超出上限
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /**
     * Start with 2025-01-01 0/12:00:00 AM GMT+0
     */
    private static final long START_TIME_STAMP = 1735689600000L;
    private static final SnowFlakeUtil INSTANCE;

    static {
        // note: workerId 應該從配置文件拿或者通過某種邏輯生成，以確保集群内不衝突。
        INSTANCE = new SnowFlakeUtil(0L);
    }

    /**
     * 工作机器 ID
     */
    private final long workerId;
    /**
     * 上次时间戳
     */
    private long lastTimeStamp = -1L;
    /**
     * 当前毫秒内序列
     */
    private long sequence = 0L;

    private SnowFlakeUtil(Long workerId) {
        this.workerId = workerId;
    }

    /**
     * 获取下一个 UUID
     */
    public static synchronized long getNextId() {
        return INSTANCE.generateNextId();
    }

    public synchronized long generateNextId() {
        // 获取当前时间戳
        long timestamp = this.getCurrentMillis();

        // 如果时间戳小于上次时间戳则报错
        if (timestamp < lastTimeStamp) {
            log.error("A time backtrack has occurred, and no more generation requests will be accepted for the next {} millisecond.", (lastTimeStamp - timestamp));
        }

        // 如果时间戳与上次时间戳相同
        if (lastTimeStamp == timestamp) {
            // 当前毫秒序列+1，与sequenceMask确保sequence不会超出上限
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒
                timestamp = waitNextMillis(lastTimeStamp);
            }
        } else {
            // 当前时间戳大于上次时间戳，开始新序列
            sequence = 0;
        }

        // 更新最后访问时间，对ID偏移组合生成最终的ID
        lastTimeStamp = timestamp;
        return ((timestamp - START_TIME_STAMP) << TIMESTAMP_LEFT_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    /**
     * 获取当前时间戳
     */
    private long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 等待新的时间戳
     */
    private long waitNextMillis(final long lastTimestamp) {
        long timestamp = this.getCurrentMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = this.getCurrentMillis();
        }
        return timestamp;
    }
}
