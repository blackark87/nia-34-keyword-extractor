package niaExtractor;

import java.io.File;
import java.io.FilenameFilter;

public class Main {

	public static void main(String[] args) {
		
		ExcelExtractor excelExtractor = new ExcelExtractor();
		ExcelExtractorT excelExtractorT = new ExcelExtractorT();
		
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
			
			System.out.println("데이터 폴더 = " + sourceDir);
			
			if(!sourceDir.isDirectory()) {
				System.out.println("폴더가 아닙니다.");
				System.exit(-1);
				
			} else {
				hosFileList = sourceDir.listFiles(hosFilter);
				menFileList = sourceDir.listFiles(menFilter);
				mobFileList = sourceDir.listFiles(mobFilter);

				System.out.println("부모 폴더 = " + parentDir);
				
				if(args.length == 1) {
					
					hosInfoFile = parentDir.listFiles(hosFilter)[0];
					menInfoFile = parentDir.listFiles(menFilter)[0];
					mobInfoFile = parentDir.listFiles(mobFilter)[0];
					
//					excelExtractorT
//					ThreadExtrator hosThread = new ThreadExtrator(hosFileList, hosInfoFile);
//					ThreadExtrator menThread = new ThreadExtrator(menFileList, menInfoFile);
//					ThreadExtrator mobThread = new ThreadExtrator(mobFileList, mobInfoFile);
//					
//					new Thread(hosThread).start();
//					new Thread(menThread).start();
//					new Thread(mobThread).start();
//					
//					hosThread.getResult();
//					menThread.getResult();
//					mobThread.getResult();
					
				} else {
					String jobType = args[1];
					
					if(jobType.equals("hos")) {
						hosInfoFile = parentDir.listFiles(hosFilter)[0];
						excelExtractor.Extractor(hosFileList, hosInfoFile);
						
					} else if(jobType.equals("men")) {
						menInfoFile = parentDir.listFiles(menFilter)[0];
						excelExtractor.Extractor(menFileList, menInfoFile);
						
					} else if(jobType.equals("mob")) {
						mobInfoFile = parentDir.listFiles(mobFilter)[0];
						excelExtractor.Extractor(mobFileList, mobInfoFile);
						
					}
					
				}
			}
			
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

}
