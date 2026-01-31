package com.example.hazirjanabvendorportal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class userordersproductadapter extends RecyclerView.Adapter<userordersproductadapter.ProductViewHolder> {
    private List<Product> productList;
    private List<productorders> orders;
    private Context context;
    RecyclerViewInterface recyclerViewInterface;

    public userordersproductadapter(Context context,List<productorders> orders, List<Product> productList, RecyclerViewInterface recyclerViewInterface) {
        this.context= context;
        this.orders=orders;
        this.productList = productList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.productorder, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product product = productList.get(position);
        productorders productorders=orders.get(position);

        byte[] imageBytes = product.getImage();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.ivProduct.setImageBitmap(bitmap);
        } else {
            holder.ivProduct.setImageResource(R.drawable.ic_product);
        }

        holder.tvTitle.setText(product.getName());
        holder.tvPrice.setText("PKR " + product.getPrice() + "/-");
        holder.tvQuantity.setText("x" + productorders.getQuantity());
        holder.txtQuantity.setText(String.valueOf(product.getQuantity()));
        holder.tvTotalPrice.setText("PKR " + product.getPrice() * product.getQuantity() + "/-");
        holder.quantity = productorders.getQuantity();
        Log.d("Quantity", String.valueOf(holder.quantity));


        // Set other views in the card as needed
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvTitle, tvPrice, tvQuantity, tvTotalPrice, txtQuantity;

        int quantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProductImage);
            tvTitle = itemView.findViewById(R.id.tvProductTitle);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalProductPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);

        }
    }
}
