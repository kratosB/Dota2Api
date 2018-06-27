import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created on 2018/6/26.
 *
 * @author zhiqiang bao
 */
public class SortTest {

    public static void main(String[] args) {
        List<SortBean> beanList = new ArrayList<>();
        beanList.add(new SortBean(1, 3, 3));
        beanList.add(new SortBean(1, 2, 3));
        beanList.add(new SortBean(1, 1, 3));
        beanList.add(new SortBean(2, 1, 3));
        beanList.add(new SortBean(2, 2, 3));
        beanList.add(new SortBean(2, 3, 3));
        beanList.add(new SortBean(3, 2, 3));
        beanList.add(new SortBean(3, 2, 2));
        beanList.add(new SortBean(3, 1, 3));

        beanList.sort((arg0, arg1) -> {
            if (arg0.getValue1().compareTo(arg1.getValue1()) == 0) {
                if (arg0.getValue2().compareTo(arg1.getValue2()) == 0) {
                    if (arg0.getValue3().compareTo(arg1.getValue3()) == 0) {
                        return 1;
                    } else {
                        return arg0.getValue3().compareTo(arg1.getValue3());
                    }
                } else {
                    return arg0.getValue2().compareTo(arg1.getValue2());
                }
            } else {
                return arg0.getValue1().compareTo(arg1.getValue1());
            }
        });
        System.out.println();
    }
}

@Data
class SortBean {

    public SortBean(int value1, int value2, int value3) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    private Integer value1;

    private Integer value2;

    private Integer value3;

}
