package iss.nus.com.logicuniversitystationary.Data;

/**
 * Created by student on 4/9/15.
 */
public class AdjustmentVoucherDetails extends java.util.HashMap<String,String> {
        public AdjustmentVoucherDetails(String productCode, String productName, String qty, String remark)
        {
            put("clerkId", "345173");
            put("productCode", productCode);
            put("productName", productName);
            put("qty", qty);
            put("remark", remark);
        }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AdjustmentVoucherDetails) {
            o = ((AdjustmentVoucherDetails) o).get("productCode");
        }
        return o.equals(this.get("productCode"));
    }
}
