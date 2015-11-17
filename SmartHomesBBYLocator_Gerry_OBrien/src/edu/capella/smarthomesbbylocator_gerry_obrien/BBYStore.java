package edu.capella.smarthomesbbylocator_gerry_obrien;

import org.json.JSONException;
import org.json.JSONObject;

public class BBYStore {

	private int storeID;
	private String shortName;
	private String longName;
	private String address;
	private String address2;
	private String city;
	private String region;
	private int zip;
	private String latitude;
	private String longitude;
	private long distance;
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", this.getStoreID());
		json.put("shortName", this.getShortName());
		json.put("longName", this.getLongName());
		json.put("address", this.getAddress());
		json.put("address2",this.getAddress2());
		json.put("city", this.getCity());
		json.put("region", this.getRegion());
		json.put("zip", this.getZip());
		json.put("lat", this.getLatitude());
		json.put("lng", this.getLongitude());
		json.put("distance", this.getDistance());
		return json;
	}
	
	
	public long getDistance() {
		return distance;
	}

	public void setDistance(long distance) {
		this.distance = distance;
	}

	public int getStoreID() {
		return storeID;
	}
	
	public void setStoreID(int storeID) {
		this.storeID = storeID;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getLongName() {
		return longName;
	}
	public void setLongName(String longName) {
		this.longName = longName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public int getZip() {
		return zip;
	}
	public void setZip(int zip) {
		this.zip = zip;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	@Override
	public String toString() {
		return this.getLongName() + ",    " + this.getDistance() + " miles away.";
	}
}
