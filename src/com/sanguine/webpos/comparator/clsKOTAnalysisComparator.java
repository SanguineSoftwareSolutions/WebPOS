/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsKOTAnalysisBean;

/**
 *
 * @author ajjim
 */
public class clsKOTAnalysisComparator implements Comparator<clsKOTAnalysisBean>
{

    private List<Comparator<clsKOTAnalysisBean>> listComparators;

    @SafeVarargs
    public clsKOTAnalysisComparator(Comparator<clsKOTAnalysisBean>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsKOTAnalysisBean o1, clsKOTAnalysisBean o2)
    {
        for (Comparator<clsKOTAnalysisBean> comparator : listComparators)
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