package com.dongdian.jj.timdemo.ui;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;


import com.dongdian.jj.timdemo.R;

import java.io.IOException;

public class VideoActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {


    private SurfaceView videoSurface;
    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);
        videoSurface = (SurfaceView) findViewById(R.id.video);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);
        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        player.setLooping(true);
        try {
            player.setDataSource(getIntent().getStringExtra("path"));
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }

    }

    @Override
    protected void onStop(){
        super.onStop();
        if (player!=null){
            player.release();
        }
    }


    /**
     * This is called immediately after the surface is first created.
     * Implementations of this should start up whatever rendering code
     * they desire.  Note that only one thread can ever draw into
     * a {@link Surface}, so you should not draw into the Surface here
     * if your normal rendering will be in another thread.
     *
     * @param holder The SurfaceHolder whose surface is being created.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            player.setDisplay(holder);
            player.prepareAsync();
        }catch (IllegalStateException e) {
            Log.e("VideoActivity", "IllegalStateException");
        }

    }

    /**
     * This is called immediately after any structural changes (format or
     * size) have been made to the surface.  You should at this point update
     * the imagery in the surface.  This method is always called at least
     * once, after {@link #surfaceCreated}.
     *
     * @param holder The SurfaceHolder whose surface has changed.
     * @param format The new PixelFormat of the surface.
     * @param width  The new width of the surface.
     * @param height The new height of the surface.
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * This is called immediately before a surface is being destroyed. After
     * returning from this call, you should no longer try to access this
     * surface.  If you have a rendering thread that directly accesses
     * the surface, you must ensure that thread is no longer touching the
     * Surface before returning from this function.
     *
     * @param holder The SurfaceHolder whose surface is being destroyed.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return false;
    }
}
