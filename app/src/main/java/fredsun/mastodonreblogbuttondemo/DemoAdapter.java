package fredsun.mastodonreblogbuttondemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by fred on 2018/4/13.
 */

public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.DemoHolder> {
    private ArrayList<String> datas;
    public DemoAdapter(ArrayList<String> datas) {
        this.datas = datas;
    }

    @Override
    public DemoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main, parent, false);
        DemoHolder demoHolder = new DemoHolder(inflate);
        return demoHolder;
    }

    @Override
    public void onBindViewHolder(DemoHolder holder, int position) {
            holder.itemText.setText(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class DemoHolder extends RecyclerView.ViewHolder{
        TextView itemText;
        public DemoHolder(View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.textMain);
        }
    }
}
