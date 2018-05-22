import com.config.Configuration;
import com.fasterxml.jackson.databind.JsonNode;
import com.util.JsonMapper;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * Created on 2017/06/19.
 */
public class HashTest {

    public static void main(String[] args) {
        long time = 1445701409000L;
        System.out.println(new Date(time));
    }

}
