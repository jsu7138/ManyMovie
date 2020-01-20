package com.example.a219.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class LaterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<Item_card_later> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_later);

        items = MainActivity.lateritems;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_later);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MylaterAdapter(items);
        recyclerView.setAdapter(adapter);

    }

    public class MylaterAdapter extends RecyclerView.Adapter<MylaterAdapter.ViewHolder>{
        private ArrayList<Item_card_later> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mImageView;
            public TextView TextView;
            public TextView eTextView;
            public TextView pTextView;
            public TextView dTextView;
            public CardView cardView;

            public ViewHolder(View logview) {
                super(logview);
                mImageView = (ImageView)logview.findViewById(R.id.card_img_later);
                TextView = (TextView)logview.findViewById(R.id.card_title_later);
                eTextView = (TextView)logview.findViewById(R.id.card_etitle_later);
                pTextView = (TextView)logview.findViewById(R.id.card_ptitle_later);
                dTextView = (TextView)logview.findViewById(R.id.card_dtitle_later);
                cardView = (CardView)logview.findViewById(R.id.cardview_later);
            }
        }

        public MylaterAdapter(ArrayList<Item_card_later> k) {
            mDataset = k;
        }

        @Override
        public MylaterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_later, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final int count = mDataset.size()-1 - position;
            holder.mImageView.setImageBitmap(mDataset.get(count).bitmap);
            holder.TextView.setText(mDataset.get(count).TItle);
            holder.eTextView.setText(mDataset.get(count).eTitle);
            holder.pTextView.setText(mDataset.get(count).pTitle);
            holder.dTextView.setText(mDataset.get(count).dTitle);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LaterActivity.this,info_Activity.class);
                    i.putExtra("title",mDataset.get(count).TItle);
                    i.putExtra("etitle",mDataset.get(count).eTitle);
                    i.putExtra("ptitle",mDataset.get(count).pTitle);
                    i.putExtra("dtitle",mDataset.get(count).dTitle);
                    i.putExtra("atitle",mDataset.get(count).aTitle);
                    i.putExtra("rtitle",mDataset.get(count).rTitle);
                    i.putExtra("bitmap",mDataset.get(count).bitmap);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    public void onBackPressed() {
        finish();
    }
}
