package com.abazy.otbasym.Menu;

import static com.abazy.otbasym.Global.gc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.folg.gedcom.model.Note;
import org.folg.gedcom.model.NoteContainer;
import org.folg.gedcom.model.NoteRef;

import java.util.ArrayList;
import java.util.List;

import com.abazy.otbasym.Memory;
import com.abazy.otbasym.R;
import com.abazy.otbasym.U;
import com.abazy.otbasym.Constants.Choice;
import com.abazy.otbasym.Details.NoteActivity;
import com.abazy.otbasym.Visitors.FindStack;
import com.abazy.otbasym.Visitors.NoteList;

public class NotesFragment extends Fragment implements NotesAdapter.ItemClickListener {

    NotesAdapter adapter;

    public static List<Note> getAllNotes(boolean sharedOnly) {
        // Shared notes
        List<Note> sharedNotes = gc.getNotes();
        ArrayList<Note> noteList = new ArrayList<>();
        noteList.addAll(sharedNotes);
        // Inline notes
        if (!sharedOnly) {
            NoteList noteVisitor = new NoteList();
            gc.accept(noteVisitor);
            noteList.addAll(noteVisitor.noteList);
        }
        return noteList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        boolean sharedOnly = getActivity().getIntent().getBooleanExtra(Choice.NOTE, false);
        List<Note> allNotes = getAllNotes(sharedOnly);
        adapter = new NotesAdapter(getContext(), allNotes, sharedOnly);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(allNotes.size() + " "
                + getString(allNotes.size() == 1 ? R.string.note : R.string.notes).toLowerCase());
        setHasOptionsMenu(allNotes.size() > 1);
        registerForContextMenu(recyclerView);
        view.findViewById(R.id.fab).setOnClickListener(v -> newNote(getContext(), null));
        return view;
    }

    // Leaving the activity without choosing a shared note resets the extra
    @Override
    public void onPause() {
        super.onPause();
        getActivity().getIntent().removeExtra(Choice.NOTE);
    }

    @Override
    public void onItemClick(View view, int position) {
        Note note = adapter.getItem(position);
        // Returns the ID of a note to ProfileActivity and DetailActivity
        if (getActivity().getIntent().getBooleanExtra(Choice.NOTE, false)) {
            Intent intent = new Intent();
            intent.putExtra("noteId", note.getId());
            getActivity().setResult(AppCompatActivity.RESULT_OK, intent);
            getActivity().finish();
        } else { // Opens the note detail
            Intent intent = new Intent(getContext(), NoteActivity.class);
            if (note.getId() != null) { // Shared note
                Memory.setFirst(note);
            } else { // Simple note
                new FindStack(gc, note);
                intent.putExtra("fromNotes", true);
            }
            getContext().startActivity(intent);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 0) { // Delete
            Object[] leaders = U.deleteNote(adapter.selectedNote, null);
            U.save(false, leaders);
            getActivity().recreate();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Search inside notes
        inflater.inflate(R.menu.search, menu);
        final SearchView searchView = (SearchView)menu.findItem(R.id.search_item).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String q) {
                searchView.clearFocus();
                return false;
            }
        });
    }

    /**
     * Creates a new shared Note, attached or not to a container.
     *
     * @param container If not null the Note will be attached to it
     */
    public static void newNote(Context context, Object container) {
        Note note = new Note();
        String id = U.newID(gc, Note.class);
        note.setId(id);
        note.setValue("");
        gc.addNote(note);
        if (container != null) {
            NoteRef noteRef = new NoteRef();
            noteRef.setRef(id);
            ((NoteContainer)container).addNoteRef(noteRef);
        }
        U.save(true, note);
        Memory.setFirst(note);
        context.startActivity(new Intent(context, NoteActivity.class));
    }
}
