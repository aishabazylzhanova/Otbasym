package com.abazy.otbasym.Details;

import android.app.Activity;

import org.folg.gedcom.model.Note;

import com.abazy.otbasym.DetailActivity;
import com.abazy.otbasym.Global;
import com.abazy.otbasym.Memory;
import com.abazy.otbasym.R;
import com.abazy.otbasym.U;
import com.abazy.otbasym.Visitors.NoteReferences;

public class NoteActivity extends DetailActivity {

    Note note;

    @Override
    public void format() {
        note = (Note)cast(Note.class);
        if (note.getId() == null) {
            setTitle(R.string.note);
            placeSlug("NOTE");
        } else {
            setTitle(R.string.shared_note);
            placeSlug("NOTE", note.getId());
        }
        place(getString(R.string.text), "Value", true, true);
        place(getString(R.string.rin), "Rin", false, false);
        placeExtensions(note);
        U.placeSourceCitations(box, note);
        U.placeChangeDate(box, note.getChange());
        if (note.getId() != null) {
            NoteReferences noteRefs = new NoteReferences(Global.gc, note.getId(), false);
            if (noteRefs.count > 0)
                U.placeCabinet(box, noteRefs.leaders.toArray(), R.string.shared_by);
        } else if (((Activity)box.getContext()).getIntent().getBooleanExtra("fromNotes", false)) {
            U.placeCabinet(box, Memory.firstObject(), R.string.written_in);
        }
    }

    @Override
    public void delete() {
        U.updateChangeDate(U.deleteNote(note, null));
    }
}
