package iss.nus.com.logicuniversitystationary.Data;

/**
 * Created by student on 9/9/15.
 */
public class DepartmentOrder extends java.util.HashMap<String,String> {
    public DepartmentOrder(String orderId, String departmentName) {
        put("orderId", orderId);
        put("departmentName", departmentName);
    }
}