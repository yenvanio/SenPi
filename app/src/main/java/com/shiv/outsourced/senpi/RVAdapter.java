package com.shiv.outsourced.senpi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.ImageView;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventViewHolder> {

    public List<QRCode> codes = new ArrayList<>();
    public Context context;

    RVAdapter(List<QRCode> codes, Context context) {
        this.codes = codes;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        EventViewHolder evh = new EventViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        try {
            eventViewHolder.name.setText(codes.get(i).getName());
            eventViewHolder.temp.setText(codes.get(i).getTemp());
            eventViewHolder.light.setText(codes.get(i).getLighting());

        } catch (NullPointerException e) {
        }
        //eventViewHolder.placeIcon.setImageResource(events.get(i).);
    }

    @Override
    public int getItemCount() {
        return codes.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView name, light, temp;
        ImageView qr;

        EventViewHolder(final View itemView) {
            super(itemView);
            final Context context = itemView.getContext();
            cv = (CardView) itemView.findViewById(R.id.cv);

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertadd = new AlertDialog.Builder(context);
                    LayoutInflater factory = LayoutInflater.from(context);
                    final View view2 = factory.inflate(R.layout.sample, null);

                    if (view2.getParent() != null) {
                        ((ViewGroup) view2.getParent()).removeView(view2);
                    }

                    ImageView qr = (ImageView) view2.findViewById(R.id.qrCode);
                    qr.setImageBitmap(codes.get(itemView.getVerticalScrollbarPosition()).getImage());
                    alertadd.setView(view2);
                    alertadd.show();
                }
            });

            name = (TextView) itemView.findViewById(R.id.name);
            light = (TextView) itemView.findViewById(R.id.textLight);
            temp = (TextView) itemView.findViewById(R.id.textTemp);
        }
    }
}