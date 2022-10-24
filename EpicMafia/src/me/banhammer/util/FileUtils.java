package me.banhammer.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils
{
	public static void addFileToZip(File source, byte[] file, String pathName)
	{
		try
		{
			File tmpZip = File.createTempFile(source.getName(), null);
			tmpZip.delete();
			if(!source.renameTo(tmpZip))
			{
				throw new Exception("Could not make temp file (" + source.getName() + ")");
			}
			byte[] buffer = new byte[4096];
			ZipInputStream zin = new ZipInputStream(new FileInputStream(tmpZip));
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(source));
			InputStream in = new ByteArrayInputStream(file);
			out.putNextEntry(new ZipEntry(pathName));
			for(int read = in.read(buffer); read > -1; read = in.read(buffer))
			{
				out.write(buffer, 0, read);
			}
			out.closeEntry();
			in.close();
			for(ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry())
			{
				if(!ze.getName().equals(pathName))
				{
					out.putNextEntry(ze);
					for(int read = zin.read(buffer); read > -1; read = zin.read(buffer))
					{
						out.write(buffer, 0, read);
					}
					out.closeEntry();
				}
			}
			out.close();
			tmpZip.delete();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static byte[] readBytes(InputStream inputStream) throws IOException
	{
		byte[] buffer = new byte[32 * 1024];
		int bufferSize = 0;
		for (;;)
		{
			int read = inputStream.read(buffer, bufferSize, buffer.length - bufferSize);
			if (read == -1)
			{
				return Arrays.copyOf(buffer, bufferSize);
			}
			bufferSize += read;
			if (bufferSize == buffer.length)
			{
				buffer = Arrays.copyOf(buffer, bufferSize * 2);
			}
		}
	}

	public static byte[] readBytes(File file) throws IOException
	{
		FileInputStream fis = null;
		byte[] result = null;
		try
		{
			fis = new FileInputStream(file);
			result = readBytes(fis);
			fis.close();
		}
		finally
		{
			if(fis != null)
				fis.close();
		}
		return result;
	}

	public static byte[] readBytes(String file) throws IOException
	{
		return readBytes(new File(file));
	}

	public static void writeBytes(File file, byte[] data) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		fos.close();
	}

	public static void writeBytes(String file, byte[] data) throws IOException
	{
		writeBytes(new File(file), data);
	}
	
	public List<String> readAllLinesJ7(String fileName) throws IOException
	{
		return Files.readAllLines(Paths.get(fileName),
	            Charset.defaultCharset());
	}
	
	public List<String> readAllLines(String fileName) throws IOException
	{
		List<String> res = new ArrayList<String>();
		BufferedReader in = new BufferedReader(new FileReader(new File(fileName)));
		String strLine;
		while ((strLine = in.readLine()) != null)
			res.add(strLine);
		in.close();
		return res;
	}
}
