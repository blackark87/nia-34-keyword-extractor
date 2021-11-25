package niaExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExtractorTask implements Runnable{

	private FileMaker fileMaker = new FileMaker();
	
	private Map<String, String> failInfoList;
	private Map<String, ArrayList<String>> infoList;
	
	private File[] fileList;
	
	private int countPerProcess;
	private int idx;
	private String type;
	
	public ExcelExtractorTask(File[] fileList, Map<String, ArrayList<String>> infoList, int countPerProcess, String type, int idx){
		this.fileList = fileList;
		this.infoList = infoList;
		this.countPerProcess = countPerProcess;
		this.type = type;
		this.idx = idx;
		
	}
	
	@Override
	public void run() {
		this.writeJson(this.infoList);
	}
		
	private void writeJson(Map<String, ArrayList<String>> infoList) {
		
		List<String> scriptMiss = new ArrayList<String>();
		List<String> wrongExcel = new ArrayList<String>();
		
		Set<String> deleteDupItem = null;
		
		File tempScriptFile = null;
		
		String threadName = Thread.currentThread().getName();
		String orginName = "";
		String tempFileName = "";
		
		Integer processRow = 0;
		
		try {
			XSSFWorkbook scriptWorkbook = null;
			XSSFSheet scriptSheet = null;
			XSSFRow scriptRow = null;

			HashMap<String, String> metaData = new LinkedHashMap<String,String>();
			HashMap<String, String> counselor = new LinkedHashMap<String,String>();
			HashMap<String, String> customer = new LinkedHashMap<String,String>();
			
			
			String fileName;
			String speakerType;
			String seqNum;
			String tempText;
			
			
			Integer counselorNum;
			Integer customerNum;
			
			boolean writeFlag;
			boolean wrongFlag;
			Iterator<String> infoKeyItr = infoList.keySet().iterator();
			
			while(infoKeyItr.hasNext()) {
				
				processRow++;
				
				if(processRow % 100 == 0) {
					System.out.println(threadName + " - " + processRow + " / " + this.countPerProcess + "처리중");
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
				
				for(File scriptFile : this.fileList) {
					tempScriptFile = scriptFile;
					
					if(!scriptFile.getName().substring(0,scriptFile.getName().lastIndexOf(".")).equals(fileName)) {
						continue;
					} else {
						orginName = scriptFile.getName();
						
						while(!scriptFile.canRead()) {
							System.out.println(scriptFile + " is not readable");
							Thread.sleep(1000L);
						}
						
						scriptWorkbook = new XSSFWorkbook(scriptFile);

						scriptFile.renameTo(new File("temp_"+tempFileName));
						
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
								if(scriptRow.getCell(2) != null && !scriptRow.getCell(2).toString().trim().equals("")) {
									tempText = scriptRow.getCell(2).getStringCellValue();
									counselorNum++;
								}
								
								if(scriptRow.getCell(3) != null && !scriptRow.getCell(3).toString().trim().equals("")) {
									tempText = scriptRow.getCell(3).toString();
									customerNum++;
								}
								
							}
							
							if(wrongFlag) {
								break;
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
					scriptFile.renameTo(new File(orginName));
				}
				if(new File("temp_" + tempFileName).exists()) {
					FileUtils.delete(new File("temp_" + tempFileName));
				}
			}
			
			System.out.println(threadName + "스크립트 파일 처리 완료");
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println(tempFileName);
			System.out.println(orginName);
			e.printStackTrace();
		} finally {
			tempScriptFile.renameTo(new File(orginName));
			
			if(new File("temp_" + tempFileName).exists()) {
				try {
					FileUtils.delete(new File("temp_" + tempFileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//중복 제거
		deleteDupItem = new HashSet<String>(wrongExcel);
		wrongExcel = new ArrayList<String>(deleteDupItem);
		
		deleteDupItem = new HashSet<String>(scriptMiss);
		scriptMiss = new ArrayList<String>(deleteDupItem);
		
		//리스트에 담기
		failInfoList.put("wrong excel", String.join("\n", wrongExcel));
		failInfoList.put("missing script", String.join("\n", scriptMiss));
		
		OutputStreamWriter osw = null;
		
		if(failInfoList.size() > 0) {
			
			try {
				File failScript = new File("./result/"+this.type+"FailScript_"+idx+".txt");
				
				osw = new OutputStreamWriter(new FileOutputStream(failScript));
				osw.write(failInfoList.toString());
				osw.flush();
				osw.close();
				
			} catch (SecurityException e){
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}