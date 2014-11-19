package codeine.utils.network;

import javax.servlet.http.HttpServletRequest;

public class UserAgentHeader {

	public static final UserAgentHeader NULL = new UserAgentHeader("null_os", "null_browser");
	private String os;
	private String browser;
	private String header;

	private UserAgentHeader(String os, String browser) {
		super();
		this.os = os;
		this.browser = browser;
	}

	private UserAgentHeader(String header) {
		super();
		this.header = header;
	}

	public static UserAgentHeader parseBrowserAndOs(HttpServletRequest request) {
		return parseBrowserAndOs(RequestUtils.getHeader(request, "User-Agent"));
	}

	public static UserAgentHeader parseBrowserAndOs(String header) {
		if (null == header) {
			return UserAgentHeader.NULL;
		}
		return new UserAgentHeader(header).parseBrowserAndOs();
	}

	private UserAgentHeader parseBrowserAndOs() {
		String lowerCaseUserHeader = header.toLowerCase();

		// log.info("User Agent for the request is===>"+browserDetails);
		// =================OS=======================
		if (lowerCaseUserHeader.indexOf("windows") >= 0) {
			os = "Windows";
		} else if (lowerCaseUserHeader.indexOf("mac") >= 0) {
			os = "Mac";
		} else if (lowerCaseUserHeader.indexOf("x11") >= 0) {
			os = "Unix";
		} else if (lowerCaseUserHeader.indexOf("android") >= 0) {
			os = "Android";
		} else if (lowerCaseUserHeader.indexOf("iphone") >= 0) {
			os = "IPhone";
		} else if (lowerCaseUserHeader.indexOf("linux") >= 0) {
			os = "Linux";
		} else {
			os = "UnKnown, More-Info: " + header;
		}
		// ===============Browser===========================
		if (lowerCaseUserHeader.contains("msie")) {
			String substring = header.substring(header.indexOf("MSIE")).split(";")[0];
			browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
		} else if (lowerCaseUserHeader.contains("trident")) {
			String substring = header.substring(header.indexOf("rv:")).split("\\)")[0];
			browser = substring.replace("rv:", "IE-");
		} else if (lowerCaseUserHeader.contains("safari") && lowerCaseUserHeader.contains("version")) {
			browser = (header.substring(header.indexOf("Safari")).split(" ")[0]).split("/")[0] + "-"
					+ (header.substring(header.indexOf("Version")).split(" ")[0]).split("/")[1];
		} else if (lowerCaseUserHeader.contains("opr") || lowerCaseUserHeader.contains("opera")) {
			if (lowerCaseUserHeader.contains("opera"))
				browser = (header.substring(header.indexOf("Opera")).split(" ")[0]).split("/")[0] + "-"
						+ (header.substring(header.indexOf("Version")).split(" ")[0]).split("/")[1];
			else if (lowerCaseUserHeader.contains("opr"))
				browser = ((header.substring(header.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR",
						"Opera");
		} else if (lowerCaseUserHeader.contains("chrome")) {
			browser = (header.substring(header.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
		} else if (lowerCaseUserHeader.contains("curl")) {
			browser = (header.split(" ")[0]).replace("/", "-");
		} else if ((lowerCaseUserHeader.indexOf("mozilla/7.0") > -1)
				|| (lowerCaseUserHeader.indexOf("netscape6") != -1)
				|| (lowerCaseUserHeader.indexOf("mozilla/4.7") != -1)
				|| (lowerCaseUserHeader.indexOf("mozilla/4.78") != -1)
				|| (lowerCaseUserHeader.indexOf("mozilla/4.08") != -1)
				|| (lowerCaseUserHeader.indexOf("mozilla/3") != -1)) {
			// browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/",
			// "-");
			browser = "Netscape-?";

		} else if (lowerCaseUserHeader.contains("firefox")) {
			browser = (header.substring(header.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
		} else {
			browser = "Unknown[" + header + "]";
		}
		// log.info("Operating System======>"+os);
		// log.info("Browser Name==========>"+browser);
		return this;
	}

	public String getOs() {
		return os;
	}

	public String getBrowser() {
		return browser;
	}

}
