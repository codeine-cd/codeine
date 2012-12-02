package yami.configuration;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class Yami
{
	public Project project = new Project();
	public GlobalConfiguration conf = new GlobalConfiguration();
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conf == null) ? 0 : conf.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Yami other = (Yami)obj;
		if (conf == null)
		{
			if (other.conf != null)
				return false;
		}
		else if (!conf.equals(other.conf))
			return false;
		if (project == null)
		{
			if (other.project != null)
				return false;
		}
		else if (!project.equals(other.project))
			return false;
		return true;
	}
}
