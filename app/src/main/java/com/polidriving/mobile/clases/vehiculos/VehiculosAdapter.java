//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.vehiculos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.polidriving.mobile.R;
import java.util.List;

public class VehiculosAdapter extends RecyclerView.Adapter<VehiculosAdapter.VehiculoViewHolder> {
    
    private List<Vehiculo> vehiculos;
    private OnVehiculoListener onVehiculoListener;

    public interface OnVehiculoListener {
        void onEditClick(Vehiculo vehiculo);
        void onDeleteClick(Vehiculo vehiculo);
        void onSelectClick(Vehiculo vehiculo);
    }

    public VehiculosAdapter(List<Vehiculo> vehiculos, OnVehiculoListener onVehiculoListener) {
        this.vehiculos = vehiculos;
        this.onVehiculoListener = onVehiculoListener;
    }

    @NonNull
    @Override
    public VehiculoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehiculo, parent, false);
        return new VehiculoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehiculoViewHolder holder, int position) {
        Vehiculo vehiculo = vehiculos.get(position);
        holder.bind(vehiculo);
    }

    @Override
    public int getItemCount() {
        return vehiculos.size();
    }

    public class VehiculoViewHolder extends RecyclerView.ViewHolder {
        private CardView cardVehiculo;
        private TextView tvMarca;
        private TextView tvModelo;
        private TextView tvAno;
        private TextView tvPlaca;
        private TextView tvActual;
        private ImageView ivActual;
        private Button btnEditar;
        private Button btnEliminar;
        private Button btnSeleccionar;

        public VehiculoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardVehiculo = itemView.findViewById(R.id.cardVehiculo);
            tvMarca = itemView.findViewById(R.id.tvMarca);
            tvModelo = itemView.findViewById(R.id.tvModelo);
            tvAno = itemView.findViewById(R.id.tvAno);
            tvPlaca = itemView.findViewById(R.id.tvPlaca);
            tvActual = itemView.findViewById(R.id.tvActual);
            ivActual = itemView.findViewById(R.id.ivActual);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionar);
        }

        public void bind(Vehiculo vehiculo) {
            tvMarca.setText(vehiculo.getMarca());
            tvModelo.setText(vehiculo.getModelo());
            tvAno.setText(vehiculo.getAnoFabricacion());
            tvPlaca.setText(vehiculo.getPlaca());
            
            if (vehiculo.isActual()) {
                // Cambiar el color de fondo de la tarjeta para destacar
                cardVehiculo.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.color_vehiculo_actual));
                tvActual.setVisibility(View.VISIBLE);
                ivActual.setVisibility(View.VISIBLE);
                btnSeleccionar.setVisibility(View.GONE);
                
                // Cambiar color del texto para mejor contraste
                tvMarca.setTextColor(itemView.getContext().getResources().getColor(R.color.color_blanco));
                tvModelo.setTextColor(itemView.getContext().getResources().getColor(R.color.color_blanco));
                tvAno.setTextColor(itemView.getContext().getResources().getColor(R.color.color_blanco));
                tvPlaca.setTextColor(itemView.getContext().getResources().getColor(R.color.color_blanco));
            } else {
                // Color normal para vehículos no seleccionados
                cardVehiculo.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.color_blanco));
                tvActual.setVisibility(View.GONE);
                ivActual.setVisibility(View.GONE);
                btnSeleccionar.setVisibility(View.VISIBLE);
                
                // Color de texto normal
                tvMarca.setTextColor(itemView.getContext().getResources().getColor(R.color.color_negro));
                tvModelo.setTextColor(itemView.getContext().getResources().getColor(R.color.color_hint));
                tvAno.setTextColor(itemView.getContext().getResources().getColor(R.color.color_hint));
                tvPlaca.setTextColor(itemView.getContext().getResources().getColor(R.color.color_negro));
            }

            btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVehiculoListener.onEditClick(vehiculo);
                }
            });

            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVehiculoListener.onDeleteClick(vehiculo);
                }
            });

            btnSeleccionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVehiculoListener.onSelectClick(vehiculo);
                }
            });
        }
    }
}
