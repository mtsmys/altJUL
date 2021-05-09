import ng.lib.logging.Logger;



public class LoggerTest
	{
	/**
	 * 
	 * @param args
	 */
	public static void main (String[] args)
		{
		try
			{
			new LoggerTest();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		return;
		}


	/**
	 * コンストラクタ．<br>
	 * 
	 * @throws Exception
	 */
	public LoggerTest () throws Exception
		{
		//========== ローカル変数 ==========
		Logger logger = null;

		//===== ロガーの構築 =====
		logger = new Logger("ng.log.test");
		logger.setConfigurationFile("log4j.xml");
		//=====  =====
		for (int i=0; i<1; i++)
			{
			logger.trace("This is test log");
			logger.debug("This is test log");
			logger.info("This is test log");
			logger.warn("This is test log");
			logger.error("This is test log");
			}
		return;
		}
	}


// End Of File
