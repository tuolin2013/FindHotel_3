package com.findhotel.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.findhotel.R;
import com.findhotel.adapter.AreaAdapter;
import com.findhotel.util.ListViewUtility;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class WantGoDestinationFragment extends Fragment {
	View mView;
	PullToRefreshListView mPullRefreshListView;
	String testJson = "{dataV:2013091019,count:2,items:[{theme:�ȵ�,data:[{val:����,name:����},{val:���,name:���},{val:��˷,name:��˷},{val:����,name:����},{val:����,name:����}]},{theme:����,data:[{val:�캣��,name:�캣��},{val:ɳ����,name:ɳ����},{val:����,name:����},{val:բ��,name:բ��}]}]}";
	AreaAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_want_go_destination, container, false);
		initView(mView);
		return mView;
	}

	void initView(View view) {
		mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_to_refresh_listview_area);
		loadTestData();
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				// new GetDataTask().execute();
			}
		});
		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				Toast.makeText(getActivity(), "End of List!", Toast.LENGTH_SHORT).show();
			}
		});

	}

	void loadTestData() {
		try {
			JSONObject json = new JSONObject(testJson);
			JSONArray datasource = json.getJSONArray("items");
			mAdapter = new AreaAdapter(getActivity(), datasource);
			mPullRefreshListView.setAdapter(mAdapter);
			ListViewUtility.setListViewHeightBasedOnChildren(mPullRefreshListView.getRefreshableView());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getActivity(), "json exception..", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
