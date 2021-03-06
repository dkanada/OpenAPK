package com.dkanada.openapk.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;

public class ButtonView extends RelativeLayout {
    public ButtonView(Context context, String title, String summary, OnClickListener onClickListener) {
        super(context);
        this.setOnClickListener(onClickListener);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_button, null);
        addView(view);

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView summaryView = (TextView) view.findViewById(R.id.summary);
        titleView.setText(title);
        if (summary == null) {
            summaryView.setVisibility(GONE);
        } else {
            summaryView.setText(summary);
        }

        View dividerTop = findViewById(R.id.divider_top);
        View dividerBottom = findViewById(R.id.divider_bottom);
        if (App.getAppPreferences().getTheme().equals("0")) {
            dividerTop.setBackgroundColor(getResources().getColor(R.color.grey_one));
            dividerBottom.setBackgroundColor(getResources().getColor(R.color.grey_one));
        } else {
            dividerTop.setBackgroundColor(getResources().getColor(R.color.grey_three));
            dividerBottom.setBackgroundColor(getResources().getColor(R.color.grey_three));
        }
    }
}
