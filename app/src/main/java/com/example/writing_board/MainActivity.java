package com.example.writing_board;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    private ImageView mIVSign;
    private ImageView iv_show;
    private ImageView iv_show_person;
    private TextView mTVSign;
    private Button show_bt;
    private Bitmap mSignBitmap;
    private String txtPath;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();

        mIVSign = (ImageView) findViewById(R.id.iv_sign);
        iv_show = (ImageView) findViewById(R.id.iv_show);
        iv_show_person = (ImageView) findViewById(R.id.iv_show_person);
        mTVSign = (TextView) findViewById(R.id.tv_sign);
        show_bt = (Button) findViewById(R.id.show_bt);
        show_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/1555990638090.jpg");
//                iv_show_person.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv_show_person.setImageBitmap(bitmap);
            }
        });

        mTVSign.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                WritePadDialog mWritePadDialog = new WritePadDialog(
                        MainActivity.this, new WriteDilogListener() {

                    @Override
                    public void onPaintDone(Object object) {
                        mSignBitmap = (Bitmap) object;
                        createSignFile();
                        //设置图片充满ImageView控件
//                        iv_show.setScaleType(ImageView.ScaleType.FIT_XY);

                        mIVSign.setImageBitmap(mSignBitmap);

                        Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "createSignFile: " + path);
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(path);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
//                        Bitmap bitmap  = BitmapFactory.decodeStream(fis);
//                        iv_show.setImageBitmap(bitmap);
//                        mTVSign.setVisibility(View.GONE);

                        fileExist = fileIsExists(path);
                        if (fileExist) {
                            //存在该图片就显示出来
                            readImg(iv_show);
                        }

//                        txtPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Logcat.txt";
                        txtPath = path;
                        boolean txtfileExist = fileIsExists(txtPath);
                        if (txtfileExist) {
                            //存在该文件这里仅仅弹出提示
                            showToast("路径正确 文件存在：" + txtPath);
                        }
                    }
                });

                mWritePadDialog.show();
            }
        });
    }


    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private boolean fileExist;
    // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
    List<String> mPermissionList = new ArrayList<>();

    private void getPermission() {
        //6.0获取多个权限
        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            Toast.makeText(MainActivity.this, "已经授权", Toast.LENGTH_LONG).show();
            //有权限，直接做自己想做得
//            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/1.jpg";
            fileExist = fileIsExists(path);
            readImg(iv_show);

        } else {//请求权限方法
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
    }

    public void readImg(View view) {
        if (path == null) {
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        iv_show.setImageBitmap(bitmap);
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[i]);
                    if (showRequestPermission) {
                        showToast("权限未申请");
                    } else {
                        //有权限，直接做自己想做得
//                        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/123.jpg";
                        fileExist = fileIsExists(path);
                        readImg(iv_show);

                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void showToast(String string) {
        Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
    }


    String path = null;

    //创建签名文件
    private void createSignFile() {
        ByteArrayOutputStream baos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
//            path = Environment.getExternalStorageDirectory() + File.separator + System.currentTimeMillis() + ".jpg";
            path = Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg";
            file = new File(path);
            fos = new FileOutputStream(file);
            baos = new ByteArrayOutputStream();
            //如果设置成Bitmap.compress(CompressFormat.JPEG, 100, fos) 图片的背景都是黑色的
            mSignBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            if (b != null) {
                fos.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}