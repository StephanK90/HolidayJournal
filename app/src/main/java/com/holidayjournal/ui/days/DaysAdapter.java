package com.holidayjournal.ui.days;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.holidayjournal.R;
import com.holidayjournal.models.DayModel;
import com.holidayjournal.ui.days.editday.EditDayActivity;
import com.holidayjournal.utils.Constants;

import java.util.ArrayList;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DaysViewHolder> {

    private final int EDIT_DAY_REQ = 1;

    private Context mContext;
    private ArrayList<DayModel> days;

    DaysAdapter(Context context) {
        this.mContext = context;
        this.days = new ArrayList<>();
    }

    void loadDays(ArrayList<DayModel> list) {
        this.days.clear();
        this.days.addAll(list);
        notifyDataSetChanged();
    }

    void updateDay(DayModel day) {
        for (int i = 0; i < days.size(); i++) {
            DayModel model = days.get(i);
            if (model.getNr() == day.getNr()) {
                days.set(i, day);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public DaysAdapter.DaysViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_day_card, viewGroup, false);
        return new DaysViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DaysAdapter.DaysViewHolder holder, int i) {
        DayModel day = this.days.get(i);

        String dateSplit[] = day.getDate().split(" ");
        String dateDay = dateSplit[0].trim();
        String dateMonth = dateSplit[1].replace(".", "").trim();
        String dateYear = dateSplit[2].trim();

        holder.day.setText(dateDay);
        holder.month.setText(dateMonth);
        holder.year.setText(dateYear);

        if (TextUtils.isEmpty(day.getTitle())) {
            holder.title.setText(mContext.getResources().getString(R.string.day_title_placeholder, String.valueOf(day.getNr())));
        } else {
            holder.title.setText(day.getTitle());
            holder.description.setText(day.getDescription());

            if (day.getRating() != 0) {
                if (day.getRating() > 7) {
                    holder.rating.setBackground(mContext.getResources().getDrawable(R.drawable.textview_rounded_green));
                } else if (day.getRating() > 5) {
                    holder.rating.setBackground(mContext.getResources().getDrawable(R.drawable.textview_rounded_yellow));
                } else if (day.getRating() > 3) {
                    holder.rating.setBackground(mContext.getResources().getDrawable(R.drawable.textview_rounded_orange));
                } else {
                    holder.rating.setBackground(mContext.getResources().getDrawable(R.drawable.textview_rounded_red));
                }
                holder.rating.setText(String.valueOf(day.getRating()));
            }

            if (day.getLocation() != null) {
                holder.location.setText(day.getLocation().getName());
            }
        }


        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, day.getTitle());
                intent.putExtra(Intent.EXTRA_TEXT, day.getDescription());
                intent.setType("text/plain");
                mContext.startActivity(Intent.createChooser(intent, mContext.getResources().getString(R.string.share_to)));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditDayActivity.class);
                intent.putExtra(Constants.DAY, day);
                ((Activity) mContext).startActivityForResult(intent, EDIT_DAY_REQ);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.days.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class DaysViewHolder extends RecyclerView.ViewHolder {

        TextView day;
        TextView month;
        TextView year;
        TextView title;
        TextView description;
        TextView rating;
        TextView location;
        ImageView share;

        DaysViewHolder(@NonNull View itemView) {
            super(itemView);

            day = itemView.findViewById(R.id.day_date_day);
            month = itemView.findViewById(R.id.day_date_month);
            year = itemView.findViewById(R.id.day_date_year);
            title = itemView.findViewById(R.id.day_title);
            description = itemView.findViewById(R.id.day_description);
            rating = itemView.findViewById(R.id.day_rating);
            location = itemView.findViewById(R.id.day_location);
            share = itemView.findViewById(R.id.day_share_btn);
        }
    }
}
