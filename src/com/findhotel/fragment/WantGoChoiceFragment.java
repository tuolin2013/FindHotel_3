package com.findhotel.fragment;

import static com.findhotel.constant.Constant.WEB_SERVER_URL;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.trinea.android.common.entity.CacheObject;
import cn.trinea.android.common.service.impl.PreloadDataCache;
import cn.trinea.android.common.service.impl.RemoveTypeEnterTimeFirst;
import cn.trinea.android.common.service.impl.PreloadDataCache.OnGetDataListener;

import com.findhotel.R;
import com.findhotel.adapter.ChoiceAdapter;
import com.findhotel.util.CacheApplication;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.R.integer;
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

public class WantGoChoiceFragment extends Fragment {
	View mView;
	PullToRefreshListView mPullRefreshListView;
	String testJson = "{pgCnt:10,pg:2,dataV:2013091012,items:[{imgUrl:www.zhaozhude.comimagedemon,hotelCnt:12,title:临水而建、枕水而居,ctg:ctg001},{imgUrl:www.zhaozhude.comimagedemon,hotelCnt:19,title:在旅途中寻觅家的味道,ctg:ctg002},{imgUrl: www.zhaozhude.comimagedemon,hotelCnt:23,title:【找住的】时尚旅馆，最炫民族风,ctg:ctg003}]}";
	ChoiceAdapter mAdapter;
	ExecutorService executorService = Executors.newCachedThreadPool();
	String cacheKey = "com.findhotel.fragment.WantGoChoiceFragment";
	CacheApplication cacheApplication;
	JSONArray datasource;
	int page_count, page_no = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		cacheApplication = (CacheApplication) getActivity().getApplication();
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
		loadData();
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = "最后一次更新在："
						+ DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				new GetDataTask().execute(1);
			}
		});
		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				++page_no;
				if (page_no <= page_count) {
					new GetDataTask().execute(page_no);
				} else {
					Toast.makeText(getActivity(), "已经是最后一页了...", Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	void loadData() {

		if (cacheApplication.getCache(cacheKey) == null) {
			executorService.execute(new LoadRunnable());
		} else {
			String cachedDatasource = cacheApplication.getCache(cacheKey);
			try {
				JSONObject jsObj = new JSONObject(cachedDatasource);
				datasource = jsObj.getJSONArray("items");
				mAdapter = new ChoiceAdapter(getActivity(), datasource);
				mPullRefreshListView.setAdapter(mAdapter);
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
			String request_url = WEB_SERVER_URL + "/zzd/nav/v1/popular";
			// JSONObject post = new JSONObject();
			// HttpEntity httpEntity = null;
			// try {
			// post.put("appId", "XXXX");
			// post.put("pg", 1);
			// StringEntity stringEntity = new StringEntity(post.toString(), HTTP.UTF_8);
			// stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			// httpEntity = stringEntity;
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			RequestParams params = new RequestParams();
			params.put("appId", "value");
			params.put("pg", "1");

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
					cacheApplication.setCache(cacheKey, arg0);
					JSONObject jsObj;
					try {
						jsObj = new JSONObject(arg0);
						page_count = jsObj.getInt("pgCnt");
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
			client.post(getActivity(), "http://121.199.33.254:8301/ws/rest/zzd/nav/v1/popular", params1, new AsyncHttpResponseHandler() {
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
				mAdapter = new ChoiceAdapter(getActivity(), datasource);
				mPullRefreshListView.setAdapter(mAdapter);
				break;

			case 1:
				Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_LONG).show();
				break;
			}
		}

	};
}
