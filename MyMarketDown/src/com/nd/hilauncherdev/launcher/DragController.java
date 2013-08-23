/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nd.hilauncherdev.launcher;

import java.util.ArrayList;

import android.view.KeyEvent;
import android.view.View;

import com.nd.hilauncherdev.framework.OnKeyDownListenner;
import com.nd.hilauncherdev.framework.view.draggersliding.DraggerChooseItem;

/**
 * Class for initiating a drag within a view or across multiple views.
 */
public class DragController implements OnKeyDownListenner {
	static final String TAG = "Launcher.DragController";

	/** Indicates the drag is a move. */
	public static int DRAG_ACTION_MOVE = 0;

	/** Indicates the drag is a copy. */
	public static int DRAG_ACTION_COPY = 1;
	
	@Override
	public boolean onKeyDownProcess(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getScrollZone() {
		return 0;
	}
	
	public void startDrag(View v, DragSource source, Object dragInfo, int dragAction, ArrayList<DraggerChooseItem> list) {

	}
}
