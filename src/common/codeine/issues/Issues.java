package codeine.issues;

import codeine.issues.Issue.State;
import codeine.utils.ExceptionUtils;
import codeine.utils.TextFileUtils;
import codeine.utils.network.HttpUtils;

import com.google.gson.Gson;


public class Issues {

	public static void main(String[] args) {
		for (int i = 1; i < 121; i++) {
			try {
				String file = "C:\\Users\\oshai\\Documents\\GitHub\\codeine\\issues\\" + (i + ".json");
				String file2 = "C:\\Users\\oshai\\Documents\\GitHub\\codeine\\issues\\";
				String text = TextFileUtils.getContents(file);
				Issue issue = new Gson().fromJson(text, Issue.class);
				if (issue.state == State.open){
					file2 += "open\\";
				} else if (issue.state == State.closed){
					file2 += "closed\\";
				} else {
					System.out.println("failed to fetch state for " + i);
					continue;
				}
				file2 += i + "." + HttpUtils.specialEncode(issue.title) + ".json";
				TextFileUtils.setContents(file2, text);
			} catch (Exception e) {
				System.out.println(ExceptionUtils.getRootCauseMessage(e));
			}
		}
	}

	public static void fetchFromGithub() {
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
