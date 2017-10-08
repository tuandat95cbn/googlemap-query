package com.mso.googlemap.direction;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TrafficModel;
import com.google.maps.model.TravelMode;
import com.mso.googlemap.model.Point;

public class DirectionQuery {
	Logger LOG = LoggerFactory.getLogger(DirectionQuery.class);
	private static List<String> keys= new ArrayList<String>();
	/**
	 * Get key googleMap default
	 * @return
	 */
	

	//private GeoApiContext geoApiContext = null;
	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public static String getRamdomKey(){
		Random random= new Random();
		return keys.get(random.nextInt(keys.size()));
	}
	private static DirectionQuery directionQuery = null;

	public static DirectionQuery getInstance() {
		List<String> keysLocal= new ArrayList<String>();
		keysLocal.add("AIzaSyCtfuO74PkmHiT3pMEddFQwxp2GHKulRLc");
		keysLocal.add("AIzaSyBwAngqkMZ4_13MlWc77yxI6n8uanVWVMM");
		keysLocal.add("AIzaSyDo2SnpK3xSm8-vhsimQRJxgSVqcQAdAHg");
		keysLocal.add("AIzaSyArN7Fl-ymFS1PvmIlXbPTnfbN57lypw2k");
		keysLocal.add("AIzaSyAruL-HLFSNh6G2MLhjS-eBTea7r7EFa5A");
		keysLocal.add("AIzaSyDEXgYFE4flSYrNfeA7VKljWB_IhrDwxL4");
		directionQuery = new DirectionQuery(keysLocal
				);
		return directionQuery;
	}

	public static DirectionQuery getInstance(List<String> key) {
		directionQuery = new DirectionQuery(key);
		return directionQuery;
	}

	public DirectionQuery(List<String> key) {
		this.keys = key;
		//Random random = new Random();
		//this.geoApiContext = new GeoApiContext.Builder().apiKey(keys.get(random.nextInt(keys.size()))).build();
	}

	/**
	 * Get a map is solution of query contain path, duration(Second), distance(meter)
	 * @param input List of Point
	 * @param numOfThreads Number thread using parallel call Service GoogleMap
	 * @return A Map with key (ID(origin)-ID(destination)) value is list path
	 */
	public Map<String,Object> getMatrixDirection(List<Point> input,
			int numOfThreads) {
		LOG.info("START");
		ExecutorService executorService = Executors
				.newFixedThreadPool(numOfThreads);
		Map<String, List<LatLng>> resPath = new HashMap<String, List<LatLng>>();
		Map<String,Double> resDistance= new HashMap<String, Double>();
		Map<String,Long> resDuration=new HashMap<String, Long>();
		for (int i = 0; i < input.size(); i++) {
			for (int j = 0; j < input.size(); j++)
				if (i != j) {
					executorService.execute(new DirectionSingle(
							resPath,resDuration,resDistance, input.get(i), input.get(j)));
				}
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String,Object> mapres= new HashMap<String, Object>();
		mapres.put("path", resPath);
		mapres.put("distance",resDistance);
		mapres.put("duration",resDuration);
		return mapres;
	}

	public static void main(String[] args) {
		//,{\"ID\":4,\"lat\":21.017414275766644,\"lng\":105.84985971450806},{\"ID\":5,\"lat\":21.013969054545914,\"lng\":105.84842205047607},{\"ID\":6,\"lat\":21.013568442262148,\"lng\":105.85000991821289},{\"ID\":7,\"lat\":21.0156916751047,\"lng\":105.84288597106934},{\"ID\":8,\"lat\":21.015571492920657,\"lng\":105.83958148956299},{\"ID\":9,\"lat\":21.011445179194784,\"lng\":105.83833694458008},{\"ID\":10,\"lat\":21.010303412030968,\"lng\":105.85108280181885},{\"ID\":11,\"lat\":21.01276721446664,\"lng\":105.85288524627686},{\"ID\":12,\"lat\":21.01479030636372,\"lng\":105.85095405578613},{\"ID\":13,\"lat\":21.009962883290306,\"lng\":105.84698438644409},{\"ID\":14,\"lat\":21.00916163612846,\"lng\":105.84136247634888},{\"ID\":15,\"lat\":21.013067675394318,\"lng\":105.83818674087524},{\"ID\":16,\"lat\":21.00683799502468,\"lng\":105.84157705307007},{\"ID\":17,\"lat\":21.006617647869064,\"lng\":105.84505319595337},{\"ID\":18,\"lat\":21.00681796347852,\"lng\":105.84874391555786},{\"ID\":19,\"lat\":21.007118436388456,\"lng\":105.84958076477051},{\"ID\":20,\"lat\":21.00906147993067,\"lng\":105.85164070129395},{\"ID\":21,\"lat\":21.010243318780276,\"lng\":105.85387229919434},{\"ID\":22,\"lat\":21.007579160341784,\"lng\":105.85318565368652},{\"ID\":23,\"lat\":21.016512917436966,\"lng\":105.84101915359497},{\"ID\":24,\"lat\":21.01955748369408,\"lng\":105.83900213241577},{\"ID\":25,\"lat\":21.02051891275537,\"lng\":105.84462404251099},{\"ID\":26,\"lat\":21.019337155327747,\"lng\":105.84951639175415},{\"ID\":27,\"lat\":21.01947736432582,\"lng\":105.84320783615112},{\"ID\":28,\"lat\":21.016332645117288,\"lng\":105.85395812988281},{\"ID\":29,\"lat\":21.012867368176448,\"lng\":105.85056781768799},{\"ID\":30,\"lat\":21.00575628768401,\"lng\":105.85119009017944},{\"ID\":31,\"lat\":21.005355653345607,\"lng\":105.84882974624634},{\"ID\":32,\"lat\":21.012026074923995,\"lng\":105.84827184677124},{\"ID\":33,\"lat\":21.009341917115076,\"lng\":105.83994626998901},{\"ID\":34,\"lat\":21.007899663122426,\"lng\":105.83904504776001},{\"ID\":35,\"lat\":21.00685802656814,\"lng\":105.8383584022522},{\"ID\":36,\"lat\":21.005696192601818,\"lng\":105.84050416946411},{\"ID\":37,\"lat\":21.005275526348846,\"lng\":105.84501028060913},{\"ID\":38,\"lat\":21.005856446100545,\"lng\":105.85262775421143},{\"ID\":39,\"lat\":21.01811532847872,\"lng\":105.85209131240845}
		String str = "[{\"ID\":0,\"lat\":21.065919341488122,\"lng\":105.78563690185547},{\"ID\":1,\"lat\":21.018061918314842,\"lng\":105.6706237796243}]";
		//System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
		JsonParser jsonParser = new JsonParser();
		JsonArray jsonArray = jsonParser.parse(str).getAsJsonArray();
		List<String> listPoint = new ArrayList<String>();
		Type listType = new TypeToken<List<Point>>() {
		}.getType();
		List<Point> yourList = new Gson().fromJson(str, listType);
		System.out.println(yourList);
		DirectionQuery directionQuery=DirectionQuery.getInstance();
		Map<String,Object> resMap= directionQuery.getMatrixDirection(yourList, 4);
		System.out.println(resMap);
	}
}
