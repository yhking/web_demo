package com.cet.devportal.ini;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author wuguohui
 * @version V1.0
 * @ClassName: IniFileProperties
 * @Description: TODO
 * @date 2019/3/12 16:37
 */
public class IniFileProperties {
    private Map<String, Map<String, String>> sectionProperties = Maps.newLinkedHashMap();
    private Map<String, String> defaultProperties = Maps.newLinkedHashMap();

    public Map<String, String> getDefaultProperties()
    {
        return defaultProperties;
    }

    public Collection<String> getSectionNames()
    {
        return Collections.unmodifiableCollection(sectionProperties.keySet());
    }

    public Map<String, String> getSectionProperties(String section)
    {
        Map<String, String> sectionProperties = doGetSectionProperties(section);
        if (sectionProperties != null)
        {
            sectionProperties = Collections.unmodifiableMap(sectionProperties);
        }
        return sectionProperties;
    }

    public void addSectionProperty(String section, String name, String value)
    {
        Map<String, String> properties = doGetSectionProperties(section);
        if (section != null && properties == null)
        {
            properties = Maps.newLinkedHashMap();
            sectionProperties.put(section, properties);
        }

        properties.put(name, value);
    }

    private Map<String, String> doGetSectionProperties(String section)
    {
        Map<String, String> properties;
        if (section == null)
        {
            properties = getDefaultProperties();
        }
        else
        {
            properties = sectionProperties.get(section);
        }

        return properties;
    }
}
