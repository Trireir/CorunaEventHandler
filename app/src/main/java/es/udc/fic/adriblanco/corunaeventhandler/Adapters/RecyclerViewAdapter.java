package es.udc.fic.adriblanco.corunaeventhandler.Adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import es.udc.fic.adriblanco.corunaeventhandler.Activities.InfoActivity;
import es.udc.fic.adriblanco.corunaeventhandler.Evento;
import es.udc.fic.adriblanco.corunaeventhandler.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.EventViewHolder>{

    List<Evento> eventos;

    public RecyclerViewAdapter(List<Evento> e){
        eventos = e;
    }

    @Override
    public int getItemCount() {
        return (eventos == null) ? 0 : eventos.size();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row, viewGroup, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        Evento e = eventos.get(i);

        //eventViewHolder.eventPhoto.setImageBitmap(e.getBitImage());
        Picasso.with(eventViewHolder.itemView.getContext()).load(e.getImage()).into(eventViewHolder.eventPhoto);
        eventViewHolder.eventName.setText(e.getName());
        eventViewHolder.eventPlace.setText(e.getPlace());
        eventViewHolder.eventDate.setText(e.stringDate());
        eventViewHolder.eventPrice.setText(e.getPrice());
        eventViewHolder.setEvento(e);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView eventPrice;
        TextView eventName;
        TextView eventPlace;
        TextView eventDate;
        ImageView eventPhoto;

        Evento event;

        public EventViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            eventPrice = (TextView) itemView.findViewById(R.id.event_price);
            eventName  = (TextView) itemView.findViewById(R.id.event_name);
            eventPlace = (TextView) itemView.findViewById(R.id.event_place);
            eventDate  = (TextView) itemView.findViewById(R.id.event_date);
            eventPhoto = (ImageView)itemView.findViewById(R.id.event_photo);
        }

        public void setEvento(Evento e){
            event = e;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), InfoActivity.class);
            intent.putExtra("event",event);
            v.getContext().startActivity(intent);
        }
    }


}