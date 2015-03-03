package net.haroeris.eve;

import com.tlabs.eve.api.EveAPI;
import com.tlabs.eve.api.EveAPIRequest;
import com.tlabs.eve.api.EveAPIResponse;
import net.haroeris.core.http.HttpConnectionService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by stw on 28.02.2015.
 */
@Service
public class ApiService {

    // TODO Make configurable
    private static String API_BASE_URL = "https://api.eveonline.com";

    private static Logger LOGGER = LoggerFactory.getLogger(ApiService.class);

    @Autowired
    private HttpConnectionService connectionService;

    public final <T extends EveAPIResponse> T apiCall(
        final EveAPIRequest<T> request, String apiID, String apiKey
    ) throws IOException, ExecutionException, InterruptedException, NoSuchAlgorithmException, KeyManagementException {

        T q = callEveAPI(request, apiID, apiKey);
        if (q!=null && q.getErrorCode() != 0) {
            throw new IllegalArgumentException("Eve API Error " + q.getErrorCode());
        }
        return q;
    }

    private <T extends EveAPIResponse> T callEveAPI(
        final EveAPIRequest<T> request, String apiID, String apiKey
    ) throws IOException, ExecutionException, InterruptedException {
        final List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        if (request instanceof EveAPIRequest.Authenticated) {
            EveAPIRequest.Authenticated auth = (EveAPIRequest.Authenticated) request;
            if (StringUtils.isBlank(auth.getKeyID())) {
                nvps.add(new BasicNameValuePair("keyID", apiID));
            }
            else {
                nvps.add(new BasicNameValuePair("keyID", auth.getKeyID()));
            }
            if (StringUtils.isBlank(auth.getKey())) {
                nvps.add(new BasicNameValuePair("vCode", apiKey));
            }
            else {
                nvps.add(new BasicNameValuePair("vCode", auth.getKey()));
            }
        }

        final Map<String, String> params = request.getParameters();
        for (String p : params.keySet()) {
            if (!"vCode".equalsIgnoreCase(p) && !"keyID".equalsIgnoreCase(p)) {
                String v = params.get(p);
                if (StringUtils.isNotBlank(v)) {
                    nvps.add(new BasicNameValuePair(p, params.get(p)));
                }
            }
        }

        String result = connectionService.post(API_BASE_URL + request.getPage(), nvps);
        if( StringUtils.isNotBlank(result) ) {
            return EveAPI.parse(request, IOUtils.toInputStream(result));
        } else {
            LOGGER.error("[callEveAPI] Nu Result returned!");
            return null;
        }
    }

}