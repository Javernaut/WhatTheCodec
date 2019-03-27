package com.javernaut.whatthecodec;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int PICK_VIDEO_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.pick_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT)
                                .setType("video/*")
                                .addCategory(Intent.CATEGORY_OPENABLE),
                        PICK_VIDEO_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String path = PathUtil.getPath(this, data.getData());
                if (path != null) {
                    tryGetVideoConfig(path);
                } else {
                    toast("Couldn't convert the URI");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void tryGetVideoConfig(String path) {
        VideoFileConfig videoFileConfig = VideoFileConfig.create(path);
        if (videoFileConfig != null) {
            setVideoConfig(videoFileConfig);
            videoFileConfig.release();
        } else {
            toast("Couldn't open the file");
        }
    }

    private void setVideoConfig(VideoFileConfig config) {
        findViewById(R.id.details_panel).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.file_format)).setText(config.getFileFormat());
        ((TextView) findViewById(R.id.video_codec)).setText(config.getCodecName());
        ((TextView) findViewById(R.id.width)).setText(String.valueOf(config.getWidth()));
        ((TextView) findViewById(R.id.height)).setText(String.valueOf(config.getHeight()));
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
