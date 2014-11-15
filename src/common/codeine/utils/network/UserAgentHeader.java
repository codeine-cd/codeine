package codeine.utils.network;

import javax.servlet.http.HttpServletRequest;

public class UserAgentHeader {

	private String os;
	private String browser;
	private HttpServletRequest request;

	
	public UserAgentHeader(HttpServletRequest request) {
		super();
		this.request = request;
	}

	public UserAgentHeader parseBrowserAndOs() {
		String  browserDetails  =   request.getHeader("User-Agent");
        String  userAgent       =   browserDetails;
        String  user            =   userAgent.toLowerCase();

//        log.info("User Agent for the request is===>"+browserDetails);
        //=================OS=======================
         if (userAgent.toLowerCase().indexOf("windows") >= 0 )
         {
             os = "Windows";
         }
         else if(userAgent.toLowerCase().indexOf("mac") >= 0)
         {
             os = "Mac";
         }
         else if(userAgent.toLowerCase().indexOf("x11") >= 0)
         {
             os = "Unix";
         }else if(userAgent.toLowerCase().indexOf("android") >= 0)
         {
             os = "Android";
         }
         else if(userAgent.toLowerCase().indexOf("iphone") >= 0)
         {
             os = "IPhone";
         }else{
             os = "UnKnown, More-Info: "+userAgent;
         }
         //===============Browser===========================
        if (user.contains("msie"))
        {
            String substring=userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            browser=substring.split(" ")[0].replace("MSIE", "IE")+"-"+substring.split(" ")[1];
        }
        else if (user.contains("safari") && user.contains("version"))
        {
            browser=(userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
        }
        else if ( user.contains("opr") || user.contains("opera"))
        {
            if(user.contains("opera"))
                browser=(userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            else if(user.contains("opr"))
                browser=((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
        }
        else if (user.contains("chrome"))
        {
            browser=(userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        }
        else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1)  || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1) )
        {
            //browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
            browser = "Netscape-?";

        }
        else if (user.contains("firefox"))
        {
            browser=(userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        }
        else
        {
            browser = "UnKnown, More-Info: "+userAgent;
        }
//        log.info("Operating System======>"+os);
//        log.info("Browser Name==========>"+browser);
        return this;
	}

	public String getOs() {
		return os;
	}

	public String getBrowser() {
		return browser;
	}

}
