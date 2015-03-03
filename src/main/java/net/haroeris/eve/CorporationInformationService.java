package net.haroeris.eve;

import com.tlabs.eve.api.IndustryJob;
import com.tlabs.eve.api.IndustryJobsResponse;
import com.tlabs.eve.api.character.CharacterInfoRequest;
import com.tlabs.eve.api.corporation.CorporationIndustryJobsRequest;
import com.tlabs.eve.api.corporation.CorporationMember;
import com.tlabs.eve.api.corporation.MemberTrackingRequest;
import com.tlabs.eve.api.corporation.MemberTrackingResponse;
import net.haroeris.core.settings.SettingEntry;
import net.haroeris.core.settings.SettingMgr;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Created by stw on 02.03.2015.
 */
@Service
public class CorporationInformationService {
    private static Logger LOGGER = LoggerFactory.getLogger(MainClass.class);
    @Autowired
    private ApiService apiService;
    @Autowired
    private SettingMgr settingMgr;

    public Collection<CorporationMember> getCorpMembers() {
        String corpApiId = settingMgr.getConfig(SettingEntry.CORP_API_ID);
        String corpApikey = settingMgr.getConfig(SettingEntry.CORP_API_KEY);
        String corpName = settingMgr.getConfig(SettingEntry.CORP_NAME);

        try {
            MemberTrackingResponse memberResponse = apiService.apiCall(
                new MemberTrackingRequest(corpName, true), corpApiId, corpApikey
            );
            return memberResponse.getCorpMembers();
        } catch (IOException | ExecutionException | NoSuchAlgorithmException | InterruptedException | KeyManagementException e) {
            LOGGER.error("Error while retrieving corpmembers!", e);
        }

        return Collections.emptyList();
    }

    public Collection<IndustryJob> getCorpJobs() {
        String corpApiId = settingMgr.getConfig(SettingEntry.CORP_API_ID);
        String corpApikey = settingMgr.getConfig(SettingEntry.CORP_API_KEY);
        String corpName = settingMgr.getConfig(SettingEntry.CORP_NAME);

        try {
            IndustryJobsResponse jobsResponse = apiService.apiCall(
                new CorporationIndustryJobsRequest(corpName), corpApiId, corpApikey
            );
            return jobsResponse.getJobs();
        } catch (IOException | ExecutionException | NoSuchAlgorithmException | InterruptedException | KeyManagementException e) {
            LOGGER.error("Error while retrieving corpjobs!", e);
        }

        return null;
    }

    /**
     * Populates the configurationFile with the list of corp members so you can easily add the api keys manualy.
     */
    public void populateConfigWithCorpMembers() {
        Collection<CorporationMember> members = getCorpMembers();

        if( members!=null ){
            for( CorporationMember thisMember : members ){
                settingMgr.setConfig(SettingEntry.CORP_MEMBER, "");
                settingMgr.setConfig(
                    SettingEntry.CORP_MEMBER_ID, thisMember.getCharacterID()
                );
                settingMgr.setConfig(
                    SettingEntry.CORP_MEMBER_NAME, thisMember.getName()
                );
                try {
                    settingMgr.getConfiguration().save();
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getCharacterInformation(CorporationMember thisMember) {
        /*apiService.apiCall(
                new CharacterInfoRequest(thisMember.getCharacterID()), corpApiId, corpApikey
        );*/
    }
}