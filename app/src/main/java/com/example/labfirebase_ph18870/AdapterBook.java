package com.example.labfirebase_ph18870;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterBook extends RecyclerView.Adapter<AdapterBook.TruyenViewHolder> {

    private List<Book> objs;
    private List<Book> objFilter;

    public void setData(List<Book> list){
        this.objs = list;

        objFilter = new ArrayList<>(objs);
        notifyDataSetChanged();
    }

    @Override
    public TruyenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcv_book,parent,false);
        return new TruyenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TruyenViewHolder holder, int position) {
        Book obj = objs.get(position);
        if (objs==null){
            return;
        }
        holder.tvName.setText(obj.getTenTruyen());
        holder.tvGia.setText(+obj.getGia()+" VNĐ");

        Picasso.get().load(obj.getLinkAnh()).into(holder.img);

    }

    @Override
    public int getItemCount() {
        if (objs != null){
            return objs.size();
        }
        return 0;
    }

    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        objs.clear();
        if (charText.length() == 0){
            objs.addAll(objFilter);
        }else {
            for (Book ls : objFilter) {
                if (ls.getTenTruyen().toLowerCase(Locale.getDefault()).contains(charText)){
                    objs.add(ls);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class TruyenViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName,tvGia;
        private ImageView img;


        public TruyenViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.item_rcv_tentruyen);
            tvGia = itemView.findViewById(R.id.item_rcv_gia);

            img = itemView.findViewById(R.id.item_rcv_img);
        }
    }
}
