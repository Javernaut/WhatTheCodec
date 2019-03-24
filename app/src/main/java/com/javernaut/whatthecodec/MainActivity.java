package com.javernaut.whatthecodec;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ja.com.whatthecodec.R;

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
                String messageToShow;
                if (path != null) {
                    messageToShow = getVideoCodecName(path);
                } else {
                    messageToShow = "Couldn't convert the URI";
                }
                Toast.makeText(this, messageToShow, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private static String getVideoCodecName(String path) {
        VideoFileConfig videoFileConfig = VideoFileConfig.create(path);
        if (videoFileConfig == null) {
            return "Couldn't open the file";
        }

        String fileFormat = videoFileConfig.getCodecName();
        videoFileConfig.release();

        return fileFormat;
    }
}
