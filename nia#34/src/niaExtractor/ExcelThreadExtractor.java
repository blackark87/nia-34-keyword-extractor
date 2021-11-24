package niaExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelThreadExtractor{
	
	private final int maxCore = Runtime.getRuntime().availableProcessors();
	private final ExecutorService executorService = Executors.newFixedThreadPool(maxCore);
	//private final BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(10);
	
	public ExcelThreadExtractor() {
		
	}
	
	public void run(File[] fileList, File infoFile) {
		int totalRow;
		int countPerProcess;
		
		try {
			XSSFWorkbook infoWorkbook = new XSSFWorkbook(infoFile);
			XSSFSheet infoSheet = infoWorkbook.getSheetAt(0);
			
			totalRow = infoSheet.getPhysicalNumberOfRows();
			countPerProcess = (int)Math.ceil(((double) totalRow) / (double) maxCore);
			
			infoWorkbook.close();
			this._runExecutor(fileList, infoFile, countPerProcess, totalRow);
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void _runExecutor(File[] fileList, File infoFile, int countPerProcess, int totalRow) {
		int start = 0;
		int end = 0;
		
		for(int idx = 0; idx < maxCore; idx++) {
			start = idx == 0 ? 0 : idx * countPerProcess;
			end = idx == maxCore - 1 ? totalRow : (idx +1) * countPerProcess;
			
			Map<String, ArrayList<String>> infoList = this.getInfo(infoFile, start, end);
			
			ExcelExtractorTask excelTask = new ExcelExtractorTask(fileList, infoList, countPerProcess);
			
			executorService.submit(excelTask);
		}
		
		this.awaitTermination(executorService);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, ArrayList<String>> getInfo(File infoFile, int startIdx, int endIdx){
		
		Map<String, ArrayList<String>> infoList = new LinkedHashMap<String, ArrayList<String>>();
		
		try {
			
			XSSFWorkbook infoWorkbook = new XSSFWorkbook(infoFile);
			XSSFSheet infoSheet = infoWorkbook.getSheetAt(0);
			XSSFRow infoRow = null;
			
			ArrayList<String> tempList = new ArrayList<String>();
						
			for(int rowIdx = startIdx; rowIdx < endIdx; rowIdx++) {
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
		
		return infoList;
	}
	
	private void awaitTermination(ExecutorService executorService) {
		while(!executorService.isTerminated()) {
			executorService.shutdown();
			
			try {
				executorService.awaitTermination(1L, TimeUnit.SECONDS);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
