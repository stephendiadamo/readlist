package com.s_diadamo.readlist.navigationDrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * Created by s-diadamo on 15-06-09.
 */
public class NavigationExpandableListView extends ExpandableListView {
    private boolean expanded = true;

    public NavigationExpandableListView(Context context) {
        super(context);
    }

    public NavigationExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isExpanded() {
        return expanded;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (expanded) {
            int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setExpanded(boolean isExpanded) {
        expanded = isExpanded;
    }
}
