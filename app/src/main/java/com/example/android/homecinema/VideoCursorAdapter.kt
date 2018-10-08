package com.example.android.homecinema

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView
import java.sql.Time

class VideoCursorAdapter(context: Context?, cursor: Cursor?) : CursorAdapter(context,cursor,0) {
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val myVideoItem = VideoItem(
                cursor!!.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA))
                )
        //view?.findViewById<ImageView>(R.id.video_thumbnail)?.setImageURI(Uri.withAppendedPath(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,myVideoItem.thumbnailPath))
        if(myVideoItem.thumbnailPath != null){
            val options = BitmapFactory.Options()
            options.inSampleSize = 1
            view?.findViewById<ImageView>(R.id.video_thumbnail)?.setImageBitmap(MediaStore.Video.Thumbnails.getThumbnail(context!!.contentResolver,myVideoItem.ID,3,options))
        }else{
            view?.findViewById<ImageView>(R.id.video_thumbnail)?.setImageBitmap(ThumbnailUtils.createVideoThumbnail(myVideoItem.path, 3))
        }
        view?.findViewById<TextView>(R.id.tv_video_name)?.text = myVideoItem.displayName
        val videoDuration = Time(myVideoItem.duration).toString()
        view?.findViewById<TextView>(R.id.video_duration)?.text = videoDuration.drop(3)
    }
}