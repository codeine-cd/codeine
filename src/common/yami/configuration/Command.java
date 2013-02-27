package yami.configuration;

import javax.xml.bind.annotation.XmlAttribute;

public class Command 
{
    @XmlAttribute public String name;
    @XmlAttribute public String title;

    public String title()
    {
	return title == null ? name : title;
    }
}
