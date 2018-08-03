/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsVoidBillDtl;

/**
 *
 * @author ajjim
 */
public class clsVoidBillComparator implements Comparator<clsVoidBillDtl>
{

    private List<Comparator<clsVoidBillDtl>> listComparators;

    @SafeVarargs
    public clsVoidBillComparator(Comparator<clsVoidBillDtl>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsVoidBillDtl o1, clsVoidBillDtl o2)
    {
        for (Comparator<clsVoidBillDtl> comparator : listComparators)
        {
            int result = comparator.compare(o1, o2);
            if (result != 0)
            {
                return result;
            }
        }
        return 0;
    }
}
