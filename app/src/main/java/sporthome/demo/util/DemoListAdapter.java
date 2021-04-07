package sporthome.demo.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sporthome.demo.R;
import sporthome.demo.sportgym.Gym;

/**
 * Demo列表Adapter
 */

public class DemoListAdapter extends BaseAdapter {

    private List<Gym> gyms;
    private Context mContext;

    public DemoListAdapter(Context context, List<Gym> gyms){
        super();
        this.gyms = gyms;
        this.mContext = context;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.room_info_item, null);
        }
        ImageView title =  convertView.findViewById(R.id.iv);
        TextView desc = (TextView) convertView.findViewById(R.id.content);
        title.setImageResource(R.drawable.sport);
        desc.setText(gyms.get(index).getName()+gyms.get(index).getId());
        return convertView;
    }

    @Override
    public int getCount() {
        return gyms.size();
    }

    @Override
    public Object getItem(int index) {
        return gyms.get(index);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }
}
