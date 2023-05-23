package com.abazy.otbasym;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.abazy.otbasym.Constants.Format;
import com.abazy.otbasym.Constants.Kind;

public class DateEditorLayout extends LinearLayout {

    GedcomDateConverter gedcomDateConverter;
    GedcomDateConverter.Data data1;
    GedcomDateConverter.Data data2;
    EditText editText;
    String[] days = {"-", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    String[] months = {"-", s(R.string.january), s(R.string.february), s(R.string.march), s(R.string.april), s(R.string.may), s(R.string.june),
            s(R.string.july), s(R.string.august), s(R.string.september), s(R.string.october), s(R.string.november), s(R.string.december)};
    String[] years = new String[101];
    int[] dateKinds = {R.string.exact, R.string.approximate, R.string.calculated, R.string.estimated,
            R.string.after, R.string.before, R.string.between_and,
            R.string.from, R.string.to, R.string.from_to, R.string.date_phrase};
    Calendar calendar = GregorianCalendar.getInstance();
    boolean trueInputText; // determines if the user is actually typing on the virtual keyboard or if the text is changed in some other way
    InputMethodManager keyboard;
    boolean keyboardVisible;

    public DateEditorLayout(Context context, AttributeSet as) {
        super(context, as);
    }

    /**
     * Actions to be done only once at the beginning.
     */
    void initialize(final EditText editText) {

        addView(inflate(getContext(), R.layout.edit_data, null), this.getLayoutParams());
        this.editText = editText;

        for (int i = 0; i < years.length - 1; i++)
            years[i] = i < 10 ? "0" + i : "" + i;
        years[100] = "-";

        gedcomDateConverter = new GedcomDateConverter(editText.getText().toString());
        data1 = gedcomDateConverter.data1;
        data2 = gedcomDateConverter.data2;




        findViewById(R.id.editdata_advanced).setVisibility(GONE);


        arredaCarro(1, findViewById(R.id.first_day), findViewById(R.id.first_month),
                findViewById(R.id.first_century), findViewById(R.id.first_year));

        arredaCarro(2, findViewById(R.id.second_day), findViewById(R.id.second_month),
                findViewById(R.id.second_century), findViewById(R.id.second_year));

        // At first focus it shows itself (EditData) hiding the keyboard
        keyboard = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.setOnFocusChangeListener((v, ciapaFocus) -> {
            if (ciapaFocus) {
                if (gedcomDateConverter.kind == Kind.PHRASE) {

                    editText.setText(gedcomDateConverter.phrase);
                } else {
                    keyboardVisible = keyboard.hideSoftInputFromWindow(editText.getWindowToken(), 0); // ok nasconde tastiera
                    editText.setInputType(InputType.TYPE_NULL); // disable keyboard text input

                }
                gedcomDateConverter.data1.date = null; // a reset
                impostaTutto();
                setVisibility(View.VISIBLE);
            } else
                setVisibility(View.GONE);
        });

        // The second touch brings up the keyboard
        editText.setOnTouchListener((vista, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT); // riabilita l'input
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                keyboardVisible = keyboard.showSoftInput(editText, 0); // fa ricomparire la tastiera
                //veroImputtext = true;
                //vista.performClick(); non ne vedo l'utilità
            }
            return false;
        });
        // Set the date publisher based on what is written
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable text) {
                if (trueInputText)
                    impostaTutto();
                trueInputText = true;
            }
        });
    }


    void arredaCarro(final int which, final NumberPicker numberPicker, final NumberPicker numberPicker1, final NumberPicker numberPicker2, final NumberPicker numberPicker3) {
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(31);
        numberPicker.setDisplayedValues(days);
        prepareWheel(numberPicker);
        numberPicker.setOnValueChangedListener((picker, vecchio, nuovo) ->
                aggiorna(which == 1 ? data1 : data2, numberPicker, numberPicker1, numberPicker2, numberPicker3)
        );
        numberPicker1.setMinValue(0);
        numberPicker1.setMaxValue(12);
        numberPicker1.setDisplayedValues(months);
        prepareWheel(numberPicker1);
        numberPicker1.setOnValueChangedListener((picker, vecchio, nuovo) ->
                aggiorna(which == 1 ? data1 : data2, numberPicker, numberPicker1, numberPicker2, numberPicker3)
        );
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(20);
        prepareWheel(numberPicker2);
        numberPicker2.setOnValueChangedListener((picker, vecchio, nuovo) ->
                aggiorna(which == 1 ? data1 : data2, numberPicker, numberPicker1, numberPicker2, numberPicker3)
        );
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(100);
        numberPicker3.setDisplayedValues(years);
        prepareWheel(numberPicker3);
        numberPicker3.setOnValueChangedListener((picker, vecchio, nuovo) ->
                aggiorna(which == 1 ? data1 : data2, numberPicker, numberPicker1, numberPicker2, numberPicker3)
        );
    }

    void prepareWheel(NumberPicker wheel) {
        // Removes the dividing blue lines on API <= 22
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                Field field = NumberPicker.class.getDeclaredField("mSelectionDivider");
                field.setAccessible(true);
                field.set(wheel, null);
            } catch (Exception e) {
            }
        }
        // Fixes the bug https://issuetracker.google.com/issues/37055335
        wheel.setSaveFromParentEnabled(false);
    }

    // Prende la stringa data, aggiorna le Date e ci modifica tutto l'editore data
    // Chiamato quando clicco sul campo editabile, e dopo ogni editazione del text
    void impostaTutto() {
        gedcomDateConverter.analyze(editText.getText().toString());

        ((TextView)findViewById(R.id.editadata_tipi)).setText(dateKinds[gedcomDateConverter.kind.ordinal()]);

        // Primo carro
        impostaCarro(data1, findViewById(R.id.first_day), findViewById(R.id.first_month),
                findViewById(R.id.first_century), findViewById(R.id.first_year));
        if (Global.settings.expert)
            impostaCecchi(data1);

        // Secondo carro
        if (gedcomDateConverter.kind == Kind.BETWEEN_AND || gedcomDateConverter.kind == Kind.FROM_TO) {
            impostaCarro(data2, findViewById(R.id.second_day), findViewById(R.id.second_month),
                    findViewById(R.id.second_century), findViewById(R.id.second_year));
            if (Global.settings.expert) {
                findViewById(R.id.editeddate_second_advanced).setVisibility(VISIBLE);
                impostaCecchi(data2);
            }
            findViewById(R.id.editeddate_second).setVisibility(VISIBLE);
        } else {
            findViewById(R.id.editeddate_second_advanced).setVisibility(GONE);
            findViewById(R.id.editeddate_second).setVisibility(GONE);
        }
    }

    // Gira le ruote di un carro in base a una data
    void impostaCarro(GedcomDateConverter.Data data, NumberPicker ruotaGiorno, NumberPicker ruotaMese, NumberPicker ruotaSecolo, NumberPicker ruotaAnno) {
        calendar.clear();
        if (data.date != null)
            calendar.setTime(data.date);
        ruotaGiorno.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        if (data.date != null && (data.isFormat(Format.D_M_Y) || data.isFormat(Format.D_M)))
            ruotaGiorno.setValue(data.date.getDate());
        else
            ruotaGiorno.setValue(0);
        if (data.date == null || data.isFormat(Format.Y))
            ruotaMese.setValue(0);
        else
            ruotaMese.setValue(data.date.getMonth() + 1);
        if (data.date == null || data.isFormat(Format.D_M))
            ruotaSecolo.setValue(0);
        else
            ruotaSecolo.setValue((data.date.getYear() + 1900) / 100);
        if (data.date == null || data.isFormat(Format.D_M))
            ruotaAnno.setValue(100);
        else
            ruotaAnno.setValue((data.date.getYear() + 1900) % 100);
    }

    // Imposta i Checkbox per una data che può essere negativa e doppia
    void impostaCecchi(GedcomDateConverter.Data data) {
        CheckBox ceccoBC, ceccoDoppia;
        if (data.equals(data1)) {
            ceccoBC = findViewById(R.id.editadata_negativa2);
            ceccoDoppia = findViewById(R.id.editadata_doppia2);
        } else {
            ceccoBC = findViewById(R.id.editadata_negativa2);
            ceccoDoppia = findViewById(R.id.editadata_doppia2);
        }
        if (data.date == null || data.isFormat(Format.EMPTY) || data.isFormat(Format.D_M)) { // date senza anno
            ceccoBC.setVisibility(INVISIBLE);
            ceccoDoppia.setVisibility(INVISIBLE);
        } else {
            ceccoBC.setChecked(data.negative);
            ceccoBC.setVisibility(VISIBLE);
            ceccoDoppia.setChecked(data.doubleDate);
            ceccoDoppia.setVisibility(VISIBLE);
        }
    }

    // Aggiorna una Data coi nuovi valori presi dalle ruote
    void aggiorna(GedcomDateConverter.Data data, NumberPicker ruotaGiorno, NumberPicker ruotaMese, NumberPicker ruotaSecolo, NumberPicker ruotaAnno) {
        if (keyboardVisible) {    // Nasconde eventuale tastiera visibile
            keyboardVisible = keyboard.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            // Nasconde subito la tastiera, ma ha bisogno di un secondo tentativo per restituire false. Comunque non è un problema
        }
        int giorno = ruotaGiorno.getValue();
        int mese = ruotaMese.getValue();
        int secolo = ruotaSecolo.getValue();
        int anno = ruotaAnno.getValue();
        // Imposta i giorni del mese in ruotaGiorno
        calendar.set(secolo * 100 + anno, mese - 1, 1);
        ruotaGiorno.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        if (data.date == null) data.date = new Date();
        data.date.setDate(giorno == 0 ? 1 : giorno);  // altrimenti la data M_A arretra di un mese
        data.date.setMonth(mese == 0 ? 0 : mese - 1);
        data.date.setYear(anno == 100 ? -1899 : secolo * 100 + anno - 1900);
        if (giorno != 0 && mese != 0 && anno != 100)
            data.format.applyPattern(Format.D_M_Y);
        else if (giorno != 0 && mese != 0)
            data.format.applyPattern(Format.D_M);
        else if (mese != 0 && anno != 100)
            data.format.applyPattern(Format.M_Y);
        else if (anno != 100)
            data.format.applyPattern(Format.Y);
        else
            data.format.applyPattern(Format.EMPTY);
        impostaCecchi(data);
        trueInputText = false;
        genera();
    }

    // Ricostruisce la stringa con la data finale e la mette in editatext
    void genera() {
        String rifatta;
        if (gedcomDateConverter.kind == Kind.EXACT)
            rifatta = rifai(data1);
        else if (gedcomDateConverter.kind == Kind.BETWEEN_AND)
            rifatta = "BET " + rifai(data1) + " AND " + rifai(data2);
        else if (gedcomDateConverter.kind == Kind.FROM_TO)
            rifatta = "FROM " + rifai(data1) + " TO " + rifai(data2);
        else if (gedcomDateConverter.kind == Kind.PHRASE) {
            // La frase viene sostituita da data esatta
            gedcomDateConverter.kind = Kind.EXACT;
            ((TextView)findViewById(R.id.editadata_tipi)).setText(dateKinds[0]);
            rifatta = rifai(data1);
        } else
            rifatta = gedcomDateConverter.kind.prefix + " " + rifai(data1);
        editText.setText(rifatta);
    }

    // Scrive la singola data in base al formato
    String rifai(GedcomDateConverter.Data data) {
        String fatta = "";
        if (data.date != null) {
            // Date con l'anno doppio
            if (data.doubleDate && !(data.isFormat(Format.EMPTY) || data.isFormat(Format.D_M))) {
                Date unAnnoDopo = new Date();
                unAnnoDopo.setYear(data.date.getYear() + 1);
                String secondoAnno = String.format(Locale.ENGLISH, "%tY", unAnnoDopo);
                fatta = data.format.format(data.date) + "/" + secondoAnno.substring(2);
            } else // Le altre date normali
                fatta = data.format.format(data.date);
        }
        if (data.negative)
            fatta += " B.C.";
        return fatta;
    }

    /**
     * If the date is a phrase adds parentheses around it.
     */
    public void finishEditing() {
        if (gedcomDateConverter.kind == Kind.PHRASE) {
            String s = "(" + editText.getText() + ")";
            editText.setText(s);
        }
    }

    String s(int id) {
        return Global.context.getString(id);
    }
}
