package com.sanguine.webpos.bean;

import java.util.Comparator;

public class clsDirectBillerItemDtl 
{
  
	private String itemName;
    private String itemCode;
    private double qty;
    private double amt;
    private double rate;
    private boolean isModifier;
    private String modifierCode;
    private String tdhComboItemYN;
    private String tdh_ComboItemCode;
    private String promoCode;
    private String seqNo;
    private String strDefaultModifierDeselectedYN;
     private String modifierGroupCode;

    public clsDirectBillerItemDtl(String itemName, String itemCode, double qty, double amt, boolean isModifier, String modifierCode, String tdhComboItemYN, String tdh_ComboItemCode, double rate, String promoCode, String seqNo)
    {
        //public clsDirectBillerItemDtl(String itemName,String itemCode,double qty,double amt,boolean isModifier,String modifierCode,String tdhComboItemYN,String tdh_ComboItemCode,double rate,String promoCode){
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.qty = qty;
        this.amt = amt;
        this.isModifier = isModifier;
        this.modifierCode = modifierCode;
        this.tdhComboItemYN = tdhComboItemYN;
        this.tdh_ComboItemCode = tdh_ComboItemCode;
        this.rate = rate;
        this.promoCode = promoCode;
        this.seqNo = seqNo;
        this.strDefaultModifierDeselectedYN="N";
        this.modifierGroupCode="";
    }

    public String getModifierGroupCode()
    {
        return modifierGroupCode;
    }

    public void setModifierGroupCode(String modifierGroupCode)
    {
        this.modifierGroupCode = modifierGroupCode;
    }

    
    
    public String getPromoCode()
    {
        return promoCode;
    }

    public void setPromoCode(String promoCode)
    {
        this.promoCode = promoCode;
    }

    public double getRate()
    {
        return rate;
    }

    public void setRate(double rate)
    {
        this.rate = rate;
    }

    /**
     * @return the itemName
     */
    public String getItemName()
    {
        return itemName;
    }

    /**
     * @param itemName the itemName to set
     */
    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    /**
     * @return the itemCode
     */
    public String getItemCode()
    {
        return itemCode;
    }

    /**
     * @param itemCode the itemCode to set
     */
    public void setItemCode(String itemCode)
    {
        this.itemCode = itemCode;
    }

    /**
     * @return the qty
     */
    public double getQty()
    {
        return qty;
    }

    /**
     * @param qty the qty to set
     */
    public void setQty(double qty)
    {
        this.qty = qty;
    }

    /**
     * @return the amt
     */
    public double getAmt()
    {
        return amt;
    }

    /**
     * @param amt the amt to set
     */
    public void setAmt(double amt)
    {
        this.amt = amt;
    }

    /**
     * @return the isModifier
     */
    public boolean isIsModifier()
    {
        return isModifier;
    }

    /**
     * @param isModifier the isModifier to set
     */
    public void setIsModifier(boolean isModifier)
    {
        this.isModifier = isModifier;
    }

    /**
     * @return the modifierCode
     */
    public String getModifierCode()
    {
        return modifierCode;
    }

    /**
     * @param modifierCode the modifierCode to set
     */
    public void setModifierCode(String modifierCode)
    {
        this.modifierCode = modifierCode;
    }

    /**
     * @return the tdhComboItemYN
     */
    public String getTdhComboItemYN()
    {
        return tdhComboItemYN;
    }

    /**
     * @param tdhComboItemYN the tdhComboItemYN to set
     */
    public void setTdhComboItemYN(String tdhComboItemYN)
    {
        this.tdhComboItemYN = tdhComboItemYN;
    }

    /**
     * @return the tdh_ComboItemCode
     */
    public String getTdh_ComboItemCode()
    {
        return tdh_ComboItemCode;
    }

    public String getSeqNo()
    {
        return seqNo;
    }

    public void setSeqNo(String seqNo)
    {
        this.seqNo = seqNo;
    }

    public String getStrDefaultModifierDeselectedYN()
    {
        return strDefaultModifierDeselectedYN;
    }

    public void setStrDefaultModifierDeselectedYN(String strDefaultModifierDeselectedYN)
    {
        this.strDefaultModifierDeselectedYN = strDefaultModifierDeselectedYN;
    }

    
    
    public static Comparator<clsDirectBillerItemDtl> comparatorDirectBillerItemDtl = new Comparator<clsDirectBillerItemDtl>()
    {

        public int compare(clsDirectBillerItemDtl s1, clsDirectBillerItemDtl s2)
        {
            /*
             String itemCode1 = s1.getItemCode().toUpperCase();
             String itemCode2 = s2.getItemCode().toUpperCase();

             //ascending order
             return itemCode1.compareTo(itemCode2);

             //descending order
             //return itemCode2.compareTo(itemCode1);
             */

            double seqNo1 = Double.parseDouble(s1.getSeqNo());
            double seqNo2 = Double.parseDouble(s2.getSeqNo());

            if (seqNo1 == seqNo2)
            {
                return 0;
            }
            else if (seqNo1 > seqNo2)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
    };
	
	
}
