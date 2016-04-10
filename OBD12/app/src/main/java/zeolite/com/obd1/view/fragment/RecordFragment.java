package zeolite.com.obd1.view.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import zeolite.com.obd1.R;
import zeolite.com.obd1.adapter.record.CardsAdapter;
import zeolite.com.obd1.db.RecordCRUB;
import zeolite.com.obd1.entity.record.RecordEntity;
import zeolite.com.obd1.view.record.AddRecordActivity;


/**
 * Created by Zeolite on 16/1/21.
 */
public class RecordFragment extends Fragment {

    private ListView cardsList;

    private Button addRecordBtn;

    private CardsAdapter cardsAdapter;

    private ArrayList<String> items;

    public RecordFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_card_layout,container,false);


        cardsList=(ListView)rootView.findViewById(R.id.cards_list);
        setupList();

        addRecordBtn=(Button)rootView.findViewById(R.id.add_record_btn);

        addRecordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),AddRecordActivity.class);
                startActivityForResult(intent,1);
            }
        });


        return rootView;
    }

    private void setupList() {
        cardsList.setAdapter(createAdapter());
        cardsList.setOnItemClickListener(new ListItemClickListener());
    }

    @TargetApi(Build.VERSION_CODES.M)
    private CardsAdapter createAdapter() {

        //sqlite data
        RecordCRUB recordCRUB=new RecordCRUB(getContext());
        List<RecordEntity> recordEntities=recordCRUB.findAllRecord();
        items=new ArrayList<String>();

        for(int i=0;i<recordEntities.size();i++){
            RecordEntity recordEntity=recordEntities.get(i);
//            Log.i("recordEntity",recordEntity.getFixitem()+"//"+recordEntity.getFixtype()+"//"+recordEntity.getCost());
//            String fixItems=recordEntity.getFixitem().substring(0,recordEntity.getFixitem().length()-1);

            items.add(recordEntity.getFixtype());
        }

        cardsAdapter=new CardsAdapter(getActivity(),items,new ListItemButtonClickListener());
        return cardsAdapter;
    }


    private final class ListItemButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            for (int i = cardsList.getFirstVisiblePosition(); i <= cardsList
                    .getLastVisiblePosition(); i++) {
                if (v == cardsList.getChildAt(i - cardsList.getFirstVisiblePosition()).findViewById(R.id.list_item_card_button_1)) {

                } else if (v == cardsList.getChildAt(i - cardsList.getFirstVisiblePosition()).findViewById(R.id.list_item_card_button_2)) {

                }
            }
        }
    }

    private final class ListItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Toast.makeText(getActivity(), "Clicked on List Item " + position,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:

                RecordCRUB recordCRUB=new RecordCRUB(getContext());
                List<RecordEntity> recordEntities=recordCRUB.findAllRecord();
                items.clear();
                for(int i=0;i<recordEntities.size();i++){
                    RecordEntity recordEntity=recordEntities.get(i);
                    items.add(recordEntity.getFixtype());
                }

                cardsAdapter.notifyDataSetChanged();
                break;
        }
    }
}


