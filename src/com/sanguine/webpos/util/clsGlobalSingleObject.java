package com.sanguine.webpos.util;
public class clsGlobalSingleObject {
   private static clsPasswordEncryptDecreat objPasswordEncryptDecreat=null;
   
   private clsGlobalSingleObject(){}
   
   /**
     * @return the objPasswordEncryptDecreat
     */
    public static clsPasswordEncryptDecreat getObjPasswordEncryptDecreat() 
    {
        if (objPasswordEncryptDecreat == null) 
        {
            objPasswordEncryptDecreat = new clsPasswordEncryptDecreat();
        }
        return objPasswordEncryptDecreat;
    }
}