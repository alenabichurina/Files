package com.example.files;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private Button btnChoose;
    private Button btnShare;
    private ImageView imageView;
    private String path = ".";
    public Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // хватаем файл
        intent=getIntent();
        if(intent != null) {
            String action=intent.getAction();
            if(Intent.ACTION_VIEW.equals(action) ) {
                Uri file_uri=intent.getData();
                if(file_uri!=null)
                    path=file_uri.getPath();
                    setImage(file_uri);
            } else if(Intent.ACTION_SEND.equals(action) ) {
                Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                path = uri.getPath();
            }
        }
        // иницилизируем с xml
        btnChoose = (Button)findViewById(R.id.btnChoose);
        btnShare = (Button)findViewById(R.id.btnShare);
        imageView = (ImageView) findViewById(R.id.imageView);
        // кодим кнопки
        btnChoose.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 14);
            }
        });
        btnShare.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(path.equals("")) {
                    Message("Ошибка", "Не выбран файл");
                    return;
                } else {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
                    startActivity(Intent.createChooser(share,"Share via"));
                }
            }
        });
    }
    private void Message(String title, String Message) {
        AlertDialog.Builder message = new AlertDialog.Builder(this);
        message.setCancelable(true);
        message.setTitle(title);
        message.setMessage(Message);
        message.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 14) {
            if (data != null && data.getData() != null) {
                Uri select = data.getData();
                InputStream inputStream = null;
                try {
                    assert select != null;
                    inputStream = getContentResolver().openInputStream(select);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                BitmapFactory.decodeStream(inputStream);
                imageView.setImageURI(select);
                path = data.getData().getPath();
//                imageView.buildDrawingCache();
//                Bitmap bitmap = imageView.getDrawingCache();
//                File storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //context.getExternalFilesDir(null);
//                File file = new File(storageLoc.getAbsolutePath(), "temp_pic" + ".jpg");
//                try{
//                    FileOutputStream fos = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                    fos.close();
//
//                    scanFile(getApplicationContext(), Uri.fromFile(file));
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        }
    }
//    private static void scanFile(Context context, Uri imageUri){
//        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        scanIntent.setData(imageUri);
//        context.sendBroadcast(scanIntent);
//
//    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void setImage(Uri select) {
        InputStream inputStream = null;
        try {
            assert select != null;
            inputStream = getContentResolver().openInputStream(select);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BitmapFactory.decodeStream(inputStream);
        imageView.setImageURI(select);
    }


}
