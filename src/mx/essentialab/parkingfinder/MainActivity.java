package mx.essentialab.parkingfinder;

import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends ActionBarActivity implements
		LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private RequestQueue mQueue;
	private SupportMapFragment map;
	private static double lat;
	private static double lon;
	private static String BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
	private static String KEY = "AIzaSyAQuvHnfF8LyAu8jDrVqDjxXfN03-1x7BQ";
	private static String LOCATION = "" + lat + "" + lon;
	private static String RADIUS = "5000";
	private static String SENSOR = "true";
	private static String QUERY = "parking";
	private LocationClient locationClient;

	// https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyAQuvHnfF8LyAu8jDrVqDjxXfN03-1x7BQ&location=37.3838,-122.037&radius=5000&sensor=true&query=parking
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		map = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		map.getMap().setMyLocationEnabled(true);

		// TODO: Ask for parking places in 5km

		mQueue = Volley.newRequestQueue(getApplicationContext());
		StringRequest register = new StringRequest(Method.POST, BASE_URL
				+ "&key=" + KEY + "&location=" + LOCATION + "&radius=" + RADIUS
				+ "&sensor=" + SENSOR + "&query=" + QUERY,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject parkingSpots = new JSONObject(response);
							Toast.makeText(MainActivity.this,
									"Parking spots:" + parkingSpots.toString(),
									Toast.LENGTH_SHORT).show();
							Log.e("***", ""+parkingSpots.toString(1));

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
		};
		// HACK: Adding RetryPolicy to increase request timeout.
		register.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 1, 1.0f));
		mQueue.add(register);
		locationClient = new LocationClient(this, this, this);

	}

	@Override
	protected void onStop() {
		locationClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onStart() {
		locationClient.connect();

		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	public void onConnected(Bundle bundle) {

	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onLocationChanged(Location location) {
		lat = location.getLatitude();
		lon = location.getLongitude();
	}

}
