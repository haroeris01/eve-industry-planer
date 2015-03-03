package net.haroeris.core.settings;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by stw on 02.03.2015.
 */
@Service
public class SettingMgr {

    private Logger LOGGER = LoggerFactory.getLogger(SettingMgr.class);
    private static String FILENAME = "config.xml";
    private XMLConfiguration configuration;

    {
        File configFile = new File(FILENAME);


        if( configFile!=null && configFile.exists() ){
            LOGGER.info("Loading Configuration!");
            try {
                configuration = new XMLConfiguration(configFile);
                configuration.setAutoSave(true);
            } catch (ConfigurationException e) {
                LOGGER.error("Error while loading configuration", e);
            }

        } else {
            LOGGER.info("Loading defaultConfiguration!");
            configuration = new XMLConfiguration();
            configuration.setAutoSave(true);
            for( SettingEntry entry : SettingEntry.values() ) {
                configuration.setProperty(entry.getSettingName(), entry.getSampleValue());
            }
            try {
                configuration.save(configFile);
            } catch (ConfigurationException e) {
                LOGGER.error("Error while saving defaultConfiguration",e);
            }
        }
    }
/*
    @Override
    protected void finalize() throws Throwable {
        LOGGER.info("Saving Configuration!");
        configuration.save(FILENAME);
        super.finalize();
    }
*/
    public XMLConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(XMLConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getConfig(SettingEntry thisSetting) {
        return getConfiguration().getString(thisSetting.getSettingName());
    }

    public void setConfig(SettingEntry thisSetting, Object value) {
        getConfiguration().addProperty(thisSetting.getSettingName(), value);
    }
}