package com.mso.googlemap.model;

public class Point{
	double lat;
	double lng;
	String ID;
	@Override
	public String toString() {
		return "Point [lat=" + lat + ", lng=" + lng + ", ID=" + ID + "]";
	}
	public String getLatLngFormat(){
		return lat+", "+lng;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public Point(double lat, double lng, String iD) {
		super();
		this.lat = lat;
		this.lng = lng;
		ID = iD;
	}
	
}
