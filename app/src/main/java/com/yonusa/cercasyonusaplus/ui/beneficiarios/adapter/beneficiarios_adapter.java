package com.yonusa.cercasyonusaplus.ui.beneficiarios.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.telephony.IccOpenLogicalChannelResponse;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yonusa.cercasyonusaplus.R;
import com.yonusa.cercasyonusaplus.ui.beneficiarios.Eliminar_Beneficiario;
import com.yonusa.cercasyonusaplus.ui.beneficiarios.mocelo.beneficiario_model;
import com.yonusa.cercasyonusaplus.ui.cercas.Asignar_Cerca;
import com.yonusa.cercasyonusaplus.ui.cercas.modelo.cercas_model_completo;

import java.util.ArrayList;


/**
 * Created by javiexpo on 26/7/16.
 */
public class beneficiarios_adapter extends RecyclerView.Adapter<beneficiarios_adapter.ViewHolder>  {
    private Context mContextCol;
    private ArrayList<beneficiario_model> mDatasetcol;
    String codigo;



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
            TextView nombre,apellido,email,rfc,id_beneficiaroodelo;
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
            nombre = (TextView) v.findViewById(R.id.alias_completo);
           apellido = (TextView) v.findViewById(R.id.apellido_ben);
            email=(TextView)v.findViewById(R.id.tv_asignar_correo);
            rfc = (TextView) v.findViewById(R.id.rfc_ben);
            id_beneficiaroodelo=(TextView) v.findViewById(R.id.id_user_completo);

           vista = (LinearLayout) v.findViewById(R.id.layout_tarjeta);
            tarjeta =(CardView) v.findViewById(R.id.tarjeta_vista);
            on = (ImageView)v.findViewById(R.id.ok_on);
            off = (ImageView)v.findViewById(R.id.ok_off);

           // texto_asignado= (TextView) v.findViewById(R.id.tv_asignar_correo);


        }

        void setOnClickListener()    {
            tarjeta.setOnClickListener(this);
        }



      @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tarjeta_vista:
                 Intent i = new Intent(context, Eliminar_Beneficiario.class);
                    i.putExtra("idBeneficiario", id_beneficiaroodelo.getText());
                    i.putExtra("nombre", nombre.getText());
                    i.putExtra("apellido", apellido.getText());
                   i.putExtra("correo", email.getText());
                   i.putExtra("rfc",rfc.getText().toString());
                    context.startActivity(i);
                    break;
            }
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public beneficiarios_adapter(Context contextcol, ArrayList<beneficiario_model> myDatasetco) {
        mDatasetcol = myDatasetco;
        mContextCol= contextcol;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beneficiario_item, parent, false);
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
        holder.nombre.setText(mDatasetcol.get(position).getNombre());
//        holder.apellido.setText(mDatasetcol.get(position).getApellidos());
    holder.email.setText(mDatasetcol.get(position).getEmail());
    holder.rfc.setText(mDatasetcol.get(position).getRfc());
        holder.id_beneficiaroodelo.setText(mDatasetcol.get(position).getIdBeneficiario());
        holder.apellido.setText(mDatasetcol.get(position).getApellidos());

        holder.setOnClickListener();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatasetcol.size();
    }


}
