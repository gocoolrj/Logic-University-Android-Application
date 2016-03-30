package iss.nus.com.logicuniversitystationary.Data;

/**
 * Created by student on 3/9/15.
 */
public class LowStockInventory extends java.util.HashMap<String,String> {
    public LowStockInventory(String productName, String quantity)
    {
        put("productName", productName);
        put("quantity", quantity);
    }
}
