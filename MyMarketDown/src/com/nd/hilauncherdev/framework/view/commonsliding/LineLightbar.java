package com.nd.hilauncherdev.framework.view.commonsliding;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonData;
import com.nd.hilauncherdev.theme.assit.ThemeUIRefreshListener;

/**
 * 类似4.0桌面的屏幕指示灯
 */
public class LineLightbar extends View implements ThemeUIRefreshListener {
	private int size, hlLineWidth, lineHeight, hlLineHeight, lineTop, hlLineTop;
	private float distanceScale;
	private Drawable lines[], hlLine;
	private int width, height;
	private CommonSlidingView slidingView;

	public LineLightbar(Context context) {
		super(context);
		init();
	}

	public LineLightbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LineLightbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		applyTheme();
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		//ThemeUIRefreshAssit.getInstance().registerRefreshListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		//ThemeUIRefreshAssit.getInstance().unregisterRefreshListener(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);

		lineTop = (height - lineHeight) / 2;
		hlLineTop = (height - hlLineHeight) / 2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (lines[0] != null) {
			lines[0].setBounds(0, lineTop, width, lineTop + lineHeight);
			lines[0].draw(canvas);
		}
		if (slidingView != null) {
			List<ICommonData> list = slidingView.getList();
			if (list != null && list.size() > 1) {
				for (int i = 1; i < list.size(); i++) {
					if (i > lines.length - 1) {
						break;
					}
					if (lines[i] == null) {
						continue;
					}
					ICommonData data = list.get(i);
					int[] pageInfo = slidingView.getDataPageInfo(data);
					lines[i].setBounds((int) (pageInfo[0] * width * distanceScale), lineTop, (int) (pageInfo[1] * width * distanceScale), lineTop + lineHeight);
					lines[i].draw(canvas);
				}
			}
		}

		hlLine.draw(canvas);
	}

	public void refresh(CommonSlidingView slidingView, int scrollX) {
		this.slidingView = slidingView;
		this.size = slidingView.getPageCount();
		distanceScale = 1.0f / this.size;

		hlLineWidth = width / this.size;

		update(scrollX);
	}

	public void update(int scrollX) {
		if (slidingView != null && slidingView.getWidth() != 0) {
			final int left = (int) (scrollX * ((float) width / slidingView.getWidth()) * distanceScale);
			hlLine.setBounds(left, hlLineTop, left + hlLineWidth, hlLineTop + hlLineHeight);
			invalidate();
		}
	}

	public void setLines(Drawable[] lines) {
		if (lines == null)
			return;
		this.lines = new Drawable[lines.length];
		for (int i = 0; i < lines.length; i++) {
			this.lines[i] = lines[i];
		}
		lineHeight = lines[0].getIntrinsicHeight();
	}

	public void setHighLine(Drawable hlLine) {
		if (hlLine == null)
			return;
		this.hlLine = hlLine;
		hlLineHeight = hlLine.getIntrinsicHeight();
	}

	@Override
	public void applyTheme() {
		Drawable[] lines = new Drawable[3];
		//lines[0] = ThemeManager.getThemeDrawable(ThemeData.LAUNCHER_LIGHT_LINE);
		setLines(lines);
		//setHighLine(ThemeManager.getThemeDrawable(ThemeData.LAUNCHER_LIGHT_HL));
	}
	
}
