package zeolite.com.obd1.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import zeolite.com.obd1.R;


/**
 * Created by XUEZE on 1/12/2016.
 */
public  class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

     int mPage;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Log.i("mpage",mPage+"");
        View view=null;
        switch (mPage){
            case 1:
                view = inflater.inflate(R.layout.fragment_connect, container, false);

                break;
            case 2:
                view = inflater.inflate(R.layout.fragment_dashboard, container, false);

                break;
            case 3:
                view = inflater.inflate(R.layout.fragment_record, container, false);

                break;
            case 4:
                view = inflater.inflate(R.layout.fragment_me, container, false);

                break;

        }


        return view;
    }
}