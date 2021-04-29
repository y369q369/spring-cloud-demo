package gs.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author grassPrince
 * @Date 2021/4/29 9:54
 * @Description TODO
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private Integer Id;

    private String name;

    private Integer age;

}
