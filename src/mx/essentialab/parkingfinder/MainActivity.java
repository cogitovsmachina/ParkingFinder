package mx.essentialab.parkingfinder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.newrelic.agent.android.NewRelic;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class MainActivity extends ActionBarActivity implements
		LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private RequestQueue mQueue;
	private SupportMapFragment map;
	private static double lat;
	private static double lon;
	private static String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	private static String KEY = "AIzaSyAQuvHnfF8LyAu8jDrVqDjxXfN03-1x7BQ";
	//private static String LOCATION = "" + lat + "" + lon;
	private static String RADIUS = "10000";
	private static String SENSOR = "false";
	//private static String QUERY = "parking";
	private static String TYPES = "parking";
	private LocationClient locationClient;
	//private ProgressDialog progressDialog;
	private String status = null;
	
	private SpinnerAdapter spinner;
	
	private JSONArray parkingPlaces = new JSONArray();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_LIST);
		map = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		map.getMap().setMyLocationEnabled(true);

		locationClient = new LocationClient(this, this, this);
		
		NewRelic.withApplicationToken(
				"AAe325ca6e0c6fad2df0e48bd0798abe048a0455b0"
				).start(this.getApplication());
		
		Parse.initialize(this, "L3zahJY9OJWVVPQJcQ4VAmDKWlfoqkZOhl0YSFis", "QM9MU35aVezSOu2sf9Xu5SOYgYYqJzt7ebgxhreb"); 
		PushService.setDefaultPushCallback(this, MainActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();
		ParseAnalytics.trackAppOpened(getIntent());
		
		spinner = ArrayAdapter.createFromResource(this, R.array.action_list,
		          android.R.layout.simple_spinner_dropdown_item);
		
		OnNavigationListener nav = new OnNavigationListener() {
			String[] strings = getResources().getStringArray(R.array.action_list);
			@Override
			public boolean onNavigationItemSelected(int position, long itemId) {
				Log.i("Selected", strings[position]);
				switch (position) {
				case 0:
					findParking("10000");
					break;
				case 1:
					findParking("20000");
					break;
				case 2:
					findParking("30000");
					break;
				default:
					break;
				}
				return true;
			}
		};
		getSupportActionBar().setListNavigationCallbacks(spinner, nav);
		
		map.getMap().setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker marker) {
				Log.w("pressed:::", marker.getPosition()+"");
				LatLng pos = marker.getPosition();
				String posi = pos.latitude+","+pos.longitude;
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+posi));
				startActivity(i);
			}
		});
	}

	@Override
	protected void onStop() {
		locationClient.disconnect();
		super.onStop();
		
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onStart() {
		locationClient.connect();
		super.onStart();
		
		EasyTracker.getInstance(this).activityStart(this);
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
		
		LatLng latLng = new LatLng(lat, lon);
	    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
	    //map.animateCamera(cameraUpdate);
	    map.getMap().moveCamera(cameraUpdate);
		/*Toast.makeText(this, "LAT " + lat + "LON " + lon, Toast.LENGTH_LONG)
				.show();*/

		findParking(RADIUS);
		//progressDialog.dismiss();
	}

	private void findParking(String radius) {
		
		Log.w("lat:", lat+"");
		Log.w("lat:", lon+"");
		
		map.getMap().clear();
		/*progressDialog = ProgressDialog.show(this, "",
				"Getting your location, please wait");*/
		mQueue = Volley.newRequestQueue(getApplicationContext());
		// https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyAQuvHnfF8LyAu8jDrVqDjxXfN03-1x7BQ&location=37.3838,-122.037&radius=5000&sensor=true&query=parking
		// https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=19.3134071,-98.918552&radius=50000&types=parking&sensor=false&key=AIzaSyAQuvHnfF8LyAu8jDrVqDjxXfN03-1x7BQ
		
		StringRequest register = new StringRequest(Method.POST,BASE_URL+"location="+lat+","+lon+
				"&radius="+radius+"&types="+TYPES+"&sensor="+SENSOR+
				"&key="+KEY, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject parkingSpots = new JSONObject(response);
							
							status = parkingSpots.getString("status");
							
							if(status.equals("OK")){
								parkingPlaces = parkingSpots.getJSONArray("results");
								for(int i = 0; i < parkingPlaces.length(); i++){
									String title = parkingPlaces.getJSONObject(i).getString("name");
									String lat =  parkingPlaces.getJSONObject(i).
											getJSONObject("geometry").getJSONObject("location").getString("lat");
									String lon = parkingPlaces.getJSONObject(i).
											getJSONObject("geometry").getJSONObject("location").getString("lng");

									drawingMarkers(title, lat, lon);	
								}
								
							}else{
								Toast.makeText(MainActivity.this,
										"Parking spots: NO Results found, try again.",
										Toast.LENGTH_SHORT).show();
							}
							
						} catch (Exception e) {
							Log.e("***", e.toString());
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						//Log.i("ERROR", error.getMessage());
						Toast.makeText(MainActivity.this,
								"Parking spots: Conexión a internet no disponible. Intente de nuevo.",
								Toast.LENGTH_SHORT).show();
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
