package es.udc.fic.adriblanco.corunaeventhandler.Activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;


import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import es.udc.fic.adriblanco.corunaeventhandler.Evento;
import es.udc.fic.adriblanco.corunaeventhandler.R;


public class AddEvent extends AppCompatActivity{
    TextView tName;
    TextView tDesc;
    TextView tPrec;
    TextView tPlac;
    TextView tFech;
    TextView tImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_activity);
        tName = (TextView) findViewById(R.id.name);
        tDesc = (TextView) findViewById(R.id.description);
        tPrec = (TextView) findViewById(R.id.price);
        tPlac = (TextView) findViewById(R.id.place);
        tFech = (TextView) findViewById(R.id.date);
        tImg = (TextView) findViewById(R.id.image);

        final Spinner dropdown = (Spinner)findViewById(R.id.category);
        String[] items = new String[]{
                "Música",
                "Cine",
                "Teatro",
                "Infantil",
                "Ciencia",
                "Deportes"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Button b = (Button) findViewById(R.id.addEvent);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = (String) dropdown.getSelectedItem();
                //Section, Name, Desc, Year, Month, Day, Price,  image, Place, lon, lat
                Evento e = new Evento(s, tName.getText().toString(), tDesc.getText().toString(), tFech.getText().toString(), tPrec.getText().toString(), tImg.getText().toString(), tPlac.getText().toString());


                if (checkForm()){
                    Snackbar.make(v, "Evento subido con éxito. Proximamente aparecerá en la lista.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    new saveEvents().execute(e);
                }else{
                    Snackbar.make(v, "Rellene todos los campos.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        Button b1 = (Button) findViewById(R.id.autocomplete);
        b1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Evento e = neutralEvent();
                if (tName.getText().toString().equals("")) tName.setText(e.getName());
                if (tDesc.getText().toString().equals("")) tDesc.setText(e.getDesc());
                if (tFech.getText().toString().equals("")) tFech.setText(e.stringDate());
                if (tPrec.getText().toString().equals("")) tPrec.setText(e.getPrice());
                if (tImg.getText().toString().equals("")) tImg.setText(e.getImage());
                if (tPlac.getText().toString().equals("")) tPlac.setText(e.getPlace());
            }
        });
    }

    private boolean checkForm(){
        if (tName.getText().toString().equals("")) return false;
        if (tDesc.getText().toString().equals("")) return false;
        if (tFech.getText().toString().equals("")) return false;
        if (tPrec.getText().toString().equals("")) return false;
        if (tImg.getText().toString().equals("")) return false;
        if (tPlac.getText().toString().equals("")) return false;
        return true;
    }

    private Evento neutralEvent(){
        return new Evento(
                "Música",
                "Jay · Dois · The Vog ✿ Ilustrados por Bea Lema · A. Gaudino",
                "O colectivo artístico Seara Records leva varios anos axitando o panorama, tanto dende Vigo como dende o resto das cidades onde van plantando as súas sementes. Autoedición, organización de concertos, exposicións, festivais, vídeo... Na súa faceta como selo discográfico contan entre outras con figuras tan puxantes como o punk-psicodelia-arrasador de Jay, o pop magnífico de Dois ou o dark-synth-punk de The Vog. Este sábado teremos a ocasión de disfrutar destas tres bandas nun marco irrepetible: coa súa música ilustrada en directo por Bea Lema e Alejandro Gaudino, nun dos concertos ilustrados coorganizado por Autobán. Unha combinación de cinco estrelas adicada ós paladares máis exquisitos!\n" +
                        "\n" +
                        "JAY: https://jayvigo.bandcamp.com/\n" +
                        "DOIS: http://doisdois.bandcamp.com/\n" +
                        "THE VOG: https://thevog.bandcamp.com/\n" +
                        "\n" +
                        "BEA LEMA: http://bealema.tumblr.com/\n" +
                        "GAUDINO: http://elgaudo.tumblr.com/",
                "23/07/2016",
                "5€",
                "https://scontent-mad1-1.xx.fbcdn.net/t31.0-8/13458496_1759339874345868_5193493549181947642_o.jpg",
                "Nave 1839"
        );
    }

    private class saveEvents extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "us-east-1:133fc07d-d22d-4156-a2e9-03e519872397",   // Identity Pool ID
                    Regions.US_EAST_1                                   // Region
            );
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            mapper.save(params[0]);
            return null;
        }
    }
}

