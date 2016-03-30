package iss.nus.com.logicuniversitystationary.Data;

/**
 * Created by student on 10/9/15.
 */
public class Representatives extends java.util.HashMap<String,String> {
    public Representatives(String RepresentativeName, String DepartmentName, String DepartmentId)
    {
        put("RepresentativeName", RepresentativeName);
        put("DepartmentName", DepartmentName);
        put("DepartmentId",DepartmentId);
    }
}
