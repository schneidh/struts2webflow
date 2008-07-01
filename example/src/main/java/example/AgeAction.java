package example;

import com.googlecode.struts2webflow.annotations.FlowOut;
import com.opensymphony.xwork2.ActionSupport;

/**
 * This action allows the user to enter their age and saves that age
 * in the session.
 */
public class AgeAction extends ActionSupport {

	private static final long serialVersionUID = 5023069489349427278L;

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

	@Override
	public void validate() {
		if (getAge() < 16) {
			addFieldError("age", "You must be at least 16 years old");
		}
		else if (getAge() > 100) {
			addFieldError("age", "Sorry, we don't ensure wheelchairs");
		}
	}
    
    
}
