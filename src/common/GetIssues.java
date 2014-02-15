import codeine.utils.ExceptionUtils;
import codeine.utils.TextFileUtils;
import codeine.utils.network.HttpUtils;


public class GetIssues {

	public static void main(String[] args) {
		System.setProperty("https.proxyHost", "proxy.iil.intel.com");
		System.setProperty("https.proxyPort", "911");
		for (int i = 61; i < 121; i++) {
			try {
				System.out.println(i);
//				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String url = "https://api.github.com/repos/oshai/codeine/issues/" + i;
				String text = HttpUtils.doGET(url);
				String file = "C:\\Users\\oshai\\Documents\\GitHub\\codeine\\issues\\" + i + ".json";
				TextFileUtils.setContents(file, text);
			} catch (Exception e) {
				System.out.println(ExceptionUtils.getRootCauseMessage(e));
			}
		}
	}

}
