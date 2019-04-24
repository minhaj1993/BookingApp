package com.grafixartist.noteapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class AddBookActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton fab;

    EditText etTitle, etDesc;

    String title, note;
    long time;

    boolean editingNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        toolbar = (Toolbar) findViewById(R.id.addnote_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_24dp);

        getSupportActionBar().setTitle("Add new Book");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        etTitle = (EditText) findViewById(R.id.addnote_title);
        etDesc = (EditText) findViewById(R.id.addnote_desc);

        fab = (FloatingActionButton) findViewById(R.id.addnote_fab);


        //  handle intent

//        editingNote = getIntent() != null;
        editingNote = getIntent().getBooleanExtra("isEditing", false);
        if (editingNote) {
            title = getIntent().getStringExtra("note_title");
            note = getIntent().getStringExtra("note");
            time = getIntent().getLongExtra("note_time", 0);

            etTitle.setText(title);
            etDesc.setText(note);

        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Add note to DB

                String newTitle = etTitle.getText().toString();
                String newDesc = etDesc.getText().toString();
                long newTime = System.currentTimeMillis();


                /**
                 * TODO: Check if note exists before saving
                 */
                if (!editingNote) {
                    Log.d("Book", "saving");
                    Book book = new Book(newTitle, newDesc, newTime);
                    book.save();
                } else {
                    Log.d("Book", "updating");

//                    List<Book> books = Book.findWithQuery(Book.class, "where title = ?", title);
                    List<Book> books = Book.find(Book.class, "title = ?", title);
                    if (books.size() > 0) {

                        Book book = books.get(0);
                        Log.d("got book", "book: " + book.title);
                        book.title = newTitle;
                        book.note = newDesc;
                        book.time = newTime;

                        book.save();

                    }

                }

                finish();


            }
        });


    }
}
