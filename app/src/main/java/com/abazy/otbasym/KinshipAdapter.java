package com.abazy.otbasym;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class KinshipAdapter  extends RecyclerView.Adapter<KinshipAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<Kinship> kinships;

    public KinshipAdapter(Context context, List<Kinship> kinships) {
        this.kinships = kinships;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public KinshipAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(KinshipAdapter.ViewHolder holder, int position) {
        Kinship kinship = kinships.get(position);

        holder.nameView.setText(kinship.getName());
        holder.definitionView.setText(kinship.getDefinition());
    }

    @Override
    public int getItemCount() {
        return kinships.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView nameView, definitionView;
        ViewHolder(View view){
            super(view);
            nameView = view.findViewById(R.id.name);
            definitionView = view.findViewById(R.id.definition);
        }
    }
}