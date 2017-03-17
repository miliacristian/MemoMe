package com.error404.memometwo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class CustomListView extends ListView {
    public CustomListView(Context context) {
        super(context, null);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.dropDownListViewStyle);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }
}
