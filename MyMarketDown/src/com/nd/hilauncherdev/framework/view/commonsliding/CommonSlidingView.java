package com.nd.hilauncherdev.framework.view.commonsliding;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Scroller;
import android.widget.TextView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.drawer.view.DrawerLayout;
//import com.nd.hilauncherdev.framework.effect.DrawerEffects;
//import com.nd.hilauncherdev.framework.effect.EffectsType;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.nd.hilauncherdev.kitset.GpuControler;
//import com.nd.hilauncherdev.settings.SettingsPreference;

/**
 * 类似GridView布局的横向布局视图
 * 
 * @author Anson
 */
public abstract class CommonSlidingView extends ViewGroup implements OnClickListener, OnLongClickListener {

	protected static final String TAG = "CommonSlidingView";

	protected static final int INVALID_SCREEN = -999;

	private static final float BASELINE_FLING_VELOCITY = 2500.f;

	private static final float FLING_VELOCITY_INFLUENCE = 0.4f;

	/**
	 * Fling灵敏度
	 */
	public static final int SNAP_VELOCITY = 500;

	/**
	 * 滑动边界
	 */
	protected int scrollingBoundary = 0;

	private static final int TOUCH_STATE_REST = 0;

	private static final int TOUCH_STATE_DOWN = 1;

	private static final int TOUCH_STATE_SCROLLING = 2;

	private static final int TOUCH_STATE_DONE_WAITING = 3;

	private final static int TOUCH_STATE_FLING_DOWN = 4;

	private final static int TOUCH_STATE_FLING_UP = 5;

	private int mTouchState = TOUCH_STATE_REST;

	/**
	 * mTouchState是否不可再更改标记位，在判定fling up/down时使用
	 */
	private boolean isTouchStateLocked = false;

	private int mTouchSlop;

	private int mMaximumVelocity;

	private float mLastMotionX;

	private float mLastMotionY;

	private float mLastDownY;

	private VelocityTracker mVelocityTracker;

	protected Scroller mScroller;

	protected ViewGroup.LayoutParams holderParams;

	/**
	 * 当前页
	 */
	private int mCurrentScreen = 0;

	/**
	 * 当前页仅供特效使用
	 */
	private int mEffectCurrentScreen = mCurrentScreen;

	/**
	 * 当前数据集
	 */
	protected ICommonData mCurrentData;

	/**
	 * 目标页 - 标记位作用 e.g. 若当前有3页，下标为0 - 2，循环滚动时，mNextScreen的范围是-1 - 3
	 */
	protected int mNextScreen = INVALID_SCREEN;

	/**
	 * 页宽
	 */
	protected int pageWidth;

	/**
	 * 页高
	 */
	protected int pageHeight;

	/**
	 * 是否锁定布局，若锁定，则在onLayout中不会调用layoutChildren()
	 */
	protected boolean isLockLayout = false;

	/**
	 * 重新布局指定数据集标记位
	 */
	private boolean isReLayoutSpecifiedData = false;

	/**
	 * reLayout之前数据集
	 */
	private ICommonData mOriginalData;

	/**
	 * 屏幕指示灯
	 */
	protected CommonLightbar lightbar;

	/**
	 * 数据集屏幕指示灯
	 */
	protected CommonLightbar splitLightbar;

	/**
	 * 4.0桌面的屏幕指示灯
	 */
	protected LineLightbar lineLightbar;

	/**
	 * 从startPage页开始布局
	 */
	protected int startPage = 0;

	/**
	 * 开始滑动时当前数据集是否处于锁定状态
	 */
	private boolean isTempLock = false;

	/**
	 * 数据集
	 */
	protected List<ICommonData> list;

	/**
	 * CommonLayout缓存
	 */
	public List<CommonLayout> pageViews = new ArrayList<CommonLayout>();

	/**
	 * 循环滚动
	 */
	private boolean isEndlessScrolling = true;

	private boolean isEndlessScrollingBackup = isEndlessScrolling;

	/**
	 * 数据集锁定时是否在数据集内循环滚动
	 */
	private boolean isEndlessScrollingIfDataLock = true;

	private boolean isEndlessScrollingIfDataLockBackup = isEndlessScrollingIfDataLock;

	/**
	 * 切换数据集监听器
	 */
	private OnSwitchDataListener onSwitchDataListener;

	/**
	 * 滑屏监听器
	 */
	private OnSnapToScreenListener onSnapToScreenListener;

	/**
	 * 单击事件监听器
	 */
	private OnCommonSlidingViewClickListener onItemClickListener;

	/**
	 * 长按事件监听器
	 */
	private OnCommonSlidingViewLongClickListener onItemLongClickListener;

	/**
	 * Fling事件监听器
	 */
	private OnFlingListener onFlingListener;

	/**
	 * 手指在y轴的移动距离
	 */
	private int fingerOffsetY;

	/**
	 * 特效是否开启 默认false
	 */
	public boolean isEffectOpened = false;

	protected Handler handler = new Handler();

	public interface OnSwitchDataListener {

		public void onSwitchData(List<ICommonData> list, int fromPosition, int toPosition);

	}

	public interface OnSnapToScreenListener {
		/**
		 * 滑屏监听器，若同时发生切换数据集事件，则先执行{@link OnSwitchDataListener}
		 */
		public void onSnapToScreen(List<ICommonData> list, int fromScreen, int toScreen);

	}

	public interface OnCommonSlidingViewClickListener {

		public void onItemClick(View v, int positionInData, int positionInScreen, int screen, ICommonData data);

	}

	public interface OnCommonSlidingViewLongClickListener {

		public boolean onItemLongClick(View v, int positionInData, int positionInScreen, int screen, ICommonData data);

	}

	public interface OnFlingListener {

		public void onFlingUp();

		public void onFlingDown(int y);

	}

	public CommonSlidingView(Context context) {
		super(context);
		initWorkspace(context);
	}

	public CommonSlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWorkspace(context);
	}

	public CommonSlidingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWorkspace(context);
	}

	public void initWorkspace(Context context) {
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		/**
		 * configuration.getScaledTouchSlop() == 24, 提高滚动灵敏度
		 */
		mTouchSlop = configuration.getScaledTouchSlop();

		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

		mScroller = new Scroller(getContext());

		holderParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		initSelf(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		// int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		//
		// setMeasuredDimension(widthSize, heightSize);
		// pageWidth = widthSize;
		// pageHeight = heightSize;

		pageWidth = this.getMeasuredWidth();
		pageHeight = this.getMeasuredHeight();

		scrollingBoundary = pageWidth / 3;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!isLockLayout) {
			isLockLayout = true;
			layoutChildren();
			onLayoutChildrenAfter();
		}
		if (lightbar != null || splitLightbar != null || lineLightbar != null) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (lightbar != null) {
						lightbar.refresh(getPageCount(), mCurrentScreen);
					}
					if (splitLightbar != null && mCurrentData != null) {
						splitLightbar.refresh(mCurrentData.getPageNum(), mCurrentScreen - getDataPageInfo(mCurrentData)[0]);
					}
					if (lineLightbar != null) {
						lineLightbar.refresh(CommonSlidingView.this, mCurrentScreen * pageWidth);
					}
				}
			}, 100);
		}
		if (mNextScreen == INVALID_SCREEN) {
			snapToScreen(mCurrentScreen);
		}
		invalidate();
	}

	protected void layoutChildren() {

		if (list != null && list.size() > 0) {
			/**
			 * 布局起始数据集
			 */
			ICommonData data = getData(startPage);
			mCurrentData = data;
			int[] pageInfo = getDataPageInfo(data);
			for (int i = startPage; i < pageInfo[1]; i++)
				makePage(i, pageInfo, data);

			if (!isReLayoutSpecifiedData) {
				/**
				 * 布局后续数据集
				 */
				int index = list.indexOf(data);
				for (int i = index + 1; i < list.size(); i++) {
					data = list.get(i);
					pageInfo = getDataPageInfo(data);
					for (int j = pageInfo[0]; j < pageInfo[1]; j++)
						makePage(j, pageInfo, data);
				}
			} else {
				isReLayoutSpecifiedData = false;
			}
		}

		/**
		 * 移除多余的Layout
		 */
		for (int i = pageViews.size() - 1; i > getPageCount() - 1; i--) {
			removeLayout(i);
		}
	}

	/**
	 * 
	 * @param pageNum
	 *            - 当前页数
	 * @param pageInfo
	 *            - 数据集起始页及结束页信息
	 * @param data
	 *            - 数据集
	 */
	private void makePage(int pageNum, int[] pageInfo, ICommonData data) {

		if (pageNum < pageInfo[0] || pageNum > pageInfo[1] - 1) {
			return;
		}

		/**
		 * 实际图标区宽度
		 */
		final int actualWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		/**
		 * 实际图标区高度
		 */
		final int actualHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

		/**
		 * 当前图标左边界位置
		 */
		int x = getPaddingLeft();

		/**
		 * 当前图标上边界位置
		 */
		int y = getPaddingTop();

		int columnNum = data.getColumnNum() > 0 ? data.getColumnNum() : 1;
		int columnWidth = actualWidth / columnNum;
		int childViewWidth = data.getChildViewWidth() > 0 ? data.getChildViewWidth() : 0;
		columnWidth = data.isKeepChildViewWidthAndHeight() ? childViewWidth : columnWidth > childViewWidth ? childViewWidth : columnWidth;

		int rowNum = data.getRowNum() > 0 ? data.getRowNum() : 1;
		int rowHeight = actualHeight / rowNum;
		int childViewHeight = data.getChildViewHeight() > 0 ? data.getChildViewHeight() : 0;
		rowHeight = data.isKeepChildViewWidthAndHeight() ? childViewHeight : rowHeight > childViewHeight ? childViewHeight : rowHeight;

		CommonLayout layout = getLayout(pageNum);
		ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		int childHeightSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(rowHeight, MeasureSpec.EXACTLY), 0, p.height);
		int childWidthSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(columnWidth, MeasureSpec.EXACTLY), 0, p.width);

		int rows = data.getRowNum();
		int columns = data.getColumnNum();
		int position = rows * columns * (pageNum - pageInfo[0]);

		int columnPadding = 0;
		int rowPadding = 0;
		if (data.isKeepChildViewWidthAndHeight() && (actualWidth / columnNum) < childViewWidth && data.getColumnNum() > 1) {
			columnPadding = (actualWidth - data.getColumnNum() * columnWidth) / (data.getColumnNum() - 1);
		} else {
			columnPadding = (actualWidth - data.getColumnNum() * columnWidth) / (data.getColumnNum() + 1);
		}
		if (data.isKeepChildViewWidthAndHeight() && (actualHeight / rowNum) < childViewHeight && data.getRowNum() > 1) {
			rowPadding = (actualHeight - data.getRowNum() * rowHeight) / (data.getRowNum() - 1);
		} else {
			rowPadding = (actualHeight - data.getRowNum() * rowHeight) / (data.getRowNum() + 1);
		}

		data.setRowPadding(rowPadding);
		data.setColumnPadding(columnPadding);
		
		data.setActualChildViewHeight(rowHeight);
		data.setActualChildViewWidth(columnWidth);
		
		for (int i = 0; i < rows; i++) {
			if (rowPadding >= 0 || (rowPadding < 0 && i > 0)) {
				y += rowPadding;
			}
			for (int j = 0; j < columns; j++, position++) {
				if (position >= data.getDataList().size()) {
					break;
				}
				View child = onGetItemView(data, position);
				if (child == null)
					child = new TextView(getContext());
				child.setLayoutParams(p);
				child.measure(childWidthSpec, childHeightSpec);

				CommonViewHolder viewHolder = new CommonViewHolder();
				viewHolder.positionInData = position;
				viewHolder.positionInScreen = position - rows * columns * (pageNum - pageInfo[0]);
				viewHolder.screen = pageNum;
				viewHolder.item = data.getDataList().get(position);
				viewHolder.item.setPosition(position);
				child.setTag(R.id.common_view_holder, viewHolder);

				child.setOnClickListener(this);
				child.setOnLongClickListener(this);
				child.setHapticFeedbackEnabled(false);

				if (columnPadding >= 0 || (columnPadding < 0 && j > 0)) {
					x += columnPadding;
				}
				int left = x;
				int top = y;
				int w = columnWidth;
				int h = rowHeight;
				child.layout(left, top, left + w, top + h);

				layout.addViewInLayout(child, layout.getChildCount(), null, true);
				x += columnWidth;
			}
			x = getPaddingLeft();
			y += rowHeight;
		}
	}

	private CommonLayout getLayout(int pageNum) {
		if (pageNum < pageViews.size()) {
			CommonLayout holder = pageViews.get(pageNum);
			/**
			 * 在使用GPU 2D硬件加速情况下, removeAllViewsInLayout()方法可能产生问题
			 */
			
			holder.removeAllViews();
			holder.layout(holder.getLeft(), holder.getTop(), holder.getRight(), getMeasuredHeight());
			return holder;
		}

		CommonLayout holder = getNewLayout();
		final int pageSpacing = pageNum * pageWidth;
		final int pageWidth = getMeasuredWidth();
		holder.layout(pageSpacing, 0, pageSpacing + pageWidth, getMeasuredHeight());
		holder.setTag(pageNum);
		addViewInLayout(holder, getChildCount(), holderParams, true);
		pageViews.add(holder);
		return holder;
	}

	/**
	 * 判定滚屏或是fling up/down事件
	 */
	protected void dealTouchStateInActionMove(float x, float y) {
		/**
		 * 是否监听Fling事件
		 */
		boolean isListenFling = onFlingListener != null ? true : false;

		final int xDiff = (int) Math.abs(mLastMotionX - x);
		final int yDiff = (int) Math.abs(mLastMotionY - y);

		if (!isTouchStateLocked) {
			if (!isListenFling) {
				if (xDiff > mTouchSlop && mTouchState != TOUCH_STATE_DONE_WAITING) {
					mTouchState = TOUCH_STATE_SCROLLING;
					isTouchStateLocked = true;
				}
			} else {
				if (xDiff > mTouchSlop && mTouchState != TOUCH_STATE_DONE_WAITING) {
					mTouchState = TOUCH_STATE_SCROLLING;
					isTouchStateLocked = true;
				} else {
					if ((y - mLastMotionY) > 0) {
						if (yDiff > (mTouchSlop * 2)) {
							mTouchState = TOUCH_STATE_FLING_DOWN;
							isTouchStateLocked = true;
						}
					} else {
						if (yDiff > (mTouchSlop * 2)) {
							mTouchState = TOUCH_STATE_FLING_UP;
							isTouchStateLocked = true;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			/**
			 * ACTION_DOWN在一个子view上时，判定fling up/down
			 */
			dealTouchStateInActionMove(x, y);
			enableChildrenCache(mCurrentScreen - 1, mCurrentScreen + 1);
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mLastDownY = y;
			if (mScroller.isFinished()) {
				mTouchState = TOUCH_STATE_REST;
				isTouchStateLocked = false;
			} else {
				mTouchState = TOUCH_STATE_SCROLLING;
				isTouchStateLocked = true;
			}
			if (isEffectOpened)
				//EffectsType.setCurrentEffect(SettingsPreference.getInstance().getDrawerScrollEffects());
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			clearChildrenCache();
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		final boolean isDataLock = mCurrentData == null ? false : mCurrentData.isLock();
		final int leftPosition = getLeftPagePosition();
		final int rightPosition = getRightPagePosition();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
				setCurrentScreen(mCurrentScreen);
				scrollTo(mCurrentScreen * pageWidth, getScrollY());

				mNextScreen = INVALID_SCREEN;
				isTempLock = false;
				isTouchStateLocked=false;
				if (lineLightbar != null) {
					lineLightbar.update(getScrollX());
				}
				clearChildrenCache();
			}

			mTouchState = TOUCH_STATE_DOWN;
			mLastMotionX = x;
			mLastMotionY = y;
			mLastDownY = y;
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				enableChildrenCache(mCurrentScreen - 1, mCurrentScreen + 1);
			}
			break;

		case MotionEvent.ACTION_MOVE:
			/**
			 * ACTION_DOWN在空白处时，判定fling up/down
			 */
			dealTouchStateInActionMove(x, y);

			if ((mTouchState != TOUCH_STATE_SCROLLING && mTouchState != TOUCH_STATE_DOWN) || !isTouchStateLocked) {
				break;
			}

			int deltaX = (int) (mLastMotionX - x);

			mLastMotionX = x;
			mLastMotionY = y;
			// 设置球特效y轴的移动距离
			fingerOffsetY = (int) (mLastDownY - y);

			if (deltaX < 0) {
				/**
				 * 向左滑
				 */
				if (isDataLock) {
					/**
					 * 数据集处于锁定状态
					 */
					if (getScrollX() > (isEndlessScrollingIfDataLock ? (leftPosition - 1) * pageWidth : leftPosition * pageWidth - scrollingBoundary)) {
						scrollBy(deltaX, 0);
					}
				} else {
					/**
					 * 数据集处于非锁定状态
					 */
					if (getScrollX() > (isEndlessScrolling ? -pageWidth : -scrollingBoundary)) {
						scrollBy(deltaX, 0);
					}
				}
			} else if (deltaX > 0) {
				/**
				 * 向右滑
				 */
				if (isDataLock) {
					/**
					 * 数据集处于锁定状态
					 */
					final int availableToScroll = rightPosition * pageWidth - getScrollX() + (isEndlessScrollingIfDataLock ? pageWidth : scrollingBoundary);
					if (availableToScroll > 0) {
						scrollBy(deltaX, 0);
					}
				} else {
					/**
					 * 数据集处于非锁定状态
					 */
					final int availableToScroll = (getPageCount() - 1) * pageWidth - getScrollX() + (isEndlessScrolling ? pageWidth : scrollingBoundary);
					if (availableToScroll > 0) {
						scrollBy(deltaX, 0);
					}
				}
			}

			/**
			 * 更新指示灯
			 */
			int moveToScreen = (int) FloatMath.floor((getScrollX() + (pageWidth / 2)) / (float) pageWidth);
			mNextScreen = moveToScreen;
			if (lightbar != null) {
				if (isDataLock) {
					/**
					 * 数据集处于锁定状态
					 */
					if (isEndlessScrollingIfDataLock && moveToScreen > rightPosition) {
						lightbar.update(leftPosition);
					} else if (isEndlessScrollingIfDataLock && moveToScreen < leftPosition) {
						lightbar.update(rightPosition);
					} else {
						lightbar.update(Math.max(leftPosition, Math.min(moveToScreen, rightPosition)));
					}
				} else {
					/**
					 * 数据集处于非锁定状态
					 */
					if (isEndlessScrolling) {
						if (getChildCount() != 0) {
							lightbar.update((getChildCount() + moveToScreen) % getChildCount());
						}
					} else {
						lightbar.update(Math.max(0, Math.min(moveToScreen, getChildCount() - 1)));
					}
				}
			}
			if (splitLightbar != null) {
				if (isDataLock) {
					/**
					 * 数据集处于锁定状态
					 */
					if (isEndlessScrollingIfDataLock && moveToScreen > rightPosition) {
						splitLightbar.update(0);
					} else if (isEndlessScrollingIfDataLock && moveToScreen < leftPosition) {
						splitLightbar.update(rightPosition - getDataPageInfo(mCurrentData)[0]);
					} else {
						splitLightbar.update(Math.max(0, Math.min(moveToScreen - getDataPageInfo(mCurrentData)[0], rightPosition - getDataPageInfo(mCurrentData)[0])));
					}
				} else {
					/**
					 * 数据集处于非锁定状态
					 */
					if (isEndlessScrolling) {
						if (getChildCount() != 0) {
							splitLightbar.update((getChildCount() + moveToScreen) % getChildCount() - getDataPageInfo(mCurrentData)[0]);
						}
					} else {
						splitLightbar.update(Math.max(0, Math.min(moveToScreen - getDataPageInfo(mCurrentData)[0], getDataPageInfo(mCurrentData)[1] - 1)));
					}
				}
			}
			if (lineLightbar != null) {
				lineLightbar.update(getScrollX());
			}

			break;

		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_FLING_DOWN || mTouchState == TOUCH_STATE_FLING_UP) {
				if (mTouchState == TOUCH_STATE_FLING_DOWN && onFlingListener != null) {
					onFlingListener.onFlingDown((int)mLastMotionY);
				} else if (mTouchState == TOUCH_STATE_FLING_UP && onFlingListener != null) {
					onFlingListener.onFlingUp();
				}
				snapToScreen(mCurrentScreen);
			} else {
				mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

				int velocityX = (int) mVelocityTracker.getXVelocity();

				int whichScreen = (int) FloatMath.floor((getScrollX() + (pageWidth / 2)) / (float) pageWidth);
				final float scrolledPos = (float) getScrollX() / pageWidth;

				if (isDataLock) {
					/**
					 * 数据集处于锁定状态
					 */
					if (velocityX > SNAP_VELOCITY && mCurrentScreen > leftPosition + (isEndlessScrollingIfDataLock ? -1 : 0)) {
						final int bound = scrolledPos < whichScreen ? mCurrentScreen - 1 : mCurrentScreen;
						whichScreen = Math.min(whichScreen, bound);
						snapToScreen(whichScreen, velocityX);
					} else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < rightPosition + (isEndlessScrollingIfDataLock ? 1 : 0)) {
						final int bound = scrolledPos > whichScreen ? mCurrentScreen + 1 : mCurrentScreen;
						whichScreen = Math.max(whichScreen, bound);
						snapToScreen(whichScreen, velocityX);
					} else {
						snapToScreen(whichScreen);
					}
				} else {
					/**
					 * 数据集处于非锁定状态
					 */
					if (velocityX > SNAP_VELOCITY && mCurrentScreen > (isEndlessScrolling ? -1 : 0)) {
						final int bound = scrolledPos < whichScreen ? mCurrentScreen - 1 : mCurrentScreen;
						whichScreen = Math.min(whichScreen, bound);
						snapToScreen(whichScreen, velocityX);
					} else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < getChildCount() - (isEndlessScrolling ? 0 : 1)) {
						final int bound = scrolledPos > whichScreen ? mCurrentScreen + 1 : mCurrentScreen;
						whichScreen = Math.max(whichScreen, bound);
						snapToScreen(whichScreen, velocityX);
					} else {
						snapToScreen(whichScreen);
					}
				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}
			mTouchState = TOUCH_STATE_REST;
			/*
			if(EffectsType.getCurrentEffect()>=EffectsType.GLOBAL&&
			   EffectsType.getCurrentEffect()<=EffectsType.TORNADO){
				invalidate();
			}
			*/
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			snapToScreen(mCurrentScreen);
			break;
		}

		return true;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			if (lineLightbar != null) {
				lineLightbar.update(mScroller.getCurrX());
			}
			invalidate();
		} else if (mNextScreen != INVALID_SCREEN) {
			final boolean isDataLock = mCurrentData == null ? false : mCurrentData.isLock();
			if (isDataLock || isTempLock) {
				/**
				 * 数据集处于锁定状态
				 */
				final int leftPosition = getLeftPagePosition();
				final int rightPosition = getRightPagePosition();
				if (mNextScreen == leftPosition - 1 && isEndlessScrollingIfDataLock) {
					setCurrentScreen(rightPosition);
					scrollTo((rightPosition - leftPosition + 1) * pageWidth + getScrollX(), getScrollY());
				} else if (mNextScreen == rightPosition + 1 && isEndlessScrollingIfDataLock) {
					setCurrentScreen(leftPosition);
					scrollTo(getScrollX() - (rightPosition - leftPosition + 1) * pageWidth, getScrollY());
				} else {
					setCurrentScreen(Math.max(leftPosition, Math.min(mNextScreen, rightPosition)));
				}
			} else {
				/**
				 * 数据集处于非锁定状态
				 */
				if (mNextScreen == -1 && isEndlessScrolling) {
					setCurrentScreen(getChildCount() - 1);
					scrollTo(getChildCount() * pageWidth + getScrollX(), getScrollY());
				} else if (mNextScreen == getChildCount() && isEndlessScrolling) {
					setCurrentScreen(0);
					scrollTo(getScrollX() - getChildCount() * pageWidth, getScrollY());
				} else {
					setCurrentScreen(Math.max(0, Math.min(mNextScreen, getChildCount() - 1)));
				}
			}
			mNextScreen = INVALID_SCREEN;
			isTempLock = false;

			if (lineLightbar != null) {
				lineLightbar.update(getScrollX());
			}
			clearChildrenCache();
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {

		if (list == null || list.size() == 0) {
			super.dispatchDraw(canvas);
			return;
		}

		boolean restore = false;
		int restoreCount = 0;

		// ViewGroup.dispatchDraw() supports many features we don't need:
		// clip to padding, layout animation, animation listener, disappearing
		// children, etc. The following implementation attempts to fast-track
		// the drawing dispatch by drawing only what we know needs to be drawn.

		if (isEndlessScrolling && (getChildCount() < 2 || (mCurrentData.isLock() && mCurrentData.getPageNum() < 2))) {
			/**
			 * 小于两屏时，不循环滚动
			 */
			isEndlessScrolling = false;
		} else if (!isEndlessScrolling && isEndlessScrollingBackup && getChildCount() >= 2) {
			isEndlessScrolling = isEndlessScrollingBackup;
		}

		boolean fastDraw = mTouchState != TOUCH_STATE_DOWN && mTouchState != TOUCH_STATE_SCROLLING && mNextScreen == INVALID_SCREEN;
		// If we are not scrolling or flinging, draw only the current screen
		if (fastDraw) {
			View v = getChildAt(mCurrentScreen);
			if (v != null) {
				callDrawChild(canvas, getChildAt(mCurrentScreen), getDrawingTime());
			} else {
				super.dispatchDraw(canvas);
			}
		} else {
			// long begin = System.currentTimeMillis();
			final long drawingTime = getDrawingTime();
			final int width = pageWidth;
			final float scrollPos = (float) getScrollX() / width;
			int leftScreen = (int) scrollPos;
			int rightScreen = leftScreen + 1;

			/**
			 * 屏幕循环滚动
			 */
			boolean isScrollToRight = false;
			int childCount = getChildCount();

			final boolean isDataLock = mCurrentData == null ? false : mCurrentData.isLock();
			final int leftPosition = getLeftPagePosition();
			final int rightPosition = getRightPagePosition();

			if (isDataLock || isTempLock) {
				/**
				 * 数据集处于锁定状态
				 */
				if (scrollPos < leftPosition && isEndlessScrollingIfDataLock) {
					leftScreen = rightPosition;
					rightScreen = leftPosition;
				} else if (scrollPos < leftPosition) {
					leftScreen = -1;
					rightScreen = leftPosition;
				} else {
					leftScreen = Math.min((int) scrollPos, rightPosition);
					rightScreen = leftScreen + 1;
					if (isEndlessScrollingIfDataLock) {
						rightScreen = rightScreen > rightPosition ? leftPosition : rightScreen;
						isScrollToRight = true;
					}
				}
			} else {
				/**
				 * 数据集处于非锁定状态
				 */
				if (scrollPos < 0 && isEndlessScrolling) {
					leftScreen = childCount - 1;
					rightScreen = 0;
				} else if (scrollPos < 0) {
					leftScreen = -1;
					rightScreen = 0;
				} else {
					leftScreen = Math.min((int) scrollPos, childCount - 1);
					rightScreen = leftScreen + 1;
					if (isEndlessScrolling) {
						rightScreen = rightScreen % childCount;
						isScrollToRight = true;
					}
				}
			}
			/*
			if (EffectsType.isLeftScreenFirst()) {
				drawLeftScreen(canvas, leftScreen, rightScreen, leftPosition, rightPosition, drawingTime, isDataLock, isScrollToRight);
				drawRightScreen(scrollPos, canvas, leftScreen, rightScreen, leftPosition, rightPosition, drawingTime, isDataLock, isScrollToRight);
			} else {
				drawRightScreen(scrollPos, canvas, leftScreen, rightScreen, leftPosition, rightPosition, drawingTime, isDataLock, isScrollToRight);
				drawLeftScreen(canvas, leftScreen, rightScreen, leftPosition, rightPosition, drawingTime, isDataLock, isScrollToRight);
			}
			*/
			drawRightScreen(scrollPos, canvas, leftScreen, rightScreen, leftPosition, rightPosition, drawingTime, isDataLock, isScrollToRight);
			drawLeftScreen(canvas, leftScreen, rightScreen, leftPosition, rightPosition, drawingTime, isDataLock, isScrollToRight);
		}

		if (restore) {
			canvas.restoreToCount(restoreCount);
		}
	}

	private void drawRightScreen(float scrollPos, Canvas canvas, int leftScreen, int rightScreen, int leftPosition, int rightPosition, long drawingTime, boolean isDataLock, boolean isScrollToRight) {
		if (scrollPos != leftScreen && isScreenValid(rightScreen)) {
			if (isDataLock || isTempLock) {
				/**
				 * 数据集处于锁定状态
				 */
				if (isEndlessScrollingIfDataLock && rightScreen == leftPosition && isScrollToRight) {
					if (isEffectOpened) {
						//DrawerEffects.getInstance().drawRightEffect(canvas, rightScreen, drawingTime, this);
					} else {
						int offset = (rightPosition - leftPosition + 1) * pageWidth;
						canvas.translate(+offset, 0);
						callDrawChild(canvas, getChildAt(rightScreen), drawingTime);
						canvas.translate(-offset, 0);
					}
				} else {
					if (isEffectOpened) {
						//DrawerEffects.getInstance().drawRightEffect(canvas, rightScreen, drawingTime, this);
					} else {
						callDrawChild(canvas, getChildAt(rightScreen), drawingTime);
					}
				}
			} else {
				/**
				 * 数据集处于非锁定状态
				 */
				if (isEndlessScrolling && rightScreen == 0 && isScrollToRight) {
					if (isEffectOpened) {
						//DrawerEffects.getInstance().drawRightEffect(canvas, rightScreen, drawingTime, this);
					} else {
						int offset = getChildCount() * pageWidth;
						canvas.translate(+offset, 0);
						callDrawChild(canvas, getChildAt(rightScreen), drawingTime);
						canvas.translate(-offset, 0);
					}
				} else {
					if (isEffectOpened) {
						//DrawerEffects.getInstance().drawRightEffect(canvas, rightScreen, drawingTime, this);
					} else {
						callDrawChild(canvas, getChildAt(rightScreen), drawingTime);
					}
				}
			}
		}
	}

	private void drawLeftScreen(Canvas canvas, int leftScreen, int rightScreen, int leftPosition, int rightPosition, long drawingTime, boolean isDataLock, boolean isScrollToRight) {
		if (isScreenValid(leftScreen)) {
			if (isDataLock || isTempLock) {
				/**
				 * 数据集处于锁定状态
				 */
				if (rightScreen == leftPosition && !isScrollToRight) {
					if (isEffectOpened) {
						//DrawerEffects.getInstance().drawLeftEffect(canvas, leftScreen, drawingTime, this);
					} else {
						int offset = (rightPosition - leftPosition + 1) * pageWidth;
						canvas.translate(-offset, 0);
						callDrawChild(canvas, getChildAt(leftScreen), drawingTime);
						canvas.translate(+offset, 0);
					}
				} else {
					if (isEffectOpened) {
						//DrawerEffects.getInstance().drawLeftEffect(canvas, leftScreen, drawingTime, this);
					} else {
						callDrawChild(canvas, getChildAt(leftScreen), drawingTime);
					}
				}
			} else {
				/**
				 * 数据集处于非锁定状态
				 */
				if (rightScreen == 0 && !isScrollToRight) {
					if (isEffectOpened) {
						//DrawerEffects.getInstance().drawLeftEffect(canvas, leftScreen, drawingTime, this);
					} else {
						int offset = getChildCount() * pageWidth;
						canvas.translate(-offset, 0);
						callDrawChild(canvas, getChildAt(leftScreen), drawingTime);
						canvas.translate(+offset, 0);
					}
				} else {
					if (isEffectOpened) {
						//DrawerEffects.getInstance().drawLeftEffect(canvas, leftScreen, drawingTime, this);
					} else {
						callDrawChild(canvas, getChildAt(leftScreen), drawingTime);
					}
				}
			}
		}
	}

	/**
	 * 获取手指在屏幕的滑动距离
	 */
	public boolean adjustDirection() {
		if (mTouchState != TOUCH_STATE_DOWN && mTouchState != TOUCH_STATE_SCROLLING && mNextScreen == INVALID_SCREEN)
			return true;
		else
			return false;
	}

	private boolean isScreenValid(int screen) {
		if (mCurrentData.isLock()) {
			return screen >= getLeftPagePosition() && screen <= getRightPagePosition();
		} else {
			return screen >= 0 && screen < getChildCount();
		}
	}

	public void snapToScreen(int whichScreen) {
		snapToScreen(whichScreen, 0);
	}

	public void snapToScreen(int whichScreen, int velocity) {
		final boolean isDataLock = mCurrentData == null ? false : mCurrentData.isLock();

		final int leftPosition = getLeftPagePosition();
		final int rightPosition = getRightPagePosition();
		if (isDataLock) {
			whichScreen = Math.max(leftPosition + (isEndlessScrollingIfDataLock ? -1 : 0), Math.min(whichScreen, rightPosition + (isEndlessScrollingIfDataLock ? 1 : 0)));
		} else {
			whichScreen = Math.max((isEndlessScrolling ? -1 : 0), Math.min(whichScreen, getChildCount() - (isEndlessScrolling ? 0 : 1)));
		}
		enableChildrenCache(mCurrentScreen, whichScreen);
		if (getScrollX() != (whichScreen * pageWidth)) {

			mNextScreen = whichScreen;

			final int delta = whichScreen * pageWidth - getScrollX();
			final int screenDelta = Math.max(1, Math.abs(whichScreen - mCurrentScreen));
			int duration = (screenDelta + 1) * (GpuControler.isOpenGpu(this) ? 210 : 150);

			velocity = Math.abs(velocity);
			if (velocity > 0) {
				duration += (duration / (velocity / BASELINE_FLING_VELOCITY)) * FLING_VELOCITY_INFLUENCE;
			} else {
				duration += 200;
			}

			mScroller.startScroll(getScrollX(), 0, delta, 0, duration);

			/**
			 * 计算真实目标屏
			 */
			int destToScreen = 0;
			if (isDataLock) {
				/**
				 * 数据集处于锁定状态
				 */
				if (mNextScreen == leftPosition - 1 && isEndlessScrollingIfDataLock) {
					destToScreen = rightPosition;
				} else if (mNextScreen == rightPosition + 1 && isEndlessScrollingIfDataLock) {
					destToScreen = leftPosition;
				} else {
					destToScreen = Math.max(leftPosition, Math.min(mNextScreen, rightPosition));
				}
				isTempLock = true;
			} else {
				/**
				 * 数据集处于非锁定状态
				 */
				if (mNextScreen == -1 && isEndlessScrolling) {
					destToScreen = getChildCount() - 1;
				} else if (mNextScreen == getChildCount() && isEndlessScrolling) {
					destToScreen = 0;
				} else {
					destToScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
				}
			}

			/**
			 * 切换数据集
			 */
			int currentDataPosition = list.indexOf(mCurrentData);
			int destDataPosition = list.indexOf(getData(destToScreen));
			if (currentDataPosition != -1 && destDataPosition != -1 && currentDataPosition != destDataPosition) {
				mCurrentData = list.get(destDataPosition);
				lockData(mCurrentData.isLock());
				if (splitLightbar != null) {
					splitLightbar.refresh(mCurrentData.getPageNum(), destToScreen - getDataPageInfo(mCurrentData)[0]);
				}
				if (onSwitchDataListener != null) {
					onSwitchDataListener.onSwitchData(list, currentDataPosition, destDataPosition);
				}
			}

			if (onSnapToScreenListener != null) {
				onSnapToScreenListener.onSnapToScreen(list, mCurrentScreen, destToScreen);
			}

			if (lightbar != null) {
				lightbar.update(destToScreen);
			}
			if (splitLightbar != null) {
				splitLightbar.update(destToScreen - getDataPageInfo(mCurrentData)[0]);
			}

			mCurrentScreen = destToScreen;

			invalidate();
		}
	}

	/**
	 * 跳转至数据集
	 */
	public void snapToData(ICommonData data) {
		if (data == null)
			return;
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
			mNextScreen = INVALID_SCREEN;
		}
		clearOriginalData();
		mCurrentData = data;
		setCurrentScreen(getDataPageInfo(mCurrentData)[0]);
		lockData(mCurrentData.isLock());
		scrollTo(mCurrentScreen * pageWidth, getScrollY());
		if (lightbar != null) {
			lightbar.update(mCurrentScreen);
		}
		if (splitLightbar != null) {
			splitLightbar.refresh(mCurrentData.getPageNum(), mCurrentScreen - getDataPageInfo(mCurrentData)[0]);
		}
		if (lineLightbar != null) {
			lineLightbar.update(mCurrentScreen * pageWidth);
		}
	}

	public void scrollLeft() {
		snapToScreen(mCurrentScreen - 1);
	}

	public void scrollRight() {
		snapToScreen(mCurrentScreen + 1);
	}
	
	/**
	 * 是否可向左滚动
	 * @return
	 */
	public boolean canbeScrollLeft() {
		final boolean isDataLock = mCurrentData == null ? false : mCurrentData.isLock();
		if (mCurrentData == null) {
			return false;
		} else if ((isDataLock && !isEndlessScrollingIfDataLock) || (!isDataLock && !isEndlessScrolling)) {
			/**
			 * 数据集处于锁定状态且锁定时不能循环滚动<br>
			 * 或<br>
			 * 数据集处于非锁定状态且不能循环滚动
			 */
			return mCurrentScreen == getLeftPagePosition() ? false : true;
		} else {
			return true;
		}
	}

	/**
	 * 是否可向右滚动
	 * @return
	 */
	public boolean canbeScrollRight() {
		final boolean isDataLock = mCurrentData == null ? false : mCurrentData.isLock();
		if (mCurrentData == null) {
			return false;
		} else if ((isDataLock && !isEndlessScrollingIfDataLock) || (!isDataLock && !isEndlessScrolling)) {
			/**
			 * 数据集处于锁定状态且锁定时不能循环滚动<br>
			 * 或<br>
			 * 数据集处于非锁定状态且不能循环滚动
			 */
			return mCurrentScreen == getRightPagePosition() ? false : true;
		} else {
			return true;
		}
	}

	protected int getPageCount() {
		if (list == null || list.size() == 0) {
			return 1;
		}

		int pages = 0;
		for (ICommonData data : list) {
			pages += data.getPageNum();
		}

		return pages;
	}

	/**
	 * 根据页数获取数据集
	 * 
	 * @param screen
	 * @return
	 */
	public ICommonData getData(int screen) {
		if (list == null)
			return null;

		int pages = 0;
		for (ICommonData data : list) {
			pages += data.getPageNum();
			if (screen < pages) {
				return data;
			}
		}
		return null;
	}

	/**
	 * 根据tag获取数据集
	 * 
	 * @param screen
	 * @return
	 */
	public ICommonData getData(Object tag) {
		if (list == null)
			return null;

		for (ICommonData data : list) {
			if (data.getTag().equals(tag)) {
				return data;
			}
		}
		return null;
	}

	/**
	 * 获取数据集开始页及结束页信息 <br>
	 * e.g. 假设该数据集占据第二页和第三页，则pageInfo[0] = 1, pageInfo[1] = 3
	 * 
	 * @param data
	 * @return
	 */
	public int[] getDataPageInfo(ICommonData data) {
		int[] pageInfo = new int[2];
		int index = list.indexOf(data);
		int pages = 0;
		for (int i = 0; i < index; i++) {
			pages += list.get(i).getPageNum();
		}
		pageInfo[0] = pages;
		pageInfo[1] = pages + data.getPageNum();
		return pageInfo;
	}

	/**
	 * 设置数据源
	 */
	public void setList(List<ICommonData> list) {
		startPage = 0;
		this.list = list;
		if (list.size() == 0)
			return;

		reLayoutWithoutSaveState();
	}

	public List<ICommonData> getList() {
		return list;
	}

	/**
	 * 锁定当前数据集
	 */
	public void lockData(boolean isLock) {
		if (mCurrentData == null)
			return;
		mCurrentData.setLock(isLock);

		if (isEndlessScrollingIfDataLock && (mCurrentData == null || mCurrentData.getPageNum() < 2)) {
			isEndlessScrollingIfDataLock = false;
		} else if (isEndlessScrollingIfDataLockBackup && !isEndlessScrollingIfDataLock && mCurrentData != null && mCurrentData.getPageNum() >= 2) {
			isEndlessScrollingIfDataLock = isEndlessScrollingIfDataLockBackup;
		}
	}

	private void checkEndlessScrollingIfDataLock() {
		if (isEndlessScrollingIfDataLockBackup && !isEndlessScrollingIfDataLock && mCurrentData != null && mCurrentData.getPageNum() >= 2) {
			isEndlessScrollingIfDataLock = isEndlessScrollingIfDataLockBackup;
		}
	}

	/**
	 * 获取最左页面position, 在当前数据集非锁定的情况下为0
	 * 
	 * @return
	 */
	private int getLeftPagePosition() {
		if ((mCurrentData != null && mCurrentData.isLock()) || isTempLock) {
			return getDataPageInfo(mCurrentData)[0];
		} else {
			return 0;
		}
	}

	/**
	 * 获取最右页面position, 在当前数据集非锁定的情况下为getChildCount() - 1
	 * 
	 * @return
	 */
	private int getRightPagePosition() {
		if ((mCurrentData != null && mCurrentData.isLock()) || isTempLock) {
			return getDataPageInfo(mCurrentData)[1] - 1;
		} else {
			return getChildCount() - 1;
		}
	}
	
	/**
	 * 刷新CommonSlidingView - 所有页面重新布局（不保存原先状态，仅供setList方法调用）
	 */
	private void reLayoutWithoutSaveState() {
		mOriginalData = null;
		startPage = 0;
		isLockLayout = false;
		isReLayoutSpecifiedData = false;
		checkEndlessScrollingIfDataLock();
		requestLayout();
	}

	/**
	 * 刷新CommonSlidingView - 所有页面重新布局
	 */
	public void reLayout() {
		saveState();
		startPage = 0;
		isLockLayout = false;
		isReLayoutSpecifiedData = false;
		checkEndlessScrollingIfDataLock();
		requestLayout();
	}

	/**
	 * 刷新CommonSlidingView
	 * 
	 * @param startPage
	 *            - 从指定页面开始重新布局
	 */
	public void reLayout(int startPage) {
		if (!isLockLayout && this.startPage <= startPage) {
			/**
			 * 若上一个布局尚未开始，比对布局起始页，起始页大等于上次布局请求则不予处理
			 */
			return;
		}

		saveState();
		this.startPage = startPage;
		isLockLayout = false;
		isReLayoutSpecifiedData = false;
		checkEndlessScrollingIfDataLock();
		requestLayout();
	}

	/**
	 * 刷新指定数据集，根据新旧数据集页数信息，判断是否刷新后续数据集
	 */
	public void reLayout(ICommonData data, int[] oldPageInfo) {
		if (!isLockLayout) {
			/**
			 * 若上一个布局尚未开始，就启动了下一个布局请求，则全部重新布局
			 */
			reLayout();
			return;
		}

		saveState();
		int newPageInfo[] = getDataPageInfo(data);
		if (newPageInfo[0] == oldPageInfo[0] && newPageInfo[1] == oldPageInfo[1]) {
			isReLayoutSpecifiedData = true;
			this.startPage = newPageInfo[0];
			isLockLayout = false;
			checkEndlessScrollingIfDataLock();
			requestLayout();
		} else {
			reLayout(newPageInfo[0]);
		}
	}

	private void saveState() {
		mOriginalData = mCurrentData;
	}

	private void restoreState() {
		if (mOriginalData == null)
			return;

		int[] pageInfo = getDataPageInfo(mOriginalData);
		if (mCurrentScreen < pageInfo[0]) {
			setCurrentScreen(pageInfo[0]);
		} else if (mCurrentScreen >= pageInfo[1]) {
			setCurrentScreen(pageInfo[1] - 1);
		}
		mCurrentData = mOriginalData;
		mOriginalData = null;
	}

	public int getPageWidth() {
		return pageWidth;
	}

	public int getPageHeight() {
		return pageHeight;
	}

	public void setCommonLightbar(CommonLightbar lightbar) {
		this.lightbar = lightbar;
	}

	public void setSplitCommonLightbar(CommonLightbar splitLightbar) {
		this.splitLightbar = splitLightbar;
	}

	public void setLineLightbar(LineLightbar lineLightbar) {
		this.lineLightbar = lineLightbar;
	}

	protected CommonLayout getNewLayout() {
		return new CommonLayout(getContext());
	}

	protected void removeLayout(int pageNum) {
		CommonLayout cl = (CommonLayout) getChildAt(pageNum);
		removeViewInLayout(cl);
		if (pageNum < pageViews.size()) {
			pageViews.remove(pageNum);
		}
	}

	public void clearLayout() {
		pageViews.clear();
		removeAllViews();
	}

	/**
	 * 获取当前数据集
	 */
	public ICommonData getCurrentData() {
		return mCurrentData;
	}

	public int getEffectCurrentScreen() {
		return mEffectCurrentScreen;
	}

	public int getCurrentScreen() {
		return mCurrentScreen;
	}

	protected void setCurrentScreen(int mCurrentScreen) {
		this.mCurrentScreen = mCurrentScreen;
		this.mEffectCurrentScreen = mCurrentScreen;
	}

	/**
	 * 获取某页的CommonLayout
	 * 
	 * @param index
	 * @return
	 */
	public CommonLayout getCommonLayout(int page) {
		return pageViews.get(page);
	}

	public void setOnSwitchDataListener(OnSwitchDataListener onSwitchDataListener) {
		this.onSwitchDataListener = onSwitchDataListener;
	}

	public void setOnSnapToScreenListener(OnSnapToScreenListener onSnapToScreenListener) {
		this.onSnapToScreenListener = onSnapToScreenListener;
	}

	public void setOnItemClickListener(OnCommonSlidingViewClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setOnItemLongClickListener(OnCommonSlidingViewLongClickListener onItemLongClickListener) {
		this.onItemLongClickListener = onItemLongClickListener;
	}

	public void setOnFlingListener(OnFlingListener onFlingListener) {
		this.onFlingListener = onFlingListener;
	}

	public void setEndlessScrolling(boolean isEndlessScrolling) {
		this.isEndlessScrolling = this.isEndlessScrollingBackup = this.isEndlessScrollingIfDataLock = this.isEndlessScrollingIfDataLockBackup = isEndlessScrolling;
	}

	@Override
	public void onClick(View v) {
		if (onItemClickListener != null) {
			CommonViewHolder viewHolder = (CommonViewHolder) v.getTag(R.id.common_view_holder);
			if (viewHolder == null)
				return;
			ICommonData data = getData(viewHolder.screen);

			onItemClickListener.onItemClick(v, viewHolder.positionInData, viewHolder.positionInScreen, viewHolder.screen, data);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		CommonViewHolder viewHolder = (CommonViewHolder) v.getTag(R.id.common_view_holder);

		if (viewHolder == null)
			return false;
		ICommonData data = getData(viewHolder.screen);

		boolean handled = false;
		handled = onItemLongClickListener == null ? false : onItemLongClickListener.onItemLongClick(v, viewHolder.positionInData, viewHolder.positionInScreen, viewHolder.screen, data);

		mTouchState = TOUCH_STATE_DONE_WAITING;

		return handled;
	}

	protected void onLayoutChildrenAfter() {
		restoreState();
	}

	/**
	 * 子类初始化动作,将在构造函数中调用
	 */
	protected abstract void initSelf(Context ctx);

	/**
	 * 
	 * @param data
	 *            - 数据集
	 * @param position
	 *            - 获取的数据在数据集中的位置
	 * @return
	 */
	public abstract View onGetItemView(ICommonData data, int position);

	public int getFingerOffsetY() {
		return fingerOffsetY;
	}

	public int getTouchState() {
		return mTouchState;
	}

	/**
	 * 开启缓存
	 */
	public void enableChildrenCache(int fromScreen, int toScreen) {
//		if (fromScreen > toScreen) {
//			final int temp = fromScreen;
//			fromScreen = toScreen;
//			toScreen = temp;
//		}
//
//		final int count = getRightPagePosition() + 1;
//		fromScreen = Math.max(fromScreen, 0);
//		toScreen = Math.min(toScreen, count - 1);
//		for (int i = fromScreen; i <= toScreen; i++) {
//			final CommonLayout layout = (CommonLayout) getChildAt(i);
//			if (layout != null) {
//				layout.setChildrenDrawnWithCacheEnabled(true);
//			}
//		}
	}

	/**
	 * 释放缓存
	 */
	public void clearChildrenCache() {
		destroyChildrenDrawingCache();
	}

	public void destroyChildrenDrawingCache() {
//		final int count = getChildCount();
//		for (int i = 0; i < count; i++) {
//			final CommonLayout layout = (CommonLayout) getChildAt(i);
//			if (layout != null) {
//				layout.setChildrenDrawnWithCacheEnabled(false);
//				layout.setChildrenDrawingCacheEnabled(false);
//			}
//		}
	}

	public CommonLightbar getLightbar() {
		return lightbar;
	}

	public CommonLightbar getSplitLightbar() {
		return splitLightbar;
	}

	public LineLightbar getLineLightbar() {
		return lineLightbar;
	}

	public boolean callDrawChild(Canvas canvas, View view, long drawingTime) {
		//DrawerLayout的硬件加速缓存若关闭则开启
		if(GpuControler.isOpenGpu(this) && view instanceof DrawerLayout && GpuControler.hasDestroyHardwareLayers(view)){
			((DrawerLayout)view).enableHardwareLayers();
		}
		
		return drawChild(canvas, view, drawingTime);
	}

	public boolean isScrollerFinished() {
		return mScroller.isFinished();
	}
	//add by zhou,为了解决快捷方式进入我的手机时，当前页面不正确的问题
	protected void clearOriginalData() {
		mOriginalData=null;
		
	}
}
