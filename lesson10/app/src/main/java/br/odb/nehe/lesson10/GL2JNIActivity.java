/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.odb.nehe.lesson10;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;

public class GL2JNIActivity extends Activity {

    GL2JNIView mView;
    boolean running = false;
    static AssetManager assets;
    private float fingerX;
    private float fingerY;
    private boolean touching;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        assets = getAssets();
        GL2JNILib.onCreate(assets);

        try {
            GL2JNILib.setTexture(new Bitmap[]{
                    BitmapFactory.decodeStream(assets.open("mud.png")),
                    BitmapFactory.decodeStream(assets.open("bricks.png")),
                    BitmapFactory.decodeStream(assets.open("grass.png"))
            });

        } catch (IOException e) {
        }

        mView = new GL2JNIView(getApplication());
        setContentView(mView);
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        touching = false;
                        return true;
                    case MotionEvent.ACTION_DOWN:
                        touching = true;
                    case MotionEvent.ACTION_MOVE:
                        fingerX = event.getX() / v.getWidth();
                        fingerY = event.getY() / v.getHeight();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_T:
                return true;

            case KeyEvent.KEYCODE_DPAD_UP:
                GL2JNILib.moveForward();
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                GL2JNILib.turnRight();
                return true;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                GL2JNILib.moveBackward();
                return true;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                GL2JNILib.turnLeft();
                return true;

            case KeyEvent.KEYCODE_R:
                GL2JNILib.reset();
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();

        running = false;
        mView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                running = true;
                while (running) {
                    try {
                        Thread.sleep(20);
                        if (touching) {
                            GL2JNILib.onTouchNormalized(fingerX, fingerY);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GL2JNILib.tick();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {

        GL2JNILib.onDestroy();

        super.onDestroy();
    }
}
