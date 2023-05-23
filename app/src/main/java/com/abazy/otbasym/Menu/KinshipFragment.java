package com.abazy.otbasym.Menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abazy.otbasym.Constants.Kinship;
import com.abazy.otbasym.R;

import java.util.ArrayList;
import java.util.Objects;


public class KinshipFragment extends Fragment {


    ArrayList<Kinship> kinships = new ArrayList<Kinship>();

    public KinshipFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.kinship);

        return inflater.inflate(R.layout.fragment_baur, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setInitialData();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_kinship);
        KinshipAdapter adapter = new KinshipAdapter(getContext(), kinships);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void setInitialData() {
        kinships.add(new Kinship ("Ата", "баланың әкесінің және анасының әкесі."));
        kinships.add(new Kinship ("Әже ", "баланың әкесінің, сондай-ақ, анасының шешесі. Одан арғылары үлкен әже деп аталады.."));
        kinships.add(new Kinship ("Әке ", "баласы бар ер адам."));
        kinships.add(new Kinship ("Ана", "балалы әйел, туған шеше."));
        kinships.add(new Kinship ("Аға", "бірге туған ағайындылардың ер жағынан жасы үлкені."));
        kinships.add(new Kinship ("Іні", "бауырлас ер адамдардың жас жағынан кішісі."));
        kinships.add(new Kinship ("Бауыр", "бірге туған қыздардың үлкендеріне бауырлас ер адамдардың жасы жағынан кішісі."));
        kinships.add(new Kinship ("Әпке, апа", "бірге туған қыздардың жас жағынан үлкені."));
        kinships.add(new Kinship ("Сіңілі", "бірге туған қыздардың үлкендеріне жас жағынан кішісі."));
        kinships.add(new Kinship ("Қарындас ", "бірге туған ағайынды ер адамдардан жасы кіші қыздар."));

    }
}