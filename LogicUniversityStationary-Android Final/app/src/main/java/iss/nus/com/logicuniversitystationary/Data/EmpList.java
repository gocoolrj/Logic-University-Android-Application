package iss.nus.com.logicuniversitystationary.Data;

/**
 * Created by student on 3/9/15.
 */
public class EmpList extends java.util.HashMap<String,String>  {
    public EmpList(String employeeName, String employeeId)
    {
        put("employeeName", employeeName);
        put("employeeId", employeeId);
    }
}
