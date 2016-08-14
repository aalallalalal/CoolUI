package com.dup.library;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by dup on 16-8-9.
 * 3D旋转 动画
 */
public class FlipCardAnimation extends Animation {
    //Type=0 : 竖直方向旋转 type =1：横向旋转
    private int type = 0;

    private boolean isContentChange = false;

    private Camera mCamera;
    private float mFromDegrees = 0;
    private float mToDegrees = 0;
    private float mCenterX = 0, mCenterY = 0;

    public void setCanContentChange() {
        isContentChange = false;
    }

    /**
     * @param fromDegrees
     * @param toDegrees
     * @param width
     * @param height
     * @param type        <br><li> type=0:竖直方向旋转</li>
     *                    <li>type= 1：横向旋转</li>
     *                    <p/>
     *                    默认竖直方向旋转
     */
    public FlipCardAnimation(int fromDegrees, int toDegrees, int width, int height, int type) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = width / 2;
        mCenterY = height / 2;
        this.type = type;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;

        float degrees = fromDegrees + (mToDegrees - fromDegrees) * interpolatedTime;

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();
        camera.save();

        if (degrees > 90 || degrees < -90) {
            if (!isContentChange) {
                if (listener != null) {
                    listener.contentChange();
                }
                isContentChange = true;
            }

            if (degrees > 0) {
                degrees = 180 + degrees;
            } else if (degrees < 0) {
                degrees = -180 + degrees;
            }
        }
        if (type == 0) {
            camera.rotateX(degrees);
        } else {
            camera.rotateY(degrees);
        }
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);

    }

    /**
     * 设置旋转方向
     *
     * @param type <br><li>
     *             type=0:竖直方向旋转</li>
     *             <li>type= 1：横向旋转</li>
     *             <p/>
     *             默认竖直方向旋转
     */
    public void setRotateOrientation(int type) {
        this.type = type;
    }

    private OnContentChangeListener listener;

    public void setOnContentChangeListener(OnContentChangeListener listener) {
        this.listener = listener;
    }

    public interface OnContentChangeListener {
        void contentChange();
    }


}
