import com.bean.match.MatchDetail;
import com.service.MatchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created on 2017/06/19.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class HashTest {

//    @Autowired
//    private MatchService matchService;

    @Test
    public void dod() {
        MatchService matchService = new MatchService();
        MatchDetail matchDetail =  matchService.getMatchDetail(3351961375l);
        System.out.println();
    }

}
