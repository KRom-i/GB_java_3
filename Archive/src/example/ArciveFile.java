package example;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class ArciveFile
{
    
	private final static String zipDir   = "Тестовая папка";
	private final static String zipFile  = "Тестовая папка.zip";

	private final String SLASH_BACK      = "/";
    private final String CHARSET_CP866   = "CP866";
    
    public  boolean ACTION_unzip = true;
    public ArciveFile()
    {
		try {
	    	if (ACTION_unzip)
	    		Unzip(zipFile);
	    	else
				Zip(zipDir, zipFile);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
    }
    private void createDir(final String dir)
    {
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
    }
    private void createFolder(final String dirName)
    {
		if (dirName.endsWith(SLASH_BACK)) {
			createDir(dirName.substring(0, dirName.length() - 1));
		}
    	
    }
    private void checkFolder(final String file_path)
    {
		if (!file_path.endsWith(SLASH_BACK) && file_path.contains(SLASH_BACK)) {
			String dir = file_path.substring(0, file_path.lastIndexOf(SLASH_BACK));
			createDir(dir);
		}
    	
    }
    private void Unzip(final String zipDir) throws Exception
    {
       	ZipFile zipFile = new ZipFile(zipDir, CHARSET_CP866);
       	Enumeration<?> entries = zipFile.getEntries();
       	while (entries.hasMoreElements()) {
       		ZipEntry entry = (ZipEntry) entries.nextElement();
       		String entryName = entry.getName();
       		if (entryName.endsWith(SLASH_BACK)) {
            	System.out.println("Создание директории <" + entryName + ">");
       			createFolder (entryName);
       			continue;
       		} else
       			checkFolder(entryName);
        	System.out.println("Чтение файла <" + entryName + ">");
       		InputStream  fis = (InputStream) zipFile.getInputStream(entry);
       		
       		FileOutputStream fos = new FileOutputStream(entryName);
       		byte[] buffer = new byte[fis.available()];
       		// Считываем буфер
       		fis.read(buffer, 0, buffer.length);
       		// Записываем из буфера в файл
       		fos.write(buffer, 0, buffer.length);
       		fis.close();
       		fos.close();
       	}
       	zipFile.close() ;      
    	System.out.println("Zip файл разархивирован!");
    }

    private void Zip(String source_dir, String zip_file) throws Exception
    {
    	// Cоздание объекта ZipOutputStream из FileOutputStream
    	FileOutputStream fout = new FileOutputStream(zip_file);
    	ZipOutputStream zout = new ZipOutputStream(fout);
    	// Определение кодировки
    	zout.setEncoding(CHARSET_CP866);
        	
    	// Создание объекта File object архивируемой директории
    	File fileSource = new File(source_dir);
               
    	addDirectory(zout, fileSource);
               
   		// Закрываем ZipOutputStream
    	zout.close();
               
    	System.out.println("Zip файл создан!");
    }
    private void addDirectory(ZipOutputStream zout, File fileSource) throws Exception
    {
        File[] files = fileSource.listFiles();
        System.out.println("Добавление директории <" + fileSource.getName() + ">");
        for(int i = 0; i < files.length; i++) {
            // Если file является директорией, то рекурсивно вызываем 
            // метод addDirectory
        	if(files[i].isDirectory()) {
        		addDirectory(zout, files[i]);
        		continue;
        	}
       		System.out.println("Добавление файла <" + files[i].getName() + ">");

       		FileInputStream fis = new FileInputStream(files[i]);
       		
       		zout.putNextEntry(new ZipEntry(files[i].getPath()));

       		byte[] buffer = new byte[4048];
       		int length;
                 
       		while((length = fis.read(buffer)) > 0)
       			zout.write(buffer, 0, length);
       		// Закрываем ZipOutputStream и InputStream
       		zout.closeEntry();
       		fis.close();
        }
    }
 
    public static void main(String[] args)
    {
    	new ArciveFile();
    	System.exit(0);
    }
}