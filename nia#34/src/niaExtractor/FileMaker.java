package niaExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileMaker {

	@SuppressWarnings("unchecked")
	public void fileMaker(ArrayList<String> infoData, HashMap<String, String> metaData, HashMap<String, String> typeData, String text, String sourceFileName, String scriptFileName) {
				
		HashMap<String, String> tempMeta = new LinkedHashMap<String,String>();

		String textPath = "Y:\\04.라벨링데이터\\";
		String audioPath = "Y:\\03.원천데이터\\";
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		JSONArray infoArray = new JSONArray();
		JSONArray dialogArray = new JSONArray();
		JSONArray textArray = new JSONArray();

		
		JSONObject dialogObject = new JSONObject(new LinkedHashMap<String,String>());
		JSONObject metaObject = new JSONObject(new LinkedHashMap<String,String>());
		JSONObject resultObject = new JSONObject(new LinkedHashMap<String,String>());
		JSONObject textObject = new JSONObject(new LinkedHashMap<String,String>());
		
		String dirLocation;
		String category1 = metaData.get("category1").toString().trim();
		String category2 = metaData.get("category2").toString().trim();
		String category3 = metaData.get("category3").toString().trim();
		
		scriptFileName = scriptFileName.substring(0, scriptFileName.lastIndexOf("."));
		
		tempMeta.putAll(metaData);
		tempMeta.putAll(typeData);
		
		tempMeta.put("sptime_all", infoData.get(0));
		tempMeta.put("sptime_start", infoData.get(1));
		tempMeta.put("sptime_end", infoData.get(2));
		tempMeta.put("rec_device", infoData.get(3));
		tempMeta.put("rec_place", infoData.get(4));
		
		metaObject.put("metadata", tempMeta);
		
		infoArray.add(metaObject);
	
		if(category1.equals("대학병원")) {
			textPath += "01.대학병원\\";
			audioPath += "01.대학병원\\";
			
			switch(category2) {
			case "진료안내" :
				textPath += "01.진료안내\\";
				audioPath += "01.진료안내\\";
				break;
			case "병원이용안내" :
				textPath += "02.병원이용안내\\";
				audioPath += "02.병원이용안내\\";
				break;
			case "민원" :
				textPath += "03.민원\\";
				audioPath += "03.민원\\";
			}
			
			switch(category3) {
			case "검사" :
				textPath += "01.검사\\";
				audioPath += "01.검사\\";
				break;
			case "입원" :
				textPath += "02.입원\\";
				audioPath += "02.입원\\";
				break;
			case "외래" :
				textPath += "03.외래\\";
				audioPath += "03.외래\\";
				break;
			case "응급" :
				textPath += "04.응급\\";
				audioPath += "04.응급\\";
				break;
			case "건강검진" :
				textPath += "05.건강검진\\";
				audioPath += "05.건강검진\\";
				break;
			case "시설안내" :
				textPath += "01.시설안내\\";
				audioPath += "01.시설안내\\";
				break;
			case "입퇴원" :
				textPath += "02.입퇴원\\";
				audioPath += "02.입퇴원\\";
				break;
			case "증명서발급" :
				textPath += "03.증명서발급\\";
				audioPath += "03.증명서발급\\";
				break;
			case "원무상담" :
				textPath += "04.원무상담\\";
				audioPath += "04.원무상담\\";
				break;
			case "장례식장안내" :
				textPath += "05.장례식장안내\\";
				audioPath += "05.장례식장안내\\";
				break;
			case "외래진료불만" :
				textPath += "01.외래진료불만\\";
				audioPath += "01.외래진료불만\\";
				break;
			case "검사불만" :
				textPath += "02.검사불만\\";
				audioPath += "02.검사불만\\";
				break;
			case "치료불만" :
				textPath += "03.치료불만\\";
				audioPath += "03.치료불만\\";
				break;
			case "응급실불만" :
				textPath += "04.응급실불만\\";
				audioPath += "04.응급실불만\\";
				break;
			case "기타서비스불만" :
				textPath += "05.기타서비스불만\\";
				audioPath += "05.기타서비스불만\\";
				break;
			}
		} else if(category1.equals("광역이동지원센터")) {
			textPath += "02.광역이동지원센터\\";
			audioPath += "02.광역이동지원센터\\";
			
			switch(category2) {
			case "상담" :
				textPath += "01.상담\\";
				audioPath += "01.상담\\";
				break;
			case "고객대응" :
				textPath += "02.고객대응\\";
				audioPath += "02.고객대응\\";
				break;
			case "민원" :
				textPath += "03.민원\\";
				audioPath += "03.민원\\";
			}
			
			switch(category3) {
			case "적용기준" :
				textPath += "01.적용기준\\";
				audioPath += "01.적용기준\\";
				break;
			case "규정문의" :
				textPath += "02.규정문의\\";
				audioPath += "02.규정문의\\";
				break;
			case "차량요청" :
				textPath += "01.차량요청\\";
				audioPath += "01.차량요청\\";
				break;
			case "예약변경 및 취소" :
				textPath += "02.예약변경 및 취소\\";
				audioPath += "02.예약변경 및 취소\\";
				break;
			case "예약불만" :
				textPath += "01.예약불만\\";
				audioPath += "01.예약불만\\";
				break;
			case "기사관련불만" :
				textPath += "02.기사관련불만\\";
				audioPath += "02.기사관련불만\\";
				break;
			case "규정불만" :
				textPath += "03.규정불만\\";
				audioPath += "03.규정불만\\";
				break;
			case "이용제한" :
				textPath += "04.이용제한\\";
				audioPath += "04.이용제한\\";
				break;
			case "서비스개선요청" :
				textPath += "05.서비스개선요청\\";
				audioPath += "05.서비스개선요청\\";
				break;
			}
		} else {
			textPath += "03.정신건강복지센터\\";
			audioPath += "03.정신건강복지센터\\";
			
			if(category2.equals("정신건강상담")) {
				textPath += "01.정신건강상담\\";
				audioPath += "01.정신건강상담\\";
				
				switch(category3) {
				case "조현병" :
					textPath += "01.조현병\\";
					audioPath += "01.조현병\\";
					break;
				case "우울증" :
					textPath += "02.우울증\\";
					audioPath += "02.우울증\\";
					break;
				case "조울증" :
					textPath += "03.조울증\\";
					audioPath += "03.조울증\\";
					break;
				case "불안장애" :
					textPath += "04.불안장애\\";
					audioPath += "04.불안장애\\";
					break;
				case "물질중독" :
					textPath += "05.물질중독\\";
					audioPath += "05.물질중독\\";
					break;
				case "행위중독" :
					textPath += "06.행위중독\\";
					audioPath += "06.행위중독\\";
					break;
				case "치매" :
					textPath += "07.치매\\";
					audioPath += "07.치매\\";
					break;
				case "기타" :
					textPath += "08.기타\\";
					audioPath += "08.기타\\";
					break;
				}
			} else {
				textPath += "02.자살위기개입\\";
				audioPath += "02.자살위기개입\\";
				
				switch(category3) {
				case "가정불화" :
					textPath += "01.가정불화\\";
					audioPath += "01.가정불화\\";
					break;
				case "경제문제" :
					textPath += "02.경제문제\\";
					audioPath += "02.경제문제\\";
					break;
				case "이성문제" :
					textPath += "03.이성문제\\";
					audioPath += "03.이성문제\\";
					break;
				case "신체정신적문제" :
					textPath += "04.신체정신적문제\\";
					audioPath += "04.신체정신적문제\\";
					break;
				case "직장문제" :
					textPath += "05.직장문제\\";
					audioPath += "05.직장문제\\";
					break;
				case "외로움고독" :
					textPath += "06.외로움고독\\";
					audioPath += "06.외로움고독\\";
					break;
				case "학교성적진로" :
					textPath += "07.학교성적진로\\";
					audioPath += "07.학교성적진로\\";
					break;
				case "친구동료문제" :
					textPath += "08.친구동료문제\\";
					audioPath += "08.친구동료문제\\";
					break;
				case "기타" :
					textPath += "09.기타\\";
					audioPath += "09.기타\\";
					break;
				}
			}
			
		}
		
		//dialogObject.put("textPath", textPath+scriptFileName+"\\"+sourceFileName+".txt"); 
		dialogObject.put("audioPath", audioPath+scriptFileName+"\\"+sourceFileName+".wav");
		
		dialogArray.add(dialogObject);
		
		textObject.put("orgtext", text);
		textArray.add(textObject);
		
		resultObject.put("dialogs", dialogArray);
		resultObject.put("info", infoArray);
		resultObject.put("inputText",textArray);
		
		dirLocation = textPath.substring(13);
		
		File dir = new File("./result/" +LocalDate.now() +"/" + dirLocation.replace("\\","/") + scriptFileName);
		File jsonFile = new File("./result/" +LocalDate.now() +"/" + dirLocation.replace("\\","/") + scriptFileName + "/" + sourceFileName+".json");
		File textFile = new File("./result/" +LocalDate.now() +"/" + dirLocation.replace("\\","/") + scriptFileName + "/" + sourceFileName+".txt");
		
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		try {
			FileWriter fileWriter = new FileWriter(jsonFile);
			
			String json = gson.toJson(resultObject);
			fileWriter.write(json);
			fileWriter.flush();
			fileWriter.close();
			
			//FileWriter textFileWriter = new FileWriter(textFile);
			fileWriter = new FileWriter(textFile);
			fileWriter.write(text);
			fileWriter.flush();
			fileWriter.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
