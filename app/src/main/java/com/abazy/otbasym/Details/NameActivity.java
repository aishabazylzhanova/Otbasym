package com.abazy.otbasym.Details;

import static com.abazy.otbasym.Global.gc;

import org.folg.gedcom.model.Name;
import org.folg.gedcom.model.Person;

import com.abazy.otbasym.DetailActivity;
import com.abazy.otbasym.Global;
import com.abazy.otbasym.Memory;
import com.abazy.otbasym.R;
import com.abazy.otbasym.U;

public class NameActivity extends DetailActivity {

    Name name;

    @Override
    public void format() {
        setTitle(R.string.name);
        placeSlug("NAME", null);
        name = (Name)cast(Name.class);
        if (Global.settings.expert)
            place(getString(R.string.value), "Value");
        else {
            String givenName = "";
            String surname = "";
            String value = name.getValue();
            if (value != null) {
                givenName = value.replaceAll("/.*?/", "").trim(); // Remove the surname
                if (value.indexOf('/') < value.lastIndexOf('/'))
                    surname = value.substring(value.indexOf('/') + 1, value.lastIndexOf('/')).trim();
            }
            placePiece(getString(R.string.given), givenName, 4043, false);
            placePiece(getString(R.string.surname), surname, 6064, false);
        }
        place(getString(R.string.nickname), "Nickname");
        place(getString(R.string.type), "Type", true, false); // _TYPE in GEDCOM 5.5, TYPE in GEDCOM 5.5.1

        placeExtensions(name);
        U.placeNotes(box, name, true);
        U.placeMedia(box, name, true); // Per GEDCOM 5.5.1 a Name should not contain Media
        U.placeSourceCitations(box, name);
    }

    @Override
    public void delete() {
        Person currentPerson = gc.getPerson(Global.indi);
        currentPerson.getNames().remove(name);
        U.updateChangeDate(currentPerson);
        Memory.setInstanceAndAllSubsequentToNull(name);
    }
}
