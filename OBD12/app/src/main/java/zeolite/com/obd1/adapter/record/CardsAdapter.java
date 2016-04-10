package zeolite.com.obd1.adapter.record;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import zeolite.com.obd1.R;
import zeolite.com.obd1.db.RecordCRUB;
import zeolite.com.obd1.entity.record.RecordEntity;

/**
 * Created by Zeolite on 16/1/21.
 */
public class CardsAdapter extends BaseAdapter {


    private List<String> items;
    private final View.OnClickListener itemButtonClickListener;
    private final Context context;

    public CardsAdapter(Context context, List<String> items, View.OnClickListener itemButtonClickListener) {
        this.items = items;
        this.itemButtonClickListener = itemButtonClickListener;
        this.context = context;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.list_item_card,null);

            holder=new ViewHolder();
            holder.itemText=(TextView)convertView.findViewById(R.id.list_item_card_text);
            holder.itemButton1= (Button) convertView.findViewById(R.id.list_item_card_button_1);
            holder.itemButton2=(Button)convertView.findViewById(R.id.list_item_card_button_2);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        holder.itemText.setText(items.get(position));


        RecordCRUB recordCRUB=new RecordCRUB(context);
        List<RecordEntity> recordEntities=recordCRUB.findAllRecord();

//        for(int i=0;i<recordEntities.size();i++){
            RecordEntity recordEntity=recordEntities.get(position);

            holder.itemButton1.setText(recordEntity.getFixitem());
            holder.itemButton2.setText(recordEntity.getCost());

//        }



        return convertView;
    }

    private static class ViewHolder{
        private TextView itemText;
        private Button itemButton1;
        private Button itemButton2;
    }

}
