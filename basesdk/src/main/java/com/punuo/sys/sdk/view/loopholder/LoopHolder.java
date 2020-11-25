package com.punuo.sys.sdk.view.loopholder;

import android.content.Context;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.punuo.sys.sdk.R;
import com.punuo.sys.sdk.model.LoopModel;
import com.punuo.sys.sdk.util.BaseHandler;

import java.util.ArrayList;
import java.util.List;

public class LoopHolder implements BaseHandler.MessageHandler {

    private boolean isStart = true;
    //是否在可见视图内
    private boolean isInLayout = true;

    private boolean isDestroy = false;
    private static final int HOME_LOOP_PAGE_ADS_MESSAGE = 0x0001;
    public static final int HOME_LOOP_PAGE_ADS_INTERVAL = 3000;

    MyAdapter mAdapter;

    View rootView;

    public View mViewRoot;

    ViewPager mViewPager;

    LoopIndicator mLoopIndicator;

    public FrameLayout mLoopContainer;

    int paddingLeft;
    int paddingTop;
    int paddingRight;
    int paddingBottom;

    List<ICallback> mICallback;

    public static LoopHolder newInstance(Context context, ViewGroup group) {
        return new LoopHolder(context,
                LayoutInflater.from(context).inflate(
                        R.layout.sdk_recycle_item_loopmodule,
                        group,
                        false)
        );
    }

    public static LoopHolder insertInstance(Context context, ViewGroup group) {
        return new LoopHolder(context,
                LayoutInflater.from(context).inflate(
                        R.layout.sdk_recycle_item_loopmodule,
                        group));
    }

    public LoopHolder(Context context, View itemView) {
        mLoopContainer = itemView.findViewById(R.id.loop_container);
        mLoopIndicator = itemView.findViewById(R.id.loop_indicator);
        mViewPager = itemView.findViewById(R.id.viewpager);
        mViewRoot = itemView.findViewById(R.id.fl_loop_root);
        rootView = itemView.findViewById(R.id.rafl);
        mAdapter = new MyAdapter(context);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mICallback = new ArrayList<>();
        mICallback.add(mLoopIndicator);

        paddingLeft = mLoopContainer.getPaddingLeft();
        paddingTop = mLoopContainer.getPaddingTop();
        paddingRight = mLoopContainer.getPaddingRight();
        paddingBottom = mLoopContainer.getPaddingBottom();
    }

    public void bindData(LoopModel loopModel) {
        mAdapter.reset(loopModel.mList);
        if (loopModel.mList.size() == 1) {
            mLoopIndicator.setVisibility(View.GONE);
            mICallback = null;
        } else {
            mLoopIndicator.setVisibility(View.VISIBLE);
            int pos = mViewPager.getCurrentItem();
            mLoopIndicator.setData(loopModel.mList.size());

            int realPos = pos % mAdapter.getRealSize();
            callback(realPos);
        }
        mAdapter.notifyDataSetChanged();

        int realPos = mViewPager.getCurrentItem() % mAdapter.getRealSize();
        if (realPos >= loopModel.mList.size()) {
            mViewPager.setCurrentItem(0);
        }

        // start loop
        isDestroy = false;
        mAdsChangeHandler.sendEmptyMessageDelayed(HOME_LOOP_PAGE_ADS_MESSAGE, HOME_LOOP_PAGE_ADS_INTERVAL);
    }

    private void callback(int realpos) {
        if (null == mICallback || mICallback.size() == 0) {
            return;
        }
        for (ICallback callback : mICallback) {
            callback.select(realpos);
        }
    }

    public void start() {
        isStart = true;
    }

    public void stop() {
        isStart = false;
    }

    public void setInLayout(boolean inLayout) {
        this.isInLayout = inLayout;
    }

    public void destroyLoop() {
        mAdsChangeHandler.removeCallbacksAndMessages(null);
    }

    public void destroy() {
        destroyLoop();
        isDestroy = true;
    }

    /**
     * 翻转ads的handler
     */
    private BaseHandler mAdsChangeHandler = new BaseHandler(this);

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mAdsChangeHandler.removeMessages(HOME_LOOP_PAGE_ADS_MESSAGE);
            mAdsChangeHandler.sendEmptyMessageDelayed(HOME_LOOP_PAGE_ADS_MESSAGE, HOME_LOOP_PAGE_ADS_INTERVAL);
            try {
                if (!isDestroy && isStart && isInLayout) {
                    int realPos = position % mAdapter.getRealSize();
                    callback(realPos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HOME_LOOP_PAGE_ADS_MESSAGE:
                if (!isDestroy && isStart && isInLayout) {
                    int nextPos = mViewPager.getCurrentItem() + 1;
                    mViewPager.setCurrentItem(nextPos);
                }

                try {
                    mAdsChangeHandler.removeMessages(HOME_LOOP_PAGE_ADS_MESSAGE);
                    mAdsChangeHandler.sendEmptyMessageDelayed(HOME_LOOP_PAGE_ADS_MESSAGE, HOME_LOOP_PAGE_ADS_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

    }

    private static class MyAdapter extends PagerAdapter {
        protected Context context;
        protected List<String> mAds;
        private int mInvalidChildCount = 0;

        public MyAdapter(Context context) {
            super();
            this.context = context;
            mAds = new ArrayList<>();
        }

        public void clear() {
            mAds.clear();
        }

        public void reset(List<String> adsList) {
            mAds.clear();
            mAds.addAll(adsList);
        }

        public List<String> getList() {
            return this.mAds;
        }

        public int getRealSize() {
            return this.mAds.size();
        }

        @Override
        public int getCount() {
            //当只有ads的size为1的时候,只展示,不轮播
            if (mAds == null || mAds.isEmpty()) {
                return 0;
            }
            return mAds.size() == 1 ? 1 : Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            // 强制在notifyDatasetChange时重绘所有子节点
            if (mInvalidChildCount > 0) {
                --mInvalidChildCount;
                return POSITION_NONE;
            }

            return super.getItemPosition(object);
        }

        @Override
        public void notifyDataSetChanged() {
            mInvalidChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.sdk_loopmodule_loop_item, container, false);

            ImageView ivFake = view.findViewById(R.id.ms_home_loop_item_iv_fake);
            ImageView iv = view.findViewById(R.id.ms_home_loop_item_iv);

            final int realposition = position % mAds.size();
            String image = mAds.get(realposition);
            if (!TextUtils.isEmpty(image)) {
                if (!image.startsWith("http")) {
                    image += "http://feeder.qinqingonline.com:8080/";
                }
                Glide.with(context).load(image).into(ivFake);
            }
            container.addView(view);
            return view;
        }
    }

    public interface ICallback {
        void select(int pos);
    }

}
