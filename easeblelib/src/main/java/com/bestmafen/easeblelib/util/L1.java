package com.bestmafen.easeblelib.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.ArrayMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xiaokai on 2018/10/16.
 */
public class L1 {
    private static final int LINE_LENGTH = 140;

    private File                     mDir;
    private LinkedBlockingQueue<Tag> mTags;
    private DateFormat               mDateFormat;
    private DateFormat               mDateFormat2;
    private Date                     mDate;

    private ArrayMap<String, PrintWriter> mMap = new ArrayMap<>();

    private L1() {

    }

    public void init() {
        mDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "smalog");
        if (!mDir.exists() || !mDir.isDirectory()) {
            if (!mDir.mkdir()) return;
        }

        mTags = new LinkedBlockingQueue<>();
        mDateFormat = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
        mDateFormat2 = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
        mDate = new Date();
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Tag tag = mTags.take();
                        if (tag == null || TextUtils.isEmpty(tag.mTag)) continue;

                        mDate.setTime(tag.mTime);
                        PrintWriter writer = getWriter();
                        if (writer == null) continue;

                        int lines = (int) Math.ceil((double) tag.mTag.length() / LINE_LENGTH);
                        for (int i = 0; i < lines; i++) {
                            String header;
                            String content;
                            if (i == 0) {
                                header = String.format("%-15s", mDateFormat2.format(mDate));
                            } else {
                                header = String.format("%-15s", "");
                            }
                            if (i == lines - 1) {
                                content = tag.mTag.substring(i * LINE_LENGTH, tag.mTag.length());
                            } else {
                                content = tag.mTag.substring(i * LINE_LENGTH, (i + 1) * LINE_LENGTH);
                            }
                            writer.print(header);
                            writer.println(content);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static L1 getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final L1 sInstance = new L1();
    }

    public void addTag(Tag tag) {
        if (mTags == null) return;

        try {
            mTags.put(tag);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private PrintWriter getWriter() {
        String date = mDateFormat.format(mDate);
        PrintWriter writer;
        try {
            if ((writer = mMap.get(date)) == null) {
                if (mMap.size() != 0) {
                    mMap.remove(mMap.keyAt(0)).close();
                }
                mMap.put(date, writer = new PrintWriter(new FileWriter(new File(mDir, date), true), true));
            }
            return writer;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    static class Tag {
        public long   mTime;
        public String mTag;

        Tag(long time, String tag) {
            mTime = time;
            mTag = tag;
        }
    }
}
