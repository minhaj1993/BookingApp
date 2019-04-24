package com.grafixartist.noteapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.NoteVH> {
    Context context;
    List<Book> books;

    OnItemClickListener clickListener;

    public BooksAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;

    }


    @Override
    public NoteVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        NoteVH viewHolder = new NoteVH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NoteVH holder, int position) {

        holder.title.setText(books.get(position).getTitle());
        holder.note.setText(books.get(position).getNote());

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class NoteVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, note;

        public NoteVH(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.note_item_title);
            note = (TextView) itemView.findViewById(R.id.note_item_desc);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}
