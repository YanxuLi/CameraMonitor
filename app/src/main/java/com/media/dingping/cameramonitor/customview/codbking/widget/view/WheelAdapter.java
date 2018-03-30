/*
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.media.dingping.cameramonitor.customview.codbking.widget.view;

/**
 * Wheel adapter interface
 * 
 * @deprecated Use WheelViewAdapter
 */
public interface WheelAdapter {
	/**
	 * Gets items count
	 * @return the count of wheel2 items
	 */
	public int getItemsCount();
	
	/**
	 * Gets a wheel2 item by index.
	 * 
	 * @param index the item index
	 * @return the wheel2 item text or null
	 */
	public String getItem(int index);
	
	/**
	 * Gets maximum item length. It is used to determine the wheel2 width.
	 * If -1 is returned there will be used the default wheel2 width.
	 * 
	 * @return the maximum item length or -1
	 */
	public int getMaximumLength();
}