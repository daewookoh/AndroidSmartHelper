package com.and.smarthelper.activity;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.abupdate.iot_libs.OtaAgentPolicy;
import com.abupdate.iot_libs.info.CustomDeviceInfo;
import com.abupdate.iot_libs.info.VersionInfo;
import com.abupdate.iot_libs.inter.ICheckVersionCallback;
import com.abupdate.iot_libs.inter.IDownloadListener;
import com.abupdate.iot_libs.security.FotaException;
import com.bestmafen.easeblelib.util.L;
import com.bestmafen.smablelib.component.SimpleSmaCallback;
import com.bestmafen.smablelib.component.SmaCallback;
import com.bestmafen.smablelib.component.SmaManager;
import com.and.smarthelper.R;
import com.and.smarthelper.util.T;

import java.io.File;
import java.util.HashMap;

/**
 * device M OTA
 */
public class FirmwareUpdateMActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTvInfo, mTvStatus;
    private Button mBtn;

    private SmaManager  mSmaManager;
    private SmaCallback mSmaCallback;
    private State       mState;

    enum State {
        VERIFY_ERROR, PREPARE, CHECKING, NEW_VERSION_FOUND, LATEST_VERSION,
        DOWNLOADING, DOWNLOAD_CANCELED, DOWNLOAD_ERROR, DOWNLOAD_COMPLETED
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_update_m;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mSmaManager = SmaManager.getInstance();
        mSmaManager.addSmaCallback(mSmaCallback = new SimpleSmaCallback() {

            @Override
            public void onLogin(final boolean ok) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mBtn.setEnabled(ok);
                    }
                });
            }

            @Override
            public void onTransferBuffer(final boolean status, final int total, final int completed) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (status) {
                            T.show(mContext, String.valueOf(status));
                            if (total == completed) {
                                finish();
                            } else {
                                int progress = (int) (completed * 100f / total);
                                mTvStatus.setText(getString(R.string.format_transferring_d_percent, progress));
                            }
                        } else {
                            updateUI();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void initView() {
        mTvInfo = (TextView) findViewById(R.id.tv_info);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mBtn = (Button) findViewById(R.id.btn);
        mBtn.setOnClickListener(this);
    }

    @Override
    protected void initComplete(Bundle bundle) {
        if (!verifyDeviceInfo()) {
            mState = State.VERIFY_ERROR;
        } else {
            mState = State.PREPARE;
        }
        updateUI();
    }

    @Override
    protected void onDestroy() {
        mSmaManager.removeSmaCallback(mSmaCallback);
        super.onDestroy();
    }

    private boolean verifyDeviceInfo() {
        String deviceInfo = getIntent().getStringExtra("flag");
        if (TextUtils.isEmpty(deviceInfo)) return false;

        mTvInfo.setText(deviceInfo.replace(";", "\n"));
        String[] allInfo = deviceInfo.split(";");
        if (allInfo.length < 6) return false;

        HashMap<String, String> infoMap = new HashMap<>();
        for (String item : allInfo) {
            String[] strings = item.split("=");
            if (strings.length == 2) {
                if (strings[0].startsWith("product_i")) {
                    infoMap.put("product_id", strings[1]);
                } else if (strings[0].startsWith("product_s")) {
                    infoMap.put("product_secret", strings[1]);
                } else {
                    infoMap.put(strings[0], strings[1]);
                }
            }
        }
//          mid=1234567890;
//          models=G3_B;
//          oem=ADUPS;
//          platform=mtk2502;
//          version=LION_U17_UWATCH_V0.04;
//          device_type=watch;
//          product_id=5235474253;
//          product_secret=546bg3g354;
        CustomDeviceInfo customDeviceInfo = new CustomDeviceInfo();
        if (!infoMap.containsKey("mid")) return false;
        customDeviceInfo.setMid(infoMap.get("mid"));

//        if (!infoMap.containsKey("models")) return false;
        if (!infoMap.containsKey("mod")) return false;
        customDeviceInfo.setModels(infoMap.get("mod"));

        if (!infoMap.containsKey("oem")) return false;
        customDeviceInfo.setOem(infoMap.get("oem"));

//        if (!infoMap.containsKey("platform")) return false;
        if (!infoMap.containsKey("pf")) return false;
        customDeviceInfo.setPlatform(infoMap.get("pf"));

//        if (infoMap.containsKey("version")) {
        if (infoMap.containsKey("ver")) {
            customDeviceInfo.setVersion(infoMap.get("ver"));
        }
//        if (!infoMap.containsKey("device_type")) return false;
        if (!infoMap.containsKey("d_ty")) return false;
        customDeviceInfo.setDeviceType(infoMap.get("d_ty"));

//        if (infoMap.containsKey("product_id")) {
        if (infoMap.containsKey("p_id")) {
            customDeviceInfo.setProductId(infoMap.get("p_id"));
        }
//        if (infoMap.containsKey("product_secret")) {
        if (infoMap.containsKey("p_sec")) {
            customDeviceInfo.setProduct_secret(infoMap.get("p_sec"));
        }

        try {
            File parent = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!parent.exists() || !parent.isDirectory()) {
                if (!parent.mkdir()) return false;
            }
            File file = new File(parent, "/adupsfota/update.zip");
            OtaAgentPolicy.init(getApplicationContext())
                          .setUpdatePath(file.getAbsolutePath())
                          .setCustomDeviceInfo(customDeviceInfo)
                          .commit();
        } catch (FotaException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void checkVersion() {
        mState = State.CHECKING;
        updateUI();
        OtaAgentPolicy.checkVersionAsync(new ICheckVersionCallback() {

            @Override
            public void onCheckSuccess(VersionInfo versionInfo) {
                mState = State.NEW_VERSION_FOUND;
                updateUI();
            }

            @Override
            public void onCheckFail(int status) {
                L.e("status=" + status);
                mState = State.LATEST_VERSION;
                updateUI();
            }
        });
    }

    private void download() {
        OtaAgentPolicy.downloadAsync(new IDownloadListener() {

            @Override
            public void onPrepare() {
                mState = State.DOWNLOADING;
                updateUI();
            }

            @Override
            public void onDownloadProgress(long downSize, long totalSize) {
                mTvStatus.setText(getString(R.string.format_downloading_d_percent, (int) ((float) downSize * 100 / totalSize)));
            }

            @Override
            public void onFailed(int error) {
                mState = State.DOWNLOAD_ERROR;
                updateUI();
            }

            @Override
            public void onCompleted() {
                mState = State.DOWNLOAD_COMPLETED;
                updateUI();
            }

            @Override
            public void onCancel() {
                mState = State.DOWNLOAD_CANCELED;
                updateUI();
            }
        });
    }

    private void upgrade() {
        mBtn.setEnabled(false);
        mSmaManager.updateM(new File(OtaAgentPolicy.getConfig().updatePath));
    }

    private void updateUI() {
        if (mState == State.VERIFY_ERROR) {
            mTvStatus.setText(R.string.verify_error);
            mBtn.setText(R.string.verify_error);
            mBtn.setEnabled(false);
        } else if (mState == State.PREPARE) {
            mBtn.setText(R.string.check);
            mBtn.setEnabled(true);
        } else if (mState == State.CHECKING) {
            mBtn.setText(R.string.checking);
            mBtn.setEnabled(false);
        } else if (mState == State.NEW_VERSION_FOUND) {
            VersionInfo versionInfo = OtaAgentPolicy.getVersionInfo();
            mTvStatus.setText(versionInfo.toString());
            mBtn.setText(R.string.download);
            mBtn.setEnabled(true);
        } else if (mState == State.LATEST_VERSION) {
            mTvStatus.setText(R.string.latest_version);
            mBtn.setText(R.string.check);
            mBtn.setEnabled(true);
        } else if (mState == State.DOWNLOADING) {
            mTvStatus.setText(R.string.downloading);
            mBtn.setText(R.string.pause);
        } else if (mState == State.DOWNLOAD_CANCELED) {
            mTvStatus.setText(R.string.paused);
            mBtn.setText(R.string.resume);
        } else if (mState == State.DOWNLOAD_ERROR) {
            mTvStatus.setText(R.string.download_failed);
            mBtn.setText(R.string.retry);
        } else if (mState == State.DOWNLOAD_COMPLETED) {
            mTvStatus.setText(R.string.download_completed);
            mBtn.setText(R.string.upgrade);
            mBtn.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (mState == State.PREPARE) {
            checkVersion();
        } else if (mState == State.NEW_VERSION_FOUND) {
            download();
        } else if (mState == State.LATEST_VERSION) {
            T.show(mContext, R.string.latest_version);
        } else if (mState == State.DOWNLOADING) {
            OtaAgentPolicy.downloadCancel();
        } else if (mState == State.DOWNLOAD_CANCELED) {
            download();
        } else if (mState == State.DOWNLOAD_ERROR) {
            download();
        } else if (mState == State.DOWNLOAD_COMPLETED) {
            upgrade();
        }
    }
}
