package com.hit.maestro.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import java.io.File;
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
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.picture_fragment,container,false);
        title=view.findViewById(R.id.change_pic_tv);

        camera=view.findViewById(R.id.camera_btn);
        album=view.findViewById(R.id.album_btn);
        picture=view.findViewById(R.id.song_iv);
        save=view.findViewById(R.id.save_pic_btn);
        //pic =Uri.parse(requireArguments().getString("image"));
        pic = registerFragment.getPic();
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
                registerFragment.setPic(pic);
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
        return view;
    }

    private void picFromMemory(){
        exit=false;
        //Intent intent= new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
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
                Toast.makeText(view.getContext(),"Can't work without permissions go to setting to grant access.",Toast.LENGTH_LONG).show();
            }
        }
    }

    public PictureFragment(RegisterFragment registerFragment) {
        this.registerFragment = registerFragment;
    }
}
