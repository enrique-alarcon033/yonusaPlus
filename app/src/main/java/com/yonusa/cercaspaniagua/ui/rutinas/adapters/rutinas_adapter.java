package com.yonusa.cercaspaniagua.ui.rutinas.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yonusa.cercaspaniagua.R;
import com.yonusa.cercaspaniagua.ui.rutinas.eliminar_rutina;
import com.yonusa.cercaspaniagua.ui.rutinas.modelo.rutinas_m;

import java.util.ArrayList;


/**
 * Created by javiexpo on 26/7/16.
 */
public class rutinas_adapter extends RecyclerView.Adapter<rutinas_adapter.ViewHolder>  {
    private Context mContextCol;
    private ArrayList<rutinas_m> mDatasetcol;
    String codigo;



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
            TextView id_rutinas,usuario_rutina,cerca_rutina,comando_rutina,zona_rutina,dia_rutina,hora_rutina,nombre_rutina,status_rutina;
        Context context;
        LinearLayout vista;

        CardView tarjeta;
        public Transition transition;

        ViewHolder(View v) {
            super(v);
            context = v.getContext();
           // contenido = (TextView) v.findViewById(R.id.Nombre_Categoria);
           // iduser =(TextView) v.findViewById(R.id.Virtual_Categoria_id);
           id_rutinas = (TextView) v.findViewById(R.id.id_rutinas);
            usuario_rutina = (TextView) v.findViewById(R.id.usuario_id_rutina);
            cerca_rutina = (TextView) v.findViewById(R.id.cerca_mac_rutinas);
            comando_rutina = (TextView) v.findViewById(R.id.comando_rutina);
            zona_rutina = (TextView) v.findViewById(R.id.zona_rutina);
            dia_rutina = (TextView) v.findViewById(R.id.dia_rutina);
            hora_rutina = (TextView) v.findViewById(R.id.hora_rutina);
            nombre_rutina = (TextView) v.findViewById(R.id.nombre_rutina);
            status_rutina = (TextView) v.findViewById(R.id.status_rutina);
           vista = (LinearLayout) v.findViewById(R.id.layout_tarjeta);
            tarjeta =(CardView) v.findViewById(R.id.tarjeta_vista);




        }

        void setOnClickListener()    {
            tarjeta.setOnClickListener(this);
        }



      @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tarjeta_vista:
                   Intent i = new Intent(context, eliminar_rutina.class);
                    i.putExtra("id", id_rutinas.getText());
                    i.putExtra("id_user", usuario_rutina.getText());
                    i.putExtra("mac", cerca_rutina.getText());
                    i.putExtra("comando", comando_rutina.getText());
                    i.putExtra("zona", zona_rutina.getText());
                    i.putExtra("dia", dia_rutina.getText());
                    i.putExtra("hora", hora_rutina.getText());
                    i.putExtra("nombre", nombre_rutina.getText());
                    i.putExtra("status", status_rutina.getText());
                   context.startActivity(i);
                    ((Activity)context).finish();

                    break;
            }
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public rutinas_adapter(Context contextcol, ArrayList<rutinas_m> myDatasetco) {
        mDatasetcol = myDatasetco;
        mContextCol= contextcol;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rutinas_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//////////
        /////////////
        //////////////


     //   Glide.with(mContextCol).load(mDatasetcol.get(position).getImageUrlco()).into(holder.mImgtViewco);
        holder.id_rutinas.setText(mDatasetcol.get(position).getId());
        holder.usuario_rutina.setText(mDatasetcol.get(position).getUsuarioId());
    holder.cerca_rutina.setText(mDatasetcol.get(position).getCercaMAC());
    holder.comando_rutina.setText(mDatasetcol.get(position).getComando());
        holder.zona_rutina.setText(mDatasetcol.get(position).getZona());
        holder.dia_rutina.setText(mDatasetcol.get(position).getDia());
        holder.hora_rutina.setText(mDatasetcol.get(position).getHora());
        holder.nombre_rutina.setText(mDatasetcol.get(position).getNombre());
        holder.status_rutina.setText(mDatasetcol.get(position).getStatus());




        holder.setOnClickListener();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatasetcol.size();
    }


}
