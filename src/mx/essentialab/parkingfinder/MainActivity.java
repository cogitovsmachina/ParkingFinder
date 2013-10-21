package mx.essentialab.parkingfinder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends ActionBarActivity implements
		LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private RequestQueue mQueue;
	private SupportMapFragment map;
	private static double lat;
	private static double lon;
	private static String BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
	private static String KEY = "AIzaSyAQuvHnfF8LyAu8jDrVqDjxXfN03-1x7BQ";
	//private static String LOCATION = "" + lat + "" + lon;
	private static String RADIUS = "5000";
	private static String SENSOR = "true";
	private static String QUERY = "parking";
	private LocationClient locationClient;
	private ProgressDialog progressDialog;
	
	private JSONArray parkingPlaces = new JSONArray();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		map = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		map.getMap().setMyLocationEnabled(true);

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
		Location currentLocation = locationClient.getLastLocation();
		lat = currentLocation.getLatitude();
		lon = currentLocation.getLongitude();
		Toast.makeText(this, "LAT " + lat + "LON " + lon, Toast.LENGTH_LONG)
				.show();
		// TODO: GET PARKING SPOTS HERE
		findParking(lat, lon);
		progressDialog.dismiss();
	}

	private void findParking(double latitude, double longitude) {
		Log.i("Latitude::", ""+latitude);
		Log.i("Longitude::", ""+longitude);

		progressDialog = ProgressDialog.show(this, "",
				"Getting your location, please wait");
		mQueue = Volley.newRequestQueue(getApplicationContext());
		// https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyAQuvHnfF8LyAu8jDrVqDjxXfN03-1x7BQ&location=37.3838,-122.037&radius=5000&sensor=true&query=parking

		StringRequest register = new StringRequest(Method.POST,
				BASE_URL + "&key=" + KEY + "&location=" + latitude + ","
						+ longitude + "&radius=" + RADIUS + "&sensor=" + SENSOR
						+ "&query=" + QUERY, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject parkingSpots = new JSONObject(response);
							//TODO: Add markers to the map
							Toast.makeText(MainActivity.this,
									"Parking spots:" + parkingSpots.toString(),
									Toast.LENGTH_SHORT).show();
							Log.e("***", "" + parkingSpots.toString(1));
							
							
							parkingPlaces = parkingSpots.getJSONArray("results");
							for(int i = 0; i < parkingPlaces.length(); i++){
								String title = parkingPlaces.getJSONObject(i).getString("formatted_address");
								String lat =  parkingPlaces.getJSONObject(i).
										getJSONObject("geometry").getJSONObject("location").getString("lat");
								String lon = parkingPlaces.getJSONObject(i).
										getJSONObject("geometry").getJSONObject("location").getString("lng");
								Log.w("Title:::", ""+title);
								Log.w("Latitude:::", ""+lat);
								Log.w("Longitude:::", ""+lon);

								drawingMarkers(title, lat, lon);
							}
							
						} catch (Exception e) {
							Log.e("***", e.toString());
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("ERROR", error.getMessage());
					}
				});
		// HACK: Adding RetryPolicy to increase request timeout.
		register.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 1, 1.0f));
		mQueue.add(register);
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onLocationChanged(Location location) {
		lat = location.getLatitude();
		lon = location.getLongitude();
	}
	
	public void drawingMarkers(String title, String lat, String lon){
		LatLng position = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
		map.getMap().addMarker(new MarkerOptions().position(position).title(title));
	}

}
