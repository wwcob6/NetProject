package com.punuo.sys.sdk.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.punuo.sys.sdk.R;

import java.util.Locale;

/**
 * Created by han.chen.
 * Date on 2019-08-05.
 **/
public class PagerSlidingTabStrip extends HorizontalScrollView {//当做工具类来使用，实现标签滑动跟踪等功能

    public static final int DRAWABLE_TOP = 0;
    public static final int DRAWABLE_LEFT = 1;
    public static final int DRAWABLE_RIGHT = 2;
    public static final int DRAWABLE_BOTTOM = 3;

    private int mTouchSlop;
    private float mFirstMotionX;
    private float mFirstMotionY;
    private float mLastMotionX;
    private float mLastMotionY;
    boolean scrolling = false;
    private Rect dirty = new Rect();// 矩形(这里只是个形式，只是用于判断是否需要动画.)

    //一个屏幕内可以有的tab数。用于均分。
    private int tabNumInScreen;

    private int drawablePosition;

    public int getTabNumInScreen() {
        return tabNumInScreen;
    }

    public void setTabNumInScreen(int tabNumInScreen) {
        this.tabNumInScreen = tabNumInScreen;
    }

    public void setDrawablePosition(int drawablePosition) {
        this.drawablePosition = drawablePosition;
    }

    private OnSelectedListener mSelectedListener;

    public void setSelectedListener(OnSelectedListener listener) {
        mSelectedListener = listener;
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{
            android.R.attr.textSize,
            android.R.attr.textColor,
    };
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public ViewPager.OnPageChangeListener delegatePageListener;

    public LinearLayout tabsContainer;
    public ViewPager pager;

    public int tabCount;

    public int currentPosition = 0;
    private float currentPositionOffset = 0f;
    private int currentPageSelected = 0; //Fix : ICON SELECTOR

    private Paint rectPaint;
    private Paint dividerPaint;
    private Paint textPaint;
    private Paint textDefaultPaint;

    private int indicatorColor = 0xFF666666;
    private int underlineColor = 0x1A000000;
    private int dividerColor = 0x1A000000;

    private boolean shouldExpand = false;
    private boolean textAllCaps = true;

    //边缘回弹效果开关
    private boolean overScroll = false;
    //字体颜色渐变开关
    private boolean colorGradualOpen = false;
    //indicator弹性效果开关
    private boolean indicatorSmoothOpen = false;

    private int scrollOffset = 52;
    private int indicatorHeight = 8;
    private int underlineHeight = 2;
    private int dividerPadding = 12;
    private int indicatorPadding = 0;
    private int drawablePadding = 4;
    private int tabPadding = 24;
    private int dividerWidth = 1;
    /**
     * indicator与text的间距
     */
    private int mIndicatorTextPadding = 4;
    private AttributeSet attrs;
    private Context context;

    private int tabTextSize = 12;
    private int tabTextSizeSelected = 12;
    private int tabTextSizeDiff = 0;
    private int tabTextColor = 0xFF666666;
    private int tabTextColorSelected = 0xFF4965;
    private Typeface tabTypeface = null;
    protected static Typeface defaultTabTypeface = null;  //设置全局默认字体库
    private int tabTypefaceStyle = Typeface.NORMAL;

    public int selectedPosition = 0;

    private int lastScrollX = 0;

    private Locale locale;

    private boolean isCanBigger = false;

    private Rect defaultBound = new Rect();
    private Rect mCurBound = new Rect();

    private int indicatorWidth = -1;

    /**
     * 当前indicator的padding
     */
    private float mIndicatorPaddingLeftRight;

    /**
     * 当前indicator的padding
     *
     * @param indicatorPaddingLeftRight px
     */
    public void setIndicatorPaddingLeftRight(float indicatorPaddingLeftRight) {
        mIndicatorPaddingLeftRight = indicatorPaddingLeftRight;
        mNextIndicatorPaddingLeftRight = indicatorPaddingLeftRight;
    }

    /**
     * 下一个indicator的padding
     */
    private float mNextIndicatorPaddingLeftRight;

    /**
     * 下一个indicator的padding
     *
     * @param nextIndicatorPaddingLeftRight px
     */
    public void setNextIndicatorPaddingLeftRight(float nextIndicatorPaddingLeftRight) {
        mNextIndicatorPaddingLeftRight = nextIndicatorPaddingLeftRight;
    }

    /**
     * RectF
     */
    private RectF mRectF = new RectF();
    /**
     * 判断是否是点击tab
     */
    private boolean mIsClicked = false;

    protected void setIsClicked(boolean isClicked) {
        mIsClicked = isClicked;
    }

    public PagerSlidingTabStrip(Context context) {
        this(context, null);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);
        this.attrs = attrs;
        this.context = context;
        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);
        tabTextSizeSelected = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSizeSelected, dm);
        indicatorPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, indicatorPadding, dm);
        tabTextSizeDiff = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSizeDiff, dm);
        mIndicatorTextPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mIndicatorTextPadding, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
        tabTextSizeSelected = a.getDimensionPixelSize(0, tabTextSizeSelected);
        tabTextColor = a.getColor(1, tabTextColor);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

        indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor);
        underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor);
        tabTextColorSelected = a.getColor(R.styleable.PagerSlidingTabStrip_pstsTextColorSelect, indicatorColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, tabPadding);
        shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);
        overScroll = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsOverScroll, overScroll);
        colorGradualOpen = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsColorGradualOpen, colorGradualOpen);
        indicatorSmoothOpen = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsIndicatorSmoothOpen, indicatorSmoothOpen);
        indicatorPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorPadding, indicatorPadding);
        tabTextSizeDiff = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTextSizeDiff, tabTextSizeDiff);

        a.recycle();
        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);
        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textDefaultPaint = new Paint();
        textDefaultPaint.setAntiAlias(true);
        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    /**
     * 设置默认的字体库（每次都去设置其实还挺烦的）
     *
     * @param typeface
     */
    public static void setDefaultTabTypeface(Typeface typeface) {
        defaultTabTypeface = typeface;
    }

    public void setCanChangeBig(boolean canBigger) {
        isCanBigger = canBigger;
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        pager.addOnPageChangeListener(pageListener);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();
        selectedPosition = pager.getCurrentItem();

        for (int i = 0; i < tabCount; i++) {
            if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
            } else {
                addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                currentPosition = pager.getCurrentItem();
                tabsContainer.getChildAt(currentPosition).setSelected(true); //Fix : ICON SELECTOR
                scrollToChild(currentPosition, 0);
            }
        });
    }

    public void addTextTab(final int position, String title) {

        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();

        if (textDrawables != null && textDrawables.length > 0) {
            int res = textDrawables[position];
            Drawable drawable = getResources().getDrawable(res);
            switch (drawablePosition) {
                case DRAWABLE_LEFT:
                    tab.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    break;
                case DRAWABLE_RIGHT:
                    tab.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                    break;
                case DRAWABLE_BOTTOM:
                    tab.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
                    break;
                default:
                    tab.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    break;
            }
            tab.setCompoundDrawablePadding(drawablePadding);
        }

        addTab(position, tab);
    }

    public void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);

        addTab(position, tab);

    }

    public void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setIsClicked(true);
                if (selectedPosition == position) {
                    if (mSelectedListener != null) {
                        mSelectedListener.toggle(position);
                    }
                } else {
                    pager.setCurrentItem(position, true);
                }
            }
        });

        if (tabNumInScreen >= tabCount) {
            tab.setPadding(tabPadding, 0, tabPadding, 0);
            tabsContainer.addView(tab, position, expandedTabLayoutParams);
        } else {
            tab.setPadding(tabPadding, 0, tabPadding, 0);
            tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
        }
    }

    public void updateTabStyles() {
        for (int i = 0; i < tabCount; i++) {
            View v = tabsContainer.getChildAt(i);
            if (v instanceof TextView) {

                TextView tab = (TextView) v;

                if (selectedPosition == i) {
                    tab.setTextColor(tabTextColorSelected);
                    tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSizeSelected);
                } else {
                    tab.setTextColor(tabTextColor);
                    tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                }

//                tab.setTextColor(tabTextColor);
                tab.setTypeface(tabTypeface == null ? defaultTabTypeface : tabTypeface, tabTypefaceStyle);

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (textAllCaps) {
                    tab.setAllCaps(true);
                }
            } else if (pager.getAdapter() instanceof NumTabProvider) {//这里回过头来再改
                ViewGroup father = (ViewGroup) v;
                ViewGroup child = (ViewGroup) father.getChildAt(0);//用badge会多套了一层
                TextView textView = (TextView) child.getChildAt(0);

                if (selectedPosition == i) {
                    textView.setTextColor(tabTextColorSelected);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSizeSelected);
                } else {
                    textView.setTextColor(tabTextColor);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                }

//                tab.setTextColor(tabTextColor);
                textView.setTypeface(tabTypeface == null ? defaultTabTypeface : tabTypeface, tabTypefaceStyle);

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (textAllCaps) {
                    textView.setAllCaps(true);
                }
            } else if (v instanceof FrameLayout) {
                FrameLayout tabLayout = (FrameLayout) v;
                for (int j = 0; j < tabLayout.getChildCount(); j++) {
                    View childView = tabLayout.getChildAt(j);
                    if (childView instanceof TextView && childView.getVisibility() == View.VISIBLE) {
                        TextView tab = (TextView) childView;

                        if (selectedPosition == i) {
                            tab.setTextColor(tabTextColorSelected);
                            tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSizeSelected);
                        } else {
                            tab.setTextColor(tabTextColor);
                            tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                        }

                        tab.setTypeface(tabTypeface == null ? defaultTabTypeface : tabTypeface, tabTypefaceStyle);

                        // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                        // pre-ICS-build
                        if (textAllCaps) {
                            tab.setAllCaps(true);
                        }
                    }
                }
            }
        }
    }

    private int textSizeFactory(int size) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, dm);
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        size = a.getDimensionPixelSize(0, size);
        a.recycle();

        return size;
    }

    public void scrollToChild(int position, int offset) {
        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (overScroll && tabsContainer != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mFirstMotionX = event.getX();
                    mFirstMotionY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    this.pullEvent(event);
                    if (isNeedAnimation()) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    dirty.setEmpty();
                    break;
                case MotionEvent.ACTION_UP:
                    if (isNeedAnimation()) {
                        backAnimation();
                    }
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void pullEvent(MotionEvent event) {
        float curMotionX = event.getX();
        int curScrollValue;
        curScrollValue = mLastMotionX == 0 ? 0 : (int) (mLastMotionX - curMotionX);
        mLastMotionX = event.getX();
        setTabScroll(curScrollValue);
    }

    private void setTabScroll(int curScrollValue) {
        // 初始化头部矩形
        if (isNeedMove(curScrollValue)) {
            if (dirty.isEmpty()) {
                // 保存正常的布局位置
                dirty.set(this.getLeft(), this.getTop(),
                        this.getRight(), this.getBottom());
            }
            this.layout(this.getLeft() - curScrollValue / 2, this.getTop(), this.getRight() - curScrollValue / 2, this.getBottom());
        } else {
            dirty.setEmpty();
        }
    }

    // 是否需要开启动画
    public boolean isNeedAnimation() {
        return !dirty.isEmpty();
    }

    public void backAnimation() {
        TranslateAnimation animation = new TranslateAnimation(this.getLeft(), dirty.left, 0, 0);
        animation.setDuration(200);
        this.startAnimation(animation);

        this.layout(0, dirty.top, this.getWidth(), dirty.bottom);
        mLastMotionX = 0;
        dirty.setEmpty();
    }


    public boolean isNeedMove(int dir) {
        int offsetX = tabsContainer.getMeasuredWidth() - getWidth();
        int scrollX = getScrollX();

        //view的内容在最左边，且向右拖拽
        if (scrollX == 0 && (this.getLeft() - dir / 2) > 0) {
            return true;
        }
        //view的内容在最右边，且向左拖拽
        if (scrollX == offsetX && (this.getRight() - dir / 2) < this.getWidth()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }
        final int height = getHeight();
        // draw indicator line

        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();
        // 计算 indicator 的左右padding
        if (pager.getAdapter() instanceof NumTabProvider) {
            ViewGroup father = (ViewGroup) currentTab;
            ViewGroup child = (ViewGroup) father.getChildAt(0);//用badge会多套了一层
            TextView textView = (TextView) child.getChildAt(0);
            calIndicatorPadding(currentTab, textView, false);
        } else if (currentTab instanceof TextView) {
            calIndicatorPadding(currentTab, (TextView) currentTab, false);
        } else if (currentTab instanceof ViewGroup) {
            calIndicatorPadding(false);
        }
        lineLeft += mIndicatorPaddingLeftRight;
        lineRight -= mIndicatorPaddingLeftRight;


        if (isCanBigger) {
            //draw text
            if (currentTab instanceof TextView) {
                String currentTitle = ((TextView) currentTab).getText().toString().trim();

                textDefaultPaint.setTextSize(tabTextSize);
                if (currentPositionOffset == 0) {
                    textPaint.setColor(tabTextColorSelected);
                } else {
                    textPaint.setColor(tabTextColor);
                }
                textPaint.setTextSize(tabTextSize + tabTextSizeDiff * (1 - currentPositionOffset));
                textPaint.getTextBounds(currentTitle, 0, currentTitle.length(), mCurBound);
                textDefaultPaint.getTextBounds(currentTitle, 0, currentTitle.length(), defaultBound);
                canvas.drawText(currentTitle, (currentTab.getWidth() - mCurBound.width()) / 2 + currentTab.getLeft(), height / 2 + (int) (defaultBound.height() / 2.3), textPaint);
            }
            for (int i = 0; i < tabsContainer.getChildCount(); i++) {
                if (i == currentPosition && currentTab instanceof TextView) {
                    tabsContainer.getChildAt(currentPosition).setVisibility(INVISIBLE);
                } else {
                    tabsContainer.getChildAt(i).setVisibility(VISIBLE);
                }
            }
        }

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            float nextTabLeft = nextTab.getLeft();
            float nextTabRight = nextTab.getRight();

            String nextTitle = null;
            if (pager.getAdapter() instanceof NumTabProvider) {
                ViewGroup father = (ViewGroup) nextTab;
                ViewGroup child = (ViewGroup) father.getChildAt(0);//用badge会多套了一层
                TextView textView = (TextView) child.getChildAt(0);
                nextTitle = textView.getText().toString().trim();
                calIndicatorPadding(nextTab, textView, true);
            } else if (nextTab instanceof TextView) {
                nextTitle = ((TextView) nextTab).getText().toString().trim();
                calIndicatorPadding(nextTab, (TextView) nextTab, true);
            } else if (currentTab instanceof ViewGroup) {
                calIndicatorPadding(true);
            }
            nextTabLeft += mNextIndicatorPaddingLeftRight;
            nextTabRight -= mNextIndicatorPaddingLeftRight;

            //indicator弹性效果
            if (indicatorSmoothOpen) {
                float offset = (float) (1.0 / (currentPositionOffset * currentPositionOffset));
                lineLeft = (currentPositionOffset / offset * nextTabLeft + (1f - currentPositionOffset / offset) * lineLeft);
                lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
            } else {
                lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
                lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
            }

            /**
             * 字体颜色渐变
             * 只有滑动的时候才会有颜色渐变过程,点击tab 不渐变(渐变会有问题)
             */
            if (colorGradualOpen && !mIsClicked) {
                int redSelected = (tabTextColorSelected & 0x00FF0000) >> 16;
                int greenSelected = (tabTextColorSelected & 0x0000FF00) >> 8;
                int blueSelected = (tabTextColorSelected & 0x000000FF) >> 0;

                int red = (tabTextColor & 0x00FF0000) >> 16;
                int green = (tabTextColor & 0x0000FF00) >> 8;
                int blue = (tabTextColor & 0x000000FF) >> 0;

                int redOffset = (int) ((redSelected - red) * currentPositionOffset);
                int greenOffset = (int) ((greenSelected - green) * currentPositionOffset);
                int blueOffset = (int) ((blueSelected - blue) * currentPositionOffset);

                if (nextTab instanceof TextView) {
                    ((TextView) nextTab).setTextColor(Color.rgb(red + redOffset, green + greenOffset, blue + blueOffset));
                }
                if (currentTab instanceof TextView) {
                    ((TextView) currentTab).setTextColor(Color.rgb(redSelected - redOffset, greenSelected - greenOffset, blueSelected - blueOffset));
                }
            }

            //选中的字母变大
            if (isCanBigger) {
                if (!TextUtils.isEmpty(nextTitle)) {
                    textPaint.setTextSize(tabTextSize + tabTextSizeDiff * currentPositionOffset);
                    textPaint.setColor(tabTextColor);
                    textPaint.getTextBounds(nextTitle, 0, nextTitle.length(), mCurBound);
                    canvas.drawText(nextTitle, (nextTab.getWidth() - mCurBound.width()) / 2 + nextTabLeft, height / 2 + (int) (defaultBound.height() / 2.3), textPaint);
                    tabsContainer.getChildAt(currentPosition + 1).setVisibility(INVISIBLE);
                }
            }
        }

        if (indicatorWidth != -1) {
            indicatorPadding = (int) ((getWidth() / (float) tabCount - indicatorWidth) / 2.0f);
            indicatorWidth = -1;
        }

        // draw indicator
        mRectF.set(lineLeft, height - mIndicatorTextPadding - indicatorHeight,
                lineRight, height - mIndicatorTextPadding);
        canvas.drawRoundRect(mRectF, indicatorHeight / 2, indicatorHeight / 2, rectPaint);
        // draw underline
        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

        // draw divider
        dividerPaint.setColor(dividerColor);
        for (int i = 0; i < tabCount - 1; i++) {
            View tab = tabsContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
        }
    }

    /**
     * 计算tab的padding
     *
     * @param tab       tab
     * @param textView  textview
     * @param isNextTab 下一个还是当前
     */
    private void calIndicatorPadding(View tab, TextView textView, boolean isNextTab) {
        float tabWidth;
        float tabTextWidth;
        tabWidth = tab.getWidth();
        String title = textView.getText().toString().trim();
        tabTextWidth = textView.getPaint().measureText(title);
        if (isNextTab) {
            mNextIndicatorPaddingLeftRight = (tabWidth - tabTextWidth) / 2.0f
                    + mIndicatorTextPadding;
        } else {
            mIndicatorPaddingLeftRight = (tabWidth - tabTextWidth) / 2.0f
                    + mIndicatorTextPadding;
        }
    }

    /**
     * 计算tab为ViewGroup时的padding
     *
     * @param isNextTab 下一个还是当前
     */
    private void calIndicatorPadding(boolean isNextTab) {
        if (isNextTab) {
            mNextIndicatorPaddingLeftRight = tabPadding + mIndicatorTextPadding;
        } else {
            mIndicatorPaddingLeftRight = tabPadding + mIndicatorTextPadding;
        }
    }

    private class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            currentPosition = position;
            currentPositionOffset = positionOffset;

            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
                //选中tab后 把点击的标注取消
                setIsClicked(false);
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(final int position) {
            tabsContainer.getChildAt(currentPageSelected).setSelected(false);
            currentPageSelected = position;
            tabsContainer.getChildAt(position).setSelected(true);

            selectedPosition = position;
            updateTabStyles();

            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }

    }

    public void setIndicatorPadding(int indicatorWidth) {
        this.indicatorWidth = indicatorWidth;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setOverScroll(boolean overScroll) {
        this.overScroll = overScroll;
    }

    public void setTextSize(int textSize) {
        tabTextSize = textSizeFactory(textSize);
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTabTextColorSelected(int textColorSelected) {
        this.tabTextColorSelected = textColorSelected;
        updateTabStyles();
    }

    public void setTabTextSizeSelected(int textSizeSelected) {
        tabTextSizeSelected = textSizeFactory(textSizeSelected);
        updateTabStyles();
    }

    public int getTabTextSizeSelected() {
        return tabTextSizeSelected;
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public int getTabTextColorSelected() {
        return tabTextColorSelected;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public boolean isColorGradualOpen() {
        return colorGradualOpen;
    }

    public void setColorGradualOpen(boolean colorGradualOpen) {
        this.colorGradualOpen = colorGradualOpen;
    }

    public boolean isIndicatorSmoothOpen() {
        return indicatorSmoothOpen;
    }

    public void setIndicatorSmoothOpen(boolean indicatorSmoothOpen) {
        this.indicatorSmoothOpen = indicatorSmoothOpen;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    private int[] textDrawables;

    public void setTextDrawables(int[] drawables) {
        this.textDrawables = drawables;
    }

    public interface IconTabProvider {
        int getPageIconResId(int position);
    }

    public interface NumTabProvider { //文字旁边带数字提醒

        int getPageNumCount(int position);

        boolean isShowDot(int position); //是否要显示一个红点
    }

    public interface OnSelectedListener {
        void toggle(int position);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}