package codeine.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtils {

	public static byte[] compress(String str){
        try {
			if (str == null || str.length() == 0) {
			    return new byte[0];
			}
			ByteArrayOutputStream obj=new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(obj);
			gzip.write(str.getBytes("UTF-8"));
			gzip.close();
			return obj.toByteArray();
		} catch (Exception e) {
			throw ExceptionUtils.asUnchecked(e);
		}
     }

      public static String decompress(byte[] bytes){
        try {
			if (bytes == null || bytes.length == 0) {
			    return "";
			}
			GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
			BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
			String outStr = "";
			String line;
			while ((line=bf.readLine())!=null) {
			  outStr += line;
			}
			return outStr;
		} catch (Exception e) {
			throw ExceptionUtils.asUnchecked(e);
		}
     }
}
