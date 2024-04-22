package com.yonusa.cercasyonusaplus.ui.rutinas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yonusa.cercasyonusaplus.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CantidadAdapter extends RecyclerView.Adapter<CantidadAdapter.ViewHolder> {
    Context context;
    ArrayList<String> arrayList;
    CantidadListener cantidadListener;
    View view;
    ArrayList<String> arrayList_0= new ArrayList<>();

    public CantidadAdapter(Context context, ArrayList<String> arrayList, CantidadListener cantidadListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.cantidadListener = cantidadListener;
    }

    public View getView() {
        return view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_dias,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull CantidadAdapter.ViewHolder holder, int position) {
if (arrayList != null && arrayList.size()>0){
    holder.checkBox.setText(arrayList.get(position));
    holder.checkBox.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (holder.checkBox.isChecked()){
                arrayList_0.add(arrayList.get(position));
            }else{
                arrayList_0.remove(arrayList.get(position));
            }
            cantidadListener.onCantidadCambio(arrayList_0);
            }
        });
    }
 }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBox_dias);
        }
    }
}
