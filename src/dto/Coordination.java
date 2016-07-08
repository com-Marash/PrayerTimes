package dto;

public class Coordination {
	private Double lat;
	private Double lng;
	
	public Coordination(double lat, double Lng){
		this.lat = lat;
		this.lng = Lng;
	}
	
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
}
