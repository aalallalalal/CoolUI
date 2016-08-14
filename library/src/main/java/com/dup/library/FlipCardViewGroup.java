package com.dup.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by dup on 16-8-9.
 * <p/>
 * 3D切换 子view
 * 支持自动切换和手动切换
 */
public class FlipCardViewGroup extends RelativeLayout {

    private static final String TAG = "TAG";
    private static final String BUNDLE_FIRST = "first";
    private static final String BUNDLE_CURRENT = "current";
    private static final String BUNDLE_DEF = "default";

    private Context context;

    //旋转动画时长
    private int duration = 1200;
    //0为纵向旋转,1为横向
    private int rotateType = 0;
    //旋转效果
    private int interpolator;
    //第一个显示的子view index
    private int firstIndex = 0;

    //是否自动旋转
    private boolean play_auto = false;
    //自动旋转起始index
    private int play_startIndex = 0;

    private int play_endIndex = 0;
    //自动旋转次数 -1为无限
    private int play_repeatCount = 1;
    //旋转开始延迟
    private int play_startDelay = 2000;
    //旋转中一项停留时间
    private int play_idle = 3000;
    //切换顺序
    private boolean play_isreverse = false;

    private int currentIndex = firstIndex;


    public FlipCardViewGroup(Context context) {
        this(context, null);
    }

    public FlipCardViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipCardViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlipCardViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int styleAttr, int defStyleAttr) {
        this.context = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlipCardViewGroup, styleAttr, defStyleAttr);

        duration = ta.getInteger(R.styleable.FlipCardViewGroup_duration, duration);
        rotateType = ta.getInteger(R.styleable.FlipCardViewGroup_rotate_type, rotateType);
        interpolator = ta.getResourceId(R.styleable.FlipCardViewGroup_interpolator, android.R.interpolator.anticipate_overshoot);
        firstIndex = ta.getInteger(R.styleable.FlipCardViewGroup_first_item_index, firstIndex);
        currentIndex = firstIndex;

        play_auto = ta.getBoolean(R.styleable.FlipCardViewGroup_play_auto, play_auto);
        play_startIndex = ta.getInteger(R.styleable.FlipCardViewGroup_play_start_item_index, play_startIndex);
        play_endIndex = ta.getInteger(R.styleable.FlipCardViewGroup_play_end_item_index, play_endIndex);
        play_repeatCount = ta.getInteger(R.styleable.FlipCardViewGroup_play_repeat_count, play_repeatCount);
        play_startDelay = ta.getInteger(R.styleable.FlipCardViewGroup_play_start_delay, play_startDelay);
        play_idle = ta.getInteger(R.styleable.FlipCardViewGroup_play_idle, play_idle);
        play_isreverse = ta.getBoolean(R.styleable.FlipCardViewGroup_play_isreverse, play_isreverse);
        ta.recycle();

        if (play_auto) {
            playAnimation(play_startIndex, play_endIndex, play_repeatCount, play_startDelay, play_idle, play_isreverse);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        if (count == 0) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }

            if (i == currentIndex) {
                child.layout(0 + getPaddingLeft(), 0 + getPaddingTop(), r - l - getPaddingRight(), b - t - getPaddingBottom());
            } else {
                child.layout(0, 0, 0, 0);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; ++i) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_FIRST, firstIndex);
        bundle.putInt(BUNDLE_CURRENT, currentIndex);
        bundle.putParcelable(BUNDLE_DEF, super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            currentIndex = bundle.getInt(BUNDLE_CURRENT);
            firstIndex = bundle.getInt(BUNDLE_FIRST);
            super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_DEF));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    /**
     * 切换至 第index个item
     *
     * @param index 要切换至的item index
     */
    public void changeToItem(final int index) {
        post(new Runnable() {
            @Override
            public void run() {
                int correctIndex = getCorrectIndex(index);
                if (correctIndex == currentIndex) {
                    return;
                }

                currentIndex = correctIndex;

                final FlipCardAnimation animation = new FlipCardAnimation(0, 180, getWidth(), getHeight(), rotateType);
                animation.setDuration(duration);
                animation.setInterpolator(context, interpolator);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        ((FlipCardAnimation) animation).setCanContentChange();
                    }
                });
                animation.setOnContentChangeListener(new FlipCardAnimation.OnContentChangeListener() {
                    @Override
                    public void contentChange() {
                        requestLayout();
                    }
                });
                startAnimation(animation);
            }
        });
    }


    /**
     * 勿多次调用,会影响当前index数据。
     *
     * @param index
     */
    public void setFirstIndex(int index) {
        firstIndex = index;
        currentIndex = firstIndex;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setRotateType(int rotateType) {
        this.rotateType = rotateType;
    }

    public void setInterpolator(int interpolator) {
        this.interpolator = interpolator;
    }

    /**
     * 重置状态，变化至 firstindex
     */
    public void reset() {
        changeToItem(firstIndex);
    }

    /**
     * 启动自动转动动画
     *
     * @param fromIndex         ：开始index
     * @param toIndex           :结束index
     * @param repeatCount：重复次数
     * @param startdelay：开始动画延时
     * @param idle：间隔动画延时
     * @param isReverse：是否反向播放  eg:如果有三个item<br>
     *                          <li>1-1-正向：item播放顺序是：1，2,0,1</li>
     *                          <li>1-1-反向：1，0,2,1</li>
     *                          <li>0-1-正向：0,1</li>
     *                          <li>0-1-反向：0,2,1</li>
     *                          <li>2-1-正向：2,0,1</li>
     *                          <li>2-1-反向：2,1</li>
     */
    public void playAnimation(final int fromIndex, final int toIndex, final int repeatCount, final long startdelay, final long idle, final boolean isReverse) {
        play_startIndex = fromIndex;
        play_endIndex = toIndex;
        play_repeatCount = repeatCount;
        play_startDelay = (int) startdelay;
        play_idle = (int) idle;
        play_isreverse = isReverse;
        post(new Runnable() {
            @Override
            public void run() {
                //1.瞬間到指定开始index
                currentIndex = fromIndex;
                requestLayout();

                mThread = new PlayThread(fromIndex, toIndex, repeatCount, isReverse);
                if (executor != null) {
                    executor.shutdownNow();
                }
                executor = new ScheduledThreadPoolExecutor(1);
                executor.scheduleWithFixedDelay(mThread, startdelay, idle, TimeUnit.MILLISECONDS);
            }
        });

    }

    public void reStartAnimation(){

        playAnimation(play_startIndex,play_endIndex,play_repeatCount,play_startDelay,play_idle,play_isreverse);
    }

    public void stopAnimation() {
        clearAnimation();
        if (executor != null) {
            executor.shutdownNow();
        }
    }


    /**
     * 校准index
     *
     * @param index
     * @return
     */
    private int getCorrectIndex(int index) {
        int tmpIndex = currentIndex;
        if (index < 0 || index >= getChildCount()) {
        } else {
            tmpIndex = index;
        }
        return tmpIndex;
    }

    /**
     * 变化线程
     */
    private PlayThread mThread;
    private ScheduledThreadPoolExecutor executor;

    /**
     * 计算下一个index,执行动画
     */
    private class PlayThread extends Thread {
        private final int fromIndex;
        private final int toIndex;
        private final int repeateCount;
        private final boolean isReverse;

        private int currentIndex;
        private int hasRepeateCount = 0;

        public PlayThread(int fromIndex, int toIndex, int repeateCount, boolean isReverse) {
            this.fromIndex = fromIndex;
            currentIndex = fromIndex;
            this.toIndex = toIndex;
            this.repeateCount = repeateCount;
            this.isReverse = isReverse;
        }

        @Override
        public void run() {
            super.run();
            //如果达到重复次数,或不为infinite就停止
            if (hasRepeateCount >= repeateCount && repeateCount != -1) {
                executor.shutdownNow();
                return;
            }
            //正向进行
            if (!isReverse) {
                currentIndex += 1;
                if (currentIndex == getChildCount()) {
                    currentIndex = 0;
                }
                if (currentIndex == toIndex) {
                    hasRepeateCount++;
                }

                changeToItem(currentIndex);
            }
            //反向进行
            else {
                currentIndex -= 1;
                if (currentIndex == -1) {
                    currentIndex = getChildCount() - 1;
                }
                if (currentIndex == toIndex) {
                    hasRepeateCount++;
                }
                changeToItem(currentIndex);
            }
        }
    }

}
