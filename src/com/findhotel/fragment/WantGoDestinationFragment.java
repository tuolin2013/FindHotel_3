package com.findhotel.fragment;

import static com.findhotel.constant.Constant.WEB_SERVER_URL;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.findhotel.R;
import com.findhotel.adapter.AreaAdapter;
import com.findhotel.adapter.ChoiceAdapter;
import com.findhotel.fragment.WantGoChoiceFragment.LoadRunnable;
import com.findhotel.util.ListViewUtility;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class WantGoDestinationFragment extends Fragment {
	View mView;
	PullToRefreshListView mPullRefreshListView;
	String testJson = "{dataV:2013091019,count:2,items:[{theme:热点,data:[{val:深圳,name:深圳},{val:香港,name:香港},{val:阳朔,name:阳朔},{val:三亚,name:三亚},{val:丽江,name:丽江}]},{theme:海边,data:[{val:红海湾,name:红海湾},{val:沙扒湾,name:沙扒湾},{val:北海,name:北海},{val:闸坡,name:闸坡}]}]}";
	AreaAdapter mAdapter;
	JSONArray datasource;
	int page_count, page_no = 1;
	String request_url = WEB_SERVER_URL + "/zzd/nav/v1/area";
	ExecutorService executorService = Executors.newCachedThreadPool();

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
		// try {
		// JSONObject json = new JSONObject(testJson);
		// JSONArray datasource = json.getJSONArray("items");
		// mAdapter = new AreaAdapter(getActivity(), datasource);
		// mPullRefreshListView.setAdapter(mAdapter);
		// ListViewUtility.setListViewHeightBasedOnChildren(mPullRefreshListView.getRefreshableView());
		//
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// Toast.makeText(getActivity(), "json exception..", Toast.LENGTH_LONG).show();
		// e.printStackTrace();
		// }
		executorService.execute(new LoadRunnable());
	}

	public class LoadRunnable implements Runnable {

		@Override
		public void run() {
			Looper.prepare();
			// TODO Auto-generated method stub

			RequestParams params = new RequestParams();
			params.put("appId", "value");
			params.put("dataV", "1");

			AsyncHttpClient client = new AsyncHttpClient();
			client.post(getActivity(), request_url, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					Toast.makeText(getActivity(), arg1, Toast.LENGTH_LONG).show();
					super.onFailure(arg0, arg1);
				}

				@Override
				public void onStart() {
					Toast.makeText(getActivity(), "start request server...", Toast.LENGTH_LONG).show();
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					super.onSuccess(arg0);
					JSONObject jsObj;
					try {
						jsObj = new JSONObject(arg0);
						// page_count = jsObj.getInt("pgCnt");
						JSONArray array = jsObj.getJSONArray("items");
						myHandler.obtainMessage(0, -1, -1, array).sendToTarget();

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						myHandler.sendEmptyMessage(1);
					}

				}

			});
			Looper.loop();
		}
	}

	private class GetDataTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {

			final Integer page = params[0];

			RequestParams params1 = new RequestParams();
			params1.put("appId", "value");
			params1.put("pg", page.toString());

			AsyncHttpClient client = new AsyncHttpClient();
			client.post(getActivity(), request_url, params1, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(final String arg0) {
					try {
						JSONObject jsObj = new JSONObject(arg0);
						JSONArray temp = jsObj.getJSONArray("items");
						if (page == 1) {// refresh
							datasource = temp;
						} else {
							for (int i = 0; i < temp.length(); i++) {
								datasource.put(temp.getJSONObject(i));
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			// mAdapter = new ChoiceAdapter(getActivity(), v);
			mAdapter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
			super.onPostExecute(v);
		}
	}

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				datasource = (JSONArray) msg.obj;
				mAdapter = new AreaAdapter(getActivity(), datasource);
				mPullRefreshListView.setAdapter(mAdapter);
				break;

			case 1:
				Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_LONG).show();
				break;
			}
		}

	};
}
