/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsAPCReport;

/**
 *
 * @author ajjim
 */
public class clsWaiterWiseAPCComparator implements Comparator<clsAPCReport>
{

    private List<Comparator<clsAPCReport>> listComparators;

    @SafeVarargs
    public clsWaiterWiseAPCComparator(Comparator<clsAPCReport>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsAPCReport o1, clsAPCReport o2)
    {
        for (Comparator<clsAPCReport> comparator : listComparators)
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