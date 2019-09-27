package com.bestmafen.smablelib.entity;


import java.util.ArrayList;
import java.util.List;

public class SmaSport implements Comparable {
    public static class Mode {
        /**
         * Non-exercise mode
         */
        public static final int SIT  = 0X10;
        public static final int WALK = 0X11;
        public static final int RUN  = 0X12;

        /**
         * Exercise mode.This type is just supported by the devices which have exercise mode function,such as smaq2.
         */
        public static final int START = 0X20;
        public static final int GOING = 0X22;
        public static final int END   = 0X2F;

        //i-MED锻炼模式
        public static final int START_IMED_6  = 0X30;
        public static final int START_IMED_12 = 0X31;
        public static final int END_IMED      = 0X3F;

        //indoor
        public static final int INDOOR_START = 0X40;
        public static final int INDOOR_GOING = 0X41;
        public static final int INDOOR_END   = 0X42;

        //outdoor
        public static final int OUTDOOR_START = 0X48;
        public static final int OUTDOOR_GOING = 0X49;
        public static final int OUTDOOR_END   = 0X4A;

        //cycling
        public static final int CYCLING_START = 0X50;
        public static final int CYCLING_GOING = 0X51;
        public static final int CYCLING_END   = 0X52;

        //swimming
        public static final int SWIMMING_START = 0X58;
        public static final int SWIMMING_GOING = 0X59;
        public static final int SWIMMING_END   = 0X5A;
    }

    public long   id;
    public String date;
    public long   time;
    public int    mode;
    public int    step;
    public double    calorie;//Kcal
    public int    distance;//m
    public String account;
    public int    synced;

    /**
     * Remove unnecessary daily sport data queried form database.
     *
     * @param origin original data
     * @return a clean result
     */
    public static List<SmaSport> anolyseSports(List<SmaSport> origin) {
        List<SmaSport> list = new ArrayList<>();
        if (origin != null && origin.size() > 0) {
            for (int i = 0, l = origin.size(); i < l; i++) {
                SmaSport sport = origin.get(i);
                if (i == 0 || i == origin.size() - 1 || (sport.mode != 0 && origin.get(i - 1).mode != sport.mode)) {
                    list.add(sport);
                }
            }
        }
        return list;
    }

    /**
     * Calculate daily sport status.
     *
     * @param list the clean sport data got by {@link SmaSport#anolyseSports(List)}
     * @return sport status which contains time of low exercise,time of walk,time of run(unit:minute)
     */
    public static int[] getSportStatus(List<SmaSport> list) {
        long lowExercise = 0, walk = 0, run = 0;
        if (list != null && list.size() > 1) {
            for (int i = 0, l = list.size() - 1; i < l; i++) {
                SmaSport s1 = list.get(i);
                SmaSport s2 = list.get(i + 1);
                long time = s2.time - s1.time;
                switch (s1.mode) {
                    case SmaSport.Mode.WALK:
                        walk += time;
                        break;
                    case SmaSport.Mode.RUN:
                        run += time;
                        break;
                    default:
                        lowExercise += time;
                        break;
                }
            }
        }
        lowExercise = lowExercise / 1000 / 60;
        walk = walk / 1000 / 60;
        run = run / 1000 / 60;
        return new int[]{(int) lowExercise, (int) walk, (int) run};
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;

        if (o instanceof SmaSport) {
            return (int) Math.signum(this.time - ((SmaSport) o).time);
        }

        return 0;
    }

    @Override
    public String toString() {
        return "SmaSport{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", time=" + time +
                ", mode=" + mode +
                ", step=" + step +
                ", calorie=" + calorie +
                ", distance=" + distance +
                ", account='" + account + '\'' +
                ", synced=" + synced +
                '}';
    }
}
