/*
 * Copyright 2018 The Android Open Source Project
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

package com.jarkeet.bestpractices.test.snaphelper;

import android.graphics.PointF;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

/**
 * Implementation of the {@link SnapHelper} supporting snapping in either vertical or horizontal
 * orientation.
 * <p>
 * The implementation will snap the center of the target child view to the center of
 * the attached {@link RecyclerView}. If you intend to change this behavior then override
 * {@link SnapHelper#calculateDistanceToFinalSnap}.
 */
public class NLinearSnapHelper extends SnapHelper {

    public static final String TAG = "NLinearSnapHelper";

    private static final float INVALID_DISTANCE = 1f;

    // Orientation helpers are lazily created per LayoutManager.
    @Nullable
    private OrientationHelper mVerticalHelper;
    @Nullable
    private OrientationHelper mHorizontalHelper;


    // Handles the snap on scroll case.
    private final RecyclerView.OnScrollListener scrollListener =
            new RecyclerView.OnScrollListener() {
                boolean mScrolled = false;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrolled) {
                        Log.d(TAG, "onScrollStateChanged()>>>>>");
                        mScrolled = false;
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dx != 0 || dy != 0) {
                        mScrolled = true;
                    }
                }
            };


    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
            throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(scrollListener);

    }

    @Override
    public int[] calculateDistanceToFinalSnap(
            @NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToCenter(targetView,
                    getHorizontalHelper(layoutManager));
        } else {
            out[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToCenter(targetView,
                    getVerticalHelper(layoutManager));
        } else {
            out[1] = 0;
        }
        return out;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX,
                                              int velocityY) {

//        final View currentView = findStartView(layoutManager, mHorizontalHelper);
//        if (currentView == null) {
//            return RecyclerView.NO_POSITION;
//        }
//
//        final int currentPosition = layoutManager.getPosition(currentView);
//        if (currentPosition == RecyclerView.NO_POSITION) {
//            return RecyclerView.NO_POSITION;
//        }

//        if(currentPosition == 0 || currentPosition == 1) {
//            return findTargetSnapPositionByPage(layoutManager, velocityX, velocityY);
//        } else {
            return findTargetSnapPositionByLinear(layoutManager, velocityX, velocityY);
//        }

    }

    public int findTargetSnapPositionByLinear(RecyclerView.LayoutManager layoutManager, int velocityX,
            int velocityY) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }

        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        final View currentView = findStartView(layoutManager, mHorizontalHelper);
        if (currentView == null) {
            return RecyclerView.NO_POSITION;
        }

        final int currentPosition = layoutManager.getPosition(currentView);
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
                (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
        // deltaJumps sign comes from the velocity which may not match the order of children in
        // the LayoutManager. To overcome this, we ask for a vector from the LayoutManager to
        // get the direction.
        PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
        if (vectorForEnd == null) {
            // cannot get a vector for the given position.
            return RecyclerView.NO_POSITION;
        }

        int vDeltaJump, hDeltaJump;
        if (layoutManager.canScrollHorizontally()) {
            hDeltaJump = estimateNextPositionDiffForFling(layoutManager,
                    getHorizontalHelper(layoutManager), velocityX, 0);
            if (vectorForEnd.x < 0) {
                hDeltaJump = -hDeltaJump;
            }
        } else {
            hDeltaJump = 0;
        }
        if (layoutManager.canScrollVertically()) {
            vDeltaJump = estimateNextPositionDiffForFling(layoutManager,
                    getVerticalHelper(layoutManager), 0, velocityY);
            if (vectorForEnd.y < 0) {
                vDeltaJump = -vDeltaJump;
            }
        } else {
            vDeltaJump = 0;
        }

        int deltaJump = layoutManager.canScrollVertically() ? vDeltaJump : hDeltaJump;
        if (deltaJump == 0) {
            return RecyclerView.NO_POSITION;
        }

        int targetPos = currentPosition + deltaJump;

        if(currentPosition == 0 && deltaJump > 1) {
            targetPos = 1;

        }

        if (targetPos <= 0) {//关键方法
            View closetView = findClosetViewToStart(layoutManager, mHorizontalHelper);
            final int closetPosition = layoutManager.getPosition(closetView);
            targetPos = Math.max(targetPos, Math.min(closetPosition, 1));
//            targetPos = 0;
        }
        if (targetPos >= itemCount) {
            targetPos = itemCount - 1;
        }


        Log.d(TAG, "currentPosition: " + currentPosition);
        Log.d(TAG, "deltaJump: " + deltaJump);
        Log.d(TAG, "targetPos: " + targetPos);
        return targetPos;
    }


    public int findTargetSnapPositionByPage(RecyclerView.LayoutManager layoutManager, int velocityX,
                                      int velocityY) {
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        final OrientationHelper orientationHelper = getOrientationHelper(layoutManager);
        if (orientationHelper == null) {
            return RecyclerView.NO_POSITION;
        }

        // A child that is exactly in the center is eligible for both before and after
        View closestChildBeforeCenter = null;
        int distanceBefore = Integer.MIN_VALUE;
        View closestChildAfterCenter = null;
        int distanceAfter = Integer.MAX_VALUE;

        // Find the first view before the center, and the first view after the center
        final int childCount = layoutManager.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            if (child == null) {
                continue;
            }
            final int distance = distanceToCenter2(child, orientationHelper);
            Log.e(TAG, "findTargetSnapPositionByPage i: " + i+  ",distance: "+ distance);
            if (distance <= 0 && distance > distanceBefore) {
                // Child is before the center and closer then the previous best
                distanceBefore = distance;
                closestChildBeforeCenter = child;
            }
            if (distance >= 0 && distance < distanceAfter) {
                // Child is after the center and closer then the previous best
                distanceAfter = distance;
                closestChildAfterCenter = child;
            }
        }

        // Return the position of the first child from the center, in the direction of the fling
        final boolean forwardDirection = isForwardFling(layoutManager, velocityX, velocityY);
        if (forwardDirection && closestChildAfterCenter != null) {
            Log.d(TAG, "closestChildAfterCenter : "  + layoutManager.getPosition(closestChildAfterCenter));
            return layoutManager.getPosition(closestChildAfterCenter);
        } else if (!forwardDirection && closestChildBeforeCenter != null) {
            Log.d(TAG, "closestChildBeforeCenter : "  + layoutManager.getPosition(closestChildBeforeCenter));
            return layoutManager.getPosition(closestChildBeforeCenter);
        }

        // There is no child in the direction of the fling. Either it doesn't exist (start/end of
        // the list), or it is not yet attached (very rare case when children are larger then the
        // viewport). Extrapolate from the child that is visible to get the position of the view to
        // snap to.
        View visibleView = forwardDirection ? closestChildBeforeCenter : closestChildAfterCenter;
        if (visibleView == null) {
            return RecyclerView.NO_POSITION;
        }
        int visiblePosition = layoutManager.getPosition(visibleView);
        int snapToPosition = visiblePosition
                + (isReverseLayout(layoutManager) == forwardDirection ? -1 : +1);
        Log.d(TAG, "visiblePosition : " + visiblePosition + ", snapToPosition: " + snapToPosition);

        if (snapToPosition < 0 || snapToPosition >= itemCount) {
            return RecyclerView.NO_POSITION;
        }
        return snapToPosition;
    }

    @Nullable
    private OrientationHelper getOrientationHelper(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return getVerticalHelper(layoutManager);
        } else if (layoutManager.canScrollHorizontally()) {
            return getHorizontalHelper(layoutManager);
        } else {
            return null;
        }
    }

    private boolean isForwardFling(RecyclerView.LayoutManager layoutManager, int velocityX,
                                   int velocityY) {
        if (layoutManager.canScrollHorizontally()) {
            return velocityX > 0;
        } else {
            return velocityY > 0;
        }
    }

    private boolean isReverseLayout(RecyclerView.LayoutManager layoutManager) {
        final int itemCount = layoutManager.getItemCount();
        if ((layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
                    (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
            PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
            if (vectorForEnd != null) {
                return vectorForEnd.x < 0 || vectorForEnd.y < 0;
            }
        }
        return false;
    }


    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return findCenterView(layoutManager, getVerticalHelper(layoutManager));
        } else if (layoutManager.canScrollHorizontally()) {
            return findCenterView(layoutManager, getHorizontalHelper(layoutManager));
        }
        return null;
    }

    private int distanceToCenter(@NonNull View targetView, OrientationHelper helper) {
//        final int childCenter = helper.getDecoratedStart(targetView)
//                + (helper.getDecoratedMeasurement(targetView) / 2);
//        final int containerCenter = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
//        return childCenter - containerCenter;

        final int childStart = helper.getDecoratedStart(targetView);
        final int containerStart = helper.getStartAfterPadding() ;
        return childStart - containerStart;
    }

    private int distanceToCenter2(@NonNull View targetView, OrientationHelper helper) {
        final int childCenter = helper.getDecoratedStart(targetView)
                + (helper.getDecoratedMeasurement(targetView) / 2);
        final int containerCenter = helper.getStartAfterPadding() ;
        return Math.abs(childCenter - containerCenter);

    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        Log.d(TAG, "onFling>>>");
        return super.onFling(velocityX, velocityY);
    }

    /**
     * Estimates a position to which SnapHelper will try to scroll to in response to a fling.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param helper        The {@link OrientationHelper} that is created from the LayoutManager.
     * @param velocityX     The velocity on the x axis.
     * @param velocityY     The velocity on the y axis.
     *
     * @return The diff between the target scroll position and the current position.
     */
    private int estimateNextPositionDiffForFling(RecyclerView.LayoutManager layoutManager,
            OrientationHelper helper, int velocityX, int velocityY) {
        int[] distances = calculateScrollDistance(velocityX, velocityY);
        float distancePerChild = computeDistancePerChild(layoutManager, helper);
        if (distancePerChild <= 0) {
            return 0;
        }
        int distance =
                Math.abs(distances[0]) > Math.abs(distances[1]) ? distances[0] : distances[1];
        return (int) Math.round(distance / distancePerChild);
    }

    /**
     * Return the child view that is currently closest to the center of this parent.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param helper The relevant {@link OrientationHelper} for the attached {@link RecyclerView}.
     *
     * @return the child view that is currently closest to the center of this parent.
     */
    @Nullable
    private View findCenterView(RecyclerView.LayoutManager layoutManager,
            OrientationHelper helper) {
//        int childCount = layoutManager.getChildCount();
//        if (childCount == 0) {
//            return null;
//        }
//
//        View closestChild = null;
//        final int center = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
//        int absClosest = Integer.MAX_VALUE;
//
//        for (int i = 0; i < childCount; i++) {
//            final View child = layoutManager.getChildAt(i);
//            int childCenter = helper.getDecoratedStart(child)
//                    + (helper.getDecoratedMeasurement(child) / 2);
//            int absDistance = Math.abs(childCenter - center);
//
//            /** if child center is closer than previous closest, set it as closest  **/
//            if (absDistance < absClosest) {
//                absClosest = absDistance;
//                closestChild = child;
//            }
//        }
//        return closestChild;

        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }


        View firstChild = layoutManager.getChildAt(0);
        View secondChild = layoutManager.getChildAt(1);
        int start = helper.getDecoratedStart(firstChild);
        int end = helper.getDecoratedEnd(firstChild);
        for(int i = 0; i < childCount; ++i) {
            View childAt = layoutManager.getChildAt(i);
            Log.w(TAG, "findCenterView text: " + ((TextView)childAt).getText());
        }


        Log.d(TAG, "start :" + start + ",end : " + end);

        return  Math.abs(start) <= Math.abs(end) ? firstChild : secondChild;
    }

    private View findStartView(RecyclerView.LayoutManager layoutManager,
                                OrientationHelper helper) {

        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }
        for(int i = 0; i < childCount; ++i) {
            View childAt = layoutManager.getChildAt(i);
            Log.w(TAG, "findStartView text: " + ((TextView)childAt).getText());

            int distanceToCenter2 = distanceToCenter2(childAt, getHorizontalHelper(mHorizontalHelper.getLayoutManager()));
            Log.d(TAG, "distanceToCenter2: " + distanceToCenter2);
        }

        View firstChild = layoutManager.getChildAt(0);


        return   firstChild;
    }

    private View findClosetViewToStart(RecyclerView.LayoutManager layoutManager,
                               OrientationHelper helper) {

        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }
        int closetDistance = Integer.MAX_VALUE;
        View closetView = layoutManager.getChildAt(0);
        for(int i = 0; i < childCount; ++i) {
            View childAt = layoutManager.getChildAt(i);
            Log.w(TAG, "findStartView text: " + ((TextView)childAt).getText());

            int distanceToCenter2 = distanceToCenter2(childAt, getHorizontalHelper(mHorizontalHelper.getLayoutManager()));
            if(distanceToCenter2 < closetDistance) {
                closetDistance = distanceToCenter2;
                closetView = childAt;
            }
            Log.d(TAG, "distanceToCenter2: " + distanceToCenter2);
        }

        return closetView;
    }


    /**
     * Computes an average pixel value to pass a single child.
     * <p>
     * Returns a negative value if it cannot be calculated.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param helper        The relevant {@link OrientationHelper} for the attached
     *                      {@link RecyclerView.LayoutManager}.
     *
     * @return A float value that is the average number of pixels needed to scroll by one view in
     * the relevant direction.
     */
    private float computeDistancePerChild(RecyclerView.LayoutManager layoutManager,
            OrientationHelper helper) {
        View minPosView = null;
        View maxPosView = null;
        int minPos = Integer.MAX_VALUE;
        int maxPos = Integer.MIN_VALUE;
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return INVALID_DISTANCE;
        }

        for (int i = 0; i < childCount; i++) {
            View child = layoutManager.getChildAt(i);
            final int pos = layoutManager.getPosition(child);
            if (pos == RecyclerView.NO_POSITION) {
                continue;
            }
            if (pos < minPos) {
                minPos = pos;
                minPosView = child;
            }
            if (pos > maxPos) {
                maxPos = pos;
                maxPosView = child;
            }
        }
        if (minPosView == null || maxPosView == null) {
            return INVALID_DISTANCE;
        }
        int start = Math.min(helper.getDecoratedStart(minPosView),
                helper.getDecoratedStart(maxPosView));
        int end = Math.max(helper.getDecoratedEnd(minPosView),
                helper.getDecoratedEnd(maxPosView));
        int distance = end - start;
        if (distance == 0) {
            return INVALID_DISTANCE;
        }
        return 1f * distance / ((maxPos - minPos) + 1);
    }

    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null || mVerticalHelper.getLayoutManager() != layoutManager) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null || mHorizontalHelper.getLayoutManager() != layoutManager) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }
}
