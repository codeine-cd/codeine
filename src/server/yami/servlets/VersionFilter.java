package yami.servlets;

import org.apache.log4j.Logger;

public class VersionFilter
{

	private static final Logger log = Logger.getLogger(VersionFilter.class);
	private final String m_paramVersion;
	private final int m_paramMax;
	private int m_count;

	public VersionFilter(String paramVersion, int paramMax)
	{
		m_paramVersion = paramVersion;
		m_paramMax = paramMax;
	}
	
	public boolean filterByVersion(String version)
	{
		log.info("filterByVersion() - version is " + version);
//		log.info("filterByVersion() - version2 is " + paramVersion);
//		log.info("filterByVersion() - m_count is " + m_count);
//		log.info("filterByVersion() - m_max is " + m_max);
		if (null == m_paramVersion)
		{
			return false;
		}
		if (!m_paramVersion.equals(version))
		{
			return true;
		}
		if (m_count >= m_paramMax)
		{
			return true;
		}
		m_count++;
		return false;
	}
}
