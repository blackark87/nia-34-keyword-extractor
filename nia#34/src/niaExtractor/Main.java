package niaExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		
		ExcelExtractor excelExtractor = new ExcelExtractor();
		
		File sourceDir = null;
		File parentDir = null;
		File infoFile = null;

		File[] fileList = null;
		
		FilenameFilter hosFilter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.contains("HOS");
			}
		};
		
		FilenameFilter mobFilter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.contains("MOB");
			}
		};
		
		FilenameFilter menFilter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.contains("MEN");
			}
		};
		
		try {
			sourceDir = new File(args[0]);
			if(!sourceDir.isDirectory()) {
				logger.info("폴더가 아닙니다.");
				System.exit(-1);
				
			} else {
				parentDir = sourceDir.getParentFile();
				
				if(args.length == 1) {
					fileList = sourceDir.listFiles(hosFilter);
					infoFile = parentDir.listFiles(hosFilter)[0];
					(new ExcelThreadExtractor()).run(fileList, infoFile, "HOS");
					
					fileList = sourceDir.listFiles(menFilter);
					infoFile = parentDir.listFiles(menFilter)[0];
					(new ExcelThreadExtractor()).run(fileList, infoFile, "MEN");
					
					fileList = sourceDir.listFiles(mobFilter);
					infoFile = parentDir.listFiles(mobFilter)[0];
					(new ExcelThreadExtractor()).run(fileList, infoFile, "MOB");
					
				} else {
					String jobType = args[1];
					
					if(jobType.equalsIgnoreCase("hos")) {
						fileList = sourceDir.listFiles(hosFilter);
						infoFile = parentDir.listFiles(hosFilter)[0];
						excelExtractor.Extractor(fileList, infoFile);
						
					} else if(jobType.equalsIgnoreCase("men")) {
						fileList = sourceDir.listFiles(menFilter);
						infoFile = parentDir.listFiles(menFilter)[0];
						excelExtractor.Extractor(fileList, infoFile);
						
					} else if(jobType.equalsIgnoreCase("mob")) {
						fileList = sourceDir.listFiles(mobFilter);
						infoFile = parentDir.listFiles(mobFilter)[0];
						excelExtractor.Extractor(fileList, infoFile);
						
					}
				}
			}

			logger.info("작업 완료");
		} catch (SecurityException e){
			e.printStackTrace();
		}
	}

}
