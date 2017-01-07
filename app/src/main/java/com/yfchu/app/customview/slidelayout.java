package com.yfchu.app.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.ViewAnimator;

import com.yfchu.app.slideview.GoAcitivity;
import com.yfchu.app.slideview.R;
import com.yfchu.app.utils.CommonUtil;

/**
 * 滑动view
 */
public class slidelayout extends ViewGroup {

    private Context mContext;
    /**
     * 滑动实例
     */
    private Scroller mScroller;

    /**
     * 滑动速度
     */
    private VelocityTracker mVelocityTracker;

    /**
     * 左边界，右边界
     */
    private int leftBorder, rightBorder;

    /**
     * top层，bottom层
     */
    private LinearLayout top_layer;
    private RelativeLayout bottom_layer;

    /**
     * 最小移动像素,滑动距离
     */
    private int mTouchSlop, mScrollSlop = 200;

    /**
     * 按下、移动、上一次移动
     */
    private float mXDown, mXMove, mXLastMove;

    /**
     * 1:减
     * 2:加
     */
    private int algorithm = 1;
    private boolean isStart = false;
    private String repeate="";
    private Handler mHandler;

    public slidelayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // 第一步，创建Scroller的实例
        mScroller = new Scroller(context);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        // 获取TouchSlop值
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(viewConfiguration);
    }

    public void setBottom_layer(RelativeLayout bottom_layer) {
        this.bottom_layer = bottom_layer;
    }
    public void setHandler(Handler h) {
        this.mHandler = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 为ScrollerLayout中的每一个子控件测量大小
            childView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int childViewHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (i == 0) {//上下层高度保持一致
                childViewHeight = childView.getMeasuredHeight();
            }
            // 为ScrollerLayout中的每一个子控件在水平方向上进行布局
            childView.layout(i * childView.getMeasuredWidth(), 0, (i + 1) * childView.getMeasuredWidth(), childViewHeight);
            childView.setOnClickListener(null);
        }
        // 初始化左右边界值
        if (getChildCount() != 0) {
            top_layer = (LinearLayout) getChildAt(0);
            leftBorder = getChildAt(0).getLeft();
            rightBorder = getChildAt(getChildCount() - 1).getRight();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getRawX();
                mXLastMove = mXMove;
                float diff = Math.abs(mXMove - mXDown);
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff > mTouchSlop) {
                    algorithm = 1;
                    repeate="";
                    if (mXMove - mXDown > 0) {
                        isStart = true;
                        repeate+="true";
                        setLayerVisibility(1);
                    } else {
                        isStart = false;
                        repeate+="false";
                        setLayerVisibility(0);
                    }
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(event);
                mXMove = event.getRawX();
                int scrolledX = (int) (mXLastMove - mXMove);
                if (getScrollX() + scrolledX < leftBorder) { //view过右边界
                    setLayerVisibility(1);
                } else if (getScrollX() + getWidth() + scrolledX > rightBorder) { //view过左边界
                    setLayerVisibility(0);
                }
                scrollBy(scrolledX, 0);
                if (algorithm == 1) {
                    top_layer.setAlpha(top_layer.getAlpha() - 0.01f);
                } else if (algorithm == 2) {
                    top_layer.setAlpha(top_layer.getAlpha() + 0.01f);
                }
                if (isStart == true) {
                    if (mXLastMove < mXMove) {
                        algorithm = 1;
                    } else {
                        algorithm = 2;
                        if (getScrollX() > -50 && getScrollX() <= 0 && isStart == true) { //view往左
                            top_layer.setAlpha(1f);
                            isStart = !isStart;
                            repeate+="false";
                        }
                    }
                } else {
                    if (mXLastMove > mXMove) {
                        algorithm = 1;
                    } else {
                        algorithm = 2;
                        if (getScrollX() > 0 && getScrollX() <= 50 && isStart == false) { //view往右
                            top_layer.setAlpha(1f);
                            isStart = !isStart;
                            repeate+="true";
                        }
                    }
                }
                mXLastMove = mXMove;
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                boolean RollBACK = false;
                if (mXDown - mXLastMove > CommonUtil.dp2px(mContext, mScrollSlop)) { //view过左边界,隐藏电话层
                    RollBACK = ScrollLeft();
                } else if (mXDown - mXLastMove < CommonUtil.dp2px(mContext, -mScrollSlop)) {
                    RollBACK = ScrollRigth();
                } else if (Math.abs(mVelocityTracker.getXVelocity()) > 500 && (repeate.equals("true") || repeate.equals("false"))) {
                    if (mXDown - mXLastMove > 0) {
                        RollBACK = ScrollLeft();
                    } else {
                        RollBACK = ScrollRigth();
                    }
                }
                if (RollBACK == false) {//滑动取消，回滚view
                    if (mXDown - mXLastMove > 0) {
                        RollBackMethod(leftBorder);
                    } else if (mXDown - mXLastMove < 0) {
                        RollBackMethod(rightBorder - getWidth());
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 快速滑动同时把view过程透明
     */
    private void Alpha0() {
        final ValueAnimator anim1 = ValueAnimator.ofFloat(top_layer.getAlpha(), 0.0f);
        anim1.setDuration(1000);
        anim1.start();
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                top_layer.setAlpha((Float) animation.getAnimatedValue());
            }
        });
    }

    /**
     * view往右
     * */
    private boolean ScrollRigth() {
        boolean RollBACK;
        bottom_layer.getChildAt(1).setVisibility(View.GONE);
        bottom_layer.getChildAt(0).setVisibility(View.VISIBLE);
        mScroller.startScroll(getScrollX(), 0, -rightBorder, 0, 2000);
        RollBACK = true;
        Alpha0();
        mHandler.sendEmptyMessage(2);
        return RollBACK;
    }

    /**
     * view往左
     * */
    private boolean ScrollLeft() {
        boolean RollBACK;
        bottom_layer.getChildAt(1).setVisibility(View.VISIBLE);
        bottom_layer.getChildAt(0).setVisibility(View.GONE);
        mScroller.startScroll(getScrollX(), 0, rightBorder, 0, 2000);
        RollBACK = true;
        Alpha0();
        mHandler.sendEmptyMessage(1);
        return RollBACK;
    }

    /**
     * 回滚
     * */
    public void RollBackMethod(int temp) {
        final ValueAnimator anim = ValueAnimator.ofInt(getScrollX(), temp);
        anim.setDuration(500);
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) anim.getAnimatedValue(), 0);
            }
        });
        final ValueAnimator anim1 = ValueAnimator.ofFloat(top_layer.getAlpha(), 1.0f);
        anim1.setDuration(500);
        anim1.start();
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                top_layer.setAlpha((Float) animation.getAnimatedValue());
            }
        });
    }

    @Override
    public void computeScroll() {
        //重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
        super.computeScroll();
    }

    private void setLayerVisibility(int i) {
        if (i == 1) {
            bottom_layer.setBackgroundResource(R.color.green);
            bottom_layer.getChildAt(1).setVisibility(View.GONE);
            bottom_layer.getChildAt(0).setVisibility(View.VISIBLE);
        } else {
            bottom_layer.setBackgroundResource(R.color.orange);
            bottom_layer.getChildAt(0).setVisibility(View.GONE);
            bottom_layer.getChildAt(1).setVisibility(View.VISIBLE);
        }
    }
}
