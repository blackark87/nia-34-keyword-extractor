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

public class Main {

	public static void main(String[] args) {
		
		ExcelExtractor excelExtractor = new ExcelExtractor();
		
		File sourceDir = new File(args[0]);
		File parentDir = sourceDir.getParentFile();
		
		File hosInfoFile = null;
		File menInfoFile = null;
		File mobInfoFile = null;
		
		File[] hosFileList = null;
		File[] menFileList = null;
		File[] mobFileList = null;
		
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
			if(!sourceDir.isDirectory()) {
				System.out.println("폴더가 아닙니다.");
				System.exit(-1);
				
			} else {
				hosFileList = sourceDir.listFiles(hosFilter);
				menFileList = sourceDir.listFiles(menFilter);
				mobFileList = sourceDir.listFiles(mobFilter);

				if(args.length == 1) {
					
					hosInfoFile = parentDir.listFiles(hosFilter)[0];
					//menInfoFile = parentDir.listFiles(menFilter)[0];
					//mobInfoFile = parentDir.listFiles(mobFilter)[0];
					
					(new ExcelThreadExtractor()).run(hosFileList, hosInfoFile, "HOS");
					//(new ExcelThreadExtractor()).run(menFileList, menInfoFile, "MEN");
					//(new ExcelThreadExtractor()).run(mobFileList, mobInfoFile, "MOB");
					
				} else {
					String jobType = args[1];
					
					if(jobType.equalsIgnoreCase("hos")) {
						hosInfoFile = parentDir.listFiles(hosFilter)[0];
						excelExtractor.Extractor(hosFileList, hosInfoFile);
						
					} else if(jobType.equalsIgnoreCase("men")) {
						menInfoFile = parentDir.listFiles(menFilter)[0];
						excelExtractor.Extractor(menFileList, menInfoFile);
						
					} else if(jobType.equalsIgnoreCase("mob")) {
						mobInfoFile = parentDir.listFiles(mobFilter)[0];
						excelExtractor.Extractor(mobFileList, mobInfoFile);
						
					}
					
				}
			}
			
			//System.out.println(args[1].toString() + " 작업 완료");
			
		} catch (SecurityException e){
			e.printStackTrace();
		}
	}

}
