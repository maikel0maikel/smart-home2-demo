package com.nd.hilauncherdev.framework.view.draggersliding;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.nd.android.pandahome2.R;
import com.nd.hilauncherdev.framework.view.commonsliding.CommonLayout;
import com.nd.hilauncherdev.framework.view.commonsliding.CommonViewHolder;
import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonData;

/**
 * 
 * @author Anson
 */
public class DraggerLayout extends CommonLayout {

	protected DraggerSlidingView workspace;

	public DraggerLayout(Context context, DraggerSlidingView workspace) {
		super(context,workspace);
		this.workspace = workspace;
	}

	/**
	 * 根据x、y坐标获取当前点在容器中所在的位置
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int findViewIndexInParentByXY(int x, int y) {
		final Rect frame = new Rect();
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if ((child.getVisibility()) == VISIBLE || child.getAnimation() != null) {
				child.getHitRect(frame);
				if (frame.contains(x, y)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 根据x、y坐标获取当前点在容器中所在的位置
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public View findViewByXY(int x, int y) {
		final Rect frame = new Rect();
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if ((child.getVisibility()) == VISIBLE || child.getAnimation() != null) {
				child.getHitRect(frame);
				if (frame.contains(x, y)) {
					return child;
				}
			}
		}
		return null;
	}

	public boolean hasEmptyPosition() {
		if (workspace == null) {
			return false;
		}
		int pageNum = (Integer) getTag();
		ICommonData data = workspace.getData(pageNum);
		if (data == null) {
			return false;
		}
		int rowNum = data.getRowNum() > 0 ? data.getRowNum() : 1;
		int columnNum = data.getColumnNum() > 0 ? data.getColumnNum() : 1;
		if (this.getChildCount() < rowNum * columnNum) {
			return true;
		}
		return false;
	}

	public void reLayout() {
		reLayout(null);
	}
	
	public void reLayout(View reacquireView) {
		if (workspace == null) {
			return;
		}

		setDrawingCacheEnabled(false);

		int pageNum = (Integer) getTag();
		ICommonData data = workspace.getData(pageNum);
		int[] pageInfo = workspace.getDataPageInfo(data);

		/**
		 * 实际图标区宽度
		 */
		final int actualWidth = getWidth() - workspace.getPaddingLeft() - workspace.getPaddingRight();
		/**
		 * 实际图标区高度
		 */
		final int actualHeight = getHeight() - workspace.getPaddingTop() - workspace.getPaddingBottom();

		/**
		 * 当前图标左边界位置
		 */
		int x = workspace.getPaddingLeft();

		/**
		 * 当前图标上边界位置
		 */
		int y = workspace.getPaddingTop();
		
		int columnNum = data.getColumnNum() > 0 ? data.getColumnNum() : 1;
		int columnWidth = actualWidth / columnNum;
		int childViewWidth = data.getChildViewWidth() > 0 ? data.getChildViewWidth() : 0;
		columnWidth = data.isKeepChildViewWidthAndHeight() ? childViewWidth : columnWidth > childViewWidth ? childViewWidth : columnWidth;
		
		int rowNum = data.getRowNum() > 0 ? data.getRowNum() : 1;
		int rowHeight = actualHeight / rowNum;
		int childViewHeight = data.getChildViewHeight() > 0 ? data.getChildViewHeight() : 0;
		rowHeight = data.isKeepChildViewWidthAndHeight() ? childViewHeight : rowHeight > childViewHeight ? childViewHeight : rowHeight;
		
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

		int count = getChildCount();

		for (int i = 0; i < rows; i++) {
			if (rowPadding >= 0 || (rowPadding < 0 && i > 0)) {
				y += rowPadding;
			}
			for (int j = 0; j < columns; j++, position++) {
				if (i * columns + j >= count) {
					break;
				}
				View child = getChildAt(i * columns + j);
				child.clearAnimation();

				if (child == reacquireView) {
					child = workspace.onGetItemView(data, position);
					if (child == null)
						child = new TextView(getContext());

					child.setLayoutParams(p);
					child.measure(childWidthSpec, childHeightSpec);

					CommonViewHolder viewHolder = (CommonViewHolder) reacquireView.getTag(R.id.common_view_holder);
					viewHolder.positionInData = position;
					viewHolder.positionInScreen = position - rows * columns * (pageNum - pageInfo[0]);
					viewHolder.screen = pageNum;
					viewHolder.item = data.getDataList().get(position);
					viewHolder.item.setPosition(position);					
					child.setTag(R.id.common_view_holder, viewHolder);

					child.setOnClickListener(workspace);
					child.setOnLongClickListener(workspace);
					child.setHapticFeedbackEnabled(false);

					child.layout(reacquireView.getLeft(), reacquireView.getTop(), reacquireView.getRight(), reacquireView.getBottom());

					removeViewInLayout(reacquireView);
					addViewInLayout(child, i * columns + j, null, true);
				} else {
					CommonViewHolder viewHolder = (CommonViewHolder) child.getTag(R.id.common_view_holder);
					viewHolder.positionInData = position;
					viewHolder.positionInScreen = position - rows * columns * (pageNum - pageInfo[0]);
					viewHolder.screen = pageNum;
					viewHolder.item.setPosition(position);
				}

				if (columnPadding >= 0 || (columnPadding < 0 && j > 0)) {
					x += columnPadding;
				}
				int left = x;
				int top = y;
				int w = columnWidth;
				int h = rowHeight;
				child.layout(left, top, left + w, top + h);

				x += columnWidth;
			}
			x = workspace.getPaddingLeft();
			y += rowHeight;
		}
	}

	public void startViewAnimation(View startView) {
		startViewAnimation(startView, null, false, 0);
	}

	public void startViewAnimation(View startView, View reacquireView, boolean fromScreen, int offsetX) {
		if (workspace == null) {
			return;
		}

		int pageNum = (Integer) getTag();
		ICommonData data = workspace.getData(pageNum);
		int[] pageInfo = workspace.getDataPageInfo(data);

		/**
		 * 实际图标区宽度
		 */
		final int actualWidth = getWidth() - workspace.getPaddingLeft() - workspace.getPaddingRight();
		/**
		 * 实际图标区高度
		 */
		final int actualHeight = getHeight() - workspace.getPaddingTop() - workspace.getPaddingBottom();

		/**
		 * 当前图标左边界位置
		 */
		int x = workspace.getPaddingLeft();

		/**
		 * 当前图标上边界位置
		 */
		int y = workspace.getPaddingTop();

		int columnNum = data.getColumnNum() > 0 ? data.getColumnNum() : 1;
		int columnWidth = actualWidth / columnNum;
		int childViewWidth = data.getChildViewWidth() > 0 ? data.getChildViewWidth() : 0;
		columnWidth = data.isKeepChildViewWidthAndHeight() ? childViewWidth : columnWidth > childViewWidth ? childViewWidth : columnWidth;

		int rowNum = data.getRowNum() > 0 ? data.getRowNum() : 1;
		int rowHeight = actualHeight / rowNum;
		int childViewHeight = data.getChildViewHeight() > 0 ? data.getChildViewHeight() : 0;
		rowHeight = data.isKeepChildViewWidthAndHeight() ? childViewHeight : rowHeight > childViewHeight ? childViewHeight : rowHeight;

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

		int count = getChildCount();

		for (int i = 0; i < rows; i++) {
			if (rowPadding >= 0 || (rowPadding < 0 && i > 0)) {
				y += rowPadding;
			}
			for (int j = 0; j < columns; j++, position++) {
				if (i * columns + j >= count) {
					break;
				}
				View child = getChildAt(i * columns + j);
				child.clearAnimation();

				if (child == reacquireView) {
					child = workspace.onGetItemView(data, position);
					if (child == null)
						child = new TextView(getContext());

					child.setLayoutParams(p);
					child.measure(childWidthSpec, childHeightSpec);

					CommonViewHolder viewHolder = (CommonViewHolder) reacquireView.getTag(R.id.common_view_holder);
					viewHolder.positionInData = position;
					viewHolder.positionInScreen = position - rows * columns * (pageNum - pageInfo[0]);
					viewHolder.screen = pageNum;
					viewHolder.item = data.getDataList().get(position);
					viewHolder.item.setPosition(position);					
					child.setTag(R.id.common_view_holder, viewHolder);

					child.setOnClickListener(workspace);
					child.setOnLongClickListener(workspace);
					child.setHapticFeedbackEnabled(false);

					child.layout(reacquireView.getLeft(), reacquireView.getTop(), reacquireView.getRight(), reacquireView.getBottom());

					removeViewInLayout(reacquireView);
					addViewInLayout(child, i * columns + j, null, true);
				} else {
					CommonViewHolder viewHolder = (CommonViewHolder) child.getTag(R.id.common_view_holder);
					viewHolder.positionInData = position;
					viewHolder.positionInScreen = position - rows * columns * (pageNum - pageInfo[0]);
					viewHolder.screen = pageNum;
					viewHolder.item.setPosition(position);
				}

				if (columnPadding >= 0 || (columnPadding < 0 && j > 0)) {
					x += columnPadding;
				}
				int left = x;
				int top = y;
				int w = columnWidth;
				int h = rowHeight;

				if (child == startView) {
					if (fromScreen && offsetX != 0) {
						translate(child, child.getLeft(), left, child.getTop(), top, w, h, fromScreen, offsetX);
					} else {
						child.layout(left, top, left + w, top + h);
					}
				} else {
					translate(child, child.getLeft(), left, child.getTop(), top, w, h);
				}
				x += columnWidth;
			}
			x = workspace.getPaddingLeft();
			y += rowHeight;
		}
	}

	/**
	 * 移动图标
	 * 
	 * @param v
	 * @param fromXDelta
	 * @param toXDelta
	 * @param fromYDelta
	 * @param toYDelta
	 */
	public void translate(View v, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, int width, int height) {
		translate(v, fromXDelta, toXDelta, fromYDelta, toYDelta, width, height, false, 0);
	}

	/**
	 * 移动图标
	 */
	public void translate(View v, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, int width, int height, boolean fromScreen, int offsetX) {
		RecordTranslateAnimation ani = (RecordTranslateAnimation) v.getAnimation();
		float fromX = fromXDelta;
		float fromY = fromYDelta;
		if (ani != null && v.getVisibility() != GONE) {
			fromX = ani.toX;
			fromY = ani.toY;
			v.clearAnimation();
			v.layout((int) fromX, (int) fromY, (int) fromX + width, (int) fromY + height);
		}

		if (toXDelta - fromX == 0 && toYDelta - fromY == 0) {
			return;
		}

		int startX = 0;
		if (fromScreen) {
			startX = offsetX;
		}
		TranslateAnimation trans = new RecordTranslateAnimation(startX, toXDelta - fromX, 0, toYDelta - fromY, toXDelta, toYDelta);
		trans.setInterpolator(new AccelerateDecelerateInterpolator());
		trans.setDuration(200);
		trans.setAnimationListener(new RecordTranslateAnimationListener(v, width, height));
		v.startAnimation(trans);
	}

	public static class RecordTranslateAnimation extends TranslateAnimation {
		public float toX;
		public float toY;

		public RecordTranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, float toX, float toY) {
			super(fromXDelta, toXDelta, fromYDelta, toYDelta);
			this.toX = toX;
			this.toY = toY;
		}
	}

	public class RecordTranslateAnimationListener implements AnimationListener {

		private View view;

		private int width;

		private int height;

		public RecordTranslateAnimationListener(View view, int width, int height) {
			this.view = view;
			this.width = width;
			this.height = height;
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			RecordTranslateAnimation ani = (RecordTranslateAnimation) animation;
			if (view.getVisibility() != GONE) {
				float toX = ani.toX;
				float toY = ani.toY;
				view.clearAnimation();
				view.layout((int) toX, (int) toY, (int) toX + width, (int) toY + height);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

	}
}
