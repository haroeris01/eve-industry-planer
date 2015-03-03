package net.haroeris.eve;

import com.tlabs.eve.api.IndustryJob;
import com.tlabs.eve.api.corporation.CorporationMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;

/**
 * Created by stw on 01.03.2015.
 */
public class MainClass {

    private static CorporationInformationService corpInfoService;
    private static Logger LOGGER = LoggerFactory.getLogger(MainClass.class);

    static {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-config.xml");
        corpInfoService = applicationContext.getBean(CorporationInformationService.class);
    }

    public static void main( String ... args ) throws Exception {
        LOGGER.info("Starting Application.");

        //corpInfoService.populateConfigWithCorpMembers();

        Collection<IndustryJob> jobs = corpInfoService.getCorpJobs();

        if( jobs!=null ){
            for(IndustryJob thisJob : jobs ){
                System.out.println(thisJob.getInstallerName() + " - " + thisJob.getType() + " - " + thisJob.getBlueprintTypeName() + " - finished="+ thisJob.getCompleted() );
            }
        } else {
            LOGGER.error("NO Jobs returned!");
        }
        Collection<CorporationMember> members = corpInfoService.getCorpMembers();

        if( members!=null ){
            for(CorporationMember thisMember : members ){
                System.out.println(thisMember.getCharacterID() + " - " + thisMember.getName() );

                corpInfoService.getCharacterInformation( thisMember );
            }
        } else {
            LOGGER.error("NO Jobs returned!");
        }
    }
}