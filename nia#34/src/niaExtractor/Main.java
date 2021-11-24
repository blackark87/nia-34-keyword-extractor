package niaExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		
		ExcelExtractor excelExtractor = new ExcelExtractor();
		
		File sourceDir = new File(args[0]);
		File parentDir = sourceDir.getParentFile();
		
		File hosInfoFile = null;
		File menInfoFile = null;
		File mobInfoFile = null;
		
		File hosFailScript = null;
		File menFailScript = null;
		File mobFailScript = null;
		
		File[] hosFileList = null;
		File[] menFileList = null;
		File[] mobFileList = null;
		
		Map<String, List<String>> hosFailList = new HashMap<String,List<String>>();
		Map<String, List<String>> menFailList = new HashMap<String,List<String>>();
		Map<String, List<String>> mobFailList = new HashMap<String,List<String>>();
		
		OutputStreamWriter osw = null;
		
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
					
					(new ExcelThreadExtractor()).run(hosFileList, hosInfoFile);
					(new ExcelThreadExtractor()).run(menFileList, menInfoFile);
					(new ExcelThreadExtractor()).run(mobFileList, mobInfoFile);
					
				} else {
					String jobType = args[1];
					
					if(jobType.equals("hos")) {
						hosInfoFile = parentDir.listFiles(hosFilter)[0];
						hosFailList = excelExtractor.Extractor(hosFileList, hosInfoFile);
						//(new ExcelThreadExtractor()).run(hosFileList, hosInfoFile);
						
						if(hosFailList.size() > 0) {
							hosFailScript = new File("./result/HOSFailScript.txt");
							
							osw = new OutputStreamWriter(new FileOutputStream(hosFailScript));
							osw.write(hosFailList.toString());
							osw.flush();
							osw.close();
						}
						
					} else if(jobType.equals("men")) {
						menInfoFile = parentDir.listFiles(menFilter)[0];
						menFailList = excelExtractor.Extractor(menFileList, menInfoFile);
						//(new ExcelThreadExtractor()).run(menFileList, menInfoFile);
						
						if(menFailList.size() > 0) {
							menFailScript = new File("./result/MENFailScript.txt");
							
							osw = new OutputStreamWriter(new FileOutputStream(menFailScript));
							osw.write(menFailList.toString());
							osw.flush();
							osw.close();
							
						}
					} else if(jobType.equals("mob")) {
						mobInfoFile = parentDir.listFiles(mobFilter)[0];
						mobFailList = excelExtractor.Extractor(mobFileList, mobInfoFile);
						//(new ExcelThreadExtractor()).run(mobFileList, mobInfoFile);
						
						if(mobFailList.size() > 0) {
							mobFailScript = new File("./result/MOBFailScript.txt");
							
							osw = new OutputStreamWriter(new FileOutputStream(mobFailScript));
							osw.write(mobFailList.toString());
							osw.flush();
							osw.close();
							
						}
					}
					
				}
			}
			
		} catch (SecurityException e){
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
