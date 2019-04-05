/*******************************************************************************
 * PatternLayout.java : The NextGraFix Application Library for JVM Platform
 * 
 * Copyright (c) 2016, Akihisa Yasuda
 * All rights reserved.
 ******************************************************************************/

package ng.lib.logging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Logging Library for JVM Platform
 * 
 * @author		NextGraFix.com
 * @since		Aug.30,2011
 * @version		1.0.0
 */
public class PatternLayout extends Layout
	{
	/** */ private List<Map<String, String>> patternList = null;
	/** */ private static final String KEY_CATEGORY		= "CATEGORY";
	/** */ private static final String KEY_CLASS		= "CLASS";
	/** */ private static final String KEY_DATE			= "DATE";
	/** */ private static final String KEY_FILE			= "FILE";
	/** */ private static final String KEY_INTERVAL		= "INTERVAL";
	/** */ private static final String KEY_LINE			= "LINE";
	/** */ private static final String KEY_LINE_FEED	= "LINE_FEED";
	/** */ private static final String KEY_LACATION		= "LOCATION";
	/** */ private static final String KEY_MESSAGE		= "MESSAGE";
	/** */ private static final String KEY_METHOD		= "METHOD";
	/** */ private static final String KEY_NDC			= "NDC";
	/** */ private static final String KEY_PERCENT		= "PERCENT";
	/** */ private static final String KEY_PRIORITY		= "PRIORITY";
	/** */ private static final String KEY_STRING		= "STRING";
	/** */ private static final String KEY_THREAD		= "THREAD";



	/**
	 * Default constructor.<br>
	 */
	public PatternLayout ()
		{
		this.setStartTime();
		return;
		}


	/**
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getLogMessage (final String message) throws Exception
		{
		//========== Variable ==========
		StackTraceElement[] stackTraceElement = null;
		final StringBuilder LOG_MESSAGE = new StringBuilder();
		final List<Map<String, String>> PATTERN_LIST = this.getPatternList();

		//=====  =====
		for (Map<String, String> patternMap : PATTERN_LIST)
			{
			//=====  =====
			if (patternMap!=null)
				{
				//===== In the case of output logger name =====
				if (patternMap.containsKey(PatternLayout.KEY_CATEGORY)==true)
					{
					LOG_MESSAGE.append(this.getLoggerName());
					}
				//===== In the case of output class name =====
				else if (patternMap.containsKey(PatternLayout.KEY_CLASS)==true)
					{
					LOG_MESSAGE.append(this.getClassPath());
					}
				//===== In the case of output date =====
				else if (patternMap.containsKey(PatternLayout.KEY_DATE)==true)
					{
					LOG_MESSAGE.append(this.getDateString(this.getDate()));
					}
				//===== In the case of output file name =====
				else if (patternMap.containsKey(PatternLayout.KEY_FILE)==true)
					{
					LOG_MESSAGE.append(this.getStackTraceElement()[0].getFileName());
					}
				//===== In the case of output interval time[msec] =====
				else if (patternMap.containsKey(PatternLayout.KEY_INTERVAL)==true)
					{
					LOG_MESSAGE.append(this.getInterval());
					}
				//===== In the case of output location information =====
				else if (patternMap.containsKey(PatternLayout.KEY_LACATION)==true)
					{
					stackTraceElement = this.getStackTraceElement();
					LOG_MESSAGE.append(stackTraceElement[0].getClassName())
							.append(".")
							.append(stackTraceElement[0].getMethodName())
							.append("(")
							.append(stackTraceElement[0].getFileName())
							.append(":")
							.append(stackTraceElement[0].getLineNumber()+")");
					}
				//===== In the case of output line =====
				else if (patternMap.containsKey(PatternLayout.KEY_LINE)==true)
					{
					LOG_MESSAGE.append(this.getStackTraceElement()[0].getLineNumber());
					}
				//===== In the case of output line feed =====
				else if (patternMap.containsKey(PatternLayout.KEY_LINE_FEED)==true)
					{
					LOG_MESSAGE.append(ng.lib.logging.SystemUtil.getLineFeed());
					}
				//===== In the case of output message =====
				else if (patternMap.containsKey(PatternLayout.KEY_MESSAGE)==true)
					{
					LOG_MESSAGE.append(message);
					}
				//===== In the case of output method name =====
				else if (patternMap.containsKey(PatternLayout.KEY_METHOD)==true)
					{
					LOG_MESSAGE.append(this.getStackTraceElement()[0].getMethodName());
					}
				//===== In the case of output NDC =====
				else if (patternMap.containsKey(PatternLayout.KEY_NDC)==true)
					{
					}
				//===== In the case of output percent character =====
				else if (patternMap.containsKey(PatternLayout.KEY_PERCENT)==true)
					{
					}
				//===== In the case of output log level =====
				else if (patternMap.containsKey(PatternLayout.KEY_PRIORITY)==true)
					{
					LOG_MESSAGE.append(this.getPriority());
					}
				//===== In the case of output arbitrary string =====
				else if (patternMap.containsKey(PatternLayout.KEY_STRING)==true)
					{
					LOG_MESSAGE.append(patternMap.get(PatternLayout.KEY_STRING));
					}
				//===== In the case of output thread number =====
				else if (patternMap.containsKey(PatternLayout.KEY_THREAD)==true)
					{
					}
				//=====  =====
				else
					{
					}
				}
			//=====  =====
			else
				{
				throw new Exception("");
				}
			}
		//=====  =====
		if (LOG_MESSAGE.length()>0)
			{
			return LOG_MESSAGE.toString();
			}
		//=====  =====
		else
			{
			return null;
			}
		}


	/**
	 * 
	 * @param pattern
	 * @throws Exception
	 */
	@Override
	public void setPattern (final String pattern) throws Exception
		{
		//========== Variable ==========
		int formatLength = 0;
		char character = 0;
		StringBuilder buffer = null;
		String option = null;
		final String KEY_STRING = PatternLayout.KEY_STRING;

		//===== Check argument =====
		if (pattern!=null && (formatLength=pattern.length())>0)
			{
			//===== Repeat for all format configuration =====
			for (int i=0; i<formatLength; i++)
				{
				//===== In the case of  =====
				if ((character=pattern.charAt(i))=='%')
					{
					//===== In the case of remaining buffer =====
					if (buffer!=null && buffer.length()>0)
						{
						//===== Generate new pattern map =====
						Map<String, String> stringPatternMap = new LinkedHashMap<String, String>();
						//===== Put pattern into map =====
						stringPatternMap.put(KEY_STRING, buffer.toString());
						//===== Set pattern =====
						this.getPatternList().add(stringPatternMap);
						}
					//===== In the case of remaining no buffer =====
					else
						{
						// do nothing
						}
					//===== Initialize buffer =====
					buffer = null;
					//===== Check index =====
					if (i++<formatLength)
						{
						//===== Get next word =====
						character = pattern.charAt(i);
						}
					//===== Error handling =====
					else
						{
						throw new Exception("Argument error! Indicated layout format(=\""+pattern+"\") is invalid");
						}
					//===== Get number in front of configuration =====
					while (Character.toString(character).matches("-")==true || Character.toString(character).matches("[0-9]")==true)
						{
						//===== Append number =====
						if (option==null)
							{
							option = Character.toString(character);
							}
						else
							{
							option += Character.toString(character);
							}
						//===== Check index =====
						if (i++<formatLength-1)
							{
							//===== Get next word =====
							character = pattern.charAt(i);
							}
						//===== Error handling =====
						else
							{
							throw new Exception("Argument error! Indicated layout format(=\""+pattern+"\") is invalid");
							}
						}
					//===== In the case of category name setting =====
					if (character=='c')
						{
						i = this.setCategoryNamePattern(pattern, i, option);
						option = null;
						}
					//===== In the case of class name setting =====
					else if (character=='C')
						{
						i = this.setClassNamePattern(pattern, i, option);
						option = null;
						}
					//=====  =====
					else if (character=='d')
						{
						i = this.setDatePattern(pattern, i, option);
						option = null;
						}
					//=====  =====
					else if (character=='l')
						{
						this.setLocationPattern(option);
						option = null;
						}
					//=====  =====
					else if (character=='F')
						{
						this.setFileNamePattern(option);
						option = null;
						}
					//=====  =====
					else if (character=='L')
						{
						this.setLineNumberPattern(option);
						option = null;
						}
					//=====  =====
					else if (character=='M')
						{
						i = this.setMethodNamePattern(pattern, i, option);
						option = null;
						}
					//=====  =====
					else if (character=='m')
						{
						this.setMessagePattern(option);
						option = null;
						}
					//=====  =====
					else if (character=='x')
						{
						this.setNDCPattern(option);
						option = null;
						}
					//=====  =====
					else if (character=='n')
						{
						this.setLineFeedPattern(option);
						option = null;
						}
					//=====  =====
					else if (character=='p')
						{
						this.setPriorityPattern(option);
						option = null;
						}
					//=====  =====
					else if (character=='r')
						{
						this.setIntervalPattern(option);
						option = null;
						}
					//=====  =====
					else if (character=='t')
						{
						this.setThreadNamePattern(option);
						option = null;
						}
					//=====  =====
					else
						{
						}
					}
				//=====  =====
				else
					{
					//===== Check buffer =====
					if (buffer==null)
						{
						//=====  =====
						buffer = new StringBuilder();
						}
					//=====  =====
					else
						{
						}
					//===== Append pattern =====
					buffer.append(character);
					}
				}
			//===== In the case of remaining last pattern =====
			if (buffer!=null && buffer.length()>0)
				{
				//===== Generate new pattern map =====
				Map<String, String> stringPatternMap = new LinkedHashMap<String, String>();
				//===== Put pattern into map =====
				stringPatternMap.put(KEY_STRING, buffer.toString());
				//===== Set pattern =====
				this.getPatternList().add(stringPatternMap);
				}
			//=====  =====
			else
				{
				// do nothing
				}
			//===== Terminate procedure =====
			return;
			}
		//===== Argument error =====
		else
			{
			throw new Exception("Argument error! Indicated layout format is null or empty");
			}
		}


	/**
	 * 
	 * @param node
	 * @throws Exception
	 */
	@Override
	public void setProperty (final Node node) throws Exception
		{
		//========== Variable ==========
		Node attribute = null;
		String name = null;
		String value = null;
		final String ATTRIBUTE_NAME = "name";
		final String ATTRIBUTE_VALUE = "value";
		final String CONVERSION_PATTERN = "conversionPattern";

		//===== Get parameter =====
		if (node!=null 
				&& (attribute=((Element)node).getAttributeNode(ATTRIBUTE_NAME))!=null 
				&& (name=attribute.getNodeValue())!=null && name.trim().length()>0 
				&& (attribute=((Element)node).getAttributeNode(ATTRIBUTE_VALUE))!=null 
				&& (value=attribute.getNodeValue())!=null && value.trim().length()>0)
			{
			//===== In the case of "conversionPattern" setting =====
			if (CONVERSION_PATTERN.equalsIgnoreCase(name)==true)
				{
				this.setPattern(value);
				}
			//=====  =====
			else
				{
//				this.setValue(name, value);
				}
			//===== Terminate procedure =====
			return;
			}
		//===== Error handling =====
		else if (node==null)
			{
			throw new Exception("Argument error! Indicated node object is null");
			}
		else if (name==null || name.trim().length()<=0)
			{
			throw new Exception("XML parsing error! Can't find the value of \""+ATTRIBUTE_NAME+"\" attribute node");
			}
		else
			{
			throw new Exception("XML parsing error! Can't find the value of \""+ATTRIBUTE_VALUE+"\" attribute node");
			}
		}


	/**
	 * 
	 * @param date
	 * @return
	 */
	private final String getDateString (final Date date)
		{
		if (this.simpleDateFormat!=null && date!=null)
			{
			return this.simpleDateFormat.format(date);
			}
		else
			{
			return null;
			}
		}


	/**
	 * 
	 * @return
	 */
	private final List<Map<String, String>> getPatternList ()
		{
		if (this.patternList!=null)
			{
			return this.patternList;
			}
		else
			{
			return (this.patternList=new ArrayList<Map<String,String>>());
			}
		}


	/**
	 * 
	 * @param format					layout format for logging
	 * @param index					index of layout format
	 * @param option					format option
	 * @return						
	 * @throws Exception	
	 */
	private final int setCategoryNamePattern (final String format, int index, final String option) throws Exception
		{
		//========== Variable ==========
		char character = 0;															// 
		int formatLength = 0;														// 
		StringBuilder pattern = null;												// 
		final char start = '{';														// 
		final char end = '}';														// 
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//===== Check argument =====
		if (format!=null && (formatLength=format.length())>0 && index<formatLength)
			{
			//===== Check the parsing start character =====
			if ((character=format.charAt(index+1))==start)
				{
				//===== Repeat for all format pattern =====
				for (index=index+2; index<formatLength; index++)
					{
					//===== Check the parsing end character =====
					if ((character=format.charAt(index))==end)
						{
						break;
						}
					//===== In the case of continuing parsing =====
					else
						{
						//===== Check buffer =====
						if (pattern!=null)
							{
							pattern.append(character);
							}
						//=====  =====
						else
							{
							pattern = new StringBuilder(character);
							}
						}
					}
				}
			//=====  =====
			else
				{
				// do nothing
				}
			//=====  =====
			PATTERN_MAP.put(PatternLayout.KEY_CATEGORY, option);
			//=====  =====
			this.getPatternList().add(PATTERN_MAP);
			//===== Return index as it is =====
			return index;
			}
		//===== Argument error =====
		else if (format==null || format.trim().length()<=0)
			{
			throw new Exception("Argument error! Indicated format layout is null or empty");
			}
		else
			{
			throw new Exception("Argument error! Indicated format index is invalid");
			}
		}


	/**
	 * 
	 * @param format					layout format for logging
	 * @param index					index of layout format
	 * @param option					format option
	 * @return						
	 * @throws Exception	
	 */
	private final int setClassNamePattern (final String format, int index, final String option) throws Exception
		{
		//========== Variable ==========
		char character = 0;															// 
		int formatLength = 0;														// 
		StringBuilder pattern = null;												// 
		final char start = '{';														// 
		final char end = '}';														// 
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//===== Check argument =====
		if (format!=null && (formatLength=format.length())>0 && index<formatLength)
			{
			//===== Check the parsing start character =====
			if ((character=format.charAt(index+1))==start)
				{
				//===== Repeat for all format pattern =====
				for (index=index+2; index<formatLength; index++)
					{
					//===== Check the parsing end character =====
					if ((character=format.charAt(index))==end)
						{
						break;
						}
					//===== In the case of continuing parsing =====
					else
						{
						//===== Check buffer =====
						if (pattern!=null)
							{
							pattern.append(character);
							}
						//=====  =====
						else
							{
							pattern = new StringBuilder(character);
							}
						}
					}
				}
			//=====  =====
			else
				{
				// do nothing
				}
			//=====  =====
			PATTERN_MAP.put(PatternLayout.KEY_CATEGORY, option);
			//=====  =====
			this.getPatternList().add(PATTERN_MAP);
			//===== Return index as it is =====
			return index;
			}
		//===== Argument error =====
		else if (format==null || format.trim().length()<=0)
			{
			throw new Exception("Argument error! Indicated format layout is null or empty");
			}
		else
			{
			throw new Exception("Argument error! Indicated format index is invalid");
			}
		}


	/**
	 * 
	 * @param format					layout format for logging
	 * @param index					index of layout format
	 * @param option					format option
	 * @return						
	 * @throws Exception	
	 */
	private final int setDatePattern (final String format, int index, final String option) throws Exception
		{
		//========== Variable ==========
		char character = 0;															// 
		int formatLength = 0;														// 
		StringBuilder pattern = null;												// 
		final char start = '{';														// 
		final char end = '}';														// 
		final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";					// 
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//===== Check argument =====
		if (format!=null && (formatLength=format.length())>0 && index<formatLength)
			{
			//===== Check the parsing start character =====
			if ((character=format.charAt(index+1))==start)
				{
				//===== Repeat for all format pattern =====
				for (index=index+2; index<formatLength; index++)
					{
					//===== Check the parsing end character =====
					if ((character=format.charAt(index))==end)
						{
						break;
						}
					//===== In the case of continuing parsing =====
					else
						{
						//===== Check buffer =====
						if (pattern!=null)
							{
							pattern.append(character);
							}
						//=====  =====
						else
							{
							pattern = new StringBuilder(character);
							}
						}
					}
				//=====  =====
				try
					{
					//=====  =====
					if (pattern!=null)
						{
						this.simpleDateFormat = new SimpleDateFormat(pattern.toString());
						}
					//=====  =====
					else
						{
						throw new Exception("Argument error! Indicated format layout(=\""+format+"\") is invalid");
						}
					}
				//===== Error handling =====
				catch (Exception e)
					{
					throw e;
					}
				}
			//=====  =====
			else
				{
				//=====  =====
				this.simpleDateFormat = new SimpleDateFormat(DEFAULT_PATTERN);
				}
			//=====  =====
			PATTERN_MAP.put(PatternLayout.KEY_DATE, option);
			//=====  =====
			this.getPatternList().add(PATTERN_MAP);
			//===== Return index as it is =====
			return index;
			}
		//===== Argument error =====
		else if (format==null || format.trim().length()<=0)
			{
			throw new Exception("Argument error! Indicated format layout is null or empty");
			}
		else
			{
			throw new Exception("Argument error! Indicated format index is invalid");
			}
		}


	/**
	 * 
	 * @param option					format option
	 * @throws Exception	
	 */
	private final void setFileNamePattern (final String option) throws Exception
		{
		//=====  =====
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//=====  =====
		this.setLocation(true);
		//=====  =====
		PATTERN_MAP.put(PatternLayout.KEY_FILE, option);
		//=====  =====
		this.getPatternList().add(PATTERN_MAP);
		//=====  =====
		return;
		}


	/**
	 * 
	 * @param option					format option
	 * @throws Exception	
	 */
	private final void setIntervalPattern (final String option) throws Exception
		{
		//=====  =====
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//=====  =====
		PATTERN_MAP.put(PatternLayout.KEY_INTERVAL, option);
		//=====  =====
		this.getPatternList().add(PATTERN_MAP);
		//=====  =====
		return;
		}


	/**
	 * 
	 * @param option					format option
	 * @return						
	 * @throws Exception	
	 */
	private final void setLineFeedPattern (final String option) throws Exception
		{
		//=====  =====
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//=====  =====
		PATTERN_MAP.put(PatternLayout.KEY_LINE_FEED, option);
		//=====  =====
		this.getPatternList().add(PATTERN_MAP);
		//=====  =====
		return;
		}


	/**
	 * 
	 * @param option					format option
	 * @throws Exception	
	 */
	private final void setLineNumberPattern (final String option) throws Exception
		{
		//=====  =====
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//=====  =====
		this.setLocation(true);
		//=====  =====
		PATTERN_MAP.put(PatternLayout.KEY_LINE, option);
		//=====  =====
		this.getPatternList().add(PATTERN_MAP);
		//=====  =====
		return;
		}


	/**
	 * 
	 * @param option					format option
	 * @throws Exception	
	 */
	private final void setLocationPattern (final String option) throws Exception
		{
		//=====  =====
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//=====  =====
		this.setLocation(true);
		//=====  =====
		PATTERN_MAP.put(PatternLayout.KEY_LACATION, option);
		//=====  =====
		this.getPatternList().add(PATTERN_MAP);
		//=====  =====
		return;
		}


	/**
	 * 
	 * @param option					format option
	 * @throws Exception	
	 */
	private final void setMessagePattern (final String option) throws Exception
		{
		//=====  =====
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//=====  =====
		PATTERN_MAP.put(PatternLayout.KEY_MESSAGE, option);
		//=====  =====
		this.getPatternList().add(PATTERN_MAP);
		//=====  =====
		return;
		}


	/**
	 * 
	 * @param format					layout format for logging
	 * @param index					index of layout format
	 * @param option					format option
	 * @return						
	 * @throws Exception	
	 */
	private final int setMethodNamePattern (final String format, int index, final String option) throws Exception
		{
		//========== Variable ==========
		char character = 0;															// 
		int formatLength = 0;														// 
		StringBuilder pattern = null;												// 
		final char start = '{';														// 
		final char end = '}';														// 
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//===== Check argument =====
		if (format!=null && (formatLength=format.length())>0 && index<formatLength)
			{
			//===== Check the parsing start character =====
			if ((character=format.charAt(index+1))==start)
				{
				//===== Repeat for all format pattern =====
				for (index=index+2; index<formatLength; index++)
					{
					//===== Check the parsing end character =====
					if ((character=format.charAt(index))==end)
						{
						break;
						}
					//===== In the case of continuing parsing =====
					else
						{
						//===== Check buffer =====
						if (pattern!=null)
							{
							pattern.append(character);
							}
						//=====  =====
						else
							{
							pattern = new StringBuilder(character);
							}
						}
					}
				}
			//=====  =====
			else
				{
				// do nothing
				}
			//=====  =====
			PATTERN_MAP.put(PatternLayout.KEY_METHOD, option);
			//=====  =====
			this.getPatternList().add(PATTERN_MAP);
			//=====  =====
			this.setLocation(true);
			//===== Return index as it is =====
			return index;
			}
		//===== Argument error =====
		else if (format==null || format.trim().length()<=0)
			{
			throw new Exception("Argument error! Indicated format layout is null or empty");
			}
		else
			{
			throw new Exception("Argument error! Indicated format index is invalid");
			}
		}


	/**
	 * 
	 * @param option					format option
	 * @throws Exception	
	 */
	private final void setNDCPattern (final String option) throws Exception
		{
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//=====  =====
		PATTERN_MAP.put(PatternLayout.KEY_NDC, option);
		//=====  =====
		this.getPatternList().add(PATTERN_MAP);
		//=====  =====
		return;
		}


	/**
	 * 
	 * @param option					format option
	 * @throws Exception	
	 */
	private final void setPriorityPattern (final String option) throws Exception
		{
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//=====  =====
		PATTERN_MAP.put(PatternLayout.KEY_PRIORITY, option);
		//=====  =====
		this.getPatternList().add(PATTERN_MAP);
		//=====  =====
		return;
		}


	/**
	 * 
	 * @param option					format option
	 * @throws Exception	
	 */
	private final void setThreadNamePattern (final String option) throws Exception
		{
		final Map<String, String> PATTERN_MAP = new LinkedHashMap<String, String>();	//

		//=====  =====
		PATTERN_MAP.put(PatternLayout.KEY_THREAD, option);
		//=====  =====
		this.getPatternList().add(PATTERN_MAP);
		//=====  =====
		return;
		}
	}


// End Of File
