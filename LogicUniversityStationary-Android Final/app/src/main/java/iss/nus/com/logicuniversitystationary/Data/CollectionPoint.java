package iss.nus.com.logicuniversitystationary.Data;

/**
 * Created by student on 8/9/15.
 */
public class CollectionPoint extends java.util.HashMap<String,String> {
    public CollectionPoint(String CollPointName, String CollPointTime)
    {
        put("CollPointName", CollPointName);
        put("CollPointTime", CollPointTime);
    }
}
