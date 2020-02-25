package com.and.smarthelper.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder mHolder;
    private Camera mCamera = null;
    int is_front = 0;

    // 필수 생성자
    public CameraSurfaceView(Context context) {
        super(context);

        init(context);
    }

    // 필수 생성자
    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    // 초기화를 위한 메서드
    private void init(Context context) {
        mHolder = getHolder(); // 서피스뷰 내에 있는 SurfaceHolder 라고 하는 객체를 참조할 수 있다.
        mHolder.addCallback(this); // holder
    }

    // 서피스뷰가 메모리에 만들어지는 시점에 호출됨
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCamera = Camera.open(getFrontCameraId()); // 카메라 객체를 참조하여 변수에 할당
        mCamera.setDisplayOrientation(90); // 이게 없으면 미리보기 화면이 회전되어 나온다.

        try {
            mCamera.setPreviewDisplay(mHolder); // Camera 객체에 이 서피스뷰를 미리보기로 하도록 설정
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getFrontCameraId(){
        int camId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo ci = new Camera.CameraInfo();

        for(int i = 0;i < numberOfCameras;i++){
            Camera.getCameraInfo(i,ci);
            if(ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                camId = i;
            }
        }

        is_front = 1;
        Log.d("TTTfrontcamid", String.valueOf(camId));
        return camId;
    }

    private int getBackCameraId(){
        int camId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo ci = new Camera.CameraInfo();

        for(int i = 0;i < numberOfCameras;i++){
            Camera.getCameraInfo(i,ci);
            if(ci.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                camId = i;
            }
        }

        is_front = 0;
        Log.d("TTTbackcamid", String.valueOf(camId));
        return camId;
    }
    /* 서피스뷰가 크기와 같은 것이 변경되는 시점에 호출
     * 화면에 보여지기 전 크기가 결정되는 시점 */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // 미리보기 화면에 픽셀로 뿌리기 시작! 렌즈로부터 들어온 영상을 뿌려줌.
        mCamera.startPreview();
    }

    // 없어질 때 호출
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.stopPreview(); // 미리보기 중지. 많은 리소스를 사용하기 때문에
        // 여러 프로그램에서 동시에 쓸 때 한쪽에서 lock 을 걸어 사용할 수 없는 상태가 될 수 있기 때문에, release 를 꼭 해주어야함
        mCamera.release(); // 리소스 해제
        mCamera = null;
    }

    // 서피스뷰에서 사진을 찍도록 하는 메서드
    public boolean capture(Camera.PictureCallback callback){
        if (mCamera != null){
            mCamera.takePicture(null, null, callback);
            return true;
        } else {
            return false;
        }
    }

    public void rotate(){

        if(mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }

        if(is_front==0) {
            mCamera = Camera.open(getFrontCameraId()); // 카메라 객체를 참조하여 변수에 할당
        }
        else {
            mCamera = Camera.open(getBackCameraId());
        }

        mCamera.setDisplayOrientation(90); // 이게 없으면 미리보기 화면이 회전되어 나온다.

        try {
            mCamera.setPreviewDisplay(mHolder); // Camera 객체에 이 서피스뷰를 미리보기로 하도록 설정
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
