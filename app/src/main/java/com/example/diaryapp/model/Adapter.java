package com.example.diaryapp.model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaryapp.diary.DiaryDetails;
import com.example.diaryapp.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
    List<String> titles;
    List<String> content;

    public Adapter(List<String> title,List<String> content){
        this.titles = title;
        this.content = content;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_view_layout,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.diaryTitle.setText(titles.get(position));
        holder.diaryContent.setText(content.get(position));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), DiaryDetails.class);
                i.putExtra("title", titles.get(position));
                i.putExtra("content", content.get(position));
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView diaryTitle, diaryContent;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            diaryTitle = itemView.findViewById(R.id.titles);
            diaryContent = itemView.findViewById(R.id.content);
            view = itemView;
        }
    }
}
