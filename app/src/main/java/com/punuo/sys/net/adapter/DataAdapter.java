package com.punuo.sys.net.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.punuo.sys.net.R;
import com.punuo.sys.sdk.recyclerview.BaseRecyclerViewAdapter;

import java.util.List;

public class DataAdapter extends BaseRecyclerViewAdapter<Content> {

    public DataAdapter (Context context,List<Content> contentList){
        super(context,contentList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.data_item,parent,false);
        DataViewHolder viewHolder = new DataViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder baseViewHolder, int position) {
        if(baseViewHolder instanceof DataViewHolder){
           ((DataViewHolder) baseViewHolder).bindData(mData.get(position));
        }
    }

    @Override
    public int getBasicItemType(int position) {
        return 0;
    }

    @Override
    public int getBasicItemCount() {
        return mData ==null? 0:mData.size();
    }

    static class DataViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView content;
        public DataViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.item_title);
            content = view.findViewById(R.id.item_content);
        }

        public void bindData(Content mContent){
            title.setText(mContent.getTitle());
            content.setText(mContent.getContent());
        }
    }
}
