package com.mso.googlemap.direction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;
import com.google.maps.model.TrafficModel;
import com.google.maps.model.TravelMode;
import com.mso.googlemap.model.Point;

public class DirectionSingle implements Runnable {
	public static final int ITER_MAX=100;
	Logger LOG = LoggerFactory.getLogger(DirectionSingle.class);
	GeoApiContext geoApiContext = null;
	Map<String, List<LatLng>> resPath = null;
	Map<String, Long> resDuration = null;
	Map<String, Double> resDistance = null;
	Point origin = null;
	Point destination = null;

	public DirectionSingle(
			Map<String, List<LatLng>> resPath, Map<String, Long> resDuration,
			Map<String, Double> resDistance, Point origin, Point destination) {
		super();
		this.geoApiContext = new GeoApiContext.Builder().apiKey(DirectionQuery.getRamdomKey()).build();
		this.resPath = resPath;
		this.resDuration = resDuration;
		this.resDistance = resDistance;
		this.origin = origin;
		this.destination = destination;
	}
	private void getSteps(List<LatLng> list,DirectionsStep step){
		DirectionsStep subStep[]=step.steps;
		if(subStep==null) return;
		for(int kk=0;kk<subStep.length;kk++){
			getSteps(list, subStep[kk]);
			LOG.info("AAAAA"+subStep[kk].startLocation+"-----"+subStep[kk].endLocation);
			list.add(subStep[kk].endLocation);
		}
	}
	public void run() {
		int  iter=0;
		while(true)
		try {
			iter++;
			LOG.info("START");
			DirectionsResult directionsResult = DirectionsApi
					.newRequest(geoApiContext).mode(TravelMode.DRIVING).origin(origin.getLatLngFormat())
					.destination(destination.getLatLngFormat()).await();
			LOG.info("QUERY DONE");
			List<LatLng> list = new ArrayList<LatLng>();
			String mapkey = origin.getID() + "_" + destination.getID();
			DirectionsRoute routes[] = directionsResult.routes;
			double distance = 0;
			Long duration = (long) 0;
			for (int i = 0; i < routes.length; i++) {
				DirectionsLeg directionsLeg[] = routes[i].legs;
				for (int j = 0; j < directionsLeg.length; j++) {
					if (i == 0 && j == 0)
						list.add(directionsLeg[0].startLocation);
					DirectionsStep steps[] = directionsLeg[j].steps;
					for (int k = 0; k < steps.length; k++) {
						getSteps(list, steps[k]);
						LOG.info("AAAAA"+steps[k].startLocation+"-----"+steps[k].endLocation);
						list.add(steps[k].endLocation);
					}
					distance += directionsLeg[j].distance.inMeters;
					duration += directionsLeg[j].duration.inSeconds;
				}
			}
			LOG.info(list.toString());
			synchronized (resPath) {
				resPath.put(mapkey, list);
			}
			synchronized (resDuration) {
				resDuration.put(mapkey, duration);
			}
			synchronized (resDistance) {
				resDistance.put(mapkey, distance);
			}
			break;
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			LOG.info("FAIL RELOAD KEY");
			setGeoApiContext(new GeoApiContext.Builder().apiKey(DirectionQuery.getRamdomKey()).build());
			if(iter>ITER_MAX) {
				LOG.error("MISSING QUERY "+origin.getLatLngFormat()+" "+destination.getLatLngFormat());
				return ;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public GeoApiContext getGeoApiContext() {
		return geoApiContext;
	}
	public void setGeoApiContext(GeoApiContext geoApiContext) {
		this.geoApiContext = geoApiContext;
	}
	

}
