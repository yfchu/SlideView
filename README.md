# SlideView
滑动拨号和发短信的view，往右滑动拨号，往左是发短信。喜欢的朋友可以看下。

![image](https://github.com/yfchu/SlideView/blob/master/apk/1.gif)   
![image](https://github.com/yfchu/SlideView/blob/master/apk/2.png)  
```java  
	//Maactivity内容:
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mSlidelayout = (slidelayout) findViewById(R.id.slidelayout);
        mSlidelayout.bringToFront();//层置顶
        mBottom_layout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mSlidelayout.setBottom_layer(mBottom_layout);//bottom层
        mSlidelayout.setHandler(mHandler);//滑动结束通知Activity操作
    }  
	
	//SlideView内容：
	/**
	* 测量子view宽高
	*/
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 为ScrollerLayout中的每一个子控件测量大小
            childView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

	/**
	* 重写layout布局
	*/
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
```
