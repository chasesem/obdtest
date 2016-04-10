package zeolite.com.obd1.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import zeolite.com.obd1.R;
import zeolite.com.obd1.view.fragment.PageFragment;


/**
 * Created by XUEZE on 1/12/2016.
 */
public class TabFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    final int PAGE_COUNT=4;
    private String tabTitles[]=new String[]{"连接","表盘","记录","我的"};
    private Context context;
    private ArrayList<Fragment> listFragments;

    public View getTabView(int position){

        View view= LayoutInflater.from(context).inflate(R.layout.custom_tab,null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(tabTitles[position]);

        return view;
    }

    public TabFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context=context;
    }

    public TabFragmentPagerAdapter(FragmentManager fm,ArrayList<Fragment> listFragments,Context context){
        super(fm);
        this.context=context;
        this.listFragments=listFragments;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
//        return PageFragment.newInstance(position + 1);
        return listFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

