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
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.cercas.Asignar_Cerca;
import com.yonusa.cercasyonusaplus.ui.cercas.modelo.cercas_model_completo;
import com.yonusa.cercasyonusaplus.ui.suscripciones.Tarjeta_pagar;
import com.yonusa.cercasyonusaplus.ui.suscripciones.modelo.paquetes_model;

import java.util.ArrayList;


/**
 * Created by javiexpo on 26/7/16.
 */
public class paquetes_adapter extends RecyclerView.Adapter<paquetes_adapter.ViewHolder>  {
    private Context mContextCol;
    private ArrayList<paquetes_model> mDatasetcol;
    String codigo;



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
            TextView precioId,nombre,moneda, periodo,precio;
        ImageView on,off;
        Context context;
        LinearLayout vista;

        CardView tarjeta;
        public Transition transition;

        ViewHolder(View v) {
            super(v);
            context = v.getContext();
           // contenido = (TextView) v.findViewById(R.id.Nombre_Categoria);
           // iduser =(TextView) v.findViewById(R.id.Virtual_Categoria_id);
            precioId = (TextView) v.findViewById(R.id.precio_id);
            nombre = (TextView) v.findViewById(R.id.nombre_paquete);
            moneda = (TextView) v.findViewById(R.id.moneda);
            periodo = (TextView) v.findViewById(R.id.periodo);
            precio = (TextView) v.findViewById(R.id.precio);

            vista = (LinearLayout) v.findViewById(R.id.layout_nombre_paquete);
            tarjeta =(CardView) v.findViewById(R.id.tarjeta_vista);

        }

        void setOnClickListener()    {
            tarjeta.setOnClickListener(this);
        }



      @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tarjeta_vista:
                    SharedPreferences misPreferencias = mContextCol.getSharedPreferences("Suscripcion", Context.MODE_PRIVATE);
                    String priceId = misPreferencias.getString("priceId", "NS");
                    if (priceId.equals(precioId.getText().toString())){
                        Toast.makeText(mContextCol, "Ya cuentas con una suscripción a este plan, elige otro si deceas actualizarlo", Toast.LENGTH_LONG).show();
                    }else{
                        Intent i = new Intent(context, Tarjeta_pagar.class);
                        i.putExtra("precioId", precioId.getText());
                        i.putExtra("nombrePaquete", nombre.getText());
                        i.putExtra("moneda", moneda.getText());
                        i.putExtra("periodo", periodo.getText());
                        i.putExtra("precio", precio.getText());
                        context.startActivity(i);
                    }

                    break;
            }
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public paquetes_adapter(Context contextcol, ArrayList<paquetes_model> myDatasetco) {
        mDatasetcol = myDatasetco;
        mContextCol= contextcol;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paquetes, parent, false);
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
        holder.precioId.setText(mDatasetcol.get(position).getPriceId());
        holder.nombre.setText(mDatasetcol.get(position).getName());
        holder.moneda.setText(mDatasetcol.get(position).getCurrency());
        holder.periodo.setText(mDatasetcol.get(position).getRecurrency());
        holder.precio.setText(mDatasetcol.get(position).getMonto());

        SharedPreferences misPreferencias = mContextCol.getSharedPreferences("Suscripcion", Context.MODE_PRIVATE);
        String priceId = misPreferencias.getString("priceId", "NS");
        if (priceId.equals(holder.precioId.getText().toString())){
            holder.nombre.setText("Plan Actual: "+mDatasetcol.get(position).getName());
            holder.vista.setBackgroundResource(R.drawable.btn_primary);
        }else{
            //holder.precioId.setText(mDatasetcol.get(position).getPriceId()+ "Plan Actual");
        }


        holder.setOnClickListener();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatasetcol.size();
    }


}
