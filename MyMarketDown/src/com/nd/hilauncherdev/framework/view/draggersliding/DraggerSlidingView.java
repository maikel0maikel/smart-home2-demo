package com.nd.hilauncherdev.framework.view.draggersliding;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.framework.view.commonsliding.CommonSlidingView;
import com.nd.hilauncherdev.framework.view.commonsliding.CommonViewHolder;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.nd.hilauncherdev.framework.view.draggersliding.datamodel.IDraggerData;
import com.nd.hilauncherdev.launcher.DragController;
import com.nd.hilauncherdev.launcher.DragScroller;
import com.nd.hilauncherdev.launcher.DragSource;
import com.nd.hilauncherdev.launcher.DragView;
import com.nd.hilauncherdev.launcher.DropTarget;

/**
 * 元素可拖动的CommonSlidingView (不允许跨数据集拖动)
 * 
 * @author Anson
 */
public abstract class DraggerSlidingView extends CommonSlidingView implements
		DragScroller, DragSource, DropTarget {

	private static final int ALIGN_LEFT = 0;

	private static final int ALIGN_CENTER = 1;

	private static final int ALIGN_RIGHT = 2;

	private int[] dragViewXY = new int[2];

	/**
	 * 拖动控制对象
	 */
	protected DragController mDragController;

	/**
	 * 拖动的原始View
	 */
	protected View mOriginalView;
	
	/**
	 * 拖动的View
	 */
	protected DragView mDragView;

	/**
	 * 拖拉对象
	 */
	protected Object mDragInfo;
	
	/**
	 * 开始拖拉时屏幕位置(拖拽的view位置变化后，会发生改变)
	 */
	protected int mOriginalScreen;

	/**
	 * 拖动View在所在页面的位置(拖拽的view位置变化后，会发生改变)
	 */
	protected int mOriginalViewPositionInOriginalPage;

	/**
	 * 拖拉View在所在数据集中的位置(拖拽的view位置变化后，会发生改变)
	 */
	protected int mOriginalViewPosition;
	
	/**
	 * 开始拖拉时屏幕位置(拖拽的view位置变化后，不会发生改变)
	 */
	protected int mOriginalFixedScreen;
	
	/**
	 * 拖动View在所在页面的位置(拖拽的view位置变化后，不会发生改变)
	 */
	protected int mOriginalViewFixedPositionInOriginalPage;
	
	/**
	 * 拖拉View在所在数据集中的位置(拖拽的view位置变化后，不会发生改变)
	 */
	protected int mOriginalViewFixedPosition;

	/**
	 * 目标View
	 */
	protected View mTargetView;

	/**
	 * 上次经过的View
	 */
	protected View mLastView;

	/**
	 * 目标View的位置
	 */
	private int mTargetViewPosition = -1;

	/**
	 * 上一次拖拉停留的位置
	 */
	private int mLastViewPosition = -1;

	/**
	 * 上一次拖拉停留相对图标的位置
	 */
	private int mLastAlign = -1;

	/**
	 * 动画的延迟时间
	 */
	protected int moveDelay = 200;

	/**
	 * 图标重合在X方向的偏差
	 */
	protected int mergeOffsetX;

	/**
	 * 图标重合在y方向的偏差
	 */
	protected int mergeOffsetY;

	/**
	 * 动画是否正在运行的标识
	 */
	private boolean mAnimationRunning = false;

	/**
	 * 上一次是否停留在图标正上方
	 */
	protected boolean mLastDragOverCenter = false;
	
	/**
	 * 上一次触发onDragOver时的坐标X
	 */
	private int mLastDragOverX;
	
	/**
	 * 上一次触发onDragOver时的坐标Y
	 */
	private int mLastDragOverY;
	
	/**
	 * 移动动画是否可被添加执行
	 */
	private boolean isAniTaskCanbeExecute = true;

	private AnimationTask mAnimationTask = null;

	public DraggerSlidingView(Context context) {
		super(context);
	}

	public DraggerSlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DraggerSlidingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void initSelf(Context context) {
		mergeOffsetX = mergeOffsetY = 0;
	}

	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		if (!acceptDrop(source, x, y, xOffset, yOffset, dragView, dragInfo)) {
			return;
		}
		removeAnimationTask();
		mTargetView = findViewByDraggerCoordinate(x, y, xOffset, yOffset, dragView);
		if (!mLastDragOverCenter) {			
			checkDrop(mOriginalView, mTargetView, x, y);
			return;
		}
		DraggerLayout currentLayout = (DraggerLayout) getChildAt(getCurrentScreen());
		mTargetViewPosition = currentLayout.findViewIndexInParentByXY(x, y);
		drop(mOriginalView, mTargetView, dragView, x, y);
		mTargetViewPosition = -1;
		mLastDragOverCenter = false;
	}

	protected void drop(View originalView, View targetView, DragView dragView, int x, int y) {
		checkDrop(originalView, targetView, x, y);
	}

	private void checkDrop(View originalView, View targetView, int x, int y) {
		if (null == targetView) {
			DraggerLayout currentLayout = (DraggerLayout) getChildAt(getCurrentScreen());
			if (currentLayout.hasEmptyPosition()) {
				if (mOriginalScreen == getCurrentScreen()
						&& isViewContainsXY(originalView, x, y)) {
					return;
				}
				mTargetViewPosition = currentLayout.getChildCount() - 1;
				handleMoveAnimation();
				return;
			}
		}
	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		
		int oldX = mLastDragOverX;
		int oldY = mLastDragOverY;
		mLastDragOverX = x;
		mLastDragOverY = y;
		
		if (!acceptDragOver(source, x, y, xOffset, yOffset, dragView, dragInfo)) {
			return;
		}

		if (mAnimationRunning) {
			removeAnimationTask();
			return;
		}

		if (!mScroller.isFinished()) { // 滚动状态不执行动画
			removeAnimationTask();
			return;
		}

		if (isScrollZoneRange(x)) { // 在滚动区域范围时则不执行动画
			removeAnimationTask();
			return;
		}		
		
		if (Math.abs(x - oldX) >= 5 || Math.abs(y - oldY) >= 5) { // 相邻两次位移较大则不执行动画
			removeAnimationTask();			
			return;
		}
		
		mDragView = dragView;

		View view = findViewByDraggerCoordinate(x, y, xOffset, yOffset, dragView);
		mTargetView = view;

		if (null == view) { // 当所在位置的View为null，则退出动画
			mLastViewPosition = -1;
			removeAnimationTask();
			checkQuitMergeFolder(mOriginalView, mLastView, dragView);
			mLastView = null;
			return;
		}
		if (view == mOriginalView)
			return; // 两View相同，则不移动

		if (mLastView != view) { // 如果目标View跟上次经过的View不一样，则移除动画
			removeAnimationTask();
			checkQuitMergeFolder(mOriginalView, mLastView, dragView);
		}

		Rect outRect = new Rect();
		view.getHitRect(outRect);
		int align = -1;
		dragViewXY[0] = x - xOffset + dragView.getWidth() / 2;
		dragViewXY[1] = y - yOffset + dragView.getHeight() / 2;
		if (checkMayMerge(outRect, dragViewXY)) { // 表示正上方
			align = ALIGN_CENTER;
		} else {
			checkQuitMergeFolder(mOriginalView, view, dragView);
			if (dragViewXY[0] < outRect.centerX()) { // 在左边
				align = ALIGN_LEFT;
			} else { // 在右边
				align = ALIGN_RIGHT;
			}
		}

		int targetIndex = findTargetViewIndexByDraggerCoordinate(x, y, xOffset,
				yOffset, dragView);
		mTargetViewPosition = targetIndex;
		if (mTargetViewPosition == mLastViewPosition
				&& mTargetView == mLastView && mLastAlign == align 
				&& !isAniTaskCanbeExecute) {
			return;
		}
		mLastViewPosition = targetIndex;
		mLastAlign = align;
		removeAnimationTask();
		mAnimationTask = new AnimationTask(view, align, getCurrentScreen());
		handler.postDelayed(mAnimationTask, moveDelay);
		isAniTaskCanbeExecute = false;
		mLastView = mTargetView;
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		removeAnimationTask();
		Boolean isDragExitInActionMove = (Boolean) dragView.getTag(R.id.drager_controller_on_drag_exit_in_action_move);
		mTargetView = findViewByDraggerCoordinate(x, y, xOffset, yOffset, dragView);
		if (mLastDragOverCenter && (mLastView != mTargetView || isDragExitInActionMove)) {
			/**
			 * 将拖拽图标移出图标区时 或 在多点触控滑屏放手后移除之前存在的图标合并动画
			 */
			checkQuitMergeFolder(mOriginalView, mLastView, dragView);
		}
	}

	@Override
	public void onDropCompleted(View target, boolean success) {
		CommonViewHolder holder = (CommonViewHolder) mOriginalView.getTag(R.id.common_view_holder);
		if (success) {
			onDataChanged(mCurrentData, holder);
		}
		//清除多选目标
		/*
		if(mDragController.getAppList() != null){
			mDragController.getAppList().clear();
		}
		*/
	}
	
	/**
	 * 判断是否允许放置
	 */
	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		return acceptDragOver(source, x, y, xOffset, yOffset, dragView,
				dragInfo) && (mOriginalView != mTargetView);
	}

	/**
	 * 判断是否触发OnDragOver方法
	 */
	public boolean acceptDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		
		/**
		 * 不允许跨数据集拖拽
		 */
		boolean isAcceptA = mCurrentData.getDataList().contains(dragInfo);

		/**
		 * 当前数据集是否允许拖拽放置，默认不允许
		 */
		boolean isAcceptB = (mCurrentData instanceof IDraggerData) ? ((IDraggerData) mCurrentData)
				.isAcceptDrop() : false;

		return isAcceptA && isAcceptB;
	}

	@Override
	public void setDragController(DragController dragger) {
		this.mDragController = dragger;
	}

	public void startDrag(View view, int positionInData, int positionInScreen,
			Object dragInfo) {
		startDrag(view, positionInData, positionInScreen, dragInfo, null);
	}
	
	public void startDrag(View view, int positionInData, int positionInScreen,
			Object dragInfo, ArrayList<DraggerChooseItem> list) {
		mOriginalScreen = mOriginalFixedScreen = getCurrentScreen();
		mOriginalView = view;
		mDragInfo = dragInfo;

		mOriginalViewPositionInOriginalPage = mOriginalViewFixedPositionInOriginalPage = positionInScreen;
		mOriginalViewPosition = mOriginalViewFixedPosition = positionInData;
		mDragController.startDrag(view, this, dragInfo,
				DragController.DRAG_ACTION_MOVE, list);
	}

	/**
	 * 删除移动动画
	 */
	private void removeAnimationTask() {
		if (mAnimationTask != null) {
			handler.removeCallbacks(mAnimationTask);
			mAnimationTask = null;
			isAniTaskCanbeExecute = true;
		}		
	}

	/**
	 * 判断是否在边界滚动范围内
	 * 
	 * @param x
	 * @return
	 */
	private boolean isScrollZoneRange(int x) {
		int width = this.getWidth();
		int scrollZoneWidth = mDragController.getScrollZone();
		if (x <= scrollZoneWidth)
			return true;
		if (x > (width - scrollZoneWidth))
			return true;
		return false;
	}

	@Override
	protected DraggerLayout getNewLayout() {
		return new DraggerLayout(getContext(), this);
	}

	/**
	 * 根据x、y坐标获取当前点在容器中所在的位置
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isViewContainsXY(View v, int x, int y) {
		if (v == null)
			return false ;
		final Rect frame = new Rect();
		v.getHitRect(frame);
		if (frame.contains(x, y)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取拖拉view所在的View
	 * 
	 * @param x
	 * @param y
	 * @param xOffset
	 * @param yOffset
	 */
	protected View findViewByDraggerCoordinate(int x, int y, int xOffset,
			int yOffset, View dragView) {
		DraggerLayout currentLayout = (DraggerLayout) getChildAt(getCurrentScreen());
		int fx = x - xOffset + (dragView == null ? 0 : dragView.getWidth() / 2);
		int fy = y - yOffset + (dragView == null ? 0 : dragView.getHeight() / 2);
		View view = currentLayout.findViewByXY(fx, fy);
		if (view == null) {
			view = currentLayout.findViewByXY(x - xOffset, fy);
		}
		return view;
	}

	/**
	 * 找到目标View在集合中的位置
	 * 
	 * @param x
	 * @param y
	 * @param xOffset
	 * @param yOffset
	 * @return
	 */
	protected int findTargetViewIndexByDraggerCoordinate(int x, int y,
			int xOffset, int yOffset, View dragView) {
		DraggerLayout currentLayout = (DraggerLayout) getChildAt(getCurrentScreen());
		int fx = x - xOffset + (dragView == null ? 0 : dragView.getWidth() / 2);
		int fy = y - yOffset + (dragView == null ? 0 : dragView.getHeight() / 2);
		int index = currentLayout.findViewIndexInParentByXY(fx, fy);
		if (index == -1) {
			index = currentLayout.findViewIndexInParentByXY(x - xOffset, fy);
		}
		return index;
	}

	/**
	 * 检测两View是否能合并
	 * 
	 * @param srcRect
	 * @param dragViewCenterXY
	 * @return
	 */
	private boolean checkMayMerge(Rect srcRect, int[] dragViewXY) {
		int centerX = srcRect.centerX();
		int centerY = srcRect.centerY();
		return (Math.abs(centerX - dragViewXY[0]) <= mergeOffsetX)
				&& (Math.abs(centerY - dragViewXY[1]) <= mergeOffsetY);
	}

	/**
	 * 用户退出文件夹合并时进行的清除
	 * 
	 * @param view
	 */
	private void checkQuitMergeFolder(View originalView, View targetView, DragView dragView) {
		if (mLastDragOverCenter) {
			onMergeOutOfRange(originalView, targetView, dragView);
			mLastDragOverCenter = false;
		}
	}

	private class AnimationTask implements Runnable {

		private View mTargetView;

		private int align;
		
		private int currentScreen;

		AnimationTask(View _targetView, int _align, int _currentScreen) {
			mTargetView = _targetView;
			align = _align;
			currentScreen = _currentScreen;
		}

		@Override
		public void run() {
			if (currentScreen != getCurrentScreen()) {
				/**
				 * 若触发拖拽时的当前屏与现在的当前屏不是同一屏，则不做任何事情
				 */
				return;
			}
			mAnimationRunning = true;
			switch (align) {
			case ALIGN_LEFT:				
				dragOverLeft();
				break;
			case ALIGN_CENTER:
				mLastDragOverCenter = true;
				onDragCenter(mOriginalView, mTargetView, mDragView);
				break;
			case ALIGN_RIGHT:
				dragOverRight();
				break;
			}
			mAnimationRunning = false;
		}
	}

	/**
	 * 拖动的View在目标View的左边时会调用此方法
	 */
	private void dragOverLeft() {
		int actualIndex = mTargetViewPosition;
		if ((getCurrentScreen() == mOriginalScreen && mOriginalViewPositionInOriginalPage < mTargetViewPosition)
				|| getCurrentScreen() > mOriginalScreen) {
			actualIndex = mTargetViewPosition - 1;
			if (actualIndex == -1
					|| (getCurrentScreen() == mOriginalScreen && actualIndex == mOriginalViewPositionInOriginalPage))
				return;
		}
		mTargetViewPosition = actualIndex;
		handleMoveAnimation();
	}

	/**
	 * 拖动的View在目标View的右边时会调用此方法
	 * 
	 * @param targetView
	 * @param targetPosition
	 * @param dragInfo
	 */
	private void dragOverRight() {
		int actualIndex = mTargetViewPosition;
		if ((getCurrentScreen() == mOriginalScreen && mOriginalViewPositionInOriginalPage > mTargetViewPosition)
				|| getCurrentScreen() < mOriginalScreen) {
			int columnNum = mCurrentData.getColumnNum() > 0 ? mCurrentData
					.getColumnNum() : 1;
			int rowNum = mCurrentData.getRowNum() > 0 ? mCurrentData
					.getRowNum() : 1;
			int maxCount = columnNum * rowNum;
			actualIndex = mTargetViewPosition + 1;
			if (actualIndex == maxCount
					|| (getCurrentScreen() == mOriginalScreen && actualIndex == mOriginalViewPositionInOriginalPage))
				return;
		}
		mTargetViewPosition = actualIndex;
		handleMoveAnimation();
	}

	/**
	 * 拖动的View在目标View的正上方时会调用此方法
	 * 
	 * @param targetView
	 */
	protected void onDragCenter(View originalView, View targetView, DragView mDragView) {
		// TODO Auto-generated method stub
	}

	/**
	 * 封装移动动画逻辑
	 */
	protected void handleMoveAnimation() {
		DraggerLayout originalLayout = (DraggerLayout) getChildAt(mOriginalScreen);
		DraggerLayout currentLayout = (DraggerLayout) getChildAt(getCurrentScreen());
		
		if (originalLayout == null || currentLayout == null || mOriginalView == null) {
			return;
		}
		
		originalLayout.removeView(mOriginalView);
		if (mOriginalScreen != getCurrentScreen()) {
			if (getCurrentScreen() < mOriginalScreen) { // 向前拖动
				for (int screen = getCurrentScreen(); screen < mOriginalScreen; screen++) {
					currentLayout = (DraggerLayout) getChildAt(screen);
					DraggerLayout nextLayout = (DraggerLayout) getChildAt(screen + 1);
					View view = currentLayout.getChildAt(currentLayout
							.getChildCount() - 1);
					currentLayout.removeView(view);
					if (screen == getCurrentScreen()) {
						currentLayout.addView(mOriginalView,
								mTargetViewPosition);
						((CommonViewHolder) mOriginalView.getTag(R.id.common_view_holder)).screen = screen;
						currentLayout.startViewAnimation(mOriginalView);
					}
					nextLayout.addView(view, 0);
					nextLayout.reLayout();
				}
			} else { // 向后拖动
				for (int screen = getCurrentScreen(); screen > mOriginalScreen; screen--) {
					currentLayout = (DraggerLayout) getChildAt(screen);
					DraggerLayout previousLayout = (DraggerLayout) getChildAt(screen - 1);
					View view = currentLayout.getChildAt(0);
					currentLayout.removeView(view);
					if (screen == getCurrentScreen()) {
						currentLayout.addView(mOriginalView,
								mTargetViewPosition);
						((CommonViewHolder) mOriginalView.getTag(R.id.common_view_holder)).screen = screen;
						currentLayout.startViewAnimation(mOriginalView);
					} else {
						currentLayout.reLayout();
					}
					previousLayout.addView(view, -1);
					previousLayout.reLayout();
				}
			}
		} else {
			currentLayout.addView(mOriginalView, mTargetViewPosition);
			currentLayout.startViewAnimation(mOriginalView);
		}
		mOriginalScreen = getCurrentScreen();
		mOriginalViewPositionInOriginalPage = mTargetViewPosition;
		mTargetViewPosition = -1;
	}
	
	/**
	 * 封装回退动画逻辑
	 */
	protected void handleFallbackAnimation(boolean animate) {
		DraggerLayout originalLayout = (DraggerLayout) getChildAt(mOriginalFixedScreen);
		DraggerLayout currentLayout = (DraggerLayout) getChildAt(mOriginalScreen);
		
		if (originalLayout == null || currentLayout == null || mOriginalView == null) {
			return;
		}
		
		currentLayout.removeView(mOriginalView);
		if (mOriginalFixedScreen != mOriginalScreen) {
			if (mOriginalScreen < mOriginalFixedScreen) { // 向前拖动后回退		
				for (int screen = mOriginalFixedScreen; screen > mOriginalScreen; screen--) {
					currentLayout = (DraggerLayout) getChildAt(screen);
					DraggerLayout previousLayout = (DraggerLayout) getChildAt(screen - 1);
					View view = currentLayout.getChildAt(0);
					currentLayout.removeView(view);
					previousLayout.addView(view);
					if (screen == mOriginalFixedScreen) {
						currentLayout.addView(mOriginalView, mOriginalViewFixedPositionInOriginalPage);
						((CommonViewHolder) mOriginalView.getTag(R.id.common_view_holder)).screen = screen;
					}						
					currentLayout.reLayout();
					if (screen == mOriginalScreen + 1) {		
						if (screen == getCurrentScreen() + 1) {
							previousLayout.startViewAnimation(previousLayout.getChildAt(previousLayout.getChildCount() - 1), null, true, pageWidth);
						} else {
							previousLayout.reLayout();
						}
					}
				}
			} else { // 向后拖动后回退				
				for (int screen = mOriginalFixedScreen; screen < mOriginalScreen; screen++) {
					currentLayout = (DraggerLayout) getChildAt(screen);
					DraggerLayout nextLayout = (DraggerLayout) getChildAt(screen + 1);
					View view = currentLayout.getChildAt(currentLayout.getChildCount() - 1);
					currentLayout.removeView(view);
					nextLayout.addView(view, 0);
					if (screen == mOriginalFixedScreen) {
						currentLayout.addView(mOriginalView, mOriginalViewFixedPositionInOriginalPage);
						((CommonViewHolder) mOriginalView.getTag(R.id.common_view_holder)).screen = screen;
					}						
					currentLayout.reLayout();
					if (screen == mOriginalScreen - 1) {		
						if (screen == getCurrentScreen() - 1) {
							nextLayout.startViewAnimation(nextLayout.getChildAt(0), null, true, -pageWidth);
						} else {
							nextLayout.reLayout();
						}
					}
				}
			}
		} else {
			currentLayout.addView(mOriginalView, mOriginalViewFixedPositionInOriginalPage);
			if (animate && mOriginalScreen == getCurrentScreen()) {
				currentLayout.startViewAnimation(mOriginalView);
			} else {
				currentLayout.reLayout();
			}
		}
	}

	public void setMergeOffsetX(int mergeOffsetX) {
		this.mergeOffsetX = mergeOffsetX;
	}

	public void setMergeOffsetY(int mergeOffsetY) {
		this.mergeOffsetY = mergeOffsetY;
	}
	
	/**
	 * 在合并范围外
	 * 
	 * @param targetView
	 */
	public void onMergeOutOfRange(View originalView, View targetView, DragView dragView) {
		
	}

	/**
	 * 数据发生变化时会调用此方法
	 */
	public abstract void onDataChanged(ICommonData data, CommonViewHolder originalViewHolder);
	
}
