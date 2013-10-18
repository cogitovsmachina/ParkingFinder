package mx.essentialab.parkingfinder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private static String BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
	private static String QUERY = "query=parking";
	private static String SENSOR = "&sensor=true";
	private static String KEY = "&key=AIzaSyAQuvHnfF8LyAu8jDrVqDjxXfN03-1x7BQ";
	private RequestQueue mQueue;

	// https://maps.googleapis.com/maps/api/place/textsearch/output?parameters

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// TODO: Ask for parking places in 5km

		mQueue = Volley.newRequestQueue(getApplicationContext());
		StringRequest register = new StringRequest(Method.POST, BASE_URL
				+ QUERY + SENSOR + KEY, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject parkingSpots = new JSONObject(response);
					Toast.makeText(MainActivity.this, "Parking spots:" +parkingSpots.toString(), 1000).show();
					

				} catch (Exception e) {
					Log.e("***", e.toString());
				}
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.i("ERROR", error.getMessage());
			}
		}) {
			// @Override
			// protected Map<String, String> getParams()
			// throws com.android.volley.AuthFailureError {
			// Map<String, String> params = new HashMap<String, String>();
			// params.put("user_id", user.get("ShoeLoversUserID"));
			// params.put("page", "1");
			//
			// return params;
			// }
		};
		// HACK: Adding RetryPolicy to increase request timeout.
		register.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 1, 1.0f));
		mQueue.add(register);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
