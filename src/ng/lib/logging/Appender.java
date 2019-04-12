/*******************************************************************************
 * Appender.java
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Handler;

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
public abstract class Appender extends Handler
	{
	/** logging layout format object			*/ protected Layout layout = null;
	/** Array of StackTrace element for logging	*/ protected StackTraceElement[] stackTraceElement = null;
	/** Flag for locating						*/ protected boolean enableLocation = false;
	/** logging property object(=key & value)	*/ protected Map<String, String> propertyMap = null;
	/** unique logger name						*/ protected String loggerName = null;



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
	 * @return
	 */
	public boolean isEnabledLocation ()
		{
		return this.enableLocation;
		}


	/**
	 * 
	 * @param node
	 */
	public void parseAppenderNode (Node node) throws Exception
		{
		}


	/**
	 * 
	 * @param paramNode
	 * @throws Exception
	 */
	public void parseParamNode (final Node paramNode) throws Exception
		{
		//========== Variable ==========
		String name = null;
		String value = null;
		Node attribute = null;
		final String ATTRIBUTE_NAME = "name";
		final String ATTRIBUTE_VALUE = "value";

		//===== Check argument =====
		if (paramNode!=null)
			{
			//=====  =====
			if ((attribute=((Element)paramNode).getAttributeNode(ATTRIBUTE_NAME))!=null 
					&& (name=attribute.getNodeValue())!=null && name.trim().length()>0 
					&& (attribute=((Element)paramNode).getAttributeNode(ATTRIBUTE_VALUE))!=null 
					&& (value=attribute.getNodeValue())!=null && value.trim().length()>0)
				{
				//=====  =====
				this.setValue(name, value);
				}
			else
				{
				throw new Exception("");
				}
			}
		//===== Argument error =====
		else
			{
			throw new Exception("Argument error! Indicated \"paramNode\" object is null");
			}
		}


	/**
	 * 
	 * @param layout
	 * @throws Exception
	 */
	public void setLayout (final Layout layout) throws Exception
		{
		try
			{
			//===== Check argument =====
			if (layout!=null)
				{
				//===== Check stack trace output =====
				this.setLocation(layout.isEnabledLocation());
				//=====  =====
				layout.setLoggerName(this.getLoggerName());
				//===== Set layout =====
				this.layout = layout;
				return;
				}
			//===== Argument error =====
			else
				{
				throw new Exception("Argument error! Indicated layout instance is null");
				}
			}
		//===== Error handling =====
		catch (Exception e)
			{
			throw e;
			}
		}


	/**
	 * 
	 * @param loggerName
	 * @throws Exception
	 */
	public void setLoggerName (final String loggerName) throws Exception
		{
		//===== Check argument =====
		if (loggerName!=null && loggerName.trim().length()>0)
			{
			//=====  =====
			this.loggerName = loggerName;
			return;
			}
		//===== Argument error =====
		else
			{
			throw new Exception("Argument error! Indicated logger name is null or empty");
			}
		}


	/**
	 * 
	 * @param stackTraceElement
	 * @throws Exception
	 */
	public void setStackTraceElement (final StackTraceElement[] stackTraceElement) throws Exception
		{
		if (stackTraceElement!=null && stackTraceElement.length>0)
			{
			this.stackTraceElement = stackTraceElement;
			}
		else
			{
			throw new Exception("Argument error! Indicated StackTraceElement class object is null");
			}
		}


	/**
	 * 
	 * @param layout
	 * @throws NextGraFixException
	 */
	public void setValue (final String key, final String value) throws Exception
		{
		//===== Check argument =====
		if (key!=null && key.trim().length()>0 && value!=null && value.trim().length()>0)
			{
			//=====  =====
			this.getPropertyMap().put(key, value);
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
	protected Layout getLayout ()
		{
		return this.layout;
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
	protected Map<String, String> getPropertyMap ()
		{
		//===== Check parameter map =====
		if (this.propertyMap!=null)
			{
			return this.propertyMap;
			}
		//===== Error handling =====
		else
			{
			return (this.propertyMap = new LinkedHashMap<String, String>());
			}
		}


	/**
	 * @return
	 */
	protected StackTraceElement[] getStackTraceElement ()
		{
		return this.stackTraceElement;
		}


	/**
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	protected String getValue (final String key) throws Exception
		{
		String value = null;

		if ((value=this.getPropertyMap().get(key))!=null && value.trim().length()>0)
			{
			return value;
			}
		else
			{
			throw new Exception("There is no value mapped by key(=\""+key+"\")");
			}
		}


	/**
	 * 
	 * @param node
	 * @throws Exception
	 */
	protected Layout parseLayoutNode (Node node) throws Exception
		{
		//========== Variable ==========
		Layout layout = null;
		String layoutClassName = null;
		Node attribute = null;
		NodeList childNodeList = null;
		int childNodeListLength = 0;
		final String ATTRIBUTE_CLASS = "class";
		final String NODE_PARAM = "param";

		try
			{
			//=====  =====
			if (node!=null
					&& (attribute=((Element)node).getAttributeNode(ATTRIBUTE_CLASS))!=null 
					&& (layoutClassName=attribute.getNodeValue())!=null && layoutClassName.trim().length()>0)
				{
				//===== Load new appender class =====
				if ((layout=(Layout)Class.forName(layoutClassName).newInstance())!=null)
					{
					//===== Check parameter for layout =====
					if ((childNodeList=node.getChildNodes())!=null && (childNodeListLength=childNodeList.getLength())>0)
						{
						//===== Repeat while remaining child nodes =====
						for (int i=0; i<childNodeListLength; i++)
							{
							//===== In the case of "param" node =====
							if ((node=childNodeList.item(i))!=null && NODE_PARAM.equals(node.getNodeName())==true)
								{
								//===== Set parameter =====
								layout.setProperty(node);
								}
							//=====  =====
							else
								{
								}
							}
						}
					//=====  =====
					else
						{
						// do nothing
						}
					//===== Terminate procedure =====
					return layout;
					}
				//===== Error handling =====
				else
					{
					throw new Exception("");
					}
				}
			//=====  =====
			else
				{
				throw new Exception("");
				}
			}
		//=====  =====
		catch (Exception e)
			{
			throw e;
			}
		}
	}


// End Of File
