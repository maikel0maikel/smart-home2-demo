package com.nd.hilauncherdev.drawer.view;

import android.content.Context;
import android.graphics.Canvas;

import com.nd.hilauncherdev.framework.view.draggersliding.DraggerLayout;
import com.nd.hilauncherdev.framework.view.draggersliding.DraggerSlidingView;
import com.nd.hilauncherdev.kitset.GpuControler;

/**
 *
 * @author Anson
 */
public class DrawerLayout extends DraggerLayout {
	
	public DrawerLayout(Context context, DraggerSlidingView workspace) {
		super(context, workspace);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

	}
	
	public void enableHardwareLayers() {
		GpuControler.enableHardwareLayers(this);
	}
	
    public void destroyHardwareLayer() {
    	GpuControler.destroyHardwareLayer(this);
    }
}
