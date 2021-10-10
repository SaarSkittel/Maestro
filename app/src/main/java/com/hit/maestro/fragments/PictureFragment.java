package com.hit.maestro.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hit.maestro.R;
import com.hit.maestro.User;
import com.hit.maestro.proxy.DatabaseProxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureFragment extends DialogFragment {
    FloatingActionButton camera;
    FloatingActionButton album;
    Button save;
    ImageView picture;
    String source;
    File file;
    Uri pic;
    TextView title;
    boolean exit=false;
    final int write_permission_request_camera = 1;
    final int write_permission_request_album = 2;
    ActivityResultLauncher<Intent> takePictureActivityResultLauncher;
    ActivityResultLauncher<Intent> picFromAlbumActivityResultLauncher;
    RegisterFragment registerFragment;
    MainFragment mainFragment;
    View view;
    String UID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.picture_fragment,container,false);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_shape);

        title=view.findViewById(R.id.change_pic_tv);
        if(mainFragment!=null){
            UID=User.getInstance().getUID();
        }
        camera=view.findViewById(R.id.camera_btn);
        album=view.findViewById(R.id.album_btn);
        picture=view.findViewById(R.id.song_iv);
        save=view.findViewById(R.id.save_pic_btn);
        Glide.with(this)
                .load(pic)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(picture);

        file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg");
        takePictureActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    exit=true;
                    Glide.with(PictureFragment.this).load(pic).into(picture);
                }
            }
        });

        picFromAlbumActivityResultLauncher =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Glide.with(PictureFragment.this).load(result.getData().getData()).into(picture);
                    pic=result.getData().getData();
                    view.getContext().getContentResolver().takePersistableUriPermission(pic
                            , result.getData().getFlags()
                                    & ( Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    + Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            )
                    );
                    exit=true;
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reduce();
                if(registerFragment!=null)
                    registerFragment.setPic(pic);
                else{
                    DatabaseProxy.getInstance().setUserImageInStorage(pic, UID);
                    mainFragment.setAndLoadPic(pic);
                }
                PictureFragment.this.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int hasWritePermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (hasWritePermission == PackageManager.PERMISSION_GRANTED) {
                        takePicture();
                    } else {

                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, write_permission_request_camera);
                    }
                } else {
                    takePicture();
                }

            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int hasWritePermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (hasWritePermission == PackageManager.PERMISSION_GRANTED) {
                        picFromMemory();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, write_permission_request_album);
                    }
                } else {
                    picFromMemory();
                }
            }
        });

        ImageView close = view.findViewById(R.id.close_pic);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureFragment.this.dismiss();
            }
        });

        return view;
    }

    private void picFromMemory(){
        exit=false;
        Intent intent= new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        picFromAlbumActivityResultLauncher.launch(intent);
    }

    private void takePicture() {
        exit=false;
        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pic= FileProvider.getUriForFile(view.getContext(),getActivity().getPackageName()+".provider",file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,pic);
        takePictureActivityResultLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == write_permission_request_camera){
                takePicture();
            }
            else if(requestCode == write_permission_request_album){
                picFromMemory();
            }
            else {
                Toast.makeText(view.getContext(),getResources().getString(R.string.permissions),Toast.LENGTH_LONG).show();
            }
        }
    }

    public PictureFragment(RegisterFragment registerFragment, Uri pic) {
        this.registerFragment = registerFragment;
        this.pic=pic;
    }

    public PictureFragment(MainFragment mainFragment, Uri pic) {
        this.mainFragment = mainFragment;
        this.pic=pic;
    }

    private Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap rotateImage(Bitmap src, float degree)
    {
        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }

    private void reduce(){
        try {
            Bitmap bitmap = decodeUri(getContext(),pic,200);
            //bitmap = rotateImage(bitmap,270);
            pic = getImageUri(getContext(),bitmap);
        }
        catch (FileNotFoundException e){

        }
    }
}
