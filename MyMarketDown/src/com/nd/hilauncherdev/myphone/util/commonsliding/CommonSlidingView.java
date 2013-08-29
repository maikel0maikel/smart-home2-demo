package com.nd.hilauncherdev.myphone.util.commonsliding;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Scroller;
import android.widget.TextView;

import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonData;

/**
 * 类似GridView布局的横向布局视图
 * 
 * @author Anson
 */
public abstract class CommonSlidingView extends ViewGroup implements OnClickListener, OnLongClickListener {

	protected static final int INVALID_SCREEN = -999;

	/**
	 * 默认滚屏Duration
	 */
	private static final int SCROLLING_SPEED = 300;
//	private static final int SCROLLING_SPEED = 700;

	private static final int SNAP_TO_DESTINATION_DURATION_OFFSET = 300;

	/**
	 * Fling灵敏度
	 */
	private static final int SNAP_VELOCITY = 600;

	private static final int TOUCH_STATE_REST = 0;

	private static final int TOUCH_STATE_DOWN = 1;

	private static final int TOUCH_STATE_SCROLLING = 2;

	private static final int TOUCH_STATE_DONE_WAITING = 3;

	private int mTouchState = TOUCH_STATE_REST;

	private int mScrollSpeed = SCROLLING_SPEED;

	private int mTouchSlop;

	private int mMaximumVelocity;

	private float mLastMotionX;

	private VelocityTracker mVelocityTracker;

	protected Scroller mScroller;

	protected ViewGroup.LayoutParams holderParams;

	/**
	 * 当前页
	 */
	protected int mCurrentScreen = 0;

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
	 * 是否在layoutChildren后调用OnLayoutChildrenAfter()方法
	 */
	private boolean isNeedCallOnLayoutChildrenAfter = false;

	/**
	 * 屏幕指示灯
	 */
	private CommonLightbar lightbar;

	/**
	 * 从startPage页开始布局
	 */
	protected int startPage = 0;

	/**
	 * 数据集
	 */
	protected List<ICommonData> list;

	/**
	 * CommonLayout缓存
	 */
	private List<CommonLayout> pageViews = new ArrayList<CommonLayout>();

	/**
	 * 循环滚动
	 */
	protected boolean isEndlessScrolling = true;

	/**
	 * 数据集锁定时是否在数据集内循环滚动
	 */
	protected boolean isEndlessScrollingIfDataLock = true;

	protected boolean isEndlessScrollingIfDataLockBackup = isEndlessScrollingIfDataLock;

	/**
	 * 切换数据集监听器
	 */
	private SwitchDataListener switchDataListener;

	/**
	 * 单击事件监听器
	 */
	private OnCommonSlidingViewClickListener onClickListener;

	/**
	 * 长按事件监听器
	 */
	private OnCommonSlidingViewLongClickListener onLongClickListener;

	public interface SwitchDataListener {

		public void switchData(List<ICommonData> list, int fromPosition, int toPosition);

	}

	public interface OnCommonSlidingViewClickListener {

		public void onItemClick(View v, int positionInData, int positionInScreen, int screen, ICommonData data);

	}

	public interface OnCommonSlidingViewLongClickListener {

		public boolean onLongClick(View v, int positionInData, int positionInScreen, int screen, ICommonData data);

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
		
//		int childCount = getChildCount();
//		for (int i = 0; i < childCount; i++) {
//			View child = getChildAt(i);
//			child.measure(child.getMeasuredWidth(), child.getMeasuredHeight());
//		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!isLockLayout) {
			isLockLayout = true;
			layoutChildren();
			onLayoutChildrenAfter();
		}
		if (lightbar != null) {
			getHandler().postDelayed(new Runnable(){
				@Override
				public void run() {
					lightbar.refresh(getPageCount(), mCurrentScreen);
				}
			}, 100);
		}
		snapToScreen(mCurrentScreen);
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

		int rowNum = data.getRowNum() > 0 ? data.getRowNum() : 1;
		int rowHeight = actualHeight / rowNum;

		rowHeight = rowHeight -15;
		columnWidth = columnWidth - 15;

		
		CommonLayout layout = getLayout(pageNum);
		ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		int childHeightSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, p.height);
		int childWidthSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(columnWidth, MeasureSpec.EXACTLY), 0, p.width);

		int rows = data.getRowNum();
		int columns = data.getColumnNum();
		int position = rows * columns * (pageNum - pageInfo[0]);

		int columnPadding = (actualWidth - data.getColumnNum() * columnWidth) / (data.getColumnNum() + 1);
		int rowPadding = (actualHeight - data.getRowNum() * rowHeight) / (data.getRowNum() + 1);
		
		for (int i = 0; i < rows; i++) {
			y += rowPadding;
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
				child.setTag(viewHolder);

				child.setOnClickListener(this);
				child.setOnLongClickListener(this);
				child.setHapticFeedbackEnabled(false);

				x += columnPadding;
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
			holder.removeAllViewsInLayout();
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

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			Log.e("test", "mTouchSlop  " + mTouchSlop);
			if (xDiff > mTouchSlop && mTouchState != TOUCH_STATE_DONE_WAITING) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
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

		final boolean isDataLock = mCurrentData == null ? false : mCurrentData.isLock();
		final int leftPosition = getLeftPagePosition();
		final int rightPosition = getRightPagePosition();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			mTouchState = TOUCH_STATE_DOWN;
			mLastMotionX = x;
			break;

		case MotionEvent.ACTION_MOVE:
			if (mTouchState != TOUCH_STATE_SCROLLING && mTouchState != TOUCH_STATE_DOWN) {
				break;
			}

			int deltaX = (int) (mLastMotionX - x);
			Log.e("test", "mTouchSlop 1111     " + mTouchSlop);
			if (Math.abs(deltaX) > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}

			mLastMotionX = x;

			if (deltaX < 0) {
				if (isDataLock) {
					if (getScrollX() > (isEndlessScrollingIfDataLock ? (leftPosition - 1) * pageWidth : leftPosition * pageWidth)) {
						scrollBy(deltaX, 0);
					}
				} else {
					if (getScrollX() > (isEndlessScrolling ? -pageWidth : -pageWidth / 2)) {
						scrollBy(deltaX, 0);
					}
				}
			} else if (deltaX > 0) {
				if (isDataLock) {
					final int availableToScroll = rightPosition * pageWidth - getScrollX() + (isEndlessScrollingIfDataLock ? pageWidth : 0);
					if (availableToScroll > 0) {
						scrollBy(deltaX, 0);
					}
				} else {
					final int availableToScroll = (getPageCount() - 1) * pageWidth - getScrollX() + (isEndlessScrolling ? pageWidth : pageWidth / 2);
					if (availableToScroll > 0) {
						scrollBy(deltaX, 0);
					}
				}
			}

			/**
			 * 更新指示灯
			 */
			int moveToScreen = (int) Math.floor((getScrollX() + (pageWidth / 2)) / (float) pageWidth);
			if (lightbar != null) {
				if (isDataLock) {
					if (isEndlessScrollingIfDataLock && moveToScreen > rightPosition) {
						lightbar.update(leftPosition);
					} else if (isEndlessScrollingIfDataLock && moveToScreen < leftPosition) {
						lightbar.update(rightPosition);
					} else {
						lightbar.update(Math.max(leftPosition, Math.min(moveToScreen, rightPosition)));
					}
				} else {
					if (isEndlessScrolling) {
						lightbar.update((getChildCount() + moveToScreen) % getChildCount());
					} else {
						lightbar.update(Math.max(0, Math.min(moveToScreen, getChildCount() - 1)));
					}
				}
			}

			break;

		case MotionEvent.ACTION_UP:
			mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

			int velocityX = (int) mVelocityTracker.getXVelocity();

			int whichScreen = (int) Math.floor((getScrollX() + (pageWidth / 2)) / (float) pageWidth);
			final float scrolledPos = (float) getScrollX() / pageWidth;

			if (isDataLock) {
				if (velocityX > SNAP_VELOCITY && mCurrentScreen > leftPosition + (isEndlessScrollingIfDataLock ? -1 : 0)) {
					final int bound = scrolledPos < whichScreen ? mCurrentScreen - 1 : mCurrentScreen;
					whichScreen = Math.min(whichScreen, bound);
					snapToScreen(whichScreen);
				} else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < rightPosition + (isEndlessScrollingIfDataLock ? 1 : 0)) {
					final int bound = scrolledPos > whichScreen ? mCurrentScreen + 1 : mCurrentScreen;
					whichScreen = Math.max(whichScreen, bound);
					snapToScreen(whichScreen);
				} else {
					snapToScreen(whichScreen, SNAP_TO_DESTINATION_DURATION_OFFSET);
				}
			} else {
				if (velocityX > SNAP_VELOCITY && mCurrentScreen > (isEndlessScrolling ? -1 : 0)) {
					final int bound = scrolledPos < whichScreen ? mCurrentScreen - 1 : mCurrentScreen;
					whichScreen = Math.min(whichScreen, bound);
					snapToScreen(whichScreen);
				} else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < getChildCount() - (isEndlessScrolling ? 0 : 1)) {
					final int bound = scrolledPos > whichScreen ? mCurrentScreen + 1 : mCurrentScreen;
					whichScreen = Math.max(whichScreen, bound);
					snapToScreen(whichScreen);
				} else {
					snapToScreen(whichScreen, SNAP_TO_DESTINATION_DURATION_OFFSET);
				}
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			snapToScreen(mCurrentScreen, SNAP_TO_DESTINATION_DURATION_OFFSET);
			break;
		}

		return true;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			invalidate();
		} else if (mNextScreen != INVALID_SCREEN) {
			final boolean isDataLock = mCurrentData == null ? false : mCurrentData.isLock();
			if (isDataLock) {
				final int leftPosition = getLeftPagePosition();
				final int rightPosition = getRightPagePosition();
				if (mNextScreen == leftPosition - 1 && isEndlessScrollingIfDataLock) {
					mCurrentScreen = rightPosition;
					scrollTo((rightPosition - leftPosition + 1) * pageWidth + getScrollX(), getScrollY());
				} else if (mNextScreen == rightPosition + 1 && isEndlessScrollingIfDataLock) {
					mCurrentScreen = leftPosition;
					scrollTo(getScrollX() - (rightPosition - leftPosition + 1) * pageWidth, getScrollY());
				} else {
					mCurrentScreen = Math.max(leftPosition, Math.min(mNextScreen, rightPosition));
				}
			} else {
				if (mNextScreen == -1 && isEndlessScrolling) {
					mCurrentScreen = getChildCount() - 1;
					scrollTo(getChildCount() * pageWidth + getScrollX(), getScrollY());
				} else if (mNextScreen == getChildCount() && isEndlessScrolling) {
					mCurrentScreen = 0;
					scrollTo(getScrollX() - getChildCount() * pageWidth, getScrollY());
				} else {
					mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
				}
			}
			mNextScreen = INVALID_SCREEN;
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

		boolean fastDraw = mTouchState != TOUCH_STATE_DOWN && mTouchState != TOUCH_STATE_SCROLLING && mNextScreen == INVALID_SCREEN;
		// If we are not scrolling or flinging, draw only the current screen
		if (fastDraw) {
			View view = getChildAt(mCurrentScreen);
			if (view != null) {
				drawChild(canvas, view, getDrawingTime());
			} else {
				super.dispatchDraw(canvas);
			}
		} else {
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

			if (isEndlessScrolling && childCount < 2) {
				isEndlessScrolling = false;
			}

			final boolean isDataLock = mCurrentData == null ? false : mCurrentData.isLock();
			final int leftPosition = getLeftPagePosition();
			final int rightPosition = getRightPagePosition();

			if (isDataLock) {
				if (scrollPos < leftPosition && isEndlessScrollingIfDataLock) {
					leftScreen = rightPosition;
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
				if (scrollPos < 0 && isEndlessScrolling) {
					leftScreen = childCount - 1;
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

			if (isScreenValid(leftScreen)) {
				if (isDataLock) {
					if (rightScreen == leftPosition && !isScrollToRight) {
						int offset = (rightPosition - leftPosition + 1) * width;
						canvas.translate(-offset, 0);
						drawChild(canvas, getChildAt(leftScreen), drawingTime);
						canvas.translate(+offset, 0);
					} else {
						drawChild(canvas, getChildAt(leftScreen), drawingTime);
					}
				} else {
					if (rightScreen == 0 && !isScrollToRight) {
						int offset = childCount * width;
						canvas.translate(-offset, 0);
						drawChild(canvas, getChildAt(leftScreen), drawingTime);
						canvas.translate(+offset, 0);
					} else {
						drawChild(canvas, getChildAt(leftScreen), drawingTime);
					}
				}
			}
			if (scrollPos != leftScreen && isScreenValid(rightScreen)) {
				if (isDataLock) {
					if (isEndlessScrollingIfDataLock && rightScreen == leftPosition && isScrollToRight) {
						int offset = (rightPosition - leftPosition + 1) * width;
						canvas.translate(+offset, 0);
						drawChild(canvas, getChildAt(rightScreen), drawingTime);
						canvas.translate(-offset, 0);
					} else {
						drawChild(canvas, getChildAt(rightScreen), drawingTime);
					}
				} else {
					if (isEndlessScrolling && rightScreen == 0 && isScrollToRight) {
						int offset = childCount * width;
						canvas.translate(+offset, 0);
						drawChild(canvas, getChildAt(rightScreen), drawingTime);
						canvas.translate(-offset, 0);
					} else {
						drawChild(canvas, getChildAt(rightScreen), drawingTime);
					}
				}
			}
		}

		if (restore) {
			canvas.restoreToCount(restoreCount);
		}
	}

	private boolean isScreenValid(int screen) {
		if (mCurrentData.isLock()) {
			return screen >= getLeftPagePosition() && screen <= getRightPagePosition();
		} else {
			return screen >= 0 && screen < getChildCount();
		}
	}

	public void snapToScreen(int whichScreen) {
		snapToScreen(whichScreen, 0, false);
	}

	public void snapToScreen(int whichScreen, int durationOffset) {
		snapToScreen(whichScreen, durationOffset, false);
	}

	public void snapToScreen(int whichScreen, int durationOffset, boolean isImmediately) {
		final boolean isDataLock = mCurrentData == null ? false : mCurrentData.isLock();

		final int leftPosition = getLeftPagePosition();
		final int rightPosition = getRightPagePosition();
		if (isDataLock) {
			whichScreen = Math.max(leftPosition + (isEndlessScrollingIfDataLock ? -1 : 0), Math.min(whichScreen, rightPosition + (isEndlessScrollingIfDataLock ? 1 : 0)));
		} else {
			whichScreen = Math.max((isEndlessScrolling ? -1 : 0), Math.min(whichScreen, getChildCount() - (isEndlessScrolling ? 0 : 1)));
		}

		if (getScrollX() != (whichScreen * pageWidth)) {
			mNextScreen = whichScreen;
			final int delta = whichScreen * pageWidth - getScrollX();
			final int duration = mScrollSpeed + durationOffset;
			mScroller.startScroll(getScrollX(), 0, delta, 0, isImmediately ? 0 : duration);
			
			int destToScreen = 0;
			if (isDataLock) {
				if (mNextScreen == leftPosition - 1
						&& isEndlessScrollingIfDataLock) {
					destToScreen = rightPosition;
				} else if (mNextScreen == rightPosition + 1
						&& isEndlessScrollingIfDataLock) {
					destToScreen = leftPosition;
				} else {
					destToScreen = Math.max(leftPosition,
							Math.min(mNextScreen, rightPosition));
				}
			} else {
				if (mNextScreen == -1 && isEndlessScrolling) {
					destToScreen = getChildCount() - 1;
				} else if (mNextScreen == getChildCount() && isEndlessScrolling) {
					destToScreen = 0;
				} else {
					destToScreen = Math.max(0,
							Math.min(mNextScreen, getChildCount() - 1));
				}
			}

			if (lightbar != null)
				lightbar.update(destToScreen);

			/**
			 * 切换数据集
			 */
			int currentDataPosition = list.indexOf(getData(mCurrentScreen));
			int destDataPosition = list.indexOf(getData(destToScreen));
			if (currentDataPosition != destDataPosition) {
				mCurrentData = list.get(destDataPosition);
				lockData(mCurrentData.isLock());
				if (switchDataListener != null) {
					switchDataListener.switchData(list, currentDataPosition,
							destDataPosition);
				}
			}

			invalidate();
		}
	}

	public void scrollLeft() {
		final boolean isDataLock = mCurrentData == null ? false : mCurrentData.isLock();

		final int leftPosition = getLeftPagePosition();
		final int rightPosition = getRightPagePosition();
		int whichScreen = mCurrentScreen - 1;
		if (isDataLock) {
			whichScreen = Math.max(leftPosition + (isEndlessScrollingIfDataLock ? -1 : 0), Math.min(whichScreen, rightPosition + (isEndlessScrollingIfDataLock ? 1 : 0)));
		} else {
			whichScreen = Math.max((isEndlessScrolling ? -1 : 0), Math.min(whichScreen, getChildCount() - (isEndlessScrolling ? 0 : 1)));
		}
		snapToScreen(whichScreen);
	}

	public void scrollRight() {
		final boolean isDataLock = mCurrentData == null ? false : mCurrentData.isLock();

		final int leftPosition = getLeftPagePosition();
		final int rightPosition = getRightPagePosition();
		int whichScreen = mCurrentScreen + 1;
		if (isDataLock) {
			whichScreen = Math.max(leftPosition + (isEndlessScrollingIfDataLock ? -1 : 0), Math.min(whichScreen, rightPosition + (isEndlessScrollingIfDataLock ? 1 : 0)));
		} else {
			whichScreen = Math.max((isEndlessScrolling ? -1 : 0), Math.min(whichScreen, getChildCount() - (isEndlessScrolling ? 0 : 1)));
		}
		snapToScreen(whichScreen);
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
		this.removeAllViews();
		this.list = list;
		if (list.size() == 0)
			return;

		
		reLayout(true);
	}

	/**
	 * 跳转至数据集
	 */
	public void snapToData(ICommonData data) {
		if (data == null)
			return;
		mCurrentData = data;
		mCurrentScreen = getDataPageInfo(mCurrentData)[0];
		lockData(mCurrentData.isLock());
		snapToScreen(mCurrentScreen, 0, true);
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
		} else if (isEndlessScrollingIfDataLockBackup && !isEndlessScrollingIfDataLock && mCurrentData != null || mCurrentData.getPageNum() >= 2) {
			isEndlessScrollingIfDataLock = isEndlessScrollingIfDataLockBackup;
		}
	}

	/**
	 * 获取最左页面position, 在当前数据集非锁定的情况下为0
	 * 
	 * @return
	 */
	private int getLeftPagePosition() {
		if (mCurrentData != null && mCurrentData.isLock()) {
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
		if (mCurrentData != null && mCurrentData.isLock()) {
			return getDataPageInfo(mCurrentData)[1] - 1;
		} else {
			return getChildCount() - 1;
		}
	}

	/**
	 * 刷新CommonSlidingView
	 * 
	 * @param isRelayoutAll
	 *            - true则所有页面重新布局，false则从当前页面开始重新布局
	 */
	public void reLayout(boolean isRelayoutAll) {
		if (isRelayoutAll)
			startPage = 0;
		else
			startPage = mCurrentScreen;
		isLockLayout = false;
		
		
		requestLayout();
	}

	/**
	 * 重新布局view
	 * @param view
	 */
	public static void reLayout(View view)
	{
		ViewParent parentView=view.getParent();
		if(parentView!=null && !parentView.getClass().getName().equals("android.view.ViewRoot"))
		{
			((View)parentView).requestLayout();
			((View)parentView).invalidate();
			reLayout((View)parentView);
		}
	}
	
	/**
	 * 刷新CommonSlidingView
	 * 
	 * @param startPage
	 *            - 从指定页面开始重新布局
	 */
	public void reLayout(int startPage) {
		this.startPage = startPage;
		isLockLayout = false;
		isNeedCallOnLayoutChildrenAfter = true;
		requestLayout();
	}

	/**
	 * 刷新指定数据集，根据新旧数据集页数信息，判断是否刷新后续数据集
	 */
	public void reLayout(ICommonData data, int[] oldPageInfo) {
		int newPageInfo[] = getDataPageInfo(data);
		if (newPageInfo[0] == oldPageInfo[0] && newPageInfo[1] == oldPageInfo[1]) {
			isReLayoutSpecifiedData = true;
			this.startPage = newPageInfo[0];
			isLockLayout = false;
			requestLayout();
		} else {
			reLayout(newPageInfo[0]);
		}
	}

	public void setCommonLightbar(CommonLightbar lightbar) {
		this.lightbar = lightbar;
	}

	protected CommonLayout getNewLayout() {
		return new CommonLayout(getContext());
	}

	protected void removeLayout(int pageNum) {
		removeViewInLayout(getChildAt(pageNum));
		if (pageNum < pageViews.size()) {
			pageViews.remove(pageNum);
		}
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

	public void setSwitchDataListener(SwitchDataListener switchDataListener) {
		this.switchDataListener = switchDataListener;
	}

	public void setOnItemClickListener(OnCommonSlidingViewClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public void setOnLongClickListener(OnCommonSlidingViewLongClickListener onLongClickListener) {
		this.onLongClickListener = onLongClickListener;
	}

	public void setmScrollSpeed(int mScrollSpeed) {
		this.mScrollSpeed = mScrollSpeed;
	}

	public void setEndlessScrolling(boolean isEndlessScrolling) {
		this.isEndlessScrolling = this.isEndlessScrollingIfDataLock = this.isEndlessScrollingIfDataLockBackup = isEndlessScrolling;
	}

	@Override
	public void onClick(View v) {
		if (onClickListener != null) {
			CommonViewHolder viewHolder = (CommonViewHolder) v.getTag();
			ICommonData data = getData(viewHolder.screen);

			onClickListener.onItemClick(v, viewHolder.positionInData, viewHolder.positionInScreen, viewHolder.screen, data);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		CommonViewHolder viewHolder = (CommonViewHolder) v.getTag();
		ICommonData data = getData(viewHolder.screen);

		boolean handled = false;
		handled = onLongClickListener == null ? false : onLongClickListener.onLongClick(v, viewHolder.positionInData, viewHolder.positionInScreen, viewHolder.screen, data);

		mTouchState = TOUCH_STATE_DONE_WAITING;

		return handled;
	}

	protected void onLayoutChildrenAfter() {
		if (isNeedCallOnLayoutChildrenAfter) {
			mCurrentData = getData(mCurrentScreen);
			isNeedCallOnLayoutChildrenAfter = false;
		}
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

	public int getPageWidth() {
		return pageWidth;
	}

	public int getPageHeight() {
		return pageHeight;
	}
}
