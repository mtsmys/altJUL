/*******************************************************************************
 * SystemUtil.java
 *
 * Copyright (c) 2019, Akihisa Yasuda
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package ng.lib.logging;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;


/**
 * This class provides utility static methods for logging library.<br>
 * 
 * @author		Akihisa Yasuda
 * @since		Jun.30,2016
 * @version		1.0.0
 */
public class SystemUtil
	{
	/**
	 * This method converts a numerical string into BigDecimal number.<br>
	 * 
	 * @param value			target data size includes unit(for example "3KB" or "10mb" or "100GB")
	 * @return				converted decimal size[Byte]
	 * @throws Exception	General error
	 */
	public static BigDecimal convertDataSize (final String value) throws Exception
		{
		//========== Variable ==========
		Matcher matcher = null;
		final BigDecimal KB_SIZE = new BigDecimal("1024");
		final BigDecimal MB_SIZE = new BigDecimal("1048576");
		final BigDecimal GB_SIZE = new BigDecimal("1073741824");
		final BigDecimal TB_SIZE = new BigDecimal("1099511627776");
		final BigDecimal EB_SIZE = new BigDecimal("1125899906842624");
		final Pattern KB_PATTERN = Pattern.compile(".*kb", Pattern.CASE_INSENSITIVE);
		final Pattern MB_PATTERN = Pattern.compile(".*mb", Pattern.CASE_INSENSITIVE);
		final Pattern GB_PATTERN = Pattern.compile(".*gb", Pattern.CASE_INSENSITIVE);
		final Pattern TB_PATTERN = Pattern.compile(".*tb", Pattern.CASE_INSENSITIVE);
		final Pattern EB_PATTERN = Pattern.compile(".*eb", Pattern.CASE_INSENSITIVE);

		//===== Check argument =====
		if (value!=null && value.trim().length()>0)
			{
			//===== In the case of unit is "KB" =====
			if ((matcher=KB_PATTERN.matcher(value))!=null && matcher.find()==true)
				{
				return (new BigDecimal(value.substring(0, value.length()-2))).multiply(KB_SIZE);
				}
			//===== In the case of unit is "MB" =====
			else if ((matcher=MB_PATTERN.matcher(value))!=null && matcher.find()==true)
				{
				return (new BigDecimal(value.substring(0, value.length()-2))).multiply(MB_SIZE);
				}
			//===== In the case of unit is "GB" =====
			else if ((matcher=GB_PATTERN.matcher(value))!=null && matcher.find()==true)
				{
				return (new BigDecimal(value.substring(0, value.length()-2))).multiply(GB_SIZE);
				}
			//===== In the case of unit is "TB" =====
			else if ((matcher=TB_PATTERN.matcher(value))!=null && matcher.find()==true)
				{
				return (new BigDecimal(value.substring(0, value.length()-2))).multiply(TB_SIZE);
				}
			//===== In the case of unit is "EB" =====
			else if ((matcher=EB_PATTERN.matcher(value))!=null && matcher.find()==true)
				{
				return (new BigDecimal(value.substring(0, value.length()-2))).multiply(EB_SIZE);
				}
			//===== In the case of no unit =====
			else
				{
				return (new BigDecimal(value));
				}
			}
		//===== Argument error =====
		else
			{
			throw new Exception("Argument error! Indicated data size is null or empty");
			}
		}


	/**
	 * This method deletes indicated file or directory completely.<br>
	 * 
	 * @param file			Delete target file or directory
	 * @throws Exception	General error
	 */
	public static void deleteFile (final File file) throws Exception
		{
		//========== Variable ==========
		File[] fileArray = null;

		//===== Check argument =====
		if (file!=null && file.exists()==true)
			{
			//===== In the case of "File" =====
			if (file.isFile())
				{
				//===== Delete a file =====
				file.delete();
				return;
				}
			//===== In the case of "Directory" =====
			else
				{
				//===== Get child files =====
				if ((fileArray=file.listFiles())!=null)
					{
					//===== Loop while all child files recursively =====
					for (File childFile : fileArray)
						{
						//===== Check the existence =====
						if (childFile!=null)
							{
							//===== Recursive deleting =====
							ng.lib.logging.SystemUtil.deleteFile(childFile);
							}
						//===== Error handling =====
						else
							{
							// Do nothing
							}
						}
					}
				//===== In the case of owning no child files =====
				else
					{
					// Do nothing
					}
				//===== Delete a directory =====
				file.delete();
				return;
				}
			}
		//===== Argument error =====
		else if (file==null)
			{
			throw new Exception("Argument error! Indicated \"File\" class object is null");
			}
		else
			{
			throw new Exception("Argument error! Indicated \"File\" class object doesn't exist");
			}
		}


	/**
	 * 
	 * @param xml			XML style string
	 * @return				Formatted XML style string includes indent and line feed
	 * @throws Exception	General error
	 */
	public static String formatXML (final String xml) throws Exception
		{
		//========== Variable ==========
		InputStream inputStream = null;
		Document dom = null;
		Transformer transformer = null;
		StringWriter stringWriter = null;

		//===== Check argument =====
		if (xml!=null && xml.trim().length()>0)
			{
			try {
				//=====  =====
				inputStream = new ByteArrayInputStream(xml.getBytes());
				dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
				transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");
				stringWriter = new StringWriter();
				transformer.transform(new DOMSource(dom), new StreamResult(stringWriter));
				return stringWriter.toString();
				}
			//===== Error handling =====
			catch (Exception e)
				{
				throw e;
				}
			//===== Terminal procedure =====
			finally
				{
				//===== In the case of opening writer =====
				if (stringWriter!=null)
					{
					try
						{
						//===== Close writer =====
						stringWriter.close();
						}
					catch (Exception e)
						{
						}
					}
				else
					{
					}
				//===== In the case of opening stream =====
				if (inputStream!=null)
					{
					try
						{
						//===== Close stream =====
						inputStream.close();
						}
					catch (Exception e)
						{
						}
					}
				else
					{
					}
				}
			}
		//===== Argument error =====
		else
			{
			throw new Exception("Argument \"xml\" is null or invalid");
			}
		}


	/**
	 * 
	 * @return
	 */
	public static String getFileSeparator ()
		{
		return System.getProperty("file.separator");
		}


	/**
	 * 
	 * @return
	 */
	public static String getLineFeed ()
		{
		return System.getProperty("line.separator");
		}


	/**
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static File getParentDirectory (final File file) throws Exception
		{
		//========== Variable ==========
		String filePath = null;
		int filePathLength = 0;
		File directory = null;

		//===== Check argument =====
		if (file!=null)
			{
			//===== Get file path =====
			if ((filePath=file.getCanonicalPath())!=null && (filePathLength=filePath.length())>0)
				{
				//===== Get parent directory =====
				directory = new File(filePath.substring(0, filePathLength-(file.getName().length()+ng.lib.logging.SystemUtil.getFileSeparator().length())));
				return directory;
				}
			//===== Error handling =====
			else
				{
				throw new Exception("Can't find parent directory of indicated file(=\""+filePath+"\")");
				}
			}
		//===== Argument error =====
		else
			{
			throw new Exception("Argument error! Indicated file is null");
			}
		}
	}


// End Of File
