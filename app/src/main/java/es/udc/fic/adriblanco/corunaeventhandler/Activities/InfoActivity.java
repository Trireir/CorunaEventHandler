package es.udc.fic.adriblanco.corunaeventhandler.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Calendars;

import com.facebook.FacebookSdk;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.Calendar;

import es.udc.fic.adriblanco.corunaeventhandler.Adapters.SimpleCustomChromeTabsHelper;
import es.udc.fic.adriblanco.corunaeventhandler.Evento;
import es.udc.fic.adriblanco.corunaeventhandler.R;

public class InfoActivity extends AppCompatActivity{
    private Evento e;

    private String PREFS_NAME = "CorunaEH";

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        e = getIntent().getParcelableExtra("event");

        CollapsingToolbarLayout toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.infoToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView iv = (ImageView) findViewById(R.id.backdrop);

        // OPCION 1: Funciona. Coje imagen guardada
        //iv.setImageResource(R.drawable.arizona_baby);

        // OPCION 2
        //new DownloadImageTask(iv).execute(e.getImage());

        // OPCION 3:
        Picasso.with(this)
                .load(e.getImage())
                //.resize(30,30)
                //.placeholder(R.drawable.arizona_baby)
                .error(R.drawable.arizona_baby)
                .into(iv);

        // OPCION 4: Descargar la imagen con:
        //      Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        //      iv.setImageBitmap(bmp);



        TextView infoName = (TextView) findViewById(R.id.infoTitle);
        infoName.setText(e.getName()+" "+e.getId());
        toolbar.setTitle(e.getName()+" "+e.getId());

        TextView infoDesc = (TextView) findViewById(R.id.infoDescription);
        infoDesc.setText(e.getDesc());

        setupMaps();
        setupFABCalendar();
        setupFacebook();

    }

    private void setupMaps(){
        final SimpleCustomChromeTabsHelper mCustomTabHelper = new SimpleCustomChromeTabsHelper(this);
        final String url = "https://www.google.es/maps?q=loc:@"+e.getLon()+","+e.getLat();

        CardView cvMaps = (CardView) findViewById(R.id.maps_card);
        cvMaps.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mCustomTabHelper.openUrl(url);
            }
        });
    }

    private void setupFABCalendar(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabFavorite);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favEvent(e)) {
                    Snackbar.make(view, "Evento añadido a favoritos", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Evento eliminado de favoritos", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void setupFacebook(){
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //Log.d(":D", FacebookSdk.getApplicationSignature(getApplicationContext()));

        CardView cvFacebook = (CardView) findViewById(R.id.facebook_card);
        cvFacebook.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Compartir en Facebook sin App", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, e.getName() + " en " + e.getPlace() + " el día " + e.getDate() + ". Enviado desde Coruña Event Handler." );
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case R.id.action_calendar:
                addEventToCalendar(e);
                Snackbar.make(getWindow().getCurrentFocus(), "Evento añadido a tu calendario", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
        default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    *   Get the first id of the list of Calendars in the system, the default.
     */
    private long getCalendarId() {
        String[] projection = new String[]{CalendarContract.Calendars._ID};
        String selection =
                Calendars.ACCOUNT_NAME +
                        " = ? AND " +
                        Calendars.ACCOUNT_TYPE +
                        " = ? ";

        int permissionCheck = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_CALENDAR);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = getContentResolver().query(
                    Calendars.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);

            if (cursor.moveToFirst()) {
                long r = cursor.getLong(0);
                cursor.close();
                return r;
            }
            cursor.close();
        }

        return -1;
    }

    private void addEventToCalendar(Evento e){
        long calID = getCalendarId();
        Log.d("Calendario ID", ""+calID);
        long startMillis = 0;

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(e.getYear(), e.getMonth()-1, e.getDay());
        startMillis = beginTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, startMillis);
        values.put(Events.TITLE, e.getName());
        values.put(Events.DESCRIPTION, e.getDesc());
        values.put(Events.EVENT_LOCATION, e.getPlace());
        values.put(Events.ALL_DAY, 1);
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.EVENT_TIMEZONE, "Europe/Madrid");
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CALENDAR);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            Uri uri = cr.insert(Events.CONTENT_URI, values);
        }
    }

    private boolean favEvent(Evento e){
        SharedPreferences preferencesReader = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesReader.edit();
        if (!preferencesReader.contains(""+e.getId())) {
            editor.putString("" + e.getId(), e.serialize());
            editor.commit();
            return true;
        }else{
            editor.remove(""+e.getId());
            editor.commit();
            return false;
        }
    }
}
