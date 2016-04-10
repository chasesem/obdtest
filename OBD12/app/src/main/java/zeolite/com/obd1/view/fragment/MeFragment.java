package zeolite.com.obd1.view.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alexzh.circleimageview.CircleImageView;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zeolite.com.obd1.R;
import zeolite.com.obd1.adapter.me.MeListAdapter;
import zeolite.com.obd1.entity.me.MeListEntity;

import zeolite.com.obd1.view.login.LoginActivity;
import zeolite.com.obd1.view.map.MapActivity;
import zeolite.com.obd1.view.me.CarMessage;


/**
 * Created by Zeolite on 16/1/20.
 */
public class MeFragment extends Fragment {

    private ListView meListView;
    private MeListAdapter meListAdapter;
    private List<MeListEntity> meListEntities;

    private CircleImageView circleImageView;
    Intent intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);


        circleImageView=(CircleImageView)view.findViewById(R.id.imageView);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
            }
        });

        meListView=(ListView)view.findViewById(R.id.me_list);

        MeListEntity meListEntity=new MeListEntity("message","完善信息");
        MeListEntity meListEntity1=new MeListEntity("locate","地图定位");
        meListEntities=new ArrayList<MeListEntity>();
        meListEntities.add(meListEntity);
        meListEntities.add(meListEntity1);

        meListAdapter=new MeListAdapter(getContext(),meListEntities);
        meListView.setAdapter(meListAdapter);


        meListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    intent = new Intent(getActivity(), CarMessage.class);
                    startActivity(intent);
                } else if (position == 1) {
                    intent = new Intent(getActivity(), MapActivity.class);
                    startActivity(intent);
                }
            }
        });


        return view;
    }



}
