package iss.nus.com.logicuniversitystationary.Data;

/**
 * Created by aravindashokkumar on 6/9/15.
 */
public class ProductsDescription extends java.util.HashMap<String,Object> {
    public ProductsDescription(String productCode, String productName)
    {
        put("productCode", productCode);
        put("productName", productName);
    }

    public ProductsDescription(String productName, int qty, String UnitOfMeasurement)
    {
        put("productName", productName);
        put("qty", qty);
        put("units",UnitOfMeasurement);
    }

    public ProductsDescription(String productCode,String productName, int quantity, String UnitOfMeasurement, String Bin)
    {
        put("productCode", productCode);
        put("productName", productName);
        put("qty", quantity);
        put("units",UnitOfMeasurement);
        put("bin",Bin);
    }

    public ProductsDescription(String productCode,String productName, int quantity, String UnitOfMeasurement)
    {
        put("productCode", productCode);
        put("productName", productName);
        put("qty", quantity);
        put("units",UnitOfMeasurement);
    }
}
