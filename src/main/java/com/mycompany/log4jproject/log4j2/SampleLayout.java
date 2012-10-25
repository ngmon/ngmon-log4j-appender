package com.mycompany.log4jproject.log4j2;

import java.nio.charset.Charset;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttr;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

@Plugin(name = "SampleLayout", type = "Core", elementType = "layout", printObject = true)
public class SampleLayout extends AbstractStringLayout {

    protected SampleLayout(boolean locationInfo, boolean properties, boolean complete, Charset charset) {
        super(charset);
    }

    @PluginFactory
    public static SampleLayout createLayout(@PluginAttr("locationInfo") String locationInfo,
                                            @PluginAttr("properties") String properties,
                                            @PluginAttr("complete") String complete,
                                            @PluginAttr("charset") String charset) {
        Charset c = Charset.isSupported("UTF-8") ? Charset.forName("UTF-8") : Charset.defaultCharset();
        if (charset != null) {
            if (Charset.isSupported(charset)) {
                c = Charset.forName(charset);
            } else {
                LOGGER.error("Charset " + charset + " is not supported for layout, using " + c.displayName());
            }
        }
        boolean info = locationInfo == null ? false : Boolean.valueOf(locationInfo);
        boolean props = properties == null ? false : Boolean.valueOf(properties);
        boolean comp = complete == null ? false : Boolean.valueOf(complete);
        return new SampleLayout(info, props, comp, c);
    }

    public String formatAs(LogEvent le) {
        return "the message is: "+le.getMessage();
    }
}