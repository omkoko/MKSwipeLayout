package moko.dev.mkswipelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Dev on 26/07/2016.
 */
public class SwipeLayout extends ViewGroup {

    public SwipeLayout(Context context) {
        this(context, null, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        center_view = null;
        left_view = null;
        right_view = null;
    }

    List<View> mMatchParentChildren;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        final boolean measureMatchParentChildren = MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY
                || MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;

        if (mMatchParentChildren == null)
            mMatchParentChildren = new ArrayList<View>();
        mMatchParentChildren.clear();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        int centerLeftMargin = 0;

        for (int i=0; i<count; i++) {
            View child = getChildAt(i);
            SwipeLayout.LayoutParams lp = (SwipeLayout.LayoutParams) child.getLayoutParams();
            if (i == LayoutParams.CENTER && center_view == null) {
                lp.position = LayoutParams.CENTER;
                center_view = child;
                center_view.setLayoutParams(lp);
            } else if (i == LayoutParams.RIGHT && right_view == null) {
                lp.position = LayoutParams.RIGHT;
                right_view = child;
                right_view.setLayoutParams(lp);
            } else if (i == LayoutParams.LEFT && left_view == null) {
                lp.position = LayoutParams.LEFT;
                left_view = child;
                left_view.setLayoutParams(lp);
            } else {
//                lp.setLayoutDirection(LayoutParams.BOTTOM);
//                addBottomView(getChildAt(i), 0, 0);
            }
        }

        View centerView = center_view; //getCenterView();
        if (centerView != null) {
            centerLeftMargin = ((LayoutParams)centerView.getLayoutParams()).leftMargin;
        }

        int leftHeight = 0, rightHeight = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                int height = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                switch (lp.position) {
                    case LayoutParams.BOTTOM_LEFT:
                    case LayoutParams.BOTTOM_RIGHT:
                    case LayoutParams.BOTTOM:
                        maxHeight += height;
                        break;
                    case LayoutParams.LEFT:
                        if (centerLeftMargin > 0)
                            leftHeight = height;
                        height = 0;
                        break;
                    case LayoutParams.RIGHT:
                        if (centerLeftMargin < 0)
                            rightHeight = height;
                        height = 0;
                        break;
                }
                maxHeight = Math.max(maxHeight, height);


                childState = combineMeasuredStates(childState, child.getMeasuredState());
                if (measureMatchParentChildren) {
                    if (lp.width == LayoutParams.MATCH_PARENT || lp.height == LayoutParams.MATCH_PARENT
                            || lp.position == LayoutParams.CENTER) {
                        mMatchParentChildren.add(child);
                    }
                }
            }
        }

        maxHeight = Math.max(maxHeight, leftHeight);
        maxHeight = Math.max(maxHeight, rightHeight);

        // Account for padding too
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();


        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Check against our foreground's minimum height and width
        final Drawable drawable = getBackground();
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
        }



        setMeasuredDimension(
                resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));

//		count = mMatchParentChildren.size();
//		if (count > 1) {
//			for (int i = 0; i < count; i++) {
//				final View child = mMatchParentChildren.get(i);
//				final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
//
//				final int childWidthMeasureSpec;
//				if (lp.width == LayoutParams.MATCH_PARENT) {
//					final int width = Math.max(0,
//							getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - lp.leftMargin - lp.rightMargin);
//					childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
//				} else {
//					childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
//							getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
//				}
//
//				final int childHeightMeasureSpec;
//				if (lp.height == LayoutParams.MATCH_PARENT) {
//					final int height = Math.max(0, getMeasuredHeight() - getPaddingTop() - getPaddingBottom()
//							- lp.topMargin - lp.bottomMargin);
//					childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//				} else {
//					childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
//							getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin, lp.height);
//				}
//
//				child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
//			}
//		}
    }

    private boolean fisDown;
    private float ftouch_X;
    private int fleftMargin;
    private int frightWidth, fleftWidth;

    private View getPositionView(int position) {
        for (int index = 0; index < getChildCount(); index++) {
            View child = getChildAt(index);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.position == position)
                return child;
        }

        return null;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        {
            int centerLeft = parentLeft;
            int centerTop = parentTop;
            int centerRight = parentRight;
            int centerBottom = parentBottom;

            final View centerView = center_view; //getCenterView();
            if (centerView != null) {
                final LayoutParams center_lp = (LayoutParams) centerView.getLayoutParams();

                final int centerWidth = this.getMeasuredWidth();
                final int centerHeight = centerView.getMeasuredHeight();

                centerLeft = parentLeft + (parentRight - parentLeft - centerWidth) / 2 + center_lp.leftMargin - center_lp.rightMargin;
                centerTop = parentTop + center_lp.topMargin;
                centerRight = centerLeft + centerWidth;
                centerBottom = centerTop + centerHeight;

                centerView.layout(centerLeft, centerTop, centerRight, centerBottom);
            }

            final View leftView = left_view; //getLeftView();
            if (leftView != null) {
                final LayoutParams lpLeft = (LayoutParams)leftView.getLayoutParams();
                final int leftHeight = leftView.getMeasuredHeight();
                int _top = centerTop + (centerBottom - centerTop - leftHeight) / 2 + lpLeft.topMargin - lpLeft.bottomMargin;
                int leftTop = Math.max(0, _top);
                int leftBottom = leftTop + leftHeight;
                leftView.layout(
                        centerLeft-leftView.getMeasuredWidth(),
                        leftTop,
                        centerLeft,
                        leftBottom);
            }

            final View rightView = right_view; //getRightView();
            if (rightView != null) {
                final LayoutParams lpRight = (LayoutParams)rightView.getLayoutParams();
                final int rightHeight = rightView.getMeasuredHeight();
                int _top = centerTop + (centerBottom - centerTop - rightHeight) / 2 + lpRight.topMargin - lpRight.bottomMargin;
                int rightTop = Math.max(0, _top);
                int rightBottom = rightTop + rightHeight;
                rightView.layout(
                        centerRight,
                        rightTop,
                        centerRight+rightView.getMeasuredWidth(),
                        rightBottom);
            }

            View[] bottoms = getBottomViews();
            if (bottoms != null) {
                int _top = centerBottom;
                for (View child:bottoms) {
                    LayoutParams lpChild = (LayoutParams)child.getLayoutParams();
                    final int bottomWidth = child.getMeasuredWidth();
                    final int bottomHeight = child.getMeasuredHeight();
                    int bottomLeft =
                            lpChild.position == LayoutParams.BOTTOM_LEFT
                                    ? centerLeft
                                    : lpChild.position == LayoutParams.BOTTOM_RIGHT
                                    ? centerRight - bottomWidth
                                    : centerLeft + (centerRight - centerLeft - bottomWidth) / 2 + lpChild.leftMargin - lpChild.rightMargin;
                    int bottomRight = bottomLeft + bottomWidth;
                    int bottomTop = _top + lpChild.topMargin;
                    int bottomBottom = bottomTop + bottomHeight;
                    child.layout(
                            bottomLeft,
                            bottomTop,
                            bottomRight,
                            bottomBottom);
                    _top = bottomBottom + lpChild.bottomMargin;
                }
            }

        }
    }

    public int getTotalWidth(View view) {
        if (view == null)
            return 0;
        view.measure(0, 0);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof MarginLayoutParams)
            return view.getMeasuredWidth()
                    +((MarginLayoutParams)params).leftMargin
                    +((MarginLayoutParams)params).rightMargin;
        else
            return view.getMeasuredWidth();
    }

    private List<View> fbottoms;

    public final View[] getBottomViews() {
        if (fbottoms == null)
            fbottoms = new ArrayList<View>();

        fbottoms.clear();
        for (int index=0; index<getChildCount(); index++) {
            View child = getChildAt(index);
            int lp_position = ((LayoutParams)child.getLayoutParams()).position;
            switch (lp_position) {
                case LayoutParams.BOTTOM_LEFT:
                case LayoutParams.BOTTOM_RIGHT:
                case LayoutParams.BOTTOM:
                    fbottoms.add(child);
                    break;
            }
        }

        return fbottoms.toArray(new View[fbottoms.size()]);
    }

    public View getCenterView() {
        return center_view;
//		return getPositionView(LayoutParams.CENTER);
    }
    public View getLeftView() {
        return left_view;
//		return getPositionView(LayoutParams.LEFT);
    }
    public View getRightView() {
        return right_view;
//		return getPositionView(LayoutParams.RIGHT);
    }

    public boolean removeLeftView() {
        return removePositionView(LayoutParams.LEFT);
    }
    public boolean removeRightView() {
        return removePositionView(LayoutParams.RIGHT);
    }
    public boolean removeBottomViews() {
        View[] views = getBottomViews();
        if (views != null && views.length > 0) {
            for (View view:views) {
                if (-1 != indexOfChild(view))
                    removeView(view);
            }
            return true;
        } else
            return false;
    }

    public boolean hasBottomViews() {
        boolean result = false;
        for (int index=0; index<getChildCount(); index++) {
            View child = getChildAt(index);
            if (child == null)
                continue;

            LayoutParams params = (LayoutParams)child.getLayoutParams();

            if (params.position == LayoutParams.BOTTOM
                    ||params.position == LayoutParams.BOTTOM_LEFT
                    ||params.position == LayoutParams.BOTTOM_RIGHT)
            {
                result = true;
                break;
            }
        }

        return result;
    }

    protected boolean removePositionView(int position) {
        View view = getPositionView(position);
        if (view != null) {
            removeView(view);
            return true;
        } else
            return false;
    }

    private View center_view, left_view, right_view;
    public LayoutParams setCenterView(View view) {
        center_view = view;
        return setView(view, LayoutParams.CENTER);
    }
    public LayoutParams setLeftView(View view) {
        left_view = view;
        return setView(view, LayoutParams.LEFT);
    }
    public LayoutParams setRightView(View view) {
        right_view = view;
        return setView(view, LayoutParams.RIGHT);
    }
    public LayoutParams addBottomView(View view, int width, int height) {
        return setView(view, LayoutParams.BOTTOM);
    }

    protected LayoutParams setView(View view, int position) {
        return setView(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, position);
    }
    protected LayoutParams setView(View view, int width, int height, int position) {
        if (view == null)
            return null;

        switch (position) {
            case LayoutParams.RIGHT:
            case LayoutParams.LEFT:
            case LayoutParams.CENTER:
            {
                View lastView = getPositionView(position);
                if (lastView != null && view != lastView)
                    removeView(lastView);
            }
            break;
        }

        LayoutParams params = new LayoutParams(
                position==LayoutParams.CENTER?LayoutParams.MATCH_PARENT:width,
                height,
                position);

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null && parent == this)
            view.setLayoutParams(params);
        else
            addView(view, params);

        return params;
    }

    public void resetSlidePosition() {
        View center = center_view; //getCenterView();
        if (center == null)
            return;

        LayoutParams params = (LayoutParams)center.getLayoutParams();
        params.leftMargin = 0;
        center.setLayoutParams(params);
    }

    private long ftouch_start;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final View centerView = center_view; //getCenterView();
        final View leftView = left_view; //getLeftView();
        final View rightView = right_view; //getRightView();

        MarginLayoutParams p_center = centerView != null ? (MarginLayoutParams) centerView.getLayoutParams() : null;

        if (p_center == null)
            return super.dispatchTouchEvent(event);

        int m = 0;
        int move = 0;

        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!fisDown) {
                    ftouch_start = System.nanoTime();

                    ftouch_X = event.getRawX();
                    fleftMargin = p_center.leftMargin;

                    if (rightView != null) {
                        rightView.measure(0, 0);
                        frightWidth = rightView.getMeasuredWidth();
                    }

                    if (leftView != null) {
                        leftView.measure(0, 0);
                        fleftWidth = leftView.getMeasuredWidth();
                    }

                    fisDown = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (fisDown) {
                    m = (int) event.getRawX() - ((int) ftouch_X);
                    move = fleftMargin + m;

                    if (event.getRawX() < ftouch_X) {
                        if (frightWidth == 0 && p_center.leftMargin == 0)
                            move = p_center.leftMargin;
                    } else if (event.getRawX() > ftouch_X) {
                        if (fleftWidth == 0 && p_center.leftMargin == 0)
                            move = p_center.leftMargin;
                    }

                    p_center.leftMargin = move;
                    centerView.setLayoutParams(p_center);

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (fisDown) {
                    if (rightView != null
                            && (
                            (event.getRawX() < ftouch_X && p_center.leftMargin < 0
                                    && Math.abs(p_center.leftMargin) >= frightWidth / 2)
                                    ||  (event.getRawX() > ftouch_X && p_center.leftMargin < 0
                                    && Math.abs(p_center.leftMargin) >= frightWidth / 3))) {

                        p_center.leftMargin = frightWidth * -1;
                    } else if (leftView != null &&
                            ((event.getRawX() > ftouch_X && p_center.leftMargin > 0
                                    && p_center.leftMargin >= fleftWidth / 2)
                                    || (event.getRawX() < ftouch_X && p_center.leftMargin > 0
                                    && p_center.leftMargin >= fleftWidth / 3))) {

                        p_center.leftMargin = fleftWidth;
                    } else {
                        p_center.leftMargin = ftouch_X==event.getRawX() || Math.abs(ftouch_X-event.getRawX())<10?fleftMargin:0;
                    }

                    centerView.setLayoutParams(p_center);

                    if (ftouch_X == event.getRawX()
                            && (System.nanoTime() - ftouch_start) / 1000000 < 600)
                    {
                        performClick();
                    }

                    fisDown = false;
                }
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public int getBaseline() {
        return -1;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new SwipeLayout.LayoutParams(getContext(), attrs);
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}, a height of
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} and no spanning.
     */
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof SwipeLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return SwipeLayout.class.getName();
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ThreeLayout_Layout);
            position = a.getInt(R.styleable.ThreeLayout_Layout_layout_position, CENTER);
            a.recycle();
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
            position = -1;
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            position = -1;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            position = -1;
        }

        public LayoutParams(int width, int height, int position) {
            super(width, height);
            this.bottomMargin = position;
        }

        public static final int CENTER = 0;
        public static final int LEFT = 1;
        public static final int RIGHT = 2;
        public static final int BOTTOM = 3;
        public static final int BOTTOM_LEFT = 4;
        public static final int BOTTOM_RIGHT = 5;
        public int position;
    }
}
