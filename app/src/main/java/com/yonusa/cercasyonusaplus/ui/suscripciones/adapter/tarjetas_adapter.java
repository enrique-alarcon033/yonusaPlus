package com.yonusa.cercasyonusaplus.ui.suscripciones.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.suscripciones.Detalle_tarjeta;
import com.yonusa.cercasyonusaplus.ui.suscripciones.Tarjeta_pagar;
import com.yonusa.cercasyonusaplus.ui.suscripciones.modelo.paquetes_model;
import com.yonusa.cercasyonusaplus.ui.suscripciones.modelo.tarjetas_model;

import java.util.ArrayList;


/**
 * Created by javiexpo on 26/7/16.
 */
public class tarjetas_adapter extends RecyclerView.Adapter<tarjetas_adapter.ViewHolder>  {
    private Context mContextCol;
    private ArrayList<tarjetas_model> mDatasetcol;
    String codigo;



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
            TextView nombre,numero,id_card,tipo,fecha,prederminada;
        ImageView on,off, tarjeta_num;
        Context context;
        LinearLayout vista;
        ConstraintLayout fondo;
        CardView tarjeta;
        public Transition transition;

        ViewHolder(View v) {
            super(v);
            context = v.getContext();
           // contenido = (TextView) v.findViewById(R.id.Nombre_Categoria);
           // iduser =(TextView) v.findViewById(R.id.Virtual_Categoria_id);
            tarjeta_num= (ImageView) v.findViewById(R.id.img_tarjeta_item);
            numero = (TextView) v.findViewById(R.id.numero_tarjeta_item);
            nombre = (TextView) v.findViewById(R.id.nombre_tarjeta_item);
            id_card = (TextView) v.findViewById(R.id.card_id_item);
            tipo    = (TextView) v.findViewById(R.id.tipo_tarjeta);
            fecha = (TextView) v.findViewById(R.id.fecha_item);
            vista = (LinearLayout) v.findViewById(R.id.layout_nombre_paquete);
            tarjeta =(CardView) v.findViewById(R.id.tarjeta_vista);
            fondo = (ConstraintLayout) v.findViewById(R.id.fondo_tarjeta);
            prederminada= (TextView) v.findViewById(R.id.predeterminada_item);

        }

        void setOnClickListener()    {
            tarjeta.setOnClickListener(this);
        }



      @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tarjeta_vista:
                 Intent i = new Intent(context, Detalle_tarjeta.class);
                     i.putExtra("id_card",id_card.getText());
                    i.putExtra("nombre", nombre.getText());
                    i.putExtra("numero", numero.getText());
                    i.putExtra("fecha", fecha.getText());
                    context.startActivity(i);
                    break;
            }
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public tarjetas_adapter(Context contextcol, ArrayList<tarjetas_model> myDatasetco) {
        mDatasetcol = myDatasetco;
        mContextCol= contextcol;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarjetas, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//////////
        /////////////
        //////////////


     //   Glide.with(mContextCol).load(mDatasetcol.get(position).getImageUrlco()).into(holder.mImgtViewco);
       holder.id_card.setText(mDatasetcol.get(position).getCardId());
        holder.nombre.setText(mDatasetcol.get(position).getNombre());
        holder.numero.setText(mDatasetcol.get(position).getTerminacion());
        holder.tipo.setText(mDatasetcol.get(position).getCard());
        holder.fecha.setText(mDatasetcol.get(position).getMes()+" / "+mDatasetcol.get(position).getYear());

        SharedPreferences misPreferencias = mContextCol.getSharedPreferences("Suscripcion", Context.MODE_PRIVATE);
        String predeter = misPreferencias.getString("idTarjeta", "NS");

        if (holder.id_card.getText().toString().equals(predeter)){
            holder.prederminada.setVisibility(View.VISIBLE);
        }

        if (holder.tipo.getText().toString().equals("American Express")){
            holder.tarjeta_num.setBackgroundResource(R.drawable.ic_american);
            holder.fondo.setBackgroundResource(R.drawable.fondo_negro);
        }else if(holder.tipo.getText().toString().equals("Visa")){
            holder.tarjeta_num.setBackgroundResource(R.drawable.ic_visa);
        }else
        {
            holder.tarjeta_num.setBackgroundResource(R.drawable.ic_master);
            holder.fondo.setBackgroundResource(R.drawable.fondo_rojo);
        }
        holder.setOnClickListener();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatasetcol.size();
    }


}
