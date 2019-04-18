/*******************************************************************************
 * FileAppender.java
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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.ErrorManager;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Logging Library for JVM Platform
 * 
 * @author		Akihisa Yasuda
 * @since		Aug.30,2011
 * @version		1.0.0
 */
public class FileAppender extends Appender
	{
	/** */ private boolean append = false;
	/** */ private boolean bufferedIO = false;
	/** */ private int maxBufferSize = 0;
	/** */ private File file = null;
	/** */ private long fileSize = 0L;
	/** */ private boolean immediateFlush = true;
	/** */ private int maxBuckupIndex = 3;
	/** */ private long maxFileSize = 16777216;
	/** */ private BufferedWriter bufferedWriter = null;


	/**
	 * This method creates new logging configuration file which is standard<br>
	 * setting.<br>
	 * 
	 * @param directory		Directory which set on log file
	 * @param logFileName	Log file name
	 * @param loggerName	Logger name
	 * @param configFile	Logging configuration file(for output)
	 * @param format		true : output formatted XML string, false : not formatted
	 * @return				Canonical path of logging configuration file
	 * @throws Exception	General error
	 */
	public static String createNewLogConfigFile (final File directory, final String logFileName, final String loggerName, final File configFile, final boolean format) throws Exception
		{
		//========== Variable ==========
		StringWriter stringWriter = null;
		PrintWriter printWriter = null;
		String xml = null;
		Document document = null;
		Element root = null;
		Element appender = null;
		Element param = null;
		Element layout = null;
		Element category = null;
		Element priority = null;
		Element appender_ref = null;
		DOMSource domSource = null;

		try
			{
			//===== Create new DOM(for configuration file) =====
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			//===== Set root node =====
			root = document.createElement("log4j:configuration");
			root.setAttribute("xmlns:log4j", "http://jakarta.apache.org/log4j/");
			document.appendChild(root);
			//===== Set appender node to root node =====
			appender = document.createElement("appender");
			appender.setAttribute("name", "file");
			appender.setAttribute("class", "ng.lib.logging.FileAppender");
			param = document.createElement("param");
			param.setAttribute("name", "Append");
			param.setAttribute("value", "true");
			appender.appendChild(param);
			param = document.createElement("param");
			param.setAttribute("name", "Encoding");
			param.setAttribute("value", Logger.UTF8);
			appender.appendChild(param);
			param = document.createElement("param");
			param.setAttribute("name", "File");
			param.setAttribute("value", new File(directory, logFileName).getCanonicalPath());
			appender.appendChild(param);
			param = document.createElement("param");
			param.setAttribute("name", "MaxFileSize");
			param.setAttribute("value", "10MB");
			appender.appendChild(param);
			layout = document.createElement("layout");
			layout.setAttribute("class", "ng.lib.logging.PatternLayout");
			param = document.createElement("param");
			param.setAttribute("name", "ConversionPattern");
			param.setAttribute("value", "[%d][%5p][%C][%c{1}][%l][%M][%L][%r][%m]%n");
			layout.appendChild(param);
			appender.appendChild(layout);
			root.appendChild(appender);
			//===== Set category node to root node =====
			category = document.createElement("category");
			category.setAttribute("name", loggerName);
			priority = document.createElement("priority");
			priority.setAttribute("value", "info");
			category.appendChild(priority);
			appender_ref = document.createElement("appender-ref");
			appender_ref.setAttribute("ref", "file");
			category.appendChild(appender_ref);
			root.appendChild(category);
			//===== Convert DOM to XML =====
			domSource = new DOMSource(document);
			TransformerFactory.newInstance().newTransformer().transform(domSource, new StreamResult(stringWriter=new StringWriter()));
			if (format==true)
				{
				xml = FileAppender.formatXML(stringWriter.toString());
				}
			else
				{
				xml = stringWriter.toString();
				}
			//===== Initialize creation file =====
			if (configFile.exists()==true && configFile.isFile()==true)
				{
				FileAppender.deleteFile(configFile);
				}
			else
				{
				}
			//===== Create configuration file =====
			printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(configFile), Logger.UTF8));
			printWriter.write(xml);
			printWriter.flush();
			//=====  =====
			return configFile.getCanonicalPath();
			}
		//===== Error handling =====
		catch (Exception e)
			{
			throw e;
			}
		finally
			{
			//=====  =====
			if (stringWriter!=null)
				{
				try
					{
					stringWriter.close();
					}
				catch (Exception e)
					{
					}
				finally
					{
					stringWriter = null;
					}
				}
			else
				{
				}
			//=====  =====
			if (printWriter!=null)
				{
				printWriter.close();
				printWriter = null;
				}
			else
				{
				}
			}
		}



	/**
	 * 
	 * @exception SecurityException	General error
	 */
	@Override
	public void close () throws SecurityException
		{
		try
			{
			this.closeBufferedWriter();
			return;
			}
		catch (Exception e)
			{
			new SecurityException(e);
			}
		}


	/**
	 * 
	 */
	@Override
	public void flush ()
		{
		//========== Check flushing independent on buffering ==========
		//=====  =====
		if (this.bufferedIO==false || this.immediateFlush==true)
			{
			try
				{
				//=====  =====
				this.getBufferedWriter().flush();
				}
			//=====  =====
			catch (Exception e)
				{
				}
			}
		//=====  =====
		else
			{
			}

		//========== Check file rotation ==========
		try
			{
			//=====  =====
			if (this.maxFileSize<=this.getFileSize())
				{
				//=====  =====
				this.getBufferedWriter().flush();
				//=====  =====
				this.rotate();
				}
			//=====  =====
			else
				{
				}
			}
		//=====  =====
		catch (Exception e)
			{
			}

		//=====  =====
		return;
		}


	/**
	 * 
	 * @param node						
	 * @exception Exception	General error
	 */
	@Override
	public void parseAppenderNode (Node node) throws Exception
		{
		//========== Variable ==========
		NodeList childNodeList = null;
		int childeNodeListLength = 0;
		final String NODE_PARAM = "param";
		final String NODE_LAYOUT = "layout";

		//===== Get child node list =====
		if (node!=null 
				&& (childNodeList=node.getChildNodes())!=null && (childeNodeListLength=childNodeList.getLength())>0)
			{
			//===== Repeat for all child nodes =====
			for (int i=0; i<childeNodeListLength; i++)
				{
				//===== Get child node =====
				if ((node=childNodeList.item(i))!=null)
					{
					//===== In the case of "param" node =====
					if (NODE_PARAM.equals(node.getNodeName())==true)
						{
						this.parseParamNode(node);
						}
					//===== In the case of "layout" node =====
					else if (NODE_LAYOUT.equals(node.getNodeName())==true)
						{
						this.setLayout(this.parseLayoutNode(node));
						}
					//=====  =====
					else
						{
						}
					}
				//===== Error handling =====
				else
					{
					throw new Exception("");
					}
				}
			//===== Terminate procedure =====
			return;
			}
		//===== Error handling =====
		else
			{
			throw new Exception("Argument error! Indicated node is null or has no child nodes");
			}
		}


	/**
	 * 
	 * @param paramNode		XML node object indicated parameter value
	 * @throws Exception	General error
	 */
	@Override
	public void parseParamNode (final Node paramNode) throws Exception
		{
		//========== Variable ==========
		String name = null;
		String value = null;
		Node attribute = null;
		final String ATTRIBUTE_NAME			= "name";
		final String ATTRIBUTE_VALUE		= "value";
		final String KEY_APPEND				= "Append";
		final String KEY_BUFFER_SIZE		= "BufferSize";
		final String KEY_BUFFERED_IO		= "BufferdIO";
		final String KEY_ENCODING			= "Encoding";
		final String KEY_FILE				= "File";
		final String KEY_IMMEDIATE_FLUSH	= "ImmediateFlush";
		final String KEY_MAX_BACUP_INDEX	= "MaxBackupIndex";
		final String KEY_MAX_FILE_SIZE		= "MaxFileSize";
		final String KEY_THRESHOLD			= "Threshold";
		final String TRUE					= "true";
		final String FALSE					= "false";

		//===== Get parameter set =====
		if (paramNode!=null 
				&& (attribute=((Element)paramNode).getAttributeNode(ATTRIBUTE_NAME))!=null 
				&& (name=attribute.getNodeValue())!=null && name.trim().length()>0 
				&& (attribute=((Element)paramNode).getAttributeNode(ATTRIBUTE_VALUE))!=null 
				&& (value=attribute.getNodeValue())!=null && value.trim().length()>0)
			{
			//===== In the case of file appending setting =====
			if (KEY_APPEND.equalsIgnoreCase(name)==true)
				{
				if (TRUE.equalsIgnoreCase(value)==true)
					{
					this.append = true;
					}
				else
					{
					this.append = false;
					}
				return;
				}
			//===== In the case of buffering setting =====
			else if (KEY_BUFFER_SIZE.equalsIgnoreCase(name)==true)
				{
				return;
				}
			//===== In the case of buffered I/O setting =====
			else if (KEY_BUFFERED_IO.equalsIgnoreCase(name)==true)
				{
				if (TRUE.equalsIgnoreCase(value)==true)
					{
					this.bufferedIO = true;
					}
				else
					{
					this.bufferedIO = false;
					}
				return;
				}
			//===== In the case of encoding setting =====
			else if (KEY_ENCODING.equalsIgnoreCase(name)==true)
				{
				//===== Check character code =====
				Charset.forName(value);
				//===== Set character code =====
				this.setEncoding(value);
				return;
				}
			//===== In the case of file path setting =====
			else if (KEY_FILE.equalsIgnoreCase(name)==true)
				{
				//===== Set a file object =====
				this.setFile(new File(value));
				return;
				}
			//===== In the case of flush setting =====
			else if (KEY_IMMEDIATE_FLUSH.equalsIgnoreCase(name)==true)
				{
				if (FALSE.equalsIgnoreCase(value)==true)
					{
					this.append = false;
					}
				else
					{
					this.append = true;
					}
				return;
				}
			//===== In the case of backup file indexing setting =====
			else if (KEY_MAX_BACUP_INDEX.equalsIgnoreCase(name)==true)
				{
				this.maxBuckupIndex = Integer.parseInt(value);
				return;
				}
			//===== In the case of file size setting =====
			else if (KEY_MAX_FILE_SIZE.equalsIgnoreCase(name)==true)
				{
				this.maxFileSize = this.convertDataSize(value).longValue();
				return;
				}
			//===== In the case of log level threshold setting =====
			else if (KEY_THRESHOLD.equalsIgnoreCase(name)==true)
				{
				this.setLevel(Logger.convertLogLevel(Logger.getLogLevel(value)));
				return;
				}
			//=====  =====
			else
				{
				// do nothing
				return;
				}
			}
		//=====  =====
		else
			{
			throw new Exception("XML parsing error! The format of indicated node is invalid");
			}
		}


	/**
	 * 
	 * 
	 * @param record
	 */
	@Override
	public void publish (final LogRecord record)
		{
		//========== Variable ==========
		String logMessage = null;
		Layout layout = null;

		try
			{
			//===== In the case of layout is indicated =====
			if ((layout=this.getLayout())!=null)
				{
				//===== Set logging date =====
				layout.setDate(new Date(record.getMillis()));
				//===== Set logging class path =====
				layout.setClassPath(record.getSourceClassName()+"."+record.getSourceMethodName()+"()");
				//===== Set logging level =====
				layout.setPriority(Logger.convertLogLevel(record.getLevel()));
				//===== In the case of output stack trace(which is used for detail information but reflect performance down) =====
				if (this.isEnabledLocation()==true)
					{
					//===== Set logging stack trace =====
					layout.setStackTraceElement(this.getStackTraceElement());
					}
				//===== In the case of no stack trace =====
				else
					{
					}
				//===== Get formatted log message =====
				logMessage = layout.getLogMessage(record.getMessage());
				}
			//===== In the case of layout doesn't indicated =====
			else
				{
				logMessage = record.getMessage();
				}
			//===== Write log message into file in indicated encoding =====
			this.getBufferedWriter().write(logMessage);
			//===== Flush log file =====
			this.flush();
			//===== Update file size =====
			this.setFileSize(this.getFileSize()+(long)logMessage.getBytes(this.getEncoding()).length);
			}
		//===== Error handling =====
		catch (Exception e)
			{
			e.printStackTrace();
			}
		return;
		}


	/**
	 * 
	 * @param em
	 */
	@Override
	public void setErrorManager (ErrorManager em)
		{
		super.setErrorManager(em);
		}


	/**
	 * 
	 * @throws Exception	General error
	 */
	private final void closeBufferedWriter () throws Exception
		{
		//=====  =====
		if (this.bufferedWriter!=null)
			{
			try
				{
				//=====  =====
				this.bufferedWriter.close();
				}
			//=====  =====
			catch (Exception e)
				{
				throw e;
				}
			//=====  =====
			finally
				{
				this.bufferedWriter = null;
				}
			return;
			}
		//=====  =====
		else
			{
			return;
			}
		}
	/**
	 * This method converts a numerical string into BigDecimal number.<br>
	 * 
	 * @param value			target data size includes unit(for example "3KB" or "10mb" or "100GB")
	 * @return				converted decimal size[Byte]
	 * @throws Exception	General error
	 */
	private final BigDecimal convertDataSize (final String value) throws Exception
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
	private static final void deleteFile (final File file) throws Exception
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
							FileAppender.deleteFile(childFile);
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
	private static final String formatXML (final String xml) throws Exception
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
	 * @throws Exception	General error
	 */
	private final BufferedWriter getBufferedWriter () throws Exception
		{
		//===== Check existing output stream =====
		if (this.bufferedWriter!=null)
			{
			return this.bufferedWriter;
			}
		//===== In the case of existing no output stream or refreshing =====
		else
			{
			try
				{
				//===== In case of indicated output buffer size =====
				if (this.maxBufferSize>0)
					{
					//===== Open new output stream with fixed output buffer size =====
					return (this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getFile(), this.append), this.getEncoding()), this.maxBufferSize));
					}
				//===== In case of not indicated output buffer size =====
				else
					{
					//===== Open new output stream with default output buffer size =====
					return (this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getFile(), this.append), this.getEncoding())));
					}
				}
			//===== Error handling =====
			catch (Exception e)
				{
				throw e;
				}
			}
		}


	/**
	 * 
	 * @return
	 * @throws Exception	General error
	 */
	private final File getFile () throws Exception
		{
		//===== Check the exists of class field =====
		if (this.file!=null)
			{
			return this.file;
			}
		//===== Error handling =====
		else
			{
			throw new Exception("Configuration error! \"File\" class object stored in class field is null");
			}
		}


	/**
	 * 
	 * @return
	 * @throws Exception	General error
	 */
	private final long getFileSize () throws Exception
		{
		if (this.fileSize>0L)
			{
			return this.fileSize;
			}
		else
			{
			this.initializeFileSize();
			return this.fileSize;
			}
		}


	/**
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private final File getParentDirectory (final File file) throws Exception
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


	/**
	 * 
	 * @throws Exception	General error
	 */
	private final void initializeFileSize () throws Exception
		{
		this.setFileSize(this.getFile().length());
		return;
		}


	/**
	 * 
	 * @throws Exception	General error
	 */
	private final void rotate () throws Exception
		{
		//========== Variable ==========
		File parentDirectory = null;
		File logFile = null;
		String[][] directoryList = null;
		int directoryListLength = 0;
		int index = 0;
		String fileName = null;
		String parentDirectoryPath = null;
		final Lock LOCK = new ReentrantLock();
		final File LOG_FILE = this.getFile();
		final String LOG_FILE_NAME = LOG_FILE.getName();
		final String FILE_SEPARATOR = ng.lib.logging.SystemUtil.getFileSeparator();
		final String REGEX = ".*"+LOG_FILE_NAME+"|"+".*"+LOG_FILE_NAME+".*";
		final Map<Integer, File> ROTATE_FILE_MAP = new LinkedHashMap<Integer, File>();

		//=====  =====
		LOCK.lock();
		try
			{
			//===== Get parent directory =====
			if ((parentDirectory=this.getParentDirectory(LOG_FILE))!=null 
					&& (parentDirectoryPath=parentDirectory.getCanonicalPath())!=null
					&& (directoryList=Logger.getDirectoryList(parentDirectoryPath))!=null 
					&& (directoryListLength=directoryList.length)>0)
				{
				//===== Close output stream for handling file =====
				this.closeBufferedWriter();
				//=====  =====
				for (int i=0; i<directoryListLength; i++)
					{
					//===== Check file list =====
					if (directoryList[i]!=null && directoryList[i].length>=2)
						{
						//===== In the case of log file includes backup file =====
						if (directoryList[i][0]!=null && directoryList[i][0].equals(FILE_SEPARATOR)==false 
								&& directoryList[i][1]!=null && directoryList[i][1].matches(REGEX)==true)
							{
							//===== Get target file =====
							logFile = new File(directoryList[i][1]);
							//===== In the case of current logging file =====
							if ((fileName=logFile.getName()).equals(LOG_FILE_NAME)==true)
								{
								index = 0;
								}
							//===== In the case of backup file =====
							else
								{
								//===== Get index number =====
								index = Integer.parseInt(fileName.substring(LOG_FILE_NAME.length()+1));
								}
							//===== Set index number and file =====
							ROTATE_FILE_MAP.put(index, logFile);
							}
						//===== In the case of unrelated file/directory =====
						else
							{
							// do nothing
							}
						}
					//===== Error handling =====
					else
						{
						throw new Exception("Internal error! Failed to get file/directory lists on parent directory(=\""+parentDirectoryPath+"\")");
						}
					}
				//===== Repeat for all log file includes backup =====
				for (index=ROTATE_FILE_MAP.size()-1; index>=0; index--)
					{
					//===== In the case of max index =====
					if (index==this.maxBuckupIndex)
						{
						//===== Delete the file =====
						ROTATE_FILE_MAP.get(index).delete();
						}
					//=====  =====
					else
						{
						//===== Update index =====
						ROTATE_FILE_MAP.get(index).renameTo(new File(parentDirectory, LOG_FILE_NAME+"."+Integer.toString(index+1)));
						}
					}
				//===== Generate new logging file =====
				LOG_FILE.createNewFile();
				//===== Terminate procedure =====
				return;
				}
			//===== Error handling =====
			else
				{
				throw new Exception("Can't find parent directory(=\""+parentDirectoryPath+"\") which log files are set");
				}
			}
		//===== Error handling =====
		catch (Exception e)
			{
			throw e;
			}
		//=====  =====
		finally
			{
			//=====  =====
			LOCK.unlock();
			//=====  =====
			this.initializeFileSize();
			}
		}


	/**
	 * 
	 * @param file
	 * @throws Exception
	 */
	private final void setFile (final File file) throws Exception
		{
		//========== Variable ==========
		File parentDirectory = null;

		//===== Check argument =====
		if (file!=null)
			{
			//===== Set a field variable =====
			this.file = file;
			//===== In case of not existing the log file =====
			if (this.file.exists()==false || this.file.isFile()==false)
				{
				//===== Get parent directory =====
				parentDirectory = this.getParentDirectory(this.file);
				//===== Generate new parent directory =====
				parentDirectory.mkdirs();
				//===== Generate a log file =====
				this.file.createNewFile();
				}
			//===== In case of existing the log file =====
			else
				{
				}
			return;
			}
		//===== Argument error =====
		else
			{
			throw new Exception("Argument error! Indicated \"File\" class object is null");
			}
		}


	/**
	 * 
	 * @param fileSize
	 * @throws Exception	General error
	 */
	private final void setFileSize (final long fileSize) throws Exception
		{
		if (fileSize>=0L)
			{
			this.fileSize = fileSize;
			return;
			}
		else
			{
			throw new Exception("Argument error! Indicated file size(=\""+fileSize+"\"[Byte]) is negative");
			}
		}
	}


// End Of File
