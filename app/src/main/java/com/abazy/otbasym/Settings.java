
package com.abazy.otbasym;

import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Represents the app preferences saved in '/data/data/com.abazy.otbasym/files/settings.json'.
 */
public class Settings {

    /**
     * It is "start" as soon as the app is installed (when 'settings.json' doesn't exist).
     * If the installation comes from a sharing it receives a 'dateId' as "20203112005959" to download the shared tree.
     * Then it becomes null and remains null forever.
     * If 'settings.json' is deleted it re-becomes "start" and then immediately null.
     */
    String referrer;
    List<Tree> trees;
    /**
     * Number of the tree currently opened. '0' means not any particular tree.
     * Must be consistent with the {@link Global#gc} opened tree.
     * It is not reset by closing the tree, to load last opened tree at startup.
     */
    public int openTree;
    boolean autoSave;
    /**
     * At startup load last opened tree.
     */
    boolean loadTree;
    /**
     * Used to display or hide all advanced tools.
     */
    public boolean expert;
    Diagram diagram;

    /**
     * Initializes first boot values.
     * False booleans don't need to be initialized.
     */
    void init() {
        referrer = "start";
        trees = new ArrayList<>();
        autoSave = true;
        diagram = new Diagram().init();
    }

    int max() {
        int num = 0;
        for (Tree tree : trees) {
            if (tree.id > num)
                num = tree.id;
        }
        return num;
    }

    void add(Tree tree) {
        trees.add(tree);
    }

    void rename(int id, String nuovoNome) {
        for (Tree tree : trees) {
            if (tree.id == id) {
                tree.title = nuovoNome;
                break;
            }
        }
        save();
    }

    void deleteTree(int id) {
        for (Tree tree : trees) {
            if (tree.id == id) {
                trees.remove(tree);
                break;
            }
        }
        if (id == openTree) {
            openTree = 0;
        }
        save();
    }

    public void save() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            FileUtils.writeStringToFile(new File(Global.context.getFilesDir(), "settings.json"), json, "UTF-8");
        } catch (Exception e) {
            Toast.makeText(Global.context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // The tree currently open
    public Tree getCurrentTree() {
        for (Tree alb : trees) {
            if (alb.id == openTree)
                return alb;
        }
        return null;
    }

    public Tree getTree(int treeId) {

        if (trees == null) {
            trees = Global.settings.trees;
        }
        if (trees != null)
            for (Tree tree : trees) {
                if (tree.id == treeId) {
                    if (tree.uris == null) // traghettatore inserito in Family Gem 0.7.15
                        tree.uris = new LinkedHashSet<>();
                    return tree;
                }
            }
        return null;
    }

    static class Diagram {
        int ancestors;
        int uncles;
        int descendants;
        int siblings;
        int cousins;
        boolean spouses;

        // Default values
        Diagram init() {
            ancestors = 3;
            uncles = 2;
            descendants = 3;
            siblings = 2;
            cousins = 1;
            spouses = true;
            return this;
        }
    }

    public static class Tree {
        public int id;
        public String title;
        Set<String> dirs;
        Set<String> uris;
        int persons;
        int generations;
        int media;
        public String root;

        public int grade;


        Tree(int id, String title, String dir, int persons, int generations, String root, int grade) {
            this.id = id;
            this.title = title;
            dirs = new LinkedHashSet<>();
            if (dir != null)
                dirs.add(dir);
            uris = new LinkedHashSet<>();
            this.persons = persons;
            this.generations = generations;
            this.root = root;
            this.grade = grade;

        }

    }





}
