/*******************************************************************************
 * Layout.java : The NextGraFix Application Library for JVM Platform
 * 
 * Copyright (c) 2016, Akihisa Yasuda
 * All rights reserved.
 ******************************************************************************/

package ng.lib.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Node;


/**
 * Logging Library for JVM Platform
 * 
 * @author		NextGraFix.com
 * @since		Aug.30,2011
 * @version		1.0.0
 */
public abstract class Layout
	{
	/** */ protected String logLevel = null;
	/** */ protected String classPath = null;
	/** */ protected String loggerName = null;
	/** */ protected Date date = null;
	/** */ protected Map<String, String> propertyMap = null;
	/** */ protected StackTraceElement[] stackTraceElement = null;
	/** */ protected boolean enableLocation = false;
	/** */ protected long startTime = 0L;
	/** */ protected SimpleDateFormat simpleDateFormat = null;



	/**
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public String getLogMessage (final String message) throws Exception
		{
		return message;
		}


	/**
	 * 
	 * @return
	 */
	public boolean isEnabledLocation ()
		{
		return enableLocation;
		}


	/**
	 * 
	 * @throws Exception
	 */
	public void setClassPath (final String classPath) throws Exception
		{
		if (classPath!=null && classPath.trim().length()>0)
			{
			this.classPath = classPath;
			return;
			}
		else
			{
			throw new Exception("Argument error! Indicated class path is null or empty");
			}
		}


	/**
	 * 
	 * @param date
	 */
	public void setDate (final Date date) throws Exception
		{
		if (date!=null)
			{
			this.date = date;
			return;
			}
		else
			{
			throw new Exception("Argument error! Indicated date object is null");
			}
		}


	/**
	 * 
	 * @param flag
	 */
	public void setLocation (final boolean flag)
		{
		if (flag==true)
			{
			this.enableLocation = true;
			}
		else
			{
			this.enableLocation = false;
			}
		return;
		}


	/**
	 * 
	 * @param pattern
	 * @throws Exception
	 */
	public void setLoggerName (final String loggerName) throws Exception
		{
		this.loggerName = loggerName;
		return;
		}


	/**
	 * 
	 * @param pattern
	 * @throws Exception
	 */
	public void setPattern (final String pattern) throws Exception
		{
		}


	/**
	 * 
	 * @param logLevel
	 * @throws Exception
	 */
	public void setPriority (final String logLevel) throws Exception
		{
		if (logLevel!=null && logLevel.trim().length()>0)
			{
			this.logLevel = logLevel;
			return;
			}
		else
			{
			throw new Exception("Argument error! Indicated log level is null or empty");
			}
		}


	/**
	 * 
	 * @param node
	 * @throws Exception
	 */
	public void setProperty (final Node node) throws Exception
		{
		}


	/**
	 * 
	 * @param stackTraceElement
	 * @throws Exception
	 */
	public void setStackTraceElement (final StackTraceElement[] stackTraceElement) throws Exception
		{
		if (stackTraceElement!=null)
			{
			this.stackTraceElement = stackTraceElement;
			return;
			}
		else
			{
			throw new Exception("Argument error! Indicated StackTraceElement class object is null");
			}
		}


	/**
	 * 
	 */
	public void setStartTime ()
		{
		this.startTime = System.currentTimeMillis();
		return;
		}


	/**
	 * 
	 * @param layout
	 * @throws Exception
	 */
	public void setValue (final String key, final String value) throws Exception
		{
		//===== Check argument =====
		if (key!=null && key.trim().length()>0 && value!=null && value.trim().length()>0)
			{
			//===== Set parameter =====
			this.getPropertyMap().put(key, value);
			//===== Terminate procedure =====
			return;
			}
		//===== Argument error =====
		else if (key==null || key.trim().length()<=0)
			{
			throw new Exception("Argument error! Indicated key parameter is null or empty");
			}
		else
			{
			throw new Exception("Argument error! Indicated value parameter is null or empty");
			}
		}


	/**
	 * 
	 * @return
	 */
	protected String getClassPath ()
		{
		return this.classPath;
		}


	/**
	 * 
	 * @return
	 */
	protected Date getDate ()
		{
		return this.date;
		}


	/**
	 * 
	 * @return
	 */
	protected long getInterval ()
		{
		if (this.startTime==0L)
			{
			this.startTime = System.currentTimeMillis();
			}
		else
			{
			}
		return (System.currentTimeMillis() - this.startTime);
		}


	/**
	 * 
	 * @return
	 */
	protected String getLoggerName ()
		{
		return this.loggerName;
		}


	/**
	 * 
	 * @return
	 */
	protected String getPriority ()
		{
		return this.logLevel;
		}


	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	protected Map<String, String> getPropertyMap () throws Exception
		{
		//===== Check parameter map =====
		if (this.propertyMap!=null)
			{
			return this.propertyMap;
			}
		//===== Error handling =====
		else
			{
			return (this.propertyMap=new LinkedHashMap<String, String>());
			}
		}


	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	protected StackTraceElement[] getStackTraceElement () throws Exception
		{
		if (this.stackTraceElement!=null)
			{
			return this.stackTraceElement;
			}
		else
			{
			throw new Exception("StackTraceElement class object which is stored in field is null");
			}
		}


	/**
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	protected String getValue (final String key) throws Exception
		{
		//========== Variable ==========
		String value = null;

		//===== Get value from KVS =====
		if ((value=this.getPropertyMap().get(key))!=null && value.trim().length()>0)
			{
			return value;
			}
		//===== Error handling =====
		else
			{
			throw new Exception("There is no value mapped by key(=\""+key+"\")");
			}
		}
	}


// End Of File
