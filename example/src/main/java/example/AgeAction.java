package example;

import com.googlecode.struts2webflow.annotations.FlowOut;
import com.opensymphony.xwork2.ActionSupport;

/**
 * This action allows the user to enter their age and saves that age
 * in the session.
 */
public class AgeAction extends ActionSupport {
    /**
     * The place in the session where the user's age is stored.
     */
    public static final String AGE_KEY = "RATING_AGE_KEY";

    @FlowOut
    private Integer age;

    public String save() {
        // flow scope interceptor handles saving the age
        return SUCCESS;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
