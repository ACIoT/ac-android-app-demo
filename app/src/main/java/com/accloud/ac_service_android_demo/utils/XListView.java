/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.accloud.ac_service_android_demo.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.cloudservice.AC;
import com.accloud.utils.PreferencesUtils;

public class XListView extends ListView implements OnScrollListener {

    private float mLastY = -1; // save event y
    private Scroller mScroller; // used for scroll back
    private OnScrollListener mScrollListener; // user's scroll listener

    // the interface to trigger refresh and load more.
    private IXListViewListener mListViewListener;

    // -- header view
    private XListViewHeader mHeaderView;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private RelativeLayout mHeaderViewContent;
    private TextView mHeaderTimeView;
    private int mHeaderViewHeight; // header view's height
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false; // is refreashing.

    // total list items, used to detect is at the bottom of listview.
    private int mTotalItemCount;

    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;

    private final static int SCROLL_DURATION = 400; // scroll back duration
    // at bottom, trigger
    // load more.
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
    // feature.

    private long lastRefreshTime = 0;
    private static final String KEY_REFRESH_TIME = "refreshtime";
    private boolean hasStart = false;

    /**
     * @param context
     */
    public XListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public XListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public XListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    private void initWithContext(final Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        super.setOnScrollListener(this);

        mHeaderView = new XListViewHeader(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView
                .findViewById(R.id.xlistview_header_content);
        mHeaderTimeView = (TextView) mHeaderView
                .findViewById(R.id.xlistview_header_time);
        addHeaderView(mHeaderView);

        // init header height
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mHeaderViewHeight = context
                                .getResources()
                                .getDimensionPixelSize(R.dimen.list_head_height);
                        // mHeaderViewHeight = mHeaderView.getHeight();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            mHeaderViewContent.setVisibility(View.INVISIBLE);
            resetHeaderHeight();
        }

    }

    private void updateRefreshTime() {
        String str = null;
        if (!hasStart) {
            lastRefreshTime = PreferencesUtils.getLong(AC.context, KEY_REFRESH_TIME, 0L);
            hasStart = true;
        }
        if (lastRefreshTime == 0) {
            str = getContext().getString(R.string.xlistview_nerver_have_a_refresh);
        } else {
            int time = (int) ((System.currentTimeMillis() - lastRefreshTime) / 1000);
            int second = time % 60;
            int min = time / 60 % 60;
            if (min > 1) {
                str = getContext().getString(R.string.xlistview_refreshtime_desc1, min, second);
                int hour = time / (60 * 60) % 24;
                if (hour > 1) {
                    str = getContext().getString(R.string.xlistview_refreshtime_desc2, hour, str);
                    int day = time / (60 * 60 * 24);
                    if (day > 1) {
                        str = getContext().getString(R.string.xlistview_refreshtime_desc3, day, str);
                    }
                }
            } else {
                str = getContext().getString(R.string.xlistview_just_a_minute);
            }
        }
        mHeaderTimeView.setText(str);
        saveRefreshTime();
    }

    private void saveRefreshTime() {
        if (hasStart == true) {
            PreferencesUtils.putLong(AC.context, KEY_REFRESH_TIME, System.currentTimeMillis());
            hasStart = false;
        }
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta
                + mHeaderView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(XListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(XListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
        // if (height == 120) // not visible.
        // return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss
        // header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();

                int[] location = new int[2];
                mHeaderView.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标

                int[] curLocation = new int[2];
                this.getLocationInWindow(curLocation);

                int[] loc = new int[2];
                getLocationInWindow(loc);
                if (mEnablePullRefresh && getFirstVisiblePosition() == 0
                        && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0
                        && location[1] == curLocation[1])) {
                    // the first item is showing, header has shown or pull down.
                    if (mHeaderViewContent.getVisibility() != View.VISIBLE) {
                        mHeaderViewContent.setVisibility(View.VISIBLE);
                    }
                    updateRefreshTime();
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();
                }
                break;
            default:
                mLastY = -1; // reset
                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (mEnablePullRefresh
                            && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                        mPullRefreshing = true;
                        mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                        }
                    } else {
                        if (!mPullRefreshing) {
                            mHeaderViewContent.setVisibility(View.INVISIBLE);
                        }
                    }
                    resetHeaderHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

//    public void toRefresh() {
//        updateRefreshTime();
//        setSelection(0);
//        mHeaderView.setVisiableHeight(mHeaderViewHeight);
//
//        if (mHeaderViewContent.getVisibility() != View.VISIBLE) {
//            mHeaderViewContent.setVisibility(View.VISIBLE);
//        }
//
//        mPullRefreshing = true;
//        mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
//        if (mListViewListener != null) {
//            mListViewListener.onRefresh();
//        }
//    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    public void setXListViewListener(IXListViewListener l) {
        mListViewListener = l;
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener {
        void onXScrolling(View view);
    }

    /**
     * implements this interface to get refresh/load more event.
     */
    public interface IXListViewListener {
        void onRefresh();
    }
}
