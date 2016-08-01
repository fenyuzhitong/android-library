

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;

public class CropImageActivity extends AppCompatActivity {

    private static final String IMAGE_PATH_KEY = "image_path";

    public static final String ORDER_TITLE = "title";
    public static final String ORDER_SELECT_FROM_FILE = "file";
    public static final String ORDER_SELECT_FROM_CAMERA = "camera";
    private static final int MAX_WIDTH = 1920;

    public static final int FROM_FILE = 1314;
    public static final int FROM_CAREMA = 1315;

    private static Bitmap image;//当前图片
    private CropImageView cropImageView;
    private String imageTemp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        imageTemp = getIntent().getStringExtra(IMAGE_PATH_KEY);
        if (imageTemp == null || imageTemp.isEmpty()) {
            finish();
            return;
        }
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cropImageView = (CropImageView) findViewById(R.id.cropView);
        cropImageView.setHandleColor(getResources().getColor(R.color.colorAccent));
        cropImageView.setFrameColor(getResources().getColor(R.color.colorAccent));
        cropImageView.setGuideColor(getResources().getColor(R.color.colorAccent));
        image = null;
        if (getIntent().getStringExtra(ORDER_TITLE) == null || getIntent().getStringExtra(ORDER_TITLE).equals("")) openCamera();
        else if (getIntent().getStringExtra(ORDER_TITLE).equals(ORDER_SELECT_FROM_FILE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, FROM_FILE);
        }
        else if (getIntent().getStringExtra(ORDER_TITLE).equals(ORDER_SELECT_FROM_CAMERA)) openCamera();
        else openCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cropImageView.setImageBitmap(image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.ok:
                BitmapHelper.saveBitmap(imageTemp, cropImageView.getCroppedBitmap());
                setResult(RESULT_OK);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            finish();
            return;
        }
        ContentResolver cr = this.getContentResolver();
        int degree;
        switch (requestCode) {
            case FROM_FILE:
                Uri uri = data.getData();
                String path = BitmapHelper.getRealFilePath(CropImageActivity.this, uri);
                degree = BitmapHelper.readPictureDegree(path);
                image = BitmapHelper.readBitmap(path, MAX_WIDTH, degree);
                break;
            case FROM_CAREMA:
                degree = BitmapHelper.readPictureDegree(imageTemp);
                image = BitmapHelper.readBitmap(imageTemp, MAX_WIDTH, degree);
                break;
        }
        if (image != null) {
            if (image.getWidth() > MAX_WIDTH)
                image = BitmapHelper.scale(image, MAX_WIDTH, (int)((float)image.getHeight() / ((float)image.getWidth() / MAX_WIDTH)));
            if (image.getHeight() > MAX_WIDTH)
                image = BitmapHelper.scale(image, (int)((float)image.getWidth() / ((float)image.getHeight() / MAX_WIDTH)), MAX_WIDTH);
        }
    }

    private void openCamera() {
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.addCategory("android.intent.category.DEFAULT");
        File file = new File(imageTemp);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, FROM_CAREMA);
    }

    /**
     * 使用这个方法启动activity
     * @param activity 跳转起始activity
     * @param imagePath 截图完成后图片保存的路径
     * @param fromCamera 是否从摄像机获取图片，默认是从手机中获取图片
     */
    public static void startActivity(Activity activity, String imagePath, boolean fromCamera, int requestCode) {
        Intent intent = new Intent(activity, CropImageActivity.class);
        intent.putExtra(IMAGE_PATH_KEY, imagePath);
        if (fromCamera) intent.putExtra(CropImageActivity.ORDER_TITLE, CropImageActivity.ORDER_SELECT_FROM_CAMERA);
        else intent.putExtra(CropImageActivity.ORDER_TITLE, CropImageActivity.ORDER_SELECT_FROM_FILE);
        activity.startActivityForResult(intent, requestCode);
    }
}
