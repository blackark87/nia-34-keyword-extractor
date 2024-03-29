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

	public static void main(String[] args) {
		
		ExcelExtractor excelExtractor = new ExcelExtractor();
		FilenameFilter nameFilter = null;

		File sourceDir = null;
		File parentDir = null;
		File infoFile = null;

		File[] fileList = null;

		try {
			if(args.length > 1) {
				sourceDir = new File(args[0]);
				if(!sourceDir.isDirectory()) {
					logger.info("해당 위치는 폴더가 아닙니다.");
					System.exit(-1);
				}
				parentDir = sourceDir.getParentFile();
			} else {
				System.out.println("스크립트 파일 위치를 입력해 주세요");
				System.exit(-1);
			}

			if(jobType.equalsIgnoreCase("hos")) {

				nameFilter = new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.contains("HOS");
					}
				};

				fileList = sourceDir.listFiles(nameFilter);
				infoFile = parentDir.listFiles(nameFilter)[0];
				excelExtractor.Extractor(fileList, infoFile);

			} else if(jobType.equalsIgnoreCase("men")) {

				nameFilter = new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.contains("MEN");
					}
				};

				fileList = sourceDir.listFiles(nameFilter);
				infoFile = parentDir.listFiles(nameFilter)[0];
				excelExtractor.Extractor(fileList, infoFile);

			} else if(jobType.equalsIgnoreCase("mob")) {

				nameFilter = new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.contains("MOB");
					}
				};

				fileList = sourceDir.listFiles(nameFilter);
				infoFile = parentDir.listFiles(nameFilter)[0];
				excelExtractor.Extractor(fileList, infoFile);

			}

			logger.info("작업 완료");
		} catch (SecurityException e){
			e.printStackTrace();
		}
	}

}
