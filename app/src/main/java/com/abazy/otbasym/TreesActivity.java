package com.abazy.otbasym;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.parser.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class TreesActivity extends AppCompatActivity {

    List<Map<String, String>> treeList;
    SimpleAdapter adapter;
    View progress;


    private boolean autoOpenedTree; // To open automatically the tree at startup only once
    // The birthday notification IDs are stored to display the corresponding person only once


    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.trees);
        ListView listView = findViewById(R.id.trees_list);
        progress = findViewById(R.id.trees_progress);


        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        } else {


            if (savedState != null) {
                autoOpenedTree = savedState.getBoolean("autoOpenedTree");
                }

            if (Global.settings.trees != null) {

                // Lista degli alberi genealogici
                treeList = new ArrayList<>();

                // Dà i dati in pasto all'adattatore
                adapter = new SimpleAdapter(this, treeList,
                        R.layout.fragment_tree_dots,
                        new String[]{"titolo", "dati"},
                        new int[]{R.id.albero_titolo, R.id.albero_dati}) {
                    // Individua ciascuna vista dell'elenco
                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent) {
                        View treeView = super.getView(position, convertView, parent);
                        int treeId = Integer.parseInt(treeList.get(position).get("id"));
                        Settings.Tree tree = Global.settings.getTree(treeId);
                        boolean derivato = tree.grade == 20;
                        boolean esaurito = tree.grade == 30;
                        if (derivato) {
                            treeView.setBackgroundColor(getResources().getColor(R.color.accent_medium));
                            ((TextView) treeView.findViewById(R.id.albero_dati)).setTextColor(getResources().getColor(R.color.text));
                            treeView.setOnClickListener(v -> {
                                tree.grade = 10; // viene retrocesso
                                Global.settings.save();
                                updateList();
                                Toast.makeText(TreesActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();

                            });
                        } else if (esaurito) {
                            treeView.setBackgroundColor(getResources().getColor(R.color.consumed));
                            ((TextView) treeView.findViewById(R.id.albero_titolo)).setTextColor(getResources().getColor(R.color.gray_text));
                            treeView.setOnClickListener(v -> {

                                tree.grade = 10; // viene retrocesso
                                Global.settings.save();
                                updateList();
                                Toast.makeText(TreesActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();

                            });
                        } else {
                            treeView.setBackgroundColor(getResources().getColor(R.color.back_element));
                            treeView.setOnClickListener(v -> {
                                progress.setVisibility(View.VISIBLE);
                                if (!(Global.gc != null && treeId == Global.settings.openTree)) { // se non è già aperto
                                    if (!openGedcom(treeId, true)) {
                                        progress.setVisibility(View.GONE);
                                        return;
                                    }
                                }
                                startActivity(new Intent(TreesActivity.this, Principal.class));
                            });
                        }
                        treeView.findViewById(R.id.albero_menu).setOnClickListener(vista -> {
                            boolean esiste = new File(getFilesDir(), treeId + ".json").exists();
                            PopupMenu popup = new PopupMenu(TreesActivity.this, vista);
                            Menu menu = popup.getMenu();
                            if (treeId == Global.settings.openTree && Global.shouldSave)
                                menu.add(0, -1, 0, R.string.save);
                            if ((Global.settings.expert && derivato) || (Global.settings.expert && esaurito))
                                menu.add(0, 0, 0, R.string.open);
                            if (!esaurito || Global.settings.expert)
                                menu.add(0, 1, 0, R.string.tree_info);
                            if ((!derivato && !esaurito) || Global.settings.expert)
                                menu.add(0, 2, 0, R.string.rename);
                            if (esiste && (!derivato || Global.settings.expert) && !esaurito)
                                menu.add(0, 3, 0, R.string.media_folders);
                            menu.add(0, 9, 0, R.string.delete);
                            popup.show();
                            popup.setOnMenuItemClickListener(item -> {
                                int id = item.getItemId();
                                if (id == -1) { // Salva
                                    U.saveJson(Global.gc, treeId);
                                    Global.shouldSave = false;
                                } else if (id == 0) { // Apre un albero derivato
                                    openGedcom(treeId, true);
                                    startActivity(new Intent(TreesActivity.this, Principal.class));
                                } else if (id == 1) { // Info Gedcom
                                    Intent intent = new Intent(TreesActivity.this, InfoActivity.class);
                                    intent.putExtra("idAlbero", treeId);
                                    startActivity(intent);
                                } else if (id == 2) { // Rinomina albero
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TreesActivity.this);
                                    View vistaMessaggio = getLayoutInflater().inflate(R.layout.title_tree, listView, false);
                                    builder.setView(vistaMessaggio).setTitle(R.string.title);
                                    EditText editaNome = vistaMessaggio.findViewById(R.id.nuovo_nome_albero);
                                    editaNome.setText(treeList.get(position).get("titolo"));
                                    AlertDialog dialogo = builder.setPositiveButton(R.string.rename, (dialog, i1) -> {
                                        Global.settings.rename(treeId, editaNome.getText().toString());
                                        updateList();
                                    }).setNeutralButton(R.string.cancel, null).create();
                                    editaNome.setOnEditorActionListener((view, action, event) -> {
                                        if (action == EditorInfo.IME_ACTION_DONE)
                                            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                                        return false;
                                    });
                                    dialogo.show();
                                    vistaMessaggio.postDelayed(() -> {
                                        editaNome.requestFocus();
                                        editaNome.setSelection(editaNome.getText().length());
                                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        inputMethodManager.showSoftInput(editaNome, InputMethodManager.SHOW_IMPLICIT);
                                    }, 300);
                                } else if (id == 3) { // Media folders
                                    startActivity(new Intent(TreesActivity.this, MediaFoldersActivity.class)
                                            .putExtra("idAlbero", treeId)
                                    );
                                } else if (id == 9) {    // Elimina albero
                                    new AlertDialog.Builder(TreesActivity.this).setMessage(R.string.really_delete_tree)
                                            .setPositiveButton(R.string.delete, (dialog, id1) -> {
                                                deleteTree(TreesActivity.this, treeId);
                                                updateList();
                                            }).setNeutralButton(R.string.cancel, null).show();
                                } else {
                                    return false;
                                }
                                return true;
                            });
                        });
                        return treeView;
                    }
                };
                listView.setAdapter(adapter);
                updateList();
            }

            // Barra personalizzata
            ActionBar bar = getSupportActionBar();
            View treesBar = getLayoutInflater().inflate(R.layout.trees_bar, null);
            treesBar.findViewById(R.id.trees_settings).setOnClickListener(v -> {
                        Intent intent = new Intent(this, SettingsActivity.class);
                        intent.putExtra("from", "trees");
                        startActivity(intent);
                    }

            );
            bar.setCustomView(treesBar);
            bar.setDisplayShowCustomEnabled(true);

            // FAB
            findViewById(R.id.fab).setOnClickListener(v -> {
                startActivity(new Intent(this, NewTreeActivity.class));
            });

        }


    }



    @Override
    protected void onResume() {
        super.onResume();
        // Nasconde la rotella, in particolare quando si ritorna indietro a questa activity
        progress.setVisibility(View.GONE);
    }

    // Essendo TreesActivity launchMode=singleTask, onRestart viene chiamato anche con startActivity (tranne il primo)
    // però ovviamente solo se TreesActivity ha chiamato onStop (facendo veloce chiama solo onPause)
    @Override
    protected void onRestart() {
        super.onRestart();
        updateList();
    }

    // New intent coming from a tapped notification


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("autoOpenedTree", autoOpenedTree);
        super.onSaveInstanceState(outState);
    }

    // If a birthday notification was tapped loads the relative tree and returns true


    void updateList() {
        treeList.clear();
        for (Settings.Tree alb : Global.settings.trees) {
            Map<String, String> dato = new HashMap<>(3);
            dato.put("id", String.valueOf(alb.id));
            dato.put("titolo", alb.title);
            // Se Gedcom già aperto aggiorna i dati
            if (Global.gc != null && Global.settings.openTree == alb.id && alb.persons < 100)
                InfoActivity.refreshData(Global.gc, alb);
            dato.put("dati", writeData(this, alb));
            treeList.add(dato);
        }
        adapter.notifyDataSetChanged();
    }

    public static String writeData(Context context, Settings.Tree alb) {
        String dati = alb.persons + " " +
                context.getString(alb.persons == 1 ? R.string.person : R.string.persons).toLowerCase();
        if (alb.persons > 1 && alb.generations > 0)
            dati += " - " + alb.generations + " " +
                    context.getString(alb.generations == 1 ? R.string.generation : R.string.generations).toLowerCase();
        if (alb.media > 0)
            dati += " - " + alb.media + " " + context.getString(R.string.media).toLowerCase();
        return dati;
    }

    // Lightly open a Gedcom tree for different purposes
    public static Gedcom openGedcomTemporarily(int treeId, boolean putInGlobal) {
        Gedcom gc;
        if (Global.gc != null && Global.settings.openTree == treeId)
            gc = Global.gc;
        else {
            gc = readJson(treeId);
            if (putInGlobal) {
                Global.gc = gc; // To be able to use for example F.oneImage()
                Global.settings.openTree = treeId; // So Global.gc and Global.settings.openTree are consistent
            }
        }
        return gc;
    }


    static boolean openGedcom(int treeId, boolean savePreferences) {
        Global.gc = readJson(treeId);
        if (Global.gc == null)
            return false;
        if (savePreferences) {
            Global.settings.openTree = treeId;
            Global.settings.save();
        }
        Global.indi = Global.settings.getCurrentTree().root;
        Global.familyNum = 0;
        Global.shouldSave = false;
        return true;
    }

    // Read the Json and return a Gedcom
    static Gedcom readJson(int treeId) {
        Gedcom gedcom;
        File file = new File(Global.context.getFilesDir(), treeId + ".json");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (Exception | Error e) {
            String message = e instanceof OutOfMemoryError ? Global.context.getString(R.string.not_memory_tree) : e.getLocalizedMessage();
            Toast.makeText(Global.context, message, Toast.LENGTH_LONG).show();
            return null;
        }
        String json = text.toString();
        json = updateLanguage(json);
        gedcom = new JsonParser().fromJson(json);
        if (gedcom == null) {
            Toast.makeText(Global.context, R.string.no_useful_data, Toast.LENGTH_LONG).show();
            return null;
        }

        return gedcom;
    }

    // Replace Italian with English in Json tree data
    // Introduced in Family Gem 0.8
    static String updateLanguage(String json) {
        json = json.replace("\"zona\":", "\"zone\":");
        json = json.replace("\"famili\":", "\"kin\":");
        json = json.replace("\"passato\":", "\"passed\":");
        return json;
    }

    public static void deleteTree(Context context, int treeId) {
        File treeFile = new File(context.getFilesDir(), treeId + ".json");
        treeFile.delete();
        File mediaDir = context.getExternalFilesDir(String.valueOf(treeId));
        deleteFilesAndDirs(mediaDir);
        if (Global.settings.openTree == treeId) {
            Global.gc = null;
        }

        Global.settings.deleteTree(treeId);
    }

    static void deleteFilesAndDirs(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles())
                deleteFilesAndDirs(child);
        }
        fileOrDirectory.delete();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


}
