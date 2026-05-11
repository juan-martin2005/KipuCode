package com.kipucode.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kipucode.R;

import java.util.List;

public class SliderTextAdapter extends RecyclerView.Adapter<SliderTextAdapter.SliderViewHolder> {

    private final List<String> mensajes;

    public SliderTextAdapter(List<String> mensajes) {
        this.mensajes = mensajes;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.descripcion.setText(mensajes.get(position));
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        TextView descripcion;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Enlazamos con el ID que le pusiste en item_slider.xml
            descripcion = itemView.findViewById(R.id.subHeadline);
        }
    }
}