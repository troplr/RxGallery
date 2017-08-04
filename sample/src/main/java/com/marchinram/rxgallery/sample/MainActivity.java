package com.marchinram.rxgallery.sample;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.marchinram.rxgallery.RxGallery;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void pickFromGallery(View view) {
        RxGallery.Request request = new RxGallery.Request.Builder()
                .setSource(RxGallery.Source.GALLERY)
                .setMultiSelectEnabled(true)
                .setMimeTypes(RxGallery.MimeType.IMAGE, RxGallery.MimeType.VIDEO)
                .build();

        RxGallery.single(this, request).subscribe(new Consumer<List<Uri>>() {
            @Override
            public void accept(List<Uri> uris) throws Exception {
                Toast.makeText(MainActivity.this, uris.size() + "", Toast.LENGTH_LONG).show();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void takePhoto(View view) {
        takePhotoOrVideo(true);
    }

    public void takeVideo(View view) {
        takePhotoOrVideo(false);
    }

    private void takePhotoOrVideo(boolean isPhoto) {
        Observable<Boolean> permissionObservable = Observable.just(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionObservable = new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        final RxGallery.Request request = new RxGallery.Request.Builder()
                .setSource(isPhoto ? RxGallery.Source.PHOTO_CAPTURE : RxGallery.Source.VIDEO_CAPTURE)
                .build();

        permissionObservable.flatMap(new Function<Boolean, ObservableSource<List<Uri>>>() {
            @Override
            public ObservableSource<List<Uri>> apply(@NonNull Boolean granted) throws Exception {
                if (!granted) {
                    return Observable.empty();
                }
                return RxGallery.observable(MainActivity.this, request);
            }
        }).subscribe(new Consumer<List<Uri>>() {
            @Override
            public void accept(List<Uri> uris) throws Exception {
                Toast.makeText(MainActivity.this, uris.size() + "", Toast.LENGTH_LONG).show();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
