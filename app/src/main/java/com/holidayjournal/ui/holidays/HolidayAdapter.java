package com.holidayjournal.ui.holidays;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.holidayjournal.R;
import com.holidayjournal.ui.days.DaysActivity;
import com.holidayjournal.ui.maps.RouteActivity;
import com.holidayjournal.utils.Constants;
import com.holidayjournal.models.HolidayModel;
import com.holidayjournal.ui.holidays.addholiday.AddHolidayActivity;
import com.holidayjournal.utils.DateFormatter;

import java.util.ArrayList;

public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder> {

    private final int ADD_HOLIDAY_REQ = 0;

    private ArrayList<HolidayModel> holidays;
    private Context mContext;

    HolidayAdapter(Context context) {
        this.mContext = context;
        holidays = new ArrayList<>();
    }

    void addHoliday(HolidayModel holiday) {
        this.holidays.add(0, holiday);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, getItemCount());
    }

    void updateHoliday(HolidayModel holiday) {
        for (int i = 0; i < holidays.size(); i++) {
            HolidayModel model = holidays.get(i);
            if (model.getId().equalsIgnoreCase(holiday.getId())) {
                holidays.set(i, holiday);
                notifyItemChanged(i);
                break;
            }
        }
    }

    void deleteHoliday(int position) {
        if (position != -1 && position < holidays.size()) {
            holidays.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }

    @NonNull
    @Override
    public HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_holiday_card, viewGroup, false);
        return new HolidayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayViewHolder holder, int i) {
        HolidayModel holiday = holidays.get(i);
        holder.title.setText(holiday.getTitle());

        if (holiday.getRating() != 0) {
            if (holiday.getRating() < 4) {
                holder.rating.setBackground(mContext.getResources().getDrawable(R.drawable.textview_rounded_red));
            } else if (holiday.getRating() < 6) {
                holder.rating.setBackground(mContext.getResources().getDrawable(R.drawable.textview_rounded_orange));
            } else if (holiday.getRating() < 8) {
                holder.rating.setBackground(mContext.getResources().getDrawable(R.drawable.textview_rounded_yellow));
            } else {
                holder.rating.setBackground(mContext.getResources().getDrawable(R.drawable.textview_rounded_green));
            }
            holder.rating.setText(String.valueOf(holiday.getRating()));
        }

        String startDate = DateFormatter.toString(holiday.getStartDate());
        String endDate = DateFormatter.toString(holiday.getEndDate());
        holder.date.setText(mContext.getString(R.string.holiday_date, startDate, endDate));

        holder.desc.setText(holiday.getDescription());

        if (!TextUtils.isEmpty(holiday.getImageUri())) {
            Glide.with(mContext)
                    .load(holiday.getImageUri())
                    .into(holder.image);
        } else {
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.drawable.holiday_stock_img))
                    .into(holder.image);
        }


        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(mContext, RouteActivity.class);
                mapIntent.putExtra(Constants.HOLIDAY, holiday);
                mContext.startActivity(mapIntent);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(mContext, AddHolidayActivity.class);
                editIntent.putExtra(Constants.HOLIDAY, holiday);
                ((Activity) mContext).startActivityForResult(editIntent, ADD_HOLIDAY_REQ);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HolidayActivity) mContext).deleteHoliday(holiday, holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DaysActivity.class);
                intent.putExtra(Constants.HOLIDAY, holiday);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != holidays ? holidays.size() : 0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class HolidayViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView rating;
        TextView date;
        TextView desc;
        ImageView image;
        LinearLayout map;
        LinearLayout edit;
        LinearLayout delete;

        HolidayViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.holiday_title);
            rating = itemView.findViewById(R.id.holiday_rating);
            date = itemView.findViewById(R.id.holiday_date);
            desc = itemView.findViewById(R.id.holiday_desc);
            image = itemView.findViewById(R.id.holiday_image);
            map = itemView.findViewById(R.id.holiday_map_btn);
            edit = itemView.findViewById(R.id.holiday_edit_btn);
            delete = itemView.findViewById(R.id.holiday_delete_btn);
        }
    }
}
