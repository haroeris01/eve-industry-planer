package net.haroeris.core.settings;

import java.util.Collections;

/**
 * Created by stw on 02.03.2015.
 */
public enum SettingEntry {
    CORP_NAME( "corporation.name", "Some corporation name" ),
    CORP_API_ID("corporation.api.id", "987654321"),
    CORP_API_KEY("corporation.api.key", "jsdklmkedsfnjsdfikklnsdfnknjsdinilksfdnesdhfbh"),

    CORP_MEMBER("corporation.members.member", null),
    CORP_MEMBER_ID(CORP_MEMBER.getSettingName()+".id", "1111111"),
    CORP_MEMBER_NAME(CORP_MEMBER.getSettingName()+".name", "Max Muster"),
    CORP_MEMBER_API_ID(CORP_MEMBER.getSettingName()+".api.id", "12345678"),
    CORP_MEMBER_API_KEY(CORP_MEMBER.getSettingName()+".api.key", "jsdklmkedsfnjsdfikklnsdfnknjsdinilksfdnesdhfbh"),

    SYSTEM_PROXY_HOST("system.proxy.host", ""),
    SYSTEM_PROXY_PORT("system.proxy.port", "")
    ;

    //private static final String

    private String settingName;
    private String sampleValue;

    private SettingEntry(String settingName, String sampleValue) {
        setSettingName(settingName);
        setSampleValue(sampleValue);
    }

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public String getSampleValue() {
        return sampleValue;
    }

    public void setSampleValue(String sampleValue) {
        this.sampleValue = sampleValue;
    }
}