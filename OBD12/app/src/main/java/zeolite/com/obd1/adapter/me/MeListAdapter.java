package zeolite.com.obd1.adapter.me;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import zeolite.com.obd1.R;
import zeolite.com.obd1.entity.me.MeListEntity;


/**
 * Created by Zeolite on 16/1/20.
 */
public class MeListAdapter extends BaseAdapter {

    private Context context;

    private List<MeListEntity> meListEntities;

    private Intent intent;

    public MeListAdapter(Context context,List<MeListEntity> meListEntities){
        this.context=context;
        this.meListEntities=meListEntities;
    }

    @Override
    public int getCount() {
        return meListEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return meListEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.me_list_item,null);

            viewHolder=new ViewHolder();
            viewHolder.icon= (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.name=(TextView)convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);


        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MeListEntity meListEntity=meListEntities.get(position);
        viewHolder.name.setText(meListEntity.getName());

        if (meListEntity.getIconName().equals("locate")){
            viewHolder.icon.setImageResource(R.drawable.map_local);
        }


        return convertView;
    }

    class ViewHolder{
        private ImageView icon;
        private TextView name;
    }
}
