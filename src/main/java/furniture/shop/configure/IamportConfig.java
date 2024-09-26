package furniture.shop.configure;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamportConfig {

    @Value("${iamport.rest-api.key}")
    private String iAmPortKey;

    @Value("${iamport.rest-api.secret}")
    private String iAmPortSecret;

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(iAmPortKey, iAmPortSecret);
    }

}
