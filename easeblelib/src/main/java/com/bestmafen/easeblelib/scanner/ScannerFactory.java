package com.bestmafen.easeblelib.scanner;

import android.os.Build;

/**
 * This class is used to create a {@link EaseScanner} object according to different API level.
 */
public class ScannerFactory {

    private ScannerFactory() {

    }

    public static synchronized EaseScanner getDefaultScanner() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static EaseScanner instance = createScanner();
    }

    public static EaseScanner createScanner() {
        return new ScannerNew();
        /*
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return new ScannerOld();
        } else {
            return new ScannerNew();
        }
        */
    }
}
