package es.udc.fic.adriblanco.corunaeventhandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStream;
import java.net.URL;

/*
    Class to take all the information about one Event
 */
public class Evento implements Parcelable{
    private static int count = 0;
    private String section;
    private String name;
    private String place;
    //private String date;
    private int year;
    private int month;
    private int day;
    private String price;
    private String desc;
    private double lon;
    private double lat;
    private String image;
    private String id;

    private Bitmap bmp;
    public Evento(){
        section = "Música";
        name = "Arizona Baby";
        place = "Sala Mardigrass";
        year = 2016;
        month = 5;
        day = 19;
        price = "10";
        desc = "El VIERNES 8 de abril visita nuestro club ARIZONA BABY, en la ya extensa gira de su disco Secret Fires, que ha ido triunfando por salas y festivales de España. Austeridad sonora, filosofía básica, folk acústico, barba y aire indomable son algunas de las características del grupo encabezado por Javier Vielba, el inconfundible Meister que también ha producido el álbum. ARIZONA BABY saben crear atomosferas y ambientazo y llevarte a lugares muy alejados de Madrid con sus notas y actitud. Por si fuera poco para la noche, abrirán Los Wallas, el grupo de dandis garajeros de origen manchego que tiene cautivados a muchos fans malasañeros. No te pierdas esta noche que evoca a humo, tugurio, guitarra y carreteras.\n" +
                "\n" +
                "http://www.subterfuge.com/artistas/arizona-baby\n" +
                "\n" +
                "Entradas:\n" +
                "12€ oferta de lanzamiento / 15€ anticipada // 20€ taquilla\n" +
                "Entradas anticipadas en: http://ochoymedioclub.com/2016/04/arizona-baby/\n" +
                "Apertura: 20:30h\n" +
                "\n" +
                "OCHOYMEDIO (sala But)\n" +
                "C/ Barceló 11 <Tribunal> Madrid5";

        // FUNCIONAN
        //image = "https://pbs.twimg.com/media/Cky4STaWkAEWD1R.jpg";
        image = "https://s-media-cache-ak0.pinimg.com/736x/74/98/31/74983195dfdc64d8044692230ee702a4.jpg";
        //image = "https://i.ytimg.com/vi/BfOdWSiyWoc/maxresdefault.jpg";
        //image = "http://img05.deviantart.net/c214/i/2008/255/d/5/_rhcp__by_rafaelmh9.jpg";

        //NO FUNCIONAN
        //image = "http://www.spotlightreport.net/wp-content/uploads/2011/08/red-hot-chili-peppers-banner.jpg";
        //image = "http://www.projectrevolver.org/wp-content/uploads/2013/05/rhcp1-251x300.gif";
        //image = "http://www.subterfuge.com/imagen2/imgbio/1412671867-Arizona_Baby.jpg";
        //image = "http://www.dc.fi.udc.es/web/images/stories/fotos/ficentrada.jpg";
        lon = 43.3746242;
        lat = -8.3982111;
        id = ""+count;
        count++;
    }
    // Section, Name, Desc, Year, Month, Day, Price,  image, Place, lon, lat
    public Evento(String s, String n, String d, int y, int m, int da, String pr, String i, String p, double lo, double la){
        section = s;
        name = n;
        place = p;
        desc = d;
        year = y;
        month = m;
        day = da;
        price = pr;
        image = i;
        lon = lo;
        lat = la;
        id = name + y + m + da;
    }

    private Evento(Parcel in){
        readFromParcel(in);
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Evento create(String data) {
        Gson gson = new Gson();

        return gson.fromJson(data, Evento.class);
    }

    public String getId() {
        return id;
    }

    public String getSection() {
        return section;
    }

    public String getName() {
        return name;
    }

    public String getPlace() { return place; }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getDate() {
        return getDay()+"/"+getMonth()+"/"+getYear();
    }

    public String getPrice() {
        return price;
    }

    public String getDesc() {return desc; }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(section);
        dest.writeString(name);
        dest.writeString(place);
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeString(price);
        dest.writeString(desc);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(image);
        //Parcel bm = Parcel.obtain();
        //bitImage.writeToParcel(bm, 0);
        dest.writeString(id);
    }

    private void readFromParcel(Parcel in){
        section = in.readString();
        name = in.readString();
        place = in.readString();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        price = in.readString();
        desc = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        image = in.readString();

        //bitImage = Bitmap.CREATOR.createFromParcel(in);
        id = in.readString();
    }

    public static final Parcelable.Creator<Evento> CREATOR = new Parcelable.Creator<Evento>() {
        public Evento createFromParcel(Parcel in) {
            return new Evento(in);
        }

        public Evento[] newArray(int size) {
            return new Evento[size];
        }
    };

    private class downloadImages extends AsyncTask {

        @Override
        protected Bitmap doInBackground(Object[] params) {
            try {
                URL url = new URL((String) params[0]);
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return bmp;
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
            }
            return null;
        }

    }
}
