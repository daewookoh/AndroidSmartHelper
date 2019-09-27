package com.bestmafen.easeblelib.scanner;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class decides results of a scan,you can use it to filter devices that you are not care about and specify devices that you
 * are only interested in.
 */
public class ScanOption {
    /**
     * Names of devices will be filtered.A device will not be found as a scan result if it's name is both filtered and specified.
     */
    List<String> mFilterNames = new ArrayList<>();

    /**
     * Addresses of devices will be filtered.A device will not be found as a scan result if it's address is both filtered and
     * specified.
     */
    List<String> mFilterAddresses = new ArrayList<>();

    /**
     * Used to scan devices with the specified names.A device will not be found as a scan result if it's name is both filtered
     * and specified.
     */
    List<String> mSpecifiedNames = new ArrayList<>();

    /**
     * Used to scan devices with the specified addresses.A device will not be found as a scan result if it's address is both
     * filtered and specified.
     */
    List<String> mSpecifiedAddresses = new ArrayList<>();

    /**
     * Minimum RSSI.A device will not be found as a scan result if is's RSSI is less than it.
     */
    int mMinRssi = -99;

    /**
     * Scan period.
     */
    long mPeriod = 5000;

    /**
     * Specify a device name.
     *
     * @param name  name to be specified
     * @param reset whether to reset specified names or not
     * @return This ScanOption object.
     */
    public ScanOption specifyName(String name, boolean reset) {
        if (reset) mSpecifiedNames.clear();

        if (!TextUtils.isEmpty(name)) {
            mSpecifiedNames.add(name);
        }
        return this;
    }

    /**
     * Specify a device name without resetting specified names.
     *
     * @param name name to be specified
     * @return This ScanOption object.
     */
    public ScanOption specifyName(String name) {
        return specifyName(name, false);
    }

    /**
     * Specify a list of device name.
     *
     * @param names names to be specified
     * @param reset whether to reset specified names or not
     * @return This ScanOption object.
     */
    public ScanOption specifyNames(List<String> names, boolean reset) {
        if (reset) mSpecifiedNames.clear();
        mSpecifiedNames.addAll(names);
        return this;
    }

    /**
     * Specify a list of device name without resetting specified names.
     *
     * @param names names to be specified
     * @return This ScanOption object.
     */
    public ScanOption specifyNames(List<String> names) {
        return specifyNames(names, false);
    }

    /**
     * Specify a device address.
     *
     * @param address address to be specified
     * @param reset   whether to reset specified addresses or not
     * @return This ScanOption object.
     */
    public ScanOption specifyAddress(String address, boolean reset) {
        if (reset) mSpecifiedAddresses.clear();

        if (!TextUtils.isEmpty(address)) {
            mSpecifiedAddresses.add(address);
        }
        return this;
    }

    /**
     * Specify a device address without resetting specified addresses.
     *
     * @param address address to be specified
     * @return This ScanOption object.
     */
    public ScanOption specifyAddress(String address) {
        return specifyAddress(address, false);
    }

    /**
     * Specify a list of device address.
     *
     * @param addresses addresses to be specified
     * @param reset     whether to reset specified addresses or not
     * @return This ScanOption object.
     */
    public ScanOption specifyAddresses(List<String> addresses, boolean reset) {
        if (reset) mSpecifiedAddresses.clear();
        mSpecifiedAddresses.addAll(addresses);
        return this;
    }

    /**
     * Specify a list of device address without resetting specified addresses.
     *
     * @param addresses addresses to be specified
     * @return This ScanOption object.
     */
    public ScanOption specifyAddresses(List<String> addresses) {
        return specifyAddresses(addresses, false);
    }

    /**
     * Filter a device name.
     *
     * @param name  name to be filtered
     * @param reset whether to reset filtered names or not
     * @return This ScanOption object.
     */
    public ScanOption filterName(String name, boolean reset) {
        if (reset) {
            mFilterNames.clear();
        }
        mFilterNames.add(name);
        return this;
    }

    /**
     * Filter a device name without resetting filtered names.
     *
     * @param name name to be filtered
     * @return This ScanOption object.
     */
    public ScanOption filterName(String name) {
        return filterName(name, false);
    }

    /**
     * Filter a list of device name.
     *
     * @param names names to be filtered
     * @param reset whether to reset filtered names or not
     * @return This ScanOption object.
     */
    public ScanOption filterNames(List<String> names, boolean reset) {
        if (reset) {
            mFilterNames.clear();
        }
        mFilterNames.addAll(names);
        return this;
    }

    /**
     * Filter a list of device name without resetting filtered names.
     *
     * @param names names to be filtered
     * @return This ScanOption object.
     */
    public ScanOption filterNames(List<String> names) {
        return filterNames(names, false);
    }

    /**
     * Filter a device address.
     *
     * @param address address to be filtered
     * @param reset   whether to reset filtered addresses or not
     * @return This ScanOption object.
     */
    public ScanOption filterAddress(String address, boolean reset) {
        if (reset) {
            mFilterAddresses.clear();
        }
        if (!TextUtils.isEmpty(address)) {
            mFilterAddresses.add(address);
        }
        return this;
    }

    /**
     * Filter a device address without resetting filtered addresses.
     *
     * @param address address to be filtered
     * @return This ScanOption object.
     */
    public ScanOption filterAddress(String address) {
        return filterAddress(address, false);
    }

    /**
     * Filter a list of device address.
     *
     * @param addresses addresses to be filtered
     * @param reset     whether to reset filtered addresses or not
     * @return This ScanOption object.
     */
    public ScanOption filterAddresses(List<String> addresses, boolean reset) {
        if (reset) {
            mFilterAddresses.clear();
        }
        mFilterAddresses.addAll(addresses);
        return this;
    }

    /**
     * Filter a list of device address without resetting filtered addresses.
     *
     * @param addresses addresses to be filtered
     * @return This ScanOption object.
     */
    public ScanOption filterAddresses(List<String> addresses) {
        return filterAddresses(addresses, false);
    }

    /**
     * Set the minimum RSSI.
     *
     * @param rssi minimum RSSI
     * @return This ScanOption object.
     */
    public ScanOption minRssi(int rssi) {
        mMinRssi = rssi;
        if (mMinRssi > -64) {
            mMinRssi = -64;
        }
        return this;
    }

    /**
     * Set the scan period.
     *
     * @param period scan period
     * @return This ScanOption object.
     */
    public ScanOption scanPeriod(long period) {
        mPeriod = period;
        if (mPeriod < 1000) {
            mPeriod = 5000;
        }
        return this;
    }
}
