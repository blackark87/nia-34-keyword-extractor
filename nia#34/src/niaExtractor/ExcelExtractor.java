package niaExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExtractor {
	
	@SuppressWarnings("unchecked")
	public void Extractor(File[] fileList, File infoFile) {
		
		Map<String, ArrayList<String>> infoList = new LinkedHashMap<String, ArrayList<String>>();
		
		FileMaker fileMaker = new FileMaker();
		
		Integer totalRow = 0;
		
		System.out.println("음원정보 파일 리딩\n시작 시간 : " + LocalTime.now());

		try {
			System.out.println("현재 음원 정보 파일 = " + infoFile.getName());
		
			XSSFWorkbook infoWorkbook = new XSSFWorkbook(infoFile);
			XSSFSheet infoSheet = infoWorkbook.getSheetAt(0);
			XSSFRow infoRow = null;
			
			ArrayList<String> tempList = new ArrayList<String>();
			
			totalRow = infoSheet.getPhysicalNumberOfRows();
			
			System.out.println("음원 정보 개수 = " + (totalRow+1) + "건");
			
			for(int rowIdx = 1; rowIdx < totalRow; rowIdx++) {
				infoRow = infoSheet.getRow(rowIdx);
				
				tempList.add(infoRow.getCell(1).getStringCellValue());
				tempList.add(infoRow.getCell(2).getStringCellValue());
				tempList.add(infoRow.getCell(3).getStringCellValue());
				tempList.add(infoRow.getCell(4).getStringCellValue());
				tempList.add(infoRow.getCell(5).getStringCellValue());
				
				infoList.put(infoRow.getCell(0).getStringCellValue(), (ArrayList<String>) tempList.clone());
				tempList.clear();
			}
			
			infoWorkbook.close();
			System.out.println("음원정보 파일 리딩 완료\n종료 시간 " + LocalTime.now());
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("스크립트 파일 리딩\n시작 시간 "+ LocalTime.now());
		
		try {
			XSSFWorkbook scriptWorkbook = null;
			XSSFSheet scriptSheet = null;
			XSSFRow scriptRow = null;

			HashMap<String, String> metaData = new LinkedHashMap<String,String>();
			HashMap<String, String> counselor = new LinkedHashMap<String,String>();
			HashMap<String, String> customer = new LinkedHashMap<String,String>();
			
			String tempFileName;
			String fileName;
			String speakerType;
			String seqNum;
			String tempText;
			
			Integer counselorNum;
			Integer customerNum;
			Integer processRow = 0;
			
			boolean writeFlag;
			
			Iterator<String> infoKeyItr = infoList.keySet().iterator();
			
			while(infoKeyItr.hasNext()) {
				processRow++;
				
				if(processRow % 100 == 0) {
					System.out.printf("\r처리중 : " + processRow + " / " + totalRow);
				}
				
				tempFileName = infoKeyItr.next();
				fileName = tempFileName.substring(0,3) + tempFileName.substring(5,12);
				speakerType = tempFileName.substring(14,15);
				seqNum = tempFileName.substring(15);
				tempText = "";
				
				counselorNum = 0;
				customerNum = 0;

				writeFlag = false;
				
				//System.out.println("현재 음원 정보 = " + tempFileName);
				for(File scriptFile : fileList) {
					if(!scriptFile.getName().substring(0,scriptFile.getName().lastIndexOf(".")).equals(fileName)) {
						continue;
					} else {
						
						scriptWorkbook = new XSSFWorkbook(scriptFile);
						scriptSheet = scriptWorkbook.getSheetAt(0);
					
						for(int rowIdx = 1; rowIdx < scriptSheet.getPhysicalNumberOfRows(); rowIdx++) {
							scriptRow = scriptSheet.getRow(rowIdx);
							
							switch(rowIdx) {
							case 1:
								metaData.put("title", scriptRow.getCell(3).getStringCellValue()); break;
							case 2:
								metaData.put("category1",scriptRow.getCell(3).getStringCellValue()); break;
							case 3:
								metaData.put("category2",scriptRow.getCell(3).getStringCellValue()); break;
							case 4:
								metaData.put("category3",scriptRow.getCell(3).getStringCellValue()); break;
							case 5:
								counselor.put("speaker_type", "상담사");
								counselor.put("speaker_id", scriptRow.getCell(3).getStringCellValue()); break;
							case 6:
								counselor.put("speaker_age", scriptRow.getCell(3).getStringCellValue()); break;
							case 7:
								counselor.put("speaker_sex", scriptRow.getCell(3).getStringCellValue()); break;
							case 8:
								customer.put("speaker_type", "고객");
								customer.put("speaker_id", scriptRow.getCell(3).getStringCellValue()); break;
							case 9:
								customer.put("speaker_age", scriptRow.getCell(3).getStringCellValue()); break;
							case 10:
								customer.put("speaker_sex", scriptRow.getCell(3).getStringCellValue()); break;
							case 11:
								break; // 발화 순번 row skip
							default:
								if(scriptRow.getCell(2) != null && !scriptRow.getCell(2).toString().trim().equals("")) {
									tempText = scriptRow.getCell(2).getStringCellValue();
									counselorNum++;
								} else if(scriptRow.getCell(3) != null && !scriptRow.getCell(3).toString().trim().equals("")) {
									tempText = scriptRow.getCell(3).toString();
									customerNum++;
								}
								
							}
							
							if(rowIdx > 11) {
								if(speakerType.equals("B")) {
									if(Integer.parseInt(seqNum) == customerNum) {
										fileMaker.fileMaker(infoList.get(tempFileName), metaData, customer, tempText, tempFileName, scriptFile.getName());
										writeFlag = true;
										break;
									}
								} else {
									if(Integer.parseInt(seqNum) == counselorNum) {
										fileMaker.fileMaker(infoList.get(tempFileName), metaData, counselor, tempText, tempFileName, scriptFile.getName());
										writeFlag = true;
										break;
									}
								}
								
							}
							
						}
						
						if(writeFlag) {
							//json 파일을 생성 하면 음원 정보가 다른 파일로 변경 될수 있음으로 엑셀 닫기
							scriptWorkbook.close();
							break;
						}
					}
					
					//안닫힌 엑셀이 있을수 있음으로 파일이 바뀌면 전 엑셀 파일 닫기
					scriptWorkbook.close();
				}

			}
			System.out.println("스크립트 파일 리딩 완료\n종료 시간 "+ LocalTime.now());
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
