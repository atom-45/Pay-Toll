package com.automatedcartollingsystem.models;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.net.URI;
import java.util.List;

public class ImageModel {

    private String name;
    private Long imageID;
    private int size;
    private Uri contentUri;
    private List<ImageModel> imageModels;



     public ImageModel(){ }

     public ImageModel(String name, Long imageID, int size, Uri contentUri){
         this.name = name;
         this.imageID = imageID;
         this.contentUri = contentUri;
         this.size = size;
     }

    /**
     * Accesses the external storage by 1st checking what type of Android System
     * is running
     * @return
     */


    public Uri getContentUri() {
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
             contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
         } else{
             contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
         }

        return contentUri;
    }

    public String getName() { return name;}
    public int getSize() { return size;}
    public Long getImageID() { return imageID; }

}
