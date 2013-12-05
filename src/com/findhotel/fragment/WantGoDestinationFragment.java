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
import com.findhotel.util.CacheApplication;
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
	ListView mListView;
	String testJson = "{dataV:2013091019,count:2,items:[{theme:热点,data:[{val:深圳,name:深圳},{val:香港,name:香港},{val:阳朔,name:阳朔},{val:三亚,name:三亚},{val:丽江,name:丽江}]},{theme:海边,data:[{val:红海湾,name:红海湾},{val:沙扒湾,name:沙扒湾},{val:北海,name:北海},{val:闸坡,name:闸坡}]}]}";
	AreaAdapter mAdapter;
	JSONArray datasource;
	int page_count, page_no = 1;
	String request_url = WEB_SERVER_URL + "/zzd/nav/v1/area";
	ExecutorService executorService = Executors.newCachedThreadPool();
	CacheApplication mCacheApplication;
	String cacheKey = "com.findhotel.fragment.WantGoDestinationFragment";

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
		mListView = (ListView) view.findViewById(R.id.lv_area);
		loadData();
	}

	void loadData() {
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
		mCacheApplication = (CacheApplication) getActivity().getApplicationContext();
		JSONObject cacheData = mCacheApplication.getCache(cacheKey);
		if (cacheData == null) {
			executorService.execute(new LoadRunnable());
		} else {
			try {
				JSONArray data = cacheData.getJSONArray("items");
				mAdapter = new AreaAdapter(getActivity(), data);
				mListView.setAdapter(mAdapter);
				// ListViewUtility.setListViewHeightBasedOnChildren(mPullRefreshListView.getRefreshableView());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
						mCacheApplication.saveCahce(cacheKey, jsObj);
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


	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				datasource = (JSONArray) msg.obj;
				mAdapter = new AreaAdapter(getActivity(), datasource);
				mListView.setAdapter(mAdapter);
				break;

			case 1:
				Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_LONG).show();
				break;
			}
		}

	};
}
