package niaExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zeroturnaround.zip.ZipUtil;


public class ExcelExtractor {
	
	
	@SuppressWarnings("unchecked")
	public void Extractor(File[] fileList, File infoFile) {
		
		Map<String, ArrayList<String>> infoList = new LinkedHashMap<String, ArrayList<String>>();
		Map<String, String> failInfoList = new HashMap<String, String>();
		
		OutputStreamWriter osw = null;
		
		List<String> scriptMiss = new ArrayList<String>();
		List<String> wrongExcel = new ArrayList<String>();
		
		Set<String> deleteDupItem = null;
		
		FileMaker fileMaker = new FileMaker();
		
		int totalRow = 0;
		String errorFile = "";
		String type = "";
		String tempTime = "";
		
		System.out.println("음원정보 파일 리딩 시작 시간 : " + LocalTime.now());

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
			System.out.println("음원정보 파일 리딩 완료 종료 시간 " + LocalTime.now());
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("스크립트 파일 리딩 시작 시간 "+ LocalTime.now());
		
		try {
			XSSFWorkbook scriptWorkbook = null;
			XSSFSheet scriptSheet = null;
			XSSFRow scriptRow = null;

			HashMap<String, String> metaData = new LinkedHashMap<String,String>();
			HashMap<String, String> counselor = new LinkedHashMap<String,String>();
			HashMap<String, String> customer = new LinkedHashMap<String,String>();
			
			String tempFileName;
			String fileName = "";
			String speakerType;
			String seqNum;
			String tempText;
			
			int counselorNum;
			int customerNum;
			int processRow = 0;
			
			boolean writeFlag;
			boolean wrongFlag;
			
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
				wrongFlag = false;
				
				//System.out.println("현재 음원 정보 = " + tempFileName);
				for(File scriptFile : fileList) {
					if(!scriptFile.getName().substring(0,scriptFile.getName().lastIndexOf(".")).equals(fileName)) {
						continue;
					} else {
						errorFile = scriptFile.getName();
						scriptWorkbook = new XSSFWorkbook(scriptFile);
						scriptSheet = scriptWorkbook.getSheetAt(0);
					
						for(int rowIdx = 1; rowIdx < scriptSheet.getPhysicalNumberOfRows(); rowIdx++) {
							scriptRow = scriptSheet.getRow(rowIdx);
							
							switch(rowIdx) {
							case 1:
								if(scriptRow.getCell(2).getStringCellValue().equalsIgnoreCase("title")) {
									metaData.put("title", scriptRow.getCell(3).getStringCellValue());
								} else {
									wrongFlag = true;
								}
								break;
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
								//고객 일 경우
								if(scriptRow != null && scriptRow.getCell(2) != null && !scriptRow.getCell(2).toString().trim().equals("")) {
									tempText = scriptRow.getCell(2).getStringCellValue();
									counselorNum++;
									
								}
								
								//상담사일 경우
								if(scriptRow != null && scriptRow.getCell(3) != null && !scriptRow.getCell(3).toString().trim().equals("")) {
									tempText = scriptRow.getCell(3).toString();
									customerNum++;
								}
								
							}
							
							//엑셀 파일 메타데이터가 잘못 됐을경우 엑셀 파일 루프 벗어남
							if(wrongFlag) {
								break;
							}
							
							//실제 스크립트는 엑셀 파일 12번쨰 row 부터 있음
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
						
						//json 파일을 썼음으로 엑셀 파일 닫기
						if(writeFlag) {
							scriptWorkbook.close();
							break;
						}
						
						//엑셀 파일이 잘못돼어 있음으로 실패에 추가 후 엘셀 닫기
						if(wrongFlag) {
							//failInfoList.put("wrong excel", tempFileName);
							wrongExcel.add(fileName);
							scriptWorkbook.close();
							break;
						}
					}
					
					//json파일을 못 썼을 경우 실패에 추가
					if(!writeFlag) {
						//failInfoList.put("missing script", tempFileName);
						scriptMiss.add(tempFileName);
					}
					
					//안닫힌 엑셀이 있을수 있음으로 파일이 바뀌면 전 엑셀 파일 닫기
					scriptWorkbook.close();
				}

			}
			System.out.println("\n스크립트 파일 리딩 완료 종료 시간 "+ LocalTime.now());
			
			System.out.println("폴더 압축 중");
			
			tempTime = LocalTime.now().toString().substring(0, LocalTime.now().toString().lastIndexOf(".")).replace(":","");

			if(fileName.contains("HOS")) {
				ZipUtil.pack(new File("./result/" +LocalDate.now() +"/01.대학병원"), 
						new File("./result/" + LocalDate.now() +"/01.대학병원_" + tempTime + ".zip"));

				FileUtils.deleteDirectory(new File("./result/" +LocalDate.now() +"/01.대학병원"));
				
				type = "hos";
			} else if(fileName.contains("MOB")) {
				ZipUtil.pack(new File("./result/" +LocalDate.now() +"/02.광역이동지원센터"),
						new File("./result/" +LocalDate.now() +"/02.광역이동지원센터_" + tempTime + ".zip"));
				
				FileUtils.deleteDirectory(new File("./result/" +LocalDate.now() +"/02.광역이동지원센터"));
				
				type = "mob";
			} else if(fileName.contains("MEN")) {
				ZipUtil.pack(new File("./result/" +LocalDate.now() +"/03.정신건강복지센터"),
						new File("./result/" +LocalDate.now() +"/03.정신건강복지센터_" + tempTime + ".zip"));
				
				FileUtils.deleteDirectory(new File("./result/" +LocalDate.now() +"/03.정신건강복지센터"));
				
				type = "men";
			}
			System.out.println("폴더 압축 완료");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("\nerror excel file = " + errorFile);
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("\nerror excel file = " + errorFile);
			e.printStackTrace();
		}
		
		//중복 제거
		deleteDupItem = new HashSet<String>(wrongExcel);
		wrongExcel = new ArrayList<String>(deleteDupItem);
		
		deleteDupItem = new HashSet<String>(scriptMiss);
		scriptMiss = new ArrayList<String>(deleteDupItem);
		
		//리스트에 담기
		failInfoList.put("엑셀 정보 이상", String.join("\n", wrongExcel));
		failInfoList.put("","");
		failInfoList.put("음원 정보에는 있으나 대본에 존재 하지 않음", String.join("\n", scriptMiss));
		
		File failScript = null;
		try {
			if(failInfoList.size() > 0) {
				switch(type) {
				case "hos":
					failScript = new File("./result/" +LocalDate.now() +"/대학병원_"+tempTime+"_실패 리스트.txt");
					break;
				case "mob":
					failScript = new File("./result/" +LocalDate.now() +"/광역이동지원센터_"+tempTime+"_실패 리스트.txt");
					break;
				case "men":
					failScript = new File("./result/" +LocalDate.now() +"/정신건강복지센터_"+tempTime+"_실패 리스트.txt");
					break;
				}
				
				osw = new OutputStreamWriter(new FileOutputStream(failScript));
				osw.write(failInfoList.toString());
				osw.flush();
				osw.close();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
