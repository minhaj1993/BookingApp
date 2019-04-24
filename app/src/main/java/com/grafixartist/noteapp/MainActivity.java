package com.grafixartist.noteapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fab;

    BooksAdapter adapter;
    List<Book> books = new ArrayList<>();

    long initialCount;

    int modifyPos = -1;


    SharedPreferences sharedPreferences;
    TextView clearpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences=getSharedPreferences(Content.PREF_NAME,MODE_PRIVATE);
        Log.d("Main", "onCreate");

        recyclerView = (RecyclerView) findViewById(R.id.main_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        clearpref= (TextView) findViewById(R.id.clearpref);

        clearpref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setLayoutManager(gridLayoutManager);

        initialCount = Book.count(Book.class);

        if (savedInstanceState != null)
            modifyPos = savedInstanceState.getInt("modify");


        if (initialCount >= 0) {

            books = Book.listAll(Book.class);

            adapter = new BooksAdapter(MainActivity.this, books);
            recyclerView.setAdapter(adapter);

            if (books.isEmpty())
                Snackbar.make(recyclerView, "No books added.", Snackbar.LENGTH_LONG).show();

        }

        // tinting FAB icon
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_add_24dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Color.WHITE);
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);

            fab.setImageDrawable(drawable);

        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, AddBookActivity.class);
                startActivity(i);

            }
        });


        // Handling swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //Remove swiped item from list and notify the RecyclerView

                final int position = viewHolder.getAdapterPosition();
                final Book book = books.get(viewHolder.getAdapterPosition());
                books.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(position);

                book.delete();
                initialCount -= 1;

                Snackbar.make(recyclerView, "Book deleted", Snackbar.LENGTH_SHORT)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                book.save();
                                books.add(position, book);
                                adapter.notifyItemInserted(position);
                                initialCount += 1;

                            }
                        })
                        .show();
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        adapter.SetOnItemClickListener(new BooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Log.d("Main", "click");

                Intent i = new Intent(MainActivity.this, AddBookActivity.class);
                i.putExtra("isEditing", true);
                i.putExtra("note_title", books.get(position).title);
                i.putExtra("note", books.get(position).note);
                i.putExtra("note_time", books.get(position).time);

                modifyPos = position;

                startActivity(i);
            }
        });


//        List<Book> books = Book.findWithQuery(Book.class, "Select title from Book where title = ?", "%note%");
//        if (books.size() > 0)
//            Log.d("Notes", "note: " + books.get(0).title);


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("modify", modifyPos);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        modifyPos = savedInstanceState.getInt("modify");
    }

    @Override
    protected void onResume() {
        super.onResume();

        final long newCount = Book.count(Book.class);

        if (newCount > initialCount) {
            // A book is added
            Log.d("Main", "Adding new book");

            // Just load the last added book (new)
            Book book = Book.last(Book.class);

            books.add(book);
            adapter.notifyItemInserted((int) newCount);

            initialCount = newCount;
        }

        if (modifyPos != -1) {
            books.set(modifyPos, Book.listAll(Book.class).get(modifyPos));
            adapter.notifyItemChanged(modifyPos);
        }

    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateFormat(long date) {
        return new SimpleDateFormat("dd MMM yyyy").format(new Date(date));
    }


}
