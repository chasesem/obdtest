package zeolite.com.obd1.adapter.record;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import zeolite.com.obd1.R;


/**
 * Created by Zeolite on 16/1/21.
 */

public class RecordGridAdapter extends BaseAdapter {
    private ArrayList<String> mNameList = new ArrayList<String>();

    private LayoutInflater mInflater;
    private Context mContext;
    LinearLayout.LayoutParams params;

    private int clickTemp = -1;
    //标识选择的Item
    public void setSeclection(int position) {
        clickTemp = position;
    }

    public RecordGridAdapter(Context context, ArrayList<String> nameList) {
        mNameList = nameList;
        mContext = context;
        mInflater = LayoutInflater.from(context);

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
    }

    public int getCount() {
        return mNameList.size();
    }

    public Object getItem(int position) {
        return mNameList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemViewTag viewTag;

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.record_gridview_item, null);

            // construct an item tag
            viewTag = new ItemViewTag( (TextView) convertView.findViewById(R.id.grid_name),(FrameLayout)convertView.findViewById(R.id.grid_bg));
            convertView.setTag(viewTag);
        } else
        {
            viewTag = (ItemViewTag) convertView.getTag();
        }

        if (clickTemp == position) {
            viewTag.gridBg.setBackgroundColor(convertView.getResources().getColor(R.color.colorAccent));

        }

//        viewTag.gridBg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewTag.gridBg.setBackgroundColor(v.getResources().getColor(R.color.colorAccent));
//            }
//        });


        // set name
        viewTag.mName.setText(mNameList.get(position));

        return convertView;
    }

    class ItemViewTag
    {

        protected TextView mName;
        private FrameLayout gridBg;

        public ItemViewTag(TextView name, FrameLayout viewById)
        {
            this.mName = name;
            this.gridBg=viewById;
        }
    }


}
