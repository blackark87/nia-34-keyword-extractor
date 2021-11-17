package niaExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExtractorT{
	
	private final int maxCore = Runtime.getRuntime().availableProcessors();
	private final ExecutorService executorService = Executors.newFixedThreadPool(maxCore);
	private final BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(10);
	
	public ExcelExtractorT() {
		
	}
	
	public void submit(File[] fileList, File infoFile) {
		executorService.submit(()->{
			this.excel(fileList, infoFile);
		});
	}
	
	public void excel(File[] fileList, File infoFile) {
		Map<String, ArrayList<String>> infoList = new LinkedHashMap<String, ArrayList<String>>();
		
		FileMaker fileMaker = new FileMaker();
		
		Integer totalRow = 0;

		try {
		
			XSSFWorkbook infoWorkbook = new XSSFWorkbook(infoFile);
			XSSFSheet infoSheet = infoWorkbook.getSheetAt(0);
			XSSFRow infoRow = null;
			
			ArrayList<String> tempList = new ArrayList<String>();
			
			totalRow = infoSheet.getPhysicalNumberOfRows();
			
			
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
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
			
			boolean writeFlag;
			
			Iterator<String> infoKeyItr = infoList.keySet().iterator();
			
			while(infoKeyItr.hasNext()) {
				tempFileName = infoKeyItr.next();
				fileName = tempFileName.substring(0,3) + tempFileName.substring(5,12);
				speakerType = tempFileName.substring(14,15);
				seqNum = tempFileName.substring(15);
				tempText = "";
				
				counselorNum = 0;
				customerNum = 0;

				writeFlag = false;
				
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
							scriptWorkbook.close();
							break;
						}
					}
				}

			}
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void close() {
		List<Runnable> unfinishedTasks = executorService.shutdownNow();
		if(!unfinishedTasks.isEmpty()) {
			System.out.println("still running");
		}
	}
}
