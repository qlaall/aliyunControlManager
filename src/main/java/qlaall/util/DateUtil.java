package qlaall.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * @author qlaall
 * @create 2020/2/21
 */
public class DateUtil {
    /**
     * 从Long表示转换为OffsetDateTime，例：
     * 20020404112334，自动添加UTC+8时区，表示为"2002-04-04T11:23:34+8:00"
     * 也即北京时间：2002年4月4日11时23分34秒
     *
     * @param dateTimeStringAsLong
     * @return
     */
    public static OffsetDateTime fromLongTypeAsBeijingTime(long dateTimeStringAsLong) {
        return fromLongType(dateTimeStringAsLong, ZoneOffset.ofHours(8));
    }

    public static OffsetDateTime fromLongType(long dateTimeStringAsLong, ZoneOffset zoneOffset) {
        int year = (int) (dateTimeStringAsLong / 10_000_000_000L);
        int month = (int) (dateTimeStringAsLong / 100_000_000L % 100);
        int day = (int) (dateTimeStringAsLong / 1_000_000 % 100);
        int hour = (int) (dateTimeStringAsLong / 10_000 % 100);
        int minute = (int) (dateTimeStringAsLong / 1_00 % 100);
        int second = (int) (dateTimeStringAsLong % 100);
        return OffsetDateTime.of(LocalDateTime.of(year, month, day, hour, minute, second), zoneOffset);
    }

    /**
     * 从OffsetDateTime中获取时间，并转为北京时间描述的Long类型，例：
     * "2020-02-20T18:19:19+8:00" 返回20200220181919
     * "2020-02-20T18:19:19Z" 返回20200221021919 因为它是UTC+0时区的2月20日18点19分，是北京时间的2月21日2点19分
     * 调用示例:toBeijingTimeLongValue(OffsetDateTime.now())获取此刻时间的Long表示,OffsetDateTime本身携带有时区信息，所以无需自己设置时区
     *
     * @param dt
     * @return
     */
    public static long toBeijingTimeLongType(OffsetDateTime dt) {
        LocalDateTime localDateTime =
                dt.withOffsetSameInstant(ZoneOffset.ofHours(8)).toLocalDateTime();
        long year = localDateTime.getYear() * 10_000_000_000L;
        long month = localDateTime.getMonthValue() * 100_000_000L;
        long day = localDateTime.getDayOfMonth() * 1_000_000L;
        long hour = localDateTime.getHour() * 10_000L;
        long minute = localDateTime.getMinute() * 1_00L;
        long second = localDateTime.getSecond();
        return year + month + day + hour + minute + second;
    }
}
