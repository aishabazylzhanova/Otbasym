package com.abazy.otbasym.list;

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

import com.abazy.otbasym.Kinship;
import com.abazy.otbasym.KinshipAdapter;
import com.abazy.otbasym.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BaurFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class BaurFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<Kinship> kinships = new ArrayList<Kinship>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BaurFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaurFragment newInstance(String param1, String param2) {
        BaurFragment fragment = new BaurFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BaurFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.kinship);

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