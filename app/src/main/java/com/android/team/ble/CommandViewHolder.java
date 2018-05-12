package com.android.team.ble;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class CommandViewHolder extends RecyclerView.ViewHolder {


    TextView direction;
    TextView distance;
    TextView directionLable;
    TextView distanceLable;
    TextView distanceLableCM;
    ImageButton delete;
    public CommandViewHolder(View itemView) {
        super(itemView);
        direction=(TextView)itemView.findViewById(R.id.directionTxt);
        distance=(TextView)itemView.findViewById(R.id.distanceTxt);
        delete=(ImageButton) itemView.findViewById(R.id.deleteBtn);

        directionLable=(TextView)itemView.findViewById(R.id.textView);
        distanceLable=(TextView)itemView.findViewById(R.id.textView2);
        distanceLableCM=(TextView) itemView.findViewById(R.id.textView3);

    }

}
