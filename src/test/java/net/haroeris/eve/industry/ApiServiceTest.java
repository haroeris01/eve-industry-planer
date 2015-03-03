package net.haroeris.eve.industry;

import com.tlabs.eve.api.IndustryJobsResponse;
import com.tlabs.eve.api.corporation.CorporationIndustryJobsRequest;
import com.tlabs.eve.api.corporation.MemberTrackingRequest;
import com.tlabs.eve.api.corporation.MemberTrackingResponse;
import net.haroeris.core.http.HttpConnectionService;
import net.haroeris.eve.ApiService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.mockito.Mockito.when;

/**
 * Created by stw on 28.02.2015.
 */
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = {"/spring-test-config.xml", "/spring-config.xml"} )
@RunWith(SpringJUnit4ClassRunner.class)
public class ApiServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServiceTest.class);
    @Autowired
    @InjectMocks
    private ApiService apiService;

    @Mock
    private HttpConnectionService connectionService;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCorporationMembers() throws Exception {
        MemberTrackingRequest request = new MemberTrackingRequest("T4T", true);
        when( // mock reply
            connectionService.post(
                Matchers.endsWith(request.getPage()), Matchers.anyList()
            )
        ).thenReturn(createResponse("CorporationMembers.xml"));

        MemberTrackingResponse members = apiService.apiCall(
            request, "1234567", "asdasdfasdfsadfasdfasdfsadfsadf"
        );

        Assert.assertNotNull( "No members returned!", members );
        Assert.assertNotNull( "No members defined!", members.getCorpMembers() );
        Assert.assertFalse( "No members found!", members.getCorpMembers().isEmpty() );
    }

    //@Test(timeout = 10000)
    @Test
    public void testCorporationJobs() throws Exception {
        CorporationIndustryJobsRequest request = new CorporationIndustryJobsRequest("T4T");
        when( // mock reply
            connectionService.post(
                Matchers.endsWith(request.getPage()), Matchers.anyList()
            )
        ).thenReturn(createResponse("CorporationIndustryJobs.xml"));

        IndustryJobsResponse jobs = apiService.apiCall(
                request, "1234567", "asdasdfasdfsadfasdfasdfsadfsadf"
        );
        Assert.assertNotNull("No Jobs returned!", jobs);
        Assert.assertNotNull("No Jobs defined!", jobs.getJobs());
        Assert.assertFalse("No Jobs found!", jobs.getJobs().isEmpty());
        /*
        for( IndustryJob job : jobs.getJobs() ){
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTimeInMillis(job.getEndDate());
            System.out.println(job.getJobID() + " - " + job.getInstallerName() + " - " + endCalendar);
        }
        */
    }

    private <T> T createResponse(String filename, Class<T> thisClass) {
        try {
            ClassPathResource pathResource = new ClassPathResource("responses/"+filename);

            JAXBContext context = JAXBContext.newInstance( thisClass );
            javax.xml.bind.Unmarshaller marshaller = context.createUnmarshaller();

            return (T) marshaller.unmarshal( pathResource.getInputStream() );

        } catch (Exception e) {
            LOGGER.error( "Fehler beim parsen der XML-Datei!", e );
            return null;
        }
    }
    private String createResponse(String filename) {
        try {
            ClassPathResource pathResource = new ClassPathResource("responses/"+filename);
            StringBuilder returnString = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(pathResource.getInputStream()));
            try {
                String line = br.readLine();
                while (line != null) {
                    returnString.append(line);
                    returnString.append("\n");
                    line = br.readLine();
                }
            } finally {
                br.close();
            }

            return returnString.toString();

        } catch (Exception e) {
            LOGGER.error( "Error while reading file!", e );
            return null;
        }
    }
}
