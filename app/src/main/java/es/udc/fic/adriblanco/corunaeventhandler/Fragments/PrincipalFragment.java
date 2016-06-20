package es.udc.fic.adriblanco.corunaeventhandler.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.udc.fic.adriblanco.corunaeventhandler.Adapters.RecyclerViewAdapter;
import es.udc.fic.adriblanco.corunaeventhandler.Evento;
import es.udc.fic.adriblanco.corunaeventhandler.R;

public class PrincipalFragment extends Fragment {

    private String category;
    protected List<Evento> dataSet = new ArrayList();

    private String PREFS_NAME = "CorunaEH";

    private FloatingActionButton fabAdd;

    public static PrincipalFragment newInstance(String c) {
        PrincipalFragment f = new PrincipalFragment();
Log.d(c, "newInstance");
        Bundle args = new Bundle();
        args.putString("category", c);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        category = getArguments().getString("category");
        dataSet = initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        fabAdd = (FloatingActionButton) rootView.findViewById(R.id.fabAddEvent);
        if (category != "Principal") fabAdd.setVisibility(View.INVISIBLE);
        else fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Añade tus propios eventos", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // The number indicates the collumns of CardViews.
        GridLayoutManager glm = new GridLayoutManager(getActivity(),1);
        rv.setLayoutManager(glm);

        RecyclerViewAdapter rva = new RecyclerViewAdapter(dataSet);
        rv.setAdapter(rva);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(category == null) return;
        if(category.equals("Principal")){
            if (isVisibleToUser) {
                Log.d(category, "VISIBLE");
                refreshPrincipalData();
            //}else{
            }
        }
    }

    private List<Evento> initData(){
        if (category.equals("Principal")){
            dataSet = new ArrayList<>();

            SharedPreferences preferencesReader = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Map<String, ?> fav = preferencesReader.getAll();

            for(Map.Entry<String, ?> eJson : fav.entrySet()){
                Evento e = Evento.create(eJson.getValue().toString());
                dataSet.add(e);
            }
        }else{
            dataSet = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                dataSet.add(new Evento());
            }
        }
        return dataSet;
    }

    public void refreshPrincipalData(){
        SharedPreferences preferencesReader = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Map<String, ?> fav = preferencesReader.getAll();
Log.d(category, "REFRESH");
        // If there are new events that aren't in the dataSet, init again.
        if (fav.size() != dataSet.size()){
            initData();
            
        }

    }

}
