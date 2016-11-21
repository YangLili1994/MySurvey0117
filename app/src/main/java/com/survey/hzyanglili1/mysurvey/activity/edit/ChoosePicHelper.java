package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;


/**
 * Created by Administrator on 2016/11/5.
 */

public class ChoosePicHelper {

    private Context context = null;

    public ChoosePicHelper(Context context) {
        this.context = context;
    }

    public String getPic(Intent intent){
        //判断安卓版本
        if (Build.VERSION.SDK_INT>=19){
            //4.4以上版本
            return handleImageOnKitKat(intent);
        }else{
            return handleImageBeforeKitKat(intent);
        }
    }

    private String handleImageOnKitKat(Intent data){
        String imagePath = null;
        Bitmap bitmap = null;
        //Uri uri = data.getData();
        Uri uri = geturi(data);

        if (DocumentsContract.isDocumentUri(context,uri)){
            //document类型的uri
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(context,contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(context,uri,null);
        }

        return imagePath;
    }


    /**
     * 解决小米手机上获取图片路径为null的情况
     * @param intent
     * @return
     */
    public Uri geturi(android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();

            Log.d("haha","uri path ===== "+path);

            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }


    private String handleImageBeforeKitKat(Intent data){
        Bitmap bitmap = null;

        Uri uri = data.getData();
        String imagePath = getImagePath(context,uri,null);

        return imagePath;
    }

    private String getImagePath(Context context,Uri uri, String selection){
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri,null,selection,null,null);

        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }

        return path;
    }

}
