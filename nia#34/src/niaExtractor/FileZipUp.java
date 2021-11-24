package niaExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileZipUp {

	public void compress(String path) {
		
		String zipFileName = path.substring(path.lastIndexOf("/")+1) + ".zip";
		String zipFileLocation = path.substring(0,path.lastIndexOf("/"));

		int len;
		
		byte[] buf = new byte[1024];
		
		File directory = new File(path);
		File secondDir = null;
		File thirdDir = null;
		File forthDir = null;
		
		File[] fileList = directory.listFiles();
		
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		FileInputStream fis = null;
		
		try {
			fos = new FileOutputStream(new File(zipFileLocation + "/" + zipFileName));
			zos = new ZipOutputStream(fos);
			
			for(File file : fileList) {
				if(file.isDirectory()) {
					secondDir = new File(file.getAbsolutePath());
					if(secondDir.isDirectory()) {
						thirdDir = new File(secondDir.getAbsolutePath());
						if(thirdDir.isDirectory()) {
							forthDir = new File(thirdDir.getAbsolutePath());
							if(!forthDir.isDirectory()) {
								fileList = forthDir.listFiles();
								file = fileList[0];
							}
						}
					}
					
				}
				
				len = 0;
				fis = new FileInputStream(file.getAbsoluteFile());
				zos.putNextEntry(new ZipEntry(file.getName()));
				
				while((len = fis.read(buf)) > 0) {
					zos.write(buf,0,len);
				}
				
				zos.closeEntry();
				fis.close();
			}
			
			zos.close();
			fos.close();
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		//return isChk;
	}
}
