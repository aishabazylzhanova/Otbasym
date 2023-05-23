package com.abazy.otbasym.Visitors;

import org.folg.gedcom.model.Note;
import org.folg.gedcom.model.NoteContainer;
import org.folg.gedcom.model.Repository;
import org.folg.gedcom.model.Source;
import java.util.ArrayList;
import java.util.List;
import com.abazy.otbasym.Global;

/**
 * Visitor collecting a list of inline (not shared) notes.
 */
public class NoteList extends TotalVisitor {

    public List<Note> noteList = new ArrayList<>();

    @Override
    boolean visit(Object object, boolean isLeader) {
        if (object instanceof NoteContainer
                && !(!Global.settings.expert && (object instanceof Source || object instanceof Repository))) {
            NoteContainer container = (NoteContainer)object;
            noteList.addAll(container.getNotes());
        }
        return true;
    }
}
