package hu.uniobuda.nik.androgamers.main_menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hu.uniobuda.nik.androgamers.R;
import hu.uniobuda.nik.androgamers.Result;
import hu.uniobuda.nik.androgamers.ResultAdapter;

public class ProfileActivity extends Activity {

    private final static String usernameFilePath = "User";
    private final int CAMERA_REQUEST = 1;
    private final int SELECT_PICTURE = 2;
    private ImageView userpic;
    private ImageView camera;
    private ImageView gallery;
    private ListView listview;
    private List<Result> results;
    private ResultAdapter adapter;
    private TextView nameLabel;

    private EditText txtUserName;

    //Kép betöltése belső tárolóból
    public static Bitmap loadImage(Context context) {

        StringBuilder builder = new StringBuilder();
        int charsRead;
        char[] buffer = new char[4096];

        String FILENAME = "UserPic";
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while ((charsRead = reader.read(buffer)) != -1) {
                String message = new String(buffer).substring(0, charsRead);
                builder.append(message);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] img_bytes = builder.toString().getBytes();
        byte[] base_decoded = Base64.decode(img_bytes, 0); //Base64

        return BitmapFactory.decodeByteArray(base_decoded, 0, base_decoded.length); //jpeg dekódolás
    }

    //read username from file
    public static String getUsername(Context context) {
        String text = "DefaultUser";
        try {
            //create if not exist
            File file = new File(String.valueOf(context.getApplicationContext().getFileStreamPath(usernameFilePath)));
            if (!file.exists()) {
                setUsername(context, text);
            }
            //read
            FileInputStream fis = context.openFileInput(usernameFilePath);
            byte[] buffer = new byte[1024];
            int len;
            text = "";
            while ((len = fis.read(buffer)) > 0) {
                text += new String(buffer, 0, len);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    //save username to file
    public static void setUsername(Context context, String username) {
        try {
            //save
            FileOutputStream fos = context.openFileOutput(usernameFilePath, MODE_PRIVATE);
            fos.write(username.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //load username
        nameLabel = (TextView) findViewById(R.id.name_label);
        nameLabel.setText(getUsername(this));
        nameLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtUserName = new EditText(ProfileActivity.this);
                txtUserName.setHint("Anonymus");
                txtUserName.setMaxWidth(200);
                txtUserName.setMaxLines(1);

                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Felhasználónév")
                        .setMessage("Add meg az új felhasználóneved:")
                        .setView(txtUserName)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String name = txtUserName.getText().toString().trim();
                                if (name.matches("")) return;

                                setUsername(ProfileActivity.this, name);
                                nameLabel.setText(name);
                            }
                        })
                        .setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        userpic = (ImageView) findViewById(R.id.userpic);
        Bitmap image = loadImage(ProfileActivity.this); //Betöltjük a felhasználó képét
        if (image == null) //Lementjük a default képet, hogy a felhasználó képe ne legyen null
            saveImage(((BitmapDrawable) userpic.getDrawable()).getBitmap());
        else
            userpic.setImageBitmap(image);

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

        // -------------- DB local highscore
        DataBaseAR dbAR = new DataBaseAR(this);

        results = new ArrayList<Result>();
        Cursor c = dbAR.loadHighScores();
        while (!c.isAfterLast()) {
            String score = c.getString(c.getColumnIndex("score"));
            results.add(new Result(Integer.valueOf(score)));
            c.moveToNext();
        }
        //---------------------------------------------

        adapter = new ResultAdapter(results);
        listview = (ListView) findViewById(R.id.list_results);
        listview.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap photo;
        Bitmap scaled_photo = null;

        //kép készítése kamerával
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            if (photo == null)
                return;

            scaled_photo = Bitmap.createScaledBitmap(photo, 200, 200, false);
            userpic.setImageBitmap(scaled_photo);
        }
        //Kép betöltése galériából
        else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //első kép betöltése -> a válaszott fájl
            photo = BitmapFactory.decodeFile(picturePath);
            if (photo == null)
                return;

            scaled_photo = Bitmap.createScaledBitmap(photo, 200, 200, false);
            userpic.setImageBitmap(scaled_photo);
        }

        if (scaled_photo == null)
            return;

        saveImage(scaled_photo);
    }

    //Kép mentése belső tárolóra: jpeg és base64 kódolás
    private void saveImage(Bitmap photo) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 85, stream);
        byte[] byteArray = stream.toByteArray();
        byte[] img_bytes = Base64.encode(byteArray, 0);

        String FILENAME = "UserPic";
        FileOutputStream fos;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);

            fos.write(img_bytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
