package edu.capella.smarthomesbbylocator_gerry_obrien;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

	public static final String LAT = "Latitude";
	public static final String LNG = "Longitude";
	public static final String S_NAME = "StoreName";
	
	private float lat;
	private float lng;
	private String s_name;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		setContentView(R.layout.map_layout);
		
		lat = getIntent().getFloatExtra(LAT, 0);
		lng = getIntent().getFloatExtra(LNG, 0);
		s_name = getIntent().getStringExtra(S_NAME);
		
		
		SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		// TODO Auto-generated method stub
		LatLng location = new LatLng(lat, lng);
		googleMap.getUiSettings().setZoomControlsEnabled(true);
		CameraPosition pos = new CameraPosition.Builder()
				.target(location)
				.zoom(17)
				.build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
		
		googleMap.addMarker(new MarkerOptions().position(location).title(s_name));
		//googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
		
		
		//googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
	}
}
