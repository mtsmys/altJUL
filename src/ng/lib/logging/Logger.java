/*******************************************************************************
 * Logger.java
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

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

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
public class Logger
	{
	/** Log level enumerated types	*/ public enum Level {TRACE, DEBUG, ERROR, INFO, WARN}
	/** UTF-8 character code name	*/ public static final String UTF8 = "UTF-8";
	/** Logging object 				*/ private java.util.logging.Logger logger = null;
	/** Logging level				*/ private Level level = Level.ERROR;
	/** Appender object				*/ private Appender appender = null;
	/** Logging entity name			*/ private String loggerName = null;



	/**
	 * Constructor.<br>
	 * Create new logger object named by argument "loggerName".<br>
	 * 
	 * @param loggerName	The name of generated logger
	 * @throws Exception	General error
	 */
	public Logger (final String loggerName) throws Exception
		{
		//===== Generate new logger object =====
		this.setLogger(this.createNewLogger(loggerName));
		return;
		}


	/**
	 * 
	 * @param level
	 * @return
	 * @throws Exception	General error
	 */
	public static String convertLogLevel (final java.util.logging.Level level) throws Exception
		{
		if (level!=null)
			{
			if (level.equals(java.util.logging.Level.SEVERE)==true)
				{
				return "ERROR";
				}
			else if (level.equals(java.util.logging.Level.WARNING)==true)
				{
				return "WARN";
				}
			else if (level.equals(java.util.logging.Level.INFO)==true)
				{
				return "INFO";
				}
			else if (level.equals(java.util.logging.Level.FINE)==true)
				{
				return "DEBUG";
				}
			else if (level.equals(java.util.logging.Level.FINEST)==true)
				{
				return "TRACE";
				}
			else
				{
				return "UNDEFINED";
				}
			}
		//===== Error handling =====
		else
			{
			throw new Exception("Argument error! Indicated \"java.util.logging.Level\" class object is null");
			}
		}


	/**
	 * 
	 * @param level
	 * @return
	 * @throws Exception	General error
	 */
	public static java.util.logging.Level convertLogLevel (final Level level) throws Exception
		{
		if (level!=null)
			{
			if (Level.ERROR.equals(level)==true)
				{
				return java.util.logging.Level.SEVERE;
				}
			else if (Level.WARN.equals(level)==true)
				{
				return java.util.logging.Level.WARNING;
				}
			else if (Level.INFO.equals(level)==true)
				{
				return java.util.logging.Level.INFO;
				}
			else if (Level.DEBUG.equals(level)==true)
				{
				return java.util.logging.Level.FINE;
				}
			else if (Level.TRACE.equals(level)==true)
				{
				return java.util.logging.Level.FINEST;
				}
			else
				{
				throw new Exception("Indicated log level is undefined");
				}
			}
		else
			{
			throw new Exception("Argument error! Indicated log level is null");
			}
		}


	/**
	 * 
	 * @param message
	 */
	public void debug (final String message)
		{
		//========== Variable ==========
		Appender appender = null;
		StackTraceElement[] stackTraceElement = null;
		int stackTraceElementLength = 0;
		StackTraceElement[] throwable = null;

		try
			{
			//===== Check log level =====
			if (this.isEnabled(Level.DEBUG)==true)
				{
				//=====  =====
				if ((appender=this.getAppender()).isEnabledLocation()==true)
					{
					//===== Get StackTraceElement object =====
					if ((stackTraceElement=new Exception().getStackTrace())!=null 
							&& (stackTraceElementLength=stackTraceElement.length)>0)
						{
						//===== In the case of length of StackTrace is 1 =====
						if (stackTraceElementLength==1)
							{
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(stackTraceElement);
							}
						//===== In the case of length of StackTrace is 2 more =====
						else
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[stackTraceElementLength-1];
							System.arraycopy(stackTraceElement, 1, throwable, 0, stackTraceElementLength-1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						}
					//===== Error handling =====
					else
						{
						throw new Exception("Can't get StackTraceElement for logging detailed information");
						}
					}
				//=====  =====
				else
					{
					}
				//===== Output log message =====
				this.getLogger().fine(message);
				//===== Terminate procedure =====
				return;
				}
			//=====  =====
			else
				{
				return;
				}
			}
		//===== Error handling =====
		catch (Exception e)
			{
			e.printStackTrace();
			return;
			}
		}


	/**
	 * @param message
	 * @throws Exception	General error
	 */
	public void debug (String message, final Throwable exception)
		{
		//========== Variable ==========
		Appender appender = null;
		StackTraceElement[] stackTraceElement = null;
		int stackTraceElementLength = 0;
		StackTraceElement[] throwable = null;

		try
			{
			//===== Check log level =====
			if (this.isEnabled(Level.DEBUG)==true)
				{
				//=====  =====
				if ((appender=this.getAppender()).isEnabledLocation()==true)
					{
					//===== Get StackTraceElement object =====
					if ((stackTraceElement=new Exception().getStackTrace())!=null 
							&& (stackTraceElementLength=stackTraceElement.length)>0)
						{
						//===== In the case of length of StackTrace is 1 =====
						if (stackTraceElementLength==1)
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[1];
							System.arraycopy(stackTraceElement, 0, throwable, 0, 1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						//===== In the case of length of StackTrace is 2 more =====
						else
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[stackTraceElementLength-1];
							System.arraycopy(stackTraceElement, 1, throwable, 0, stackTraceElementLength-1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						//===== Output log message =====
						this.getLogger().log(java.util.logging.Level.FINE, this.getMessage(message, throwable), throwable);
						return;
						}
					//===== Error handling =====
					else
						{
						throw new Exception("Can't get StackTraceElement for logging detailed information");
						}
					}
				//=====  =====
				else
					{
					//===== Output log message =====
					this.getLogger().log(java.util.logging.Level.FINE, message, exception);
					return;
					}
				}
			//=====  =====
			else
				{
				return;
				}
			}
		//===== Error handling =====
		catch (Exception e)
			{
			e.printStackTrace();
			return;
			}
		}


	/**
	 * 
	 * @param message
	 */
	public void error (final String message)
		{
		//========== Variable ==========
		Appender appender = null;
		StackTraceElement[] stackTraceElement = null;
		int stackTraceElementLength = 0;
		StackTraceElement[] throwable = null;

		try
			{
			//=====  =====
			if (this.isEnabled(Level.ERROR)==true)
				{
				//=====  =====
				if ((appender=this.getAppender()).isEnabledLocation()==true)
					{
					//===== Get StackTraceElement object =====
					if ((stackTraceElement=new Exception().getStackTrace())!=null 
							&& (stackTraceElementLength=stackTraceElement.length)>0)
						{
						//===== In the case of length of StackTrace is 1 =====
						if (stackTraceElementLength==1)
							{
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(stackTraceElement);
							}
						//===== In the case of length of StackTrace is 2 more =====
						else
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[stackTraceElementLength-1];
							System.arraycopy(stackTraceElement, 1, throwable, 0, stackTraceElementLength-1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						}
					//===== Error handling =====
					else
						{
						throw new Exception("Can't get StackTraceElement for logging detailed information");
						}
					}
				//=====  =====
				else
					{
					}
				//===== Output log message =====
				this.getLogger().severe(message);
				//===== Terminate procedure =====
				return;
				}
			//=====  =====
			else
				{
				return;
				}
			}
		//===== Error handling =====
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}


	/**
	 * 
	 * @param message
	 * @param exception
	 */
	public void error (String message, final Throwable exception)
		{
		//========== Variable ==========
		Appender appender = null;
		StackTraceElement[] stackTraceElement = null;
		int stackTraceElementLength = 0;
		StackTraceElement[] throwable = null;

		try
			{
			//===== In the case of enabling "ERROR" level log =====
			if (this.isEnabled(Level.ERROR)==true)
				{
				//=====  =====
				if ((appender=this.getAppender()).isEnabledLocation()==true)
					{
					//===== Get StackTraceElement object =====
					if ((stackTraceElement=new Exception().getStackTrace())!=null 
							&& (stackTraceElementLength=stackTraceElement.length)>0)
						{
						//===== In the case of length of StackTrace is 1 =====
						if (stackTraceElementLength==1)
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[1];
							System.arraycopy(stackTraceElement, 0, throwable, 0, 1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						//===== In the case of length of StackTrace is 2 more =====
						else
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[stackTraceElementLength-1];
							System.arraycopy(stackTraceElement, 1, throwable, 0, stackTraceElementLength-1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						//===== Output log message =====
						this.getLogger().log(java.util.logging.Level.SEVERE, this.getMessage(message, throwable), throwable);
						//===== Terminate procedure =====
						return;
						}
					//===== Error handling =====
					else
						{
						throw new Exception("Can't get StackTraceElement for logging detailed information");
						}
					}
				//=====  =====
				else
					{
					//===== Output log message =====
					this.getLogger().log(java.util.logging.Level.SEVERE, message, exception);
					//===== Terminate procedure =====
					return;
					}
				}
			//===== In the case of not enabling "ERROR" level log =====
			else
				{
				return;
				}
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
	 * 
	 * @param path			
	 * @return				The first array is "/" or "\"(=file separator), or integer string(=file size[Byte]), <br>
	 * 						and the second array is canonical file path.
	 * @throws Exception	General error
	 */
	public static String[][] getDirectoryList (final String path) throws Exception
		{
		//========== Variable ==========
		File file = null;						// 
		File[] fileList = null;					// 
		int fileListLength = 0;					// 
		String[][] directoryInformation = null;	// 

		//========== Get Directory Information from Indicated Path ==========
		//===== Argument check =====
		if (path!=null && path.length()>0 
				&& (file=new File(path))!=null && file.exists()==true)
			{
			//===== When the path is directory =====
			if (file.isDirectory()==true)
				{
				//===== When the directory has files =====
				if ((fileList=file.listFiles())!=null && (fileListLength=fileList.length)>0)
					{
					//===== Prepare array for storing information =====
					directoryInformation = new String[fileListLength][2];
					//===== Get directory name or file name =====
					for (int i=0; i<fileListLength; i++)
						{
						//===== Check directory or not =====
						if ((file=fileList[i])!=null && file.isDirectory()==true)
							{
							// get directory name(added "/" at the head)
							directoryInformation[i][0] = ng.lib.logging.SystemUtil.getFileSeparator();
							directoryInformation[i][1] = file.getCanonicalPath();
							}
						//===== Check file or not =====
						else if (file!=null && file.isFile()==true)
							{
							// get file name
							directoryInformation[i][0] = Long.toString(file.length());
							directoryInformation[i][1] = file.getCanonicalPath();
							}
						//===== Error handling =====
						else
							{
							throw new Exception("System error! Unknown kind of file exists");
							}
						}
					//===== Set null into variable =====
					file = null;
					fileList = null;
					//===== Return directory information =====
					return directoryInformation;
					}
				//===== When the directory is vacant(=no file exists) =====
				else
					{
					return null;
					}
				}
			//===== When the path is file =====
			else if (file.isFile()==true)
				{
				return null;
				}
			//===== Error handling =====
			else
				{
				throw new Exception("System error! Unknown kind of file exists");
				}
			}
		//===== Error handling =====
		else
			{
			throw new Exception("Indicated path is invalid");
			}
		}


	/**
	 * 
	 * @return
	 */
	public Level getLevel ()
		{
		return this.level;
		}


	/**
	 * 
	 * @param level
	 * @return
	 * @throws Exception
	 */
	public static Level getLogLevel (final String level) throws Exception
		{
		//========== Variable ==========
		final String ERROR = "error";
		final String WARN = "warn";
		final String INFO = "info";
		final String DEBUG = "debug";
		final String TRACE = "trace";

		//===== In the case of error level =====
		if (ERROR.equalsIgnoreCase(level)==true)
			{
			return Level.ERROR;
			}
		//===== In the case of warn level =====
		else if (WARN.equalsIgnoreCase(level)==true)
			{
			return Level.WARN;
			}
		//===== In the case of info level =====
		else if (INFO.equalsIgnoreCase(level)==true)
			{
			return Level.INFO;
			}
		//===== In the case of debug level =====
		else if (DEBUG.equalsIgnoreCase(level)==true)
			{
			return Level.DEBUG;
			}
		//===== In the case of trace level =====
		else if (TRACE.equalsIgnoreCase(level)==true)
			{
			return Level.TRACE;
			}
		//===== Error handling =====
		else
			{
			throw new Exception("Argument error! Indicated log level(=\""+level+"\") is invalid");
			}
		}


	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getName () throws Exception
		{
		//========== Variable ==========
		final String NAME = this.getLogger().getName();

		//===== Get the logger name =====
		if (NAME!=null && NAME.trim().length()>0)
			{
			return NAME;
			}
		//===== Error handling =====
		else
			{
			throw new Exception("Logger name is null or vacant");
			}
		}


	/**
	 * 
	 * @return
	 */
	public String getStackTraceElementMessage (final StackTraceElement[] stackTraceElement)
		{
		//========== Variable ==========
		StringWriter stringWriter = null;
		PrintWriter printWriter = null;
		final Exception EXCEPTION = new Exception();

		try
			{
			//=====  =====
			if (stackTraceElement!=null 
					&& (stringWriter=new StringWriter())!=null 
					&& (printWriter=new PrintWriter(stringWriter))!=null)
				{
				//=====  =====
				EXCEPTION.setStackTrace(stackTraceElement);
				//=====  =====
				EXCEPTION.printStackTrace(printWriter);
				printWriter.flush();
				//=====  =====
				return stringWriter.toString();
				}
			//===== Error handling =====
			else
				{
				throw new Exception();
				}
			}
		//===== Error handling =====
		catch (Exception e)
			{
			return null;
			}
		finally
			{
			if (printWriter!=null)
				{
				try
					{
					printWriter.close();
					}
				catch (Exception e)
					{
					}
				}
			else
				{
				}
			if (stringWriter!=null)
				{

				try
					{
					stringWriter.close();
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


	/**
	 * 
	 * @param message
	 */
	public void info (final String message)
		{
		//========== Variable ==========
		Appender appender = null;
		StackTraceElement[] stackTraceElement = null;
		int stackTraceElementLength = 0;
		StackTraceElement[] throwable = null;

		try
			{
			//=====  =====
			if (this.isEnabled(Level.INFO)==true)
				{
				//=====  =====
				if ((appender=this.getAppender()).isEnabledLocation()==true)
					{
					//===== Get StackTraceElement object =====
					if ((stackTraceElement=new Exception().getStackTrace())!=null 
							&& (stackTraceElementLength=stackTraceElement.length)>0)
						{
						//===== In the case of length of StackTrace is 1 =====
						if (stackTraceElementLength==1)
							{
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(stackTraceElement);
							}
						//===== In the case of length of StackTrace is 2 more =====
						else
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[stackTraceElementLength-1];
							System.arraycopy(stackTraceElement, 1, throwable, 0, stackTraceElementLength-1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						}
					//===== Error handling =====
					else
						{
						throw new Exception("Can't get StackTraceElement for logging detailed information");
						}
					}
				//=====  =====
				else
					{
					}
				//===== Output log message =====
				this.getLogger().info(message);
				//===== Terminate procedure =====
				return;
				}
			//=====  =====
			else
				{
				return;
				}
			}
		//=====  =====
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}


	/**
	 * 
	 * @param message
	 * @param exception
	 */
	public void info (String message, final Throwable exception)
		{
		//========== Variable ==========
		Appender appender = null;
		StackTraceElement[] stackTraceElement = null;
		int stackTraceElementLength = 0;
		StackTraceElement[] throwable = null;

		try
			{
			//=====  =====
			if (this.isEnabled(Level.INFO)==true)
				{
				//=====  =====
				if ((appender=this.getAppender()).isEnabledLocation()==true)
					{
					//===== Get StackTraceElement object =====
					if ((stackTraceElement=new Exception().getStackTrace())!=null 
							&& (stackTraceElementLength=stackTraceElement.length)>0)
						{
						//===== In the case of length of StackTrace is 1 =====
						if (stackTraceElementLength==1)
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[1];
							System.arraycopy(stackTraceElement, 0, throwable, 0, 1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						//===== In the case of length of StackTrace is 2 more =====
						else
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[stackTraceElementLength-1];
							System.arraycopy(stackTraceElement, 1, throwable, 0, stackTraceElementLength-1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						//===== Output log message =====
						this.getLogger().log(java.util.logging.Level.INFO, this.getMessage(message, throwable), throwable);
						//===== Terminate procedure =====
						return;
						}
					//===== Error handling =====
					else
						{
						throw new Exception("Can't get StackTraceElement for logging detailed information");
						}
					}
				//=====  =====
				else
					{
					//===== Output log message =====
					this.getLogger().log(java.util.logging.Level.INFO, message, exception);
					//===== Terminate procedure =====
					return;
					}
				}
			//=====  =====
			else
				{
				return;
				}
			}
		//=====  =====
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}


	/**
	 * 
	 * @param level
	 * @return
	 * @throws Exception
	 */
	public boolean isEnabled (final Level level) throws Exception
		{
		//========== Variable ==========
		final Level CONFIGURATION_LEVEL = this.getLevel();
		final Level ERROR = Level.ERROR;
		final Level WARN = Level.WARN;
		final Level INFO = Level.INFO;
		final Level DEBUG = Level.DEBUG;
		final Level TRACE = Level.TRACE;

		//===== Check argument =====
		if (level!=null)
			{
			//===== In the case of ERROR =====
			if (level.equals(ERROR)==true)
				{
				return true;
				}
			//===== In the case of WARN and configuration is lower =====
			else if (level.equals(WARN)==true 
					&& CONFIGURATION_LEVEL.equals(ERROR)==false)
				{
				return true;
				}
			//===== In the case of INFO and configuration is lower =====
			else if (level.equals(INFO)==true 
					&& CONFIGURATION_LEVEL.equals(ERROR)==false && CONFIGURATION_LEVEL.equals(WARN)==false)
				{
				return true;
				}
			//===== In the case of DEBUG and so configuration =====
			else if (level.equals(DEBUG)==true 
					&& (CONFIGURATION_LEVEL.equals(DEBUG)==true || CONFIGURATION_LEVEL.equals(TRACE)==true))
				{
				return true;
				}
			//===== In the case of TRACE and so configuration =====
			else if (level.equals(TRACE)==true 
					&& CONFIGURATION_LEVEL.equals(TRACE)==true)
				{
				return true;
				}
			//===== In the case of enabled logging =====
			else
				{
				return false;
				}
			}
		//===== Argument error =====
		else
			{
			throw new Exception("Argument error! Indicated log level is null");
			}
		}


	/**
	 * 
	 * @param appender
	 * @throws Exception
	 */
	public void setAppender (final Appender appender) throws Exception
		{
		//===== Check argument =====
		if (appender!=null)
			{
			//===== Set appender into java.util.logging.Logger =====
			this.appender = appender;
			//===== Update Appender class instance for handling logging procedure =====
			this.getLogger().addHandler(this.appender);
			return;
			}
		//===== Argument error =====
		else
			{
			throw new Exception("Argument error! Indicated instance derived by Appender class is null");
			}
		}


	/**
	 * Set the logging configuration file in Java class path.<br>
	 * 
	 * @param configurationFile	A configuration file name in Java class path
	 * @throws Exception		General error
	 */
	public void setConfigurationFile (final String configurationFilePath) throws Exception
		{
		//========== Variable ==========
		File configurationFile = null;
		Appender appender = null;
		final String XML_FILE_EXTENSION = "xml";
//		final String JSON_FILE_EXTENSION = "json";

		//===== Check existence of configuration file =====
		if ((configurationFile=this.resolveResourceFile(configurationFilePath)).exists()==true 
				&& configurationFile.isFile()==true 
				&& configurationFile.canRead()==true)
			{
			//===== In case of XML file =====
			if (configurationFile.getCanonicalPath().toLowerCase().endsWith(XML_FILE_EXTENSION)==true)
				{
				//===== Parse XML configuration file =====
				appender = this.parseXMLConfigurationFile(configurationFile);
				//===== Store into field variable of this class =====
				this.setAppender(appender);
				return;
				}
/*
			//===== In case of JSON file =====
			else if (configurationFile.getCanonicalPath().toLowerCase().endsWith(JSON_FILE_EXTENSION)==true)
				{
				//===== Parse JSON configuration file =====
				this.parseJSONConfigurationFile(configurationFile);
				return;
				}
*/
			//===== Error handling =====
			else
				{
				throw new Exception("Argument error! Indicated file(=\""+configurationFile.getCanonicalPath()+"\") includes invalid extension");
				}
			}
		//===== Error handling =====
		else
			{
			throw new Exception("Argument error! Indicated file(=\""+configurationFile.getCanonicalPath()+"\") doesn't exist");
			}
		}


	/**
	 * 
	 * @param level
	 * @throws Exception
	 */
	public void setLevel (final Level level) throws Exception
		{
		//========== Variable ==========
		final java.util.logging.Logger LOGGER = this.getLogger();

		//=====  =====
		if (level!=null)
			{
			//===== In the case of ERROR level =====
			if (level.equals(Level.ERROR)==true)
				{
				LOGGER.setLevel(java.util.logging.Level.SEVERE);
				}
			//===== In the case of WARN level =====
			else if (level.equals(Level.WARN)==true)
				{
				LOGGER.setLevel(java.util.logging.Level.WARNING);
				}
			//===== In the case of INFO level =====
			else if (level.equals(Level.INFO)==true)
				{
				LOGGER.setLevel(java.util.logging.Level.INFO);
				}
			//===== In the case of DEBUG level =====
			else if (level.equals(Level.DEBUG)==true)
				{
				LOGGER.setLevel(java.util.logging.Level.FINE);
				}
			//===== In the case of TRACE level =====
			else if (level.equals(Level.TRACE)==true)
				{
				LOGGER.setLevel(java.util.logging.Level.FINEST);
				}
			//===== Error handling =====
			else
				{
				throw new Exception("Indicated log level isn't valid type(=\"ERROR\" or \"WARN\" or \"INFO\" or \"DEBUG\" or \"TRACE\")");
				}
			//=====  =====
			this.level = level;
			//=====  =====
			return;
			}
		else
			{
			throw new Exception("Indicated log level isn't valid type(=\"ERROR\" or \"WARN\" or \"INFO\" or \"DEBUG\" or \"TRACE\")");
			}
		}


	/**
	 * Set indicated logger name into a field variable of this class.<br>
	 * 
	 * @param loggerName	Logger name
	 * @throws Exception
	 */
	public void setLoggerName (final String loggerName) throws Exception
		{
		//===== Check argument =====
		if (loggerName!=null && loggerName.trim().length()>0)
			{
			//===== Set logger name into field variable =====
			this.loggerName = loggerName;
			return;
			}
		//===== Argument error =====
		else
			{
			throw new Exception("Argument error! Indicated category name is null or empty");
			}
		}


	/**
	 * 
	 * @param message
	 */
	public void trace (final String message)
		{
		//========== Variable ==========
		Appender appender = null;
		StackTraceElement[] stackTraceElement = null;
		int stackTraceElementLength = 0;
		StackTraceElement[] throwable = null;

		try
			{
			//=====  =====
			if (this.isEnabled(Level.TRACE)==true)
				{
				//=====  =====
				if ((appender=this.getAppender()).isEnabledLocation()==true)
					{
					//===== Get StackTraceElement object =====
					if ((stackTraceElement=new Exception().getStackTrace())!=null 
							&& (stackTraceElementLength=stackTraceElement.length)>0)
						{
						//===== In the case of length of StackTrace is 1 =====
						if (stackTraceElementLength==1)
							{
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(stackTraceElement);
							}
						//===== In the case of length of StackTrace is 2 more =====
						else
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[stackTraceElementLength-1];
							System.arraycopy(stackTraceElement, 1, throwable, 0, stackTraceElementLength-1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						}
					//===== Error handling =====
					else
						{
						throw new Exception("Can't get StackTraceElement for logging detailed information");
						}
					}
				//=====  =====
				else
					{
					}
				//===== Output log message =====
				this.getLogger().finest(message);
				//===== Terminate procedure =====
				return;
				}
			//=====  =====
			else
				{
				return;
				}
			}
		//=====  =====
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}


	/**
	 * 
	 * @param message
	 * @param exception
	 */
	public void trace (String message, final Throwable exception)
		{
		//========== Variable ==========
		Appender appender = null;
		StackTraceElement[] stackTraceElement = null;
		int stackTraceElementLength = 0;
		StackTraceElement[] throwable = null;

		try
			{
			//=====  =====
			if (this.isEnabled(Level.TRACE)==true)
				{
				//=====  =====
				if ((appender=this.getAppender()).isEnabledLocation()==true)
					{
					//===== Get StackTraceElement object =====
					if ((stackTraceElement=new Exception().getStackTrace())!=null 
							&& (stackTraceElementLength=stackTraceElement.length)>0)
						{
						//===== In the case of length of StackTrace is 1 =====
						if (stackTraceElementLength==1)
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[1];
							System.arraycopy(stackTraceElement, 0, throwable, 0, 1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						//===== In the case of length of StackTrace is 2 more =====
						else
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[stackTraceElementLength-1];
							System.arraycopy(stackTraceElement, 1, throwable, 0, stackTraceElementLength-1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						//===== Output log message =====
						this.getLogger().log(java.util.logging.Level.FINEST, this.getMessage(message, throwable), throwable);
						//===== Terminate procedure =====
						return;
						}
					//===== Error handling =====
					else
						{
						throw new Exception("Can't get StackTraceElement for logging detailed information");
						}
					}
				//=====  =====
				else
					{
					//===== Output log message =====
					this.getLogger().log(java.util.logging.Level.FINEST, message, exception);
					//===== Terminate procedure =====
					return;
					}
				}
			//=====  =====
			else
				{
				return;
				}
			}
		//=====  =====
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}


	/**
	 * 
	 * @param message
	 */
	public void warn (final String message)
		{
		//========== Variable ==========
		Appender appender = null;
		StackTraceElement[] stackTraceElement = null;
		int stackTraceElementLength = 0;
		StackTraceElement[] throwable = null;

		try
			{
			//=====  =====
			if (this.isEnabled(Level.WARN)==true)
				{
				//=====  =====
				if ((appender=this.getAppender()).isEnabledLocation()==true)
					{
					//===== Get StackTraceElement object =====
					if ((stackTraceElement=new Exception().getStackTrace())!=null 
							&& (stackTraceElementLength=stackTraceElement.length)>0)
						{
						//===== In the case of length of StackTrace is 1 =====
						if (stackTraceElementLength==1)
							{
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(stackTraceElement);
							}
						//===== In the case of length of StackTrace is 2 more =====
						else
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[stackTraceElementLength-1];
							System.arraycopy(stackTraceElement, 1, throwable, 0, stackTraceElementLength-1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						}
					//===== Error handling =====
					else
						{
						throw new Exception("Can't get StackTraceElement for logging detailed information");
						}
					}
				//=====  =====
				else
					{
					}
				//===== Output log message =====
				this.getLogger().warning(message);
				//===== Terminate procedure =====
				return;
				}
			//=====  =====
			else
				{
				return;
				}
			}
		//=====  =====
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}


	/**
	 * 
	 * @param message
	 * @param exception
	 */
	public void warn (String message, final Throwable exception)
		{
		//========== Variable ==========
		Appender appender = null;
		StackTraceElement[] stackTraceElement = null;
		int stackTraceElementLength = 0;
		StackTraceElement[] throwable = null;

		try
			{
			//=====  =====
			if (this.isEnabled(Level.WARN)==true)
				{
				//=====  =====
				if ((appender=this.getAppender()).isEnabledLocation()==true)
					{
					//===== Get StackTraceElement object =====
					if ((stackTraceElement=new Exception().getStackTrace())!=null 
							&& (stackTraceElementLength=stackTraceElement.length)>0)
						{
						//===== In the case of length of StackTrace is 1 =====
						if (stackTraceElementLength==1)
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[1];
							System.arraycopy(stackTraceElement, 0, throwable, 0, 1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						//===== In the case of length of StackTrace is 2 more =====
						else
							{
							//===== Copy StackTrace element =====
							throwable = new StackTraceElement[stackTraceElementLength-1];
							System.arraycopy(stackTraceElement, 1, throwable, 0, stackTraceElementLength-1);
							//===== Set StackTraceElement object =====
							appender.setStackTraceElement(throwable);
							}
						//===== Output log message =====
						this.getLogger().log(java.util.logging.Level.WARNING, this.getMessage(message, throwable), throwable);
						//===== Terminate procedure =====
						return;
						}
					//===== Error handling =====
					else
						{
						throw new Exception("Can't get StackTraceElement for logging detailed information");
						}
					}
				//=====  =====
				else
					{
					//===== Output log message =====
					this.getLogger().log(java.util.logging.Level.WARNING, message, exception);
					//===== Terminate procedure =====
					return;
					}
				}
			//=====  =====
			else
				{
				return;
				}
			}
		//=====  =====
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}


	/**
	 * This method generates new java.util.logging.Logger object in indicated name.<br>
	 * 
	 * @param name			Logger name
	 * @throws Exception	
	 */
	protected java.util.logging.Logger createNewLogger (final String name) throws Exception
		{
		//========== Variable ==========
		java.util.logging.Logger logger = null;

		//===== Check argument =====
		if (name!=null && name.trim().length()>0)
			{
			//===== Generate new logger =====
			logger = java.util.logging.Logger.getLogger(name);
			//===== Don't use parent logger(=root logger) =====
			logger.setUseParentHandlers(false);
			//===== Return generated logger =====
			return logger;
			}
		//===== Argument error =====
		else
			{
			throw new Exception("Argument error! Indicated logger name is null or empty");
			}
		}


	/**
	 * This method returns an Appender class instance stored in class field.<br>
	 * 
	 * @return				Appender class instance stored in class field
	 * @throws Exception	General error
	 */
	protected Appender getAppender () throws Exception
		{
		//===== Check appender instance =====
		if (this.appender!=null)
			{
			return this.appender;
			}
		//===== Error handling =====
		else
			{
			throw new Exception("Configuration error! Haven't set \"Appender\" class instance yet");
			}
		}


	/**
	 * 
	 * @param appenderName
	 * @param node
	 * @return
	 * @throws Exception
	 */
	protected Appender getAppender (final String appenderName, final Node node) throws Exception
		{
		//========== Variable ==========
		Appender appender = null;
		String name = null;
		String appenderClassName = null;
		Node nameAttributeNode = null;
		Node classAttributeNode = null;
		final String ATTRIBUTE_NAME = "name";
		final String ATTRIBUTE_CLASS = "class";

		//===== Check argument =====
		if (appenderName!=null && appenderName.trim().length()>0 
				&& node!=null)
			{
			try
				{
				if ((nameAttributeNode=((Element)node).getAttributeNode(ATTRIBUTE_NAME))!=null 
						&& (name=nameAttributeNode.getNodeValue())!=null && name.equals(appenderName)==true 
						&& (classAttributeNode=((Element)node).getAttributeNode(ATTRIBUTE_CLASS))!=null 
						&& (appenderClassName=classAttributeNode.getNodeValue())!=null)
					{
					//===== Load new appender class =====
					appender = (Appender)Class.forName(appenderClassName).newInstance();
					//===== Set logger name =====
					appender.setLoggerName(this.getLoggerName());
					//===== Set property information =====
					appender.parseAppenderNode(node);
					//=====  =====
					return appender;
					}
				//===== Error handling =====
				else if (nameAttributeNode==null)
					{
					throw new Exception("Failed to get a \"//"+node.getNodeName()+"/"+ATTRIBUTE_NAME+"\" attribute node in configuration file");
					}
				else if (name==null || name.equals(appenderName)==false)
					{
					throw new Exception("\"//"+node.getNodeName()+"/"+ATTRIBUTE_NAME+"\" attribute node value(=\""+name+"\") is null or doesn't equal \""+appenderName+"\"");
					}
				else if (classAttributeNode==null)
					{
					throw new Exception("Failed to get a \"//"+node.getNodeName()+"/"+ATTRIBUTE_CLASS+"\" attribute node in configuration file");
					}
				else
					{
					throw new Exception("\"//"+node.getNodeName()+"/"+ATTRIBUTE_CLASS+"\" attribute node value is null or vacant");
					}
				}
			//===== Error handling =====
			catch (Exception e)
				{
				return null;
				}
			}
		//===== Argument error =====
		else if (appenderName==null || appenderName.trim().length()<=0)
			{
			throw new Exception("Argument error! Indicated \"appenderName\" string is null or vacant");
			}
		else
			{
			throw new Exception("Argument error! Indicated \"org.w3c.dom.Node\" class object is null");
			}
		}


	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String getLoggerName () throws Exception
		{
		//===== Check logger name =====
		if (this.loggerName!=null && this.loggerName.trim().length()>0)
			{
			return this.loggerName;
			}
		else
			{
			return this.getLogger().getName();
			}
		}


	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	protected java.util.logging.Logger getLogger () throws Exception
		{
		//===== Check logger instance =====
		if (this.logger!=null)
			{
			return this.logger;
			}
		//===== Error handling =====
		else
			{
			throw new Exception("Unknown error has happened! Logger instance which generated at constructor is null");
			}
		}


	/**
	 * This method constructs a log message appended StackTrame.<br>
	 * 
	 * @param message			log message
	 * @param stackTraceElement	StackTraceElement class object including stack trace information
	 * @return					constructed message
	 */
	protected String getMessage (String message, final StackTraceElement[] stackTraceElement)
		{
		//===== Check argument =====
		if (message!=null)
			{
			}
		//===== Argument error =====
		else
			{
			message = "";
			}
		return message+ng.lib.logging.SystemUtil.getLineFeed()+this.getStackTraceElementMessage(stackTraceElement);
		}


	/**
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	protected String parseLoggerNode (Node node) throws Exception
		{
		//========== Variable ==========
		String name = null;
		String level = null;
		String appenderName = null;
		Node attribute = null;
		NodeList childNodeList = null;
		int childNodeListLength = 0;
		final String LOGGER_NAME = this.getName();
		final String ATTRIBUTE_NAME = "name";
		final String ATTRIBUTE_VALUE = "value";
		final String ATTRIBUTE_REF = "ref";
		final String NODE_PRIORITY = "priority";
		final String NODE_APPENDER_REF = "appender-ref";

		//===== Check argument =====
		if (node!=null 
				&& (attribute=((Element)node).getAttributeNode(ATTRIBUTE_NAME))!=null 
				&& (name=attribute.getNodeValue())!=null && name.trim().length()>0)
			{
			//=====  =====
			if (name.startsWith(LOGGER_NAME)==true)
				{
				//=====  =====
				if ((childNodeList=node.getChildNodes())!=null && (childNodeListLength=childNodeList.getLength())>0)
					{
					//=====  =====
					for (int i=0; i<childNodeListLength; i++)
						{
						//=====  =====
						if ((node=childNodeList.item(i))!=null && NODE_PRIORITY.equals(node.getNodeName())==true)
							{
							//===== Get attribute node =====
							if ((attribute=((Element)node).getAttributeNode(ATTRIBUTE_VALUE))!=null 
									&& (level=attribute.getNodeValue())!=null && level.trim().length()>0)
								{
								//===== Set log level =====
								this.setLevel(Logger.getLogLevel(level));
								}
							//===== Error handling =====
							else if (attribute==null)
								{
								throw new Exception("Failed to get \""+ATTRIBUTE_VALUE+"\" attribute node in \""+node.getNodeName()+"\" node");
								}
							else
								{
								throw new Exception("Failed to get \""+ATTRIBUTE_VALUE+"\" attribute value in \""+node.getNodeName()+"\" node");
								}
							}
						//=====  =====
						else if ((node=childNodeList.item(i))!=null && NODE_APPENDER_REF.equals(node.getNodeName())==true)
							{
							//=====  =====
							if ((attribute=((Element)node).getAttributeNode(ATTRIBUTE_REF))!=null 
									&& (appenderName=attribute.getNodeValue())!=null && appenderName.trim().length()>0)
								{
								break;
								}
							//===== Error handling =====
							else if (attribute==null)
								{
								throw new Exception("Failed to get \""+ATTRIBUTE_REF+"\" attribute node in \""+node.getNodeName()+"\" node");
								}
							else
								{
								throw new Exception("Failed to get \""+ATTRIBUTE_REF+"\" attribute value in \""+node.getNodeName()+"\" node");
								}
							}
						//=====  =====
						else
							{
							}
						}
					//===== Check the result =====
					if (this.getLevel()!=null && appenderName!=null)
						{
						//===== Set category name =====
						this.setLoggerName(name);
						//===== Terminate procedure =====
						return appenderName;
						}
					//===== Error handling =====
					else if (appenderName==null)
						{
						throw new Exception("Failed to get \""+ATTRIBUTE_REF+"\" attribute value in \""+node.getNodeName()+"\" node");
						}
					else
						{
						throw new Exception("Failed to get \""+ATTRIBUTE_VALUE+"\" attribute value in \""+node.getNodeName()+"\" node");
						}
					}
				//===== Error handling =====
				else
					{
					throw new Exception("Configuration error! \""+node.getNodeName()+"\" node doesn't have child nodes");
					}
				}
			//=====  =====
			else
				{
				return null;
				}
			}
		//===== Argument error =====
		else if (node==null)
			{
			throw new Exception("Argument error! \"org.w3c.dom.Node\" class object is null");
			}
		else if (attribute==null)
			{
			throw new Exception("Argument error! Failed to get \""+ATTRIBUTE_NAME+"\" attribute node in \""+node.getNodeName()+"\" node");
			}
		else
			{
			throw new Exception("Argument error! Failed to get \""+ATTRIBUTE_NAME+"\" attribute value in \""+node.getNodeName()+"\" node");
			}
		}


	/**
	 * 
	 * @param xmlConfigurationFile
	 * @return
	 * @throws Exception
	 */
	protected Appender parseXMLConfigurationFile (final File xmlConfigurationFile) throws Exception
		{
		//========== Variable ==========
		Document document = null;
		NodeList loggerNodeList = null;
		NodeList appenderNodeList = null;
		String appenderName = null;
		Appender appender = null;
		int nodeListLength = 0;
		final String NODE_LOGGER= "category";
		final String NODE_APPENDER = "appender";

		//===== Get "category" nodes =====
		if ((document=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlConfigurationFile))!=null 
				&& (loggerNodeList=document.getElementsByTagName(NODE_LOGGER))!=null
				&& (nodeListLength=loggerNodeList.getLength())>0)
			{
			//===== Repeat while the node exists =====
			for (int i=0; i<nodeListLength; i++)
				{
				//===== Get appender name from XML configuration node =====
				if ((appenderName=this.parseLoggerNode(loggerNodeList.item(i)))!=null)
					{
					break;
					}
				//===== Error handling =====
				else
					{
					}
				}
			//===== Get node set from XML configuration node =====
			if ((appenderNodeList=document.getElementsByTagName(NODE_APPENDER))!=null 
					&& (nodeListLength=appenderNodeList.getLength())>0)
				{
				//===== Repeat while the node exists =====
				for (int i=0; i<nodeListLength; i++)
					{
					//===== Set-up Appender class object with XML configuration node =====
					if ((appender=this.getAppender(appenderName, appenderNodeList.item(i)))!=null)
						{
						return appender;
						}
					//===== Error handling =====
					else
						{
						}
					}
				//===== Error handling =====
				throw new Exception("Failed to get \""+appenderName+"\" node in configuration file(=\""+xmlConfigurationFile.getCanonicalPath()+"\")");
				}
			//===== Error handling =====
			else if (appenderNodeList==null)
				{
				throw new Exception("Failed to get \""+NODE_APPENDER+"\" node in configuration file(=\""+xmlConfigurationFile.getCanonicalPath()+"\")");
				}
			else
				{
				throw new Exception("There are no \""+NODE_APPENDER+"\" nodes in configuration file(=\""+xmlConfigurationFile.getCanonicalPath()+"\")");
				}
			}
		//=====  =====
		else
			{
			throw new Exception("XML parsing error! There isn't \""+NODE_LOGGER+"\" node in indicated file(=\""+xmlConfigurationFile.getCanonicalPath()+"\")");
			}
		}


	/**
	 * Search & detect the indicated file in class path and return the file path
	 * 
	 * @param resourceFilePath	A target file pathname in class path
	 * @return					The file pathname string in class path
	 * @throws Exception		General error
	 */
	protected File resolveResourceFile (final String resourceFilePath) throws Exception
		{
		//========== Variable ==========
		URL url = null;
		URI uri = null;
		File resourceFile = null;
		final String FILE_SCHEME = "file://";
		final String SLASH = "/";
		final String OS = System.getProperty("os.name").toLowerCase();
		final String WINDOWS = "windows";

		try
			{
			//===== Get file path on class path =====
			if ((url=this.getClass().getClassLoader().getResource(resourceFilePath))!=null 
					&& (uri=url.toURI())!=null 
					&& (resourceFile=new File(uri)).exists()==true 
					&& resourceFile.isFile()==true 
					&& resourceFile.canRead()==true
					)
				{
				//===== Return the file pathname string =====
				return resourceFile;
				}
			//===== Get file path from URI =====
			else if (((OS.contains(WINDOWS)==true && (url=new URL(FILE_SCHEME+SLASH+resourceFilePath))!=null) 
							|| (url=new URL(FILE_SCHEME+resourceFilePath))!=null)
					&& (uri=url.toURI())!=null 
					&& (resourceFile=new File(uri)).exists()==true 
					&& resourceFile.isFile()==true 
					&& resourceFile.canRead()==true 
					)
				{
				//===== Return the file pathname string =====
				return resourceFile;
				}
			//===== Error handling =====
			else
				{
				throw new Exception("Failed to resolve the file(=\""+resourceFilePath+"\") in classpath(=\""+System.getProperty("java.class.path")+"\")");
				}
			}
		//===== Error handling =====
		catch (Exception e)
			{
			throw new Exception("\""+url.getPath()+"\" is invalid resource expression");
			}
		}


	/**
	 * 
	 * @param logger
	 * @throws Exception
	 */
	protected void setLogger (final java.util.logging.Logger logger) throws Exception
		{
		//===== Check argument =====
		if (logger!=null)
			{
			this.logger = logger;
			return;
			}
		//===== Argument error =====
		else
			{
			throw new Exception("Argument error! Indicated \"java.util.logging.Logger\" class object is null");
			}
		}
	}


// End Of File
