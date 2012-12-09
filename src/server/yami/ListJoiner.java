package yami;

import java.util.*;

public class ListJoiner 
{

	public String listToString(List<String> list, String lineDelimiter)
	{
		StringBuilder sb = new StringBuilder();
		if (list == null || list.isEmpty())
		{
			return "";
		}
		String delimiter = null == lineDelimiter ? "" : lineDelimiter;
		for (String line : list)
		{
			sb.append(line);
			sb.append(delimiter);
		}
		sb.delete((sb.length() - delimiter.length()), sb.length());		
		return sb.toString();
	}
	
}