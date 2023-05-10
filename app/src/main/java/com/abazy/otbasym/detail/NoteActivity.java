package com.abazy.otbasym.detail;

import android.app.Activity;

import org.folg.gedcom.model.Note;

import com.abazy.otbasym.DetailActivity;
import com.abazy.otbasym.Global;
import com.abazy.otbasym.Memory;
import com.abazy.otbasym.R;
import com.abazy.otbasym.U;
import com.abazy.otbasym.visitor.NoteReferences;

public class NoteActivity extends DetailActivity {

    Note n;

    @Override
    public void format() {
        n = (Note)cast(Note.class);
        if (n.getId() == null) {
            setTitle(R.string.note);
            placeSlug("NOTE");
        } else {
            setTitle(R.string.shared_note);
            placeSlug("NOTE", n.getId());
        }
        place(getString(R.string.text), "Value", true, true);
        place(getString(R.string.rin), "Rin", false, false);
        placeExtensions(n);
        U.placeSourceCitations(box, n);
        U.placeChangeDate(box, n.getChange());
        if (n.getId() != null) {
            NoteReferences noteRefs = new NoteReferences(Global.gc, n.getId(), false);
            if (noteRefs.count > 0)
                U.placeCabinet(box, noteRefs.leaders.toArray(), R.string.shared_by);
        } else if (((Activity)box.getContext()).getIntent().getBooleanExtra("fromNotes", false)) {
            U.placeCabinet(box, Memory.firstObject(), R.string.written_in);
        }
    }

    @Override
    public void delete() {
        U.updateChangeDate(U.deleteNote(n, null));
    }
}
