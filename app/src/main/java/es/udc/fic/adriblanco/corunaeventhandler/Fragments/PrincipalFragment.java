package es.udc.fic.adriblanco.corunaeventhandler.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.udc.fic.adriblanco.corunaeventhandler.Activities.AddEvent;
import es.udc.fic.adriblanco.corunaeventhandler.Activities.InfoActivity;
import es.udc.fic.adriblanco.corunaeventhandler.Adapters.RecyclerViewAdapter;
import es.udc.fic.adriblanco.corunaeventhandler.Evento;
import es.udc.fic.adriblanco.corunaeventhandler.MeteEventos;
import es.udc.fic.adriblanco.corunaeventhandler.R;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;

public class PrincipalFragment extends Fragment {

    private String category;
    protected List<Evento> dataSet = new ArrayList();
    private static DynamoDBMapper mapper;

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
        initAWS();
        initData();
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

                Intent intent = new Intent(v.getContext(), AddEvent.class);
                v.getContext().startActivity(intent);
                Snackbar.make(v, "Añade tus propios eventos", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (dataSet.size() == 0){
            TextView tvne = (TextView) rootView.findViewById(R.id.TVNoEvents);
            if(category != "Principal"){
                tvne.setText("Actualmente no tenemos eventos de esta categoría\n:(");
            }else{
                tvne.setText("No tienes eventos guardados :(\nPulsa en la estrella del evento para tener tus eventos localizados");
            }
        }
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
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(category == "Principal"){
            refreshPrincipalData();
        }
    }

    private void initAWS(){
        if (mapper != null) return;
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getContext(),
                "us-east-1:133fc07d-d22d-4156-a2e9-03e519872397",   // Identity Pool ID
                Regions.US_EAST_1                                   // Region
        );
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
    }

    private void initData(){
        if (category.equals("Principal")){
            dataSet = new ArrayList<>();

            SharedPreferences preferencesReader = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Map<String, ?> fav = preferencesReader.getAll();

            for(Map.Entry<String, ?> eJson : fav.entrySet()){
                Evento e = Evento.create(eJson.getValue().toString());
                dataSet.add(e);
            }
        }else{
            if (isConnectedToInternet()) {
                try {
                    new getEvents().execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }else{
                Toast.makeText(getContext(), "Fallo en la conexión a internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void refreshPrincipalData(){
        SharedPreferences preferencesReader = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Map<String, ?> fav = preferencesReader.getAll();

        // If there are new events that aren't in the dataSet, init again.
        if (fav.size() != dataSet.size()){
            //initData();
            dataSet = new ArrayList<>();
            for(Map.Entry<String, ?> eJson : fav.entrySet()){
                Evento e = Evento.create(eJson.getValue().toString());
                dataSet.add(e);
            }
            final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.detach(this);
            ft.attach(this);
            ft.commit();
        }

    }

    public void setData(List<Evento> data){
        dataSet = new ArrayList<Evento>(data);
        Collections.sort(dataSet);
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.detach(this);
        ft.attach(this);
        ft.commit();
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    private class getEvents extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            Thread.currentThread().setPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);

            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":v1", new AttributeValue().withS(category));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("Category = :v1")
                    .withExpressionAttributeValues(eav);

            List<Evento> scanResult = mapper.parallelScan(Evento.class, scanExpression, 1);

            return scanResult;
        }

        @Override
        protected void onPostExecute(Object result){
            setData((List<Evento>) result);

        }
    }

}
