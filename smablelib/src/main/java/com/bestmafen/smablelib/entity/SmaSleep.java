package com.bestmafen.smablelib.entity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmaSleep implements Comparable {
    public static class Mode {
        public static final int START = 0X11;
        public static final int END = 0X22;
        public static final int DEEP = 0X01;
        public static final int LIGHT = 0X02;
        public static final int AWAKE = 0X03;
    }

    public long id;
    public String date;
    public long time;
    public int mode;
    public int soft;
    public int strong;
    public String account;
    public int synced;

    /**
     * The daily sleep data(from 23:00 to 10:00) queried from database may be incorrect,and could not reflect the real sleep
     * status.So we should process the original data by invoking this method to get a correct result.
     *
     * @param origin original data
     * @return a correct result
     */
    public static final List<SmaSleep> analyseSleeps(List<SmaSleep> origin) {
        if (origin == null || origin.size() < 2)
            return new ArrayList<>();

        Collections.sort(origin);
        List<SmaSleep> result = new ArrayList<>();
        boolean isHaveFallAsleep = false;
        boolean isHaveWakeUp = false;
        for (SmaSleep sleep : origin) {
            if (sleep.mode == SmaSleep.Mode.START) {
                if (!isHaveFallAsleep) {
                    isHaveFallAsleep = true;
                }
                result.clear();

                //把入睡拆分成前15分钟浅睡，后面深睡
                SmaSleep ss = new SmaSleep();
                ss.time = sleep.time;
                ss.date = sleep.date;
                ss.mode = SmaSleep.Mode.LIGHT;
                result.add(ss);

                sleep.mode = SmaSleep.Mode.DEEP;
                sleep.time += 1000 * 60 * 15;
            } else if (sleep.mode == SmaSleep.Mode.END) {
                isHaveWakeUp = true;
            } else {
                if (sleep.strong > 2) {//如果重动>2，则为清醒
                    sleep.mode = SmaSleep.Mode.AWAKE;
                    sleep.time -= 1000 * 60 * 15;
                } else {
                    if (sleep.soft > 2) {//重动<=2,轻动>2,则为浅睡
                        sleep.mode = SmaSleep.Mode.LIGHT;
                        sleep.time -= 1000 * 60 * 15;
                    }
                }
            }
            if (result.size() == 0 || result.get(result.size() - 1).mode != sleep.mode) {
                result.add(sleep);
            }
        }

        if (!isHaveWakeUp && result.size() > 1) {
            result.get(result.size() - 1).mode = SmaSleep.Mode.END;//如果没有退出睡眠，把最后一条置为退出睡眠
        }

        if (!isHaveFallAsleep && result.size() > 1) {
            result.get(0).mode = SmaSleep.Mode.LIGHT;
            SmaSleep sleep2 = new SmaSleep();

            sleep2.mode = SmaSleep.Mode.DEEP;
            sleep2.time = result.get(0).time + 1000 * 60 * 15;
            result.add(1, sleep2);
        }

        Collections.sort(result);
        return result;
    }

    /**
     * Calculate daily sleep status.
     *
     * @param list the correct sleep data got by {@link SmaSleep#analyseSleeps(List)}
     * @return sleep status which contains duration,time of deep,time of light,time of awake(unit:minute)
     */
    public static int[] getSleepStatus(List<SmaSleep> list) {
        long duration = 0, deep = 0, light = 0, awake = 0;

        if (list != null && list.size() > 1) {
            for (int i = 0, l = list.size() - 1; i < l; i++) {
                SmaSleep s1 = list.get(i);
                SmaSleep s2 = list.get(i + 1);
                long time = s2.time - s1.time;
                switch (s1.mode) {
                    case SmaSleep.Mode.DEEP:
                        deep += time;
                        break;
                    case SmaSleep.Mode.LIGHT:
                        light += time;
                        break;
                    case SmaSleep.Mode.AWAKE:
                        awake += time;
                        break;
                }
            }
            long start = list.get(0).time;
            start = (start / 1000 / 60) % (60 * 24);

            long end = list.get(list.size() - 1).time;
            end = (end / 1000 / 60) % (60 * 24);

            if (start <= 600) {
                start += 1440;
            }

            if (end <= 600) {
                end += 1440;
            }
            duration = end - start;
            deep = deep / 1000 / 60;
            light = light / 1000 / 60;
            awake = duration - deep - light;
        }

        return new int[]{(int) duration, (int) deep, (int) light, (int) awake};
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;

        if (o instanceof SmaSleep) {
            return (int) Math.signum(this.time - ((SmaSleep) o).time);
        }

        return 0;
    }

    @Override
    public String toString() {
        return "SmaSleep{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", time=" + time +
                ", mode=" + mode +
                ", strong=" + strong +
                ", soft=" + soft +
                ", account='" + account + '\'' +
                ", synced=" + synced +
                '}';
    }
}
