package com.oenik.bir.skillgame;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Activity {

    private ImageView userpic;
    private ImageView camera;
    private ImageView gallery;

    private ListView listview;
    private List<Result> results;
    private ResultAdapter adapter;

    private final int CAMERA_REQUEST = 1;
    private final int SELECT_PICTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userpic = (ImageView) findViewById(R.id.userpic);
        camera = (ImageView) findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        gallery = (ImageView) findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_PICTURE);
            }
        });

        results = new ArrayList<Result>();
        results.add(new Result("Béla", 22));
        results.add(new Result("János", 20));
        results.add(new Result("Ferenc", 12));
        results.add(new Result("István", 9));

        adapter = new ResultAdapter(results);
        listview = (ListView) findViewById(R.id.list_results);
        listview.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            userpic.setImageBitmap(photo);
        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            userpic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }
}
