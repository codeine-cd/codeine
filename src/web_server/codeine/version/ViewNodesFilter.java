package codeine.version;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.utils.StringUtils;

public class ViewNodesFilter {

	private static final Logger log = Logger.getLogger(ViewNodesFilter.class);
	private final String m_paramVersion;
	private final int m_paramMax;
	private int m_count;
	private int m_skipCount;
	private Pattern pattern;
	private String regexp;
	private int paramSkip;

	public ViewNodesFilter(String paramVersion, int paramMax, String regexp, int paramSkip) {
		m_paramVersion = paramVersion;
		m_paramMax = paramMax;
		this.regexp = regexp;
		this.paramSkip = paramSkip;
		if (null != regexp){
			this.pattern = Pattern.compile(".*" + regexp + ".*");
		}
	}

	/**
	 * @return true if should be filtered (skipped)
	 */
	public boolean filter(String version, String alias) {
		log.debug("filterByVersion() - version is " + version);
		// log.info("filterByVersion() - version2 is " + paramVersion);
		// log.info("filterByVersion() - m_count is " + m_count);
		// log.info("filterByVersion() - m_max is " + m_max);
		if (null != m_paramVersion) {
			if (versionNotMatch(version) || m_count >= m_paramMax) {
				return true;
			}
		}
		if (null != regexp) {
			if (regexpNotMatch(alias)) {
				return true;
			}
		}
		if (m_skipCount < paramSkip){
			m_skipCount++;
			return true;
		}
		m_count++;
		return false;
	}

	private boolean regexpNotMatch(String alias) {
		return !pattern.matcher(StringUtils.safeToString(alias)).matches();
	}

	private boolean versionNotMatch(String version) {
		if (Constants.ALL_VERSION.equals(m_paramVersion)) {
			return false;
		}
		if (Constants.NO_VERSION.equals(m_paramVersion)) {
			return !StringUtils.isEmpty(version) && ! Constants.NO_VERSION.equals(version);
		}
		return !m_paramVersion.equals(version);
	}
}
