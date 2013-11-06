package mx.essentialab.parkingplaces;

import java.util.ArrayList;

import mx.essentialab.model.SFParkingLot;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class SFParking {
	//http://api.sfpark.org/sfpark/rest/availabilityservice?lat=37.3838&long=-122.037&radius=100&uom=mile&response=json
	//private double lat = 37.3838;
	//private double lon = -122.037;
	private RequestQueue mQueue;
	/*private String RADIUS = "10000";
	private String UOM = "km";
	private String RESPONSE_TYPE = "json";
	private String BASE_URL ="http://api.sfpark.org/sfpark/rest/availabilityservice?";*/
	
	ArrayList<SFParkingLot> parkingLots = new ArrayList<SFParkingLot>();
	private SupportMapFragment maps;

	public void getSFParkData(Context _context, SupportMapFragment map){
		maps = map;
		
	//	Log.w("lat:", lat+"");
	//	Log.w("lon:", lon+"");
		mQueue = Volley.newRequestQueue(_context.getApplicationContext());
		
		JsonObjectRequest jObjectRequest = new JsonObjectRequest("http://api.sfpark.org/sfpark/rest/availabilityservice?lat=37.3838&long=-122.037&radius=100&uom=km&response=json",
				null, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						String status = null;
						JSONArray avl = new JSONArray();
						
						try {
						//	Log.w("RESPONSE", response.getString("STATUS"));
							status = response.getString("STATUS");
							if(status.equals("SUCCESS")){
								
								avl = response.getJSONArray("AVL");
								if(avl != null){
								//	Log.w("RESPONSE", avl.toString());
									if(parkingLots != null){
										for(int i = 0; i < avl.length(); i++){
											JSONObject objectLot = avl.getJSONObject(i);
											SFParkingLot lot = new SFParkingLot();
											lot.setLotType(objectLot.getString("TYPE"));
											lot.setLotName(objectLot.getString("NAME"));
											lot.setLotPts(objectLot.getString("PTS"));
											lot.setLotLoc(objectLot.getString("LOC"));
											parkingLots.add(lot);
										}
									}
								}
							}
							
							for(int j = 0; j < parkingLots.size(); j++){
							//	Log.i("LOT::", parkingLots.get(j).getLotLoc());
								
								drawingLots(parkingLots.get(j).getLotLoc(),
										parkingLots.get(j).getLotName());
							}
							
						} catch (Exception e) {
							Log.e("***", e.toString());
						}
						
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("ERROR", error.getMessage().toString());
						
					}
				});
		jObjectRequest	
		.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 1, 1.0f));

		mQueue.add(jObjectRequest);

	}
	
	private void drawingLots(String location, String title){
		//Log.w("DRAWING", "OK");
		String loca = location;
		int pos1 = loca.indexOf(",");
		int pos2 = loca.indexOf(",", pos1+1);
		double lats = 0.0;
		double lons = 0.0;
		lats = Double.parseDouble(loca.substring(0, pos1));
		if(pos2 == -1){
			lons = Double.parseDouble(loca.substring(pos1+1, loca.length()));
		}else{
			lons = Double.parseDouble(loca.substring(pos1+1, pos2));
		}
		LatLng position = new LatLng(lons,lats);
		maps.getMap().addMarker(new MarkerOptions().position(position).title(title));
	}
}
