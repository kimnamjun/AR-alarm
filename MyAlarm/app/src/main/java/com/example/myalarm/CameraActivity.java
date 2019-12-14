package com.example.myalarm;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static com.example.myalarm.AlarmRunningActivity.MP;
import static com.example.myalarm.AlarmSettingActivity.image;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "opencv";
    private Mat matInput;
    private Mat matResult;

    private CameraBridgeViewBase mOpenCvCameraView;

    public long cascadeClassifierEye;
    public long cascadeClassifierFace;
    public native long loadCascade(String cascadeFileName);
    public native int Accessory(long cascadeClassifierEye, long cascadeClassifierFace, long matAddrInput, long matAddrResult);

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d( TAG, "copyFile :: 다음 경로로 파일복사 "+ pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 "+e.toString() );
        }
    }

    private void read_cascade_file(){
        String file_name1 = "haarcascade_eye.xml";
        String file_name2 = "haarcascade_frontalface_default.xml";

        copyFile(file_name1); // 인자 : file name
        copyFile(file_name2);

        Log.d(TAG, "read_cascade_file:");
        cascadeClassifierEye = loadCascade(file_name1); // 인자 : cascade file name
        cascadeClassifierFace = loadCascade(file_name2);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
            else read_cascade_file();
        }
        else read_cascade_file();

        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mOpenCvCameraView.setCameraIndex(1); // front-camera(1),  back-camera(0)

        MP = MediaPlayer.create(this, R.raw.alarm1);
        MP.setLooping(true);

        MP.start();

        Bitmap image0 = BitmapFactory.decodeResource(getResources(), R.drawable.camera);
        Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.glasses_a);
        Bitmap image2 = BitmapFactory.decodeResource(getResources(), R.drawable.glasses_b);
        Bitmap image3 = BitmapFactory.decodeResource(getResources(), R.drawable.glasses_x);
        Bitmap image4 = BitmapFactory.decodeResource(getResources(), R.drawable.hat_a);
        Bitmap image5 = BitmapFactory.decodeResource(getResources(), R.drawable.hat_b);
        Bitmap image6 = BitmapFactory.decodeResource(getResources(), R.drawable.hat_x);
        Bitmap image7 = BitmapFactory.decodeResource(getResources(), R.drawable.third_a);
        Bitmap image8 = BitmapFactory.decodeResource(getResources(), R.drawable.third_b);
        Bitmap image9 = BitmapFactory.decodeResource(getResources(), R.drawable.third_x);

        OutputStream outStream = null;

        try{
            outStream = new FileOutputStream(getExternalCacheDir().getAbsolutePath()+"camera.png");
            image0.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream = new FileOutputStream(getExternalCacheDir().getAbsolutePath()+"glassesA.png");
            image1.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream = new FileOutputStream(getExternalCacheDir().getAbsolutePath()+"glassesB.png");
            image2.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream = new FileOutputStream(getExternalCacheDir().getAbsolutePath()+"glassesX.png");
            image3.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream = new FileOutputStream(getExternalCacheDir().getAbsolutePath()+"hatA.png");
            image4.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream = new FileOutputStream(getExternalCacheDir().getAbsolutePath()+"hatB.png");
            image5.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream = new FileOutputStream(getExternalCacheDir().getAbsolutePath()+"hatX.png");
            image6.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream = new FileOutputStream(getExternalCacheDir().getAbsolutePath()+"thirdA.png");
            image7.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream = new FileOutputStream(getExternalCacheDir().getAbsolutePath()+"thirdB.png");
            image8.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream = new FileOutputStream(getExternalCacheDir().getAbsolutePath()+"thirdX.png");
            image9.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

            Toast.makeText(this,"downloaded", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        MP.stop();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResume :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        matInput = inputFrame.rgba();

        if ( matResult == null )
            matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());
//        Core.flip(matInput, matInput, -1);

        int returnCode = Accessory(cascadeClassifierEye, cascadeClassifierFace, matInput.getNativeObjAddr(), matResult.getNativeObjAddr());
        if(returnCode == 1){
            MP.stop();
            finish();
        }

        Core.flip(matResult, matResult, 1);
        return matResult;
    }

    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;

    protected void onCameraPermissionGranted() {
        List<? extends CameraBridgeViewBase> cameraViews = getCameraViewList();
        if (cameraViews == null) {
            return;
        }
        for (CameraBridgeViewBase cameraBridgeViewBase: cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean havePermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                havePermission = false;
            }
        }
        if (havePermission) {
            onCameraPermissionGranted();
        }
    }

//    @Override
//    @TargetApi(Build.VERSION_CODES.M)
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0
//                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            onCameraPermissionGranted();
//        }else{
//            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }


    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;

    //20190514
    String[] PERMISSIONS  = {"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    boolean writePermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !writePermissionAccepted) {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    }else
                    {
                        read_cascade_file();
                    }
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( CameraActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
}