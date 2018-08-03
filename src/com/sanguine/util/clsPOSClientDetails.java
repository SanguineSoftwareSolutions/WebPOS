package com.sanguine.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sanguine.webpos.controller.clsSMSPackDtl;

public class clsPOSClientDetails {

	

    public static Map<String, clsPOSClientDetails> hmClientDtl;
    private static final SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
    public String id;
    public String Client_Name;
    public Date installDate;
    public Date expiryDate;
    private String posVersion;//Enterprise/Lite
    private int intMAXTerminal;//No. of POS Machines
    private int intMAXAPOSTerminals;//No. of APOS Devices
    private clsSMSPackDtl objSMSPackDtl;//client SMS pack details
    private String strComments;

    private clsPOSClientDetails(String id, String Client_Name, Date installDate, Date expiryDate)
    {
        this.id = id;
        this.Client_Name = Client_Name;
        this.installDate = installDate;
        this.expiryDate = expiryDate;
    }

    private clsPOSClientDetails(String id, String Client_Name, Date installDate, Date expiryDate, String posVersion, int intMAXTerminal, clsSMSPackDtl smsPackDtl, int intMAXAPOSTerminals, String comments)
    {

        this.id = id;
        this.Client_Name = Client_Name;
        this.installDate = installDate;
        this.expiryDate = expiryDate;
        this.posVersion = posVersion;
        this.intMAXTerminal = intMAXTerminal;
        this.intMAXAPOSTerminals = intMAXAPOSTerminals;
        this.objSMSPackDtl = smsPackDtl;
        this.strComments = comments;

    }

    public static clsPOSClientDetails createClientDetails(String id, String clientName, Date installDate, Date expiryDate)
    {
        return new clsPOSClientDetails(id, clientName, installDate, expiryDate);
    }

    public static clsPOSClientDetails createClientDetails(String id, String clientName, Date installDate, Date expiryDate, String posVersion, int intMAXTerminal, clsSMSPackDtl smsPackDtl, int intMAXAPOSTerminals, String comments)
    {
        return new clsPOSClientDetails(id, clientName, installDate, expiryDate, posVersion, intMAXTerminal, smsPackDtl, intMAXAPOSTerminals, comments);
    }

    public static void funAddClientCodeAndName()
    {
        try
        {
            hmClientDtl = new HashMap<String, clsPOSClientDetails>();
            clsSMSPackDtl objNoSMSPackDtl = new clsSMSPackDtl("", "", "", "NOSMSPACK");

            hmClientDtl.put("000.000", createClientDetails("000.000", "Demo ompany", dFormat.parse("2014-06-19"), dFormat.parse("2018-10-22"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("000.001", createClientDetails("001.001", "Monginis - Hadapsar", dFormat.parse("2014-06-19"), dFormat.parse("2014-06-19"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            //renamed Red Peppers to LANTERNS
            hmClientDtl.put("002.001", createClientDetails("002.001", "LANTERNS", dFormat.parse("2014-07-14"), dFormat.parse("2018-10-15"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 07-11-2017 for 1 year //renamed Red Peppers to LANTERNS"));
            hmClientDtl.put("003.001", createClientDetails("003.001", "FOODJOCKEYS LLP", dFormat.parse("2014-03-02"), dFormat.parse("2016-03-02"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("004.001", createClientDetails("004.001", "CLIMAX OF FLAVORS", dFormat.parse("2014-03-02"), dFormat.parse("2016-04-07"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("009.001", createClientDetails("009.001", "SANSKAR BAZAAR", dFormat.parse("2014-05-01"), dFormat.parse("2016-05-01"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("010.001", createClientDetails("010.001", "Junnos Pizza", dFormat.parse("2014-05-19"), dFormat.parse("2016-05-07"), "Enterprise", 100, objNoSMSPackDtl, 1, "//HO "));//
            hmClientDtl.put("010.002", createClientDetails("010.002", "JP", dFormat.parse("2014-06-14"), dFormat.parse("2018-11-11"), "Enterprise", 100, objNoSMSPackDtl, 1, "//rename Junnos Pizza to JP,renewed on 11-11-2017 for 1 year till 11-11-2018"));// 
            hmClientDtl.put("011.001", createClientDetails("011.001", "Soul Cuisine", dFormat.parse("2014-06-19"), dFormat.parse("2016-06-22"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            //cancel this license  instead of 024.004 Eden Call Center
            //hmClientDtl.put("012.001", createClientDetails("012.001", "Dales Eden", dFormat.parse("2014-06-25"), dFormat.parse("2015-01-15"), "Enterprise", 100, objNoSMSPackDtl));
            hmClientDtl.put("013.001", createClientDetails("013.001", "Life Positive Pvt Ltd", dFormat.parse("2014-06-25"), dFormat.parse("2050-01-15"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("014.001", createClientDetails("014.001", "SONAI RESTAURANT & BAR", dFormat.parse("2014-06-26"), dFormat.parse("2015-06-26"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("015.001", createClientDetails("015.001", "SWAGAT VEG.", dFormat.parse("2014-04-26"), dFormat.parse("2015-06-05"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("016.001", createClientDetails("016.001", "BASIL", dFormat.parse("2014-07-07"), dFormat.parse("2017-01-07"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("018.001", createClientDetails("018.001", "Life Positive Foundation", dFormat.parse("2014-07-11"), dFormat.parse("2015-07-11"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("019.001", createClientDetails("019.001", "Aisha Fashion Avenue", dFormat.parse("2014-07-14"), dFormat.parse("2015-07-14"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("020.001", createClientDetails("020.001", "Zeal", dFormat.parse("2014-08-06"), dFormat.parse("2016-11-19"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("021.001", createClientDetails("021.001", "Mezbaan Carters Blue", dFormat.parse("2014-08-13"), dFormat.parse("2014-11-10"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("022.001", createClientDetails("022.001", "MR Fried Chicken", dFormat.parse("2014-09-23"), dFormat.parse("2018-08-14"), "Enterprise", 100, objNoSMSPackDtl, 1, "// renewed on 14-08-2017 for 1 year"));
            hmClientDtl.put("023.001", createClientDetails("023.001", "SID Hospitality", dFormat.parse("2014-09-23"), dFormat.parse("2015-10-15"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            //Eden Cake Shop 024.xxx renewed on 05-10-2017 till 31-12-2018
            hmClientDtl.put("024.001", createClientDetails("024.001", "Eden Cake Shop", dFormat.parse("2014-09-23"), dFormat.parse("2018-12-31"), "Enterprise", 100, objNoSMSPackDtl, 1, "// Outlet 1 HO "));
            hmClientDtl.put("024.002", createClientDetails("024.002", "Eden Cake Shop", dFormat.parse("2015-09-17"), dFormat.parse("2018-12-31"), "Enterprise", 100, objNoSMSPackDtl, 1, "// Outlet 2 Lokhandwala"));
            hmClientDtl.put("024.003", createClientDetails("024.003", "Eden Cake Shop", dFormat.parse("2016-03-23"), dFormat.parse("2018-12-31"), "Enterprise", 100, objNoSMSPackDtl, 1, "// Outlet 3 Cl.. Road"));
            hmClientDtl.put("024.004", createClientDetails("024.004", "Eden Cake Shop", dFormat.parse("2016-03-23"), dFormat.parse("2018-12-31"), "Enterprise", 100, objNoSMSPackDtl, 1, "// Outlet 4 Call Center"));
            hmClientDtl.put("025.001", createClientDetails("025.001", "Dravidas Bistro", dFormat.parse("2014-10-02"), dFormat.parse("2015-10-15"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("026.001", createClientDetails("026.001", "FUNBITEZ", dFormat.parse("2014-09-23"), dFormat.parse("2017-05-25"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("026.002", createClientDetails("026.002", "CREATIVE FOODS", dFormat.parse("2015-07-08"), dFormat.parse("2016-07-08"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("026.003", createClientDetails("026.003", "CREATIVE FOODS", dFormat.parse("2015-07-08"), dFormat.parse("2016-07-08"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("026.000", createClientDetails("026.000", "CREATIVE FOODS", dFormat.parse("2015-07-08"), dFormat.parse("2016-07-08"), "Enterprise", 100, objNoSMSPackDtl, 1, "//HO Licence"));
            hmClientDtl.put("027.001", createClientDetails("027.001", "Cilantro", dFormat.parse("2014-10-23"), dFormat.parse("2095-01-15"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("028.001", createClientDetails("028.001", "THE APPETITE MOMOS", dFormat.parse("2014-11-01"), dFormat.parse("2018-01-20"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 20-01-2017 by 1 year"));
            hmClientDtl.put("030.001", createClientDetails("030.001", "11 SPICES", dFormat.parse("2014-11-19"), dFormat.parse("2017-12-19"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("031.001", createClientDetails("031.001", "INTERVAL FOODCOURT", dFormat.parse("2014-11-29"), dFormat.parse("2016-1-29"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("032.001", createClientDetails("032.001", "ROLLING STOVE", dFormat.parse("2014-11-24"), dFormat.parse("2015-12-24"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("032.002", createClientDetails("032.002", "ROLLING STOVE", dFormat.parse("2014-11-24"), dFormat.parse("2015-12-24"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("032.003", createClientDetails("032.003", "ROLLING STOVE", dFormat.parse("2014-11-24"), dFormat.parse("2015-12-24"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("033.001", createClientDetails("033.001", "THE J", dFormat.parse("2014-12-06"), dFormat.parse("2016-12-06"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("034.001", createClientDetails("034.001", "THE BLUE ROOF CLUB", dFormat.parse("2014-12-08"), dFormat.parse("2018-03-07"), "Enterprise", 100, objNoSMSPackDtl, 1, "//(Lemon Caters) renewed on 07-03-2017 "));
            hmClientDtl.put("035.001", createClientDetails("035.001", "The Local Cafe", dFormat.parse("2014-12-09"), dFormat.parse("2017-03-31"), "Enterprise", 100, objNoSMSPackDtl, 1, "// old name was \"NE THING FOR CHOCOLATE\" rename on 16-08-2016"));
            //hmClientDtl.put("036.001", createClientDetails("036.001", "CREATIVE FOODS",dFormat.parse("2014-12-09"),dFormat.parse("2015-01-09")));
            hmClientDtl.put("037.001", createClientDetails("037.001", "BAKERS KRAFT", dFormat.parse("2014-12-19"), dFormat.parse("2016-02-19"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("037.002", createClientDetails("037.002", "BAKERS KRAFT", dFormat.parse("2014-12-19"), dFormat.parse("2016-02-19"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("038.001", createClientDetails("038.001", "SPICE FUSION", dFormat.parse("2014-12-19"), dFormat.parse("2018-04-19"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 19-04-2017 for 1 year"));
            hmClientDtl.put("039.001", createClientDetails("039.001", "ABHIRUCHI", dFormat.parse("2014-12-31"), dFormat.parse("2015-01-31"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("040.001", createClientDetails("040.001", "HAPPINESS DELI", dFormat.parse("2015-01-03"), dFormat.parse("2016-01-03"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("041.001", createClientDetails("041.001", "THE WHITE LOUNGE", dFormat.parse("2015-01-09"), dFormat.parse("2017-01-09"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("042.001", createClientDetails("042.001", "Ashoka Pure Veg", dFormat.parse("2015-01-09"), dFormat.parse("2018-05-31"), "Enterprise", 100, new clsSMSPackDtl("AshokaPureVeg", "2017@AshokaPureVeg@2017", "SANPOS", "Transactional"), 0, "//renewed on 31-05-2017  for year,1000 SMS pack activated on 09-08-2017"));
            hmClientDtl.put("042.002", createClientDetails("042.002", "Ashoka Spice", dFormat.parse("2015-01-09"), dFormat.parse("2017-01-09"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("043.001", createClientDetails("043.001", "HAKKA EXPRESS", dFormat.parse("2015-01-14"), dFormat.parse("2018-01-13"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 13-01-2017 for 043.001 1 year"));
            hmClientDtl.put("043.002", createClientDetails("043.002", "HAKKA EXPRESS", dFormat.parse("2015-10-14"), dFormat.parse("2016-10-14"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));//
            hmClientDtl.put("044.001", createClientDetails("044.001", "SWEETS INDIA", dFormat.parse("2015-09-11"), dFormat.parse("2015-11-12"), "Enterprise", 100, objNoSMSPackDtl, 1, "// HO Licence For OHRIS"));
            //hmClientDtl.put("044.002", createClientDetails("044.002", "HOTEL KAMAL PVT LTD",dFormat.parse("2015-09-11"),dFormat.parse("2015-11-12")));
            hmClientDtl.put("045.001", createClientDetails("045.001", "Mr Beans", dFormat.parse("2015-02-05"), dFormat.parse("2016-03-10"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("046.001", createClientDetails("046.001", "CHANSON HOSPITALITY PVT. LTD", dFormat.parse("2015-02-10"), dFormat.parse("2015-05-11"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("047.001", createClientDetails("047.001", "ROYAL CONNAUGHT BOAT CLUB", dFormat.parse("2015-03-07"), dFormat.parse("2095-03-07"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("048.001", createClientDetails("048.001", "WHITE CASTLE", dFormat.parse("2015-03-17"), dFormat.parse("2017-03-01"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("049.001", createClientDetails("049.001", "DIVINE BOWL", dFormat.parse("2015-03-17"), dFormat.parse("2017-03-17"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("050.001", createClientDetails("050.001", "HOTEL OM SAIRAJ", dFormat.parse("2015-03-17"), dFormat.parse("2017-05-17"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renamed \"SAIRAJ PURE VEG\" as \"HOTEL OM SAIRAJ\" on 23-01-2017"));
            hmClientDtl.put("051.001", createClientDetails("051.001", "BOMBAY CATERING COMPANY", dFormat.parse("2015-03-23"), dFormat.parse("2015-06-23"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("052.001", createClientDetails("052.001", "CURRY N BITES", dFormat.parse("2015-04-04"), dFormat.parse("2016-04-04"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("053.001", createClientDetails("053.001", "WHITE DOVE", dFormat.parse("2015-04-11"), dFormat.parse("2018-11-20"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renew on 21-11-2017 for 1 year till 20-11-2018"));
            hmClientDtl.put("054.001", createClientDetails("054.001", "J HEARSCH COMPANY", dFormat.parse("2015-04-16"), dFormat.parse("2018-08-28"), "Enterprise", 100, objNoSMSPackDtl, 1, "// renewed on 28-08-2017 for 1 year"));
            hmClientDtl.put("055.001", createClientDetails("055.001", "BOMBAY BURGER", dFormat.parse("2015-04-16"), dFormat.parse("2017-05-16"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renew on 08-01-2016"));
            hmClientDtl.put("056.001", createClientDetails("056.001", "SYKZ CAFE", dFormat.parse("2015-05-04"), dFormat.parse("2017-05-04"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("057.001", createClientDetails("057.001", "LALAz mini punjabb", dFormat.parse("2015-05-11"), dFormat.parse("2015-06-31"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("058.001", createClientDetails("058.001", "Jainsons", dFormat.parse("2015-05-09"), dFormat.parse("2015-05-20"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("059.001", createClientDetails("059.001", "SHAGUN", dFormat.parse("2015-05-14"), dFormat.parse("2015-07-14"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("060.001", createClientDetails("060.001", "FLYING SAUCER SKY BAR", dFormat.parse("2015-06-01"), dFormat.parse("2018-05-31"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renew on 01-12-2017 for 1 month till 31-12-2017//renewed on 02-01-2018 till 31-05-2018"));
            hmClientDtl.put("061.001", createClientDetails("061.001", "THE FOOD PLAZA", dFormat.parse("2015-06-01"), dFormat.parse("2017-06-01"), "Enterprise", 100, objNoSMSPackDtl, 1, "//rename SHAN E MAROL to THE FOOD PLAZA"));
            hmClientDtl.put("062.001", createClientDetails("062.001", "THE PATIO", dFormat.parse("2015-06-24"), dFormat.parse("2017-08-24"), "Enterprise", 100, objNoSMSPackDtl, 1, "// extened 7  day from 24-07-2016 now date 17-08-2017"));
            hmClientDtl.put("063.001", createClientDetails("063.001", "NFC", dFormat.parse("2015-07-01"), dFormat.parse("2016-07-24"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("064.001", createClientDetails("064.001", "Cafe Olivo", dFormat.parse("2015-07-06"), dFormat.parse("2017-07-24"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("065.001", createClientDetails("065.001", "The Pizza Farm", dFormat.parse("2015-07-23"), dFormat.parse("2017-07-23"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renew on 08-01-2016"));
            hmClientDtl.put("066.001", createClientDetails("066.001", "A&K HOSPITALITY", dFormat.parse("2015-08-04"), dFormat.parse("2017-09-30"), "Enterprise", 100, objNoSMSPackDtl, 1, " //changed expriry date from 2016-12-19 to 2017-09-30 on 4th sept 2017 as per sachin sir"));
            hmClientDtl.put("067.001", createClientDetails("067.001", "SHUBHAM GLOBAL FOODS LTD (CURRYMIA)", dFormat.parse("2015-08-08"), dFormat.parse("2016-08-08"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("068.001", createClientDetails("068.001", "Saloni Retail LLP", dFormat.parse("2015-08-11"), dFormat.parse("2016-08-11"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("069.001", createClientDetails("069.001", "DAIWONG", dFormat.parse("2015-08-13"), dFormat.parse("2018-09-23"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 23-09-2017 for 1 year"));
            hmClientDtl.put("070.001", createClientDetails("070.001", "Bubsterrs", dFormat.parse("2015-08-20"), dFormat.parse("2017-09-01"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 01-09-2016"));
            //"071.001", "La Bouchee dOr" first License and "162.001,SWEET LAVENDER FOODS LLP" Second Outlet license
            hmClientDtl.put("071.001", createClientDetails("071.001", "La Bouchee dOr", dFormat.parse("2015-08-22"), dFormat.parse("2018-11-23"), "Enterprise", 100, objNoSMSPackDtl, 1, " //071.001, La Bouchee dOr first License and 162.001,SWEET LAVENDER FOODS LLP Second Outlet license//renewed on 24-10-2017 for 1 month"));
            hmClientDtl.put("072.001", createClientDetails("072.001", "MOHANLAL S MITHAIWALA", dFormat.parse("2015-08-25"), dFormat.parse("2018-09-14"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 14-09-2017 for 1 year"));
            hmClientDtl.put("073.001", createClientDetails("073.001", "I HOSPITALITY", dFormat.parse("2015-09-01"), dFormat.parse("2016-03-31"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("074.001", createClientDetails("074.001", "THE POONA CLUB LTD", dFormat.parse("2015-09-02"), dFormat.parse("2017-05-20"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("075.001", createClientDetails("075.001", "SWEETS INDIA", dFormat.parse("2015-01-20"), dFormat.parse("2016-02-21"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("076.001", createClientDetails("076.001", "KLOCK KITCHEN", dFormat.parse("2015-09-16"), dFormat.parse("2018-09-07"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewd on 07-10-2017 for 1 year"));
            hmClientDtl.put("077.001", createClientDetails("077.001", "JAINSONS SWEETS", dFormat.parse("2015-09-16"), dFormat.parse("2018-10-16"), "Enterprise", 100, objNoSMSPackDtl, 1, "// HO  // renewed on 16-10-2017 for 1 year "));
            hmClientDtl.put("077.002", createClientDetails("077.002", "JAINSONS SWEETS", dFormat.parse("2015-09-16"), dFormat.parse("2018-10-16"), "Enterprise", 100, objNoSMSPackDtl, 1, "// Outlet 1"));
            hmClientDtl.put("077.003", createClientDetails("077.003", "JAINSONS SWEETS", dFormat.parse("2015-09-16"), dFormat.parse("2018-10-16"), "Enterprise", 100, objNoSMSPackDtl, 1, " // Outlet 2"));
            hmClientDtl.put("078.001", createClientDetails("078.001", "THE NORTHERN FRONTIER", dFormat.parse("2015-09-18"), dFormat.parse("2016-10-18"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("079.001", createClientDetails("079.001", "Baked & Wired", dFormat.parse("2015-09-18"), dFormat.parse("2016-11-02"), "Enterprise", 10, objNoSMSPackDtl, 1, "//renewed on 27-10-2016"));
            hmClientDtl.put("080.001", createClientDetails("080.001", "KAREEMS", dFormat.parse("2015-10-14"), dFormat.parse("2017-11-15"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 16-11-2016 for 1 year"));
            hmClientDtl.put("081.001", createClientDetails("081.001", "THE APPETITE DESTINATION", dFormat.parse("2015-10-14"), dFormat.parse("2016-10-14"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("082.001", createClientDetails("082.001", "HONEYGUIDES FOOD PARADISE", dFormat.parse("2015-10-14"), dFormat.parse("2016-10-14"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("083.001", createClientDetails("083.001", "KIMLING EXPRESS", dFormat.parse("2015-10-20"), dFormat.parse("2016-10-20"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("084.001", createClientDetails("084.001", "CAFE GOA", dFormat.parse("2015-10-20"), dFormat.parse("2016-01-19"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("085.001", createClientDetails("085.001", "CHOPS AND HOPS HOSPITALITY SERVICES LLP", dFormat.parse("2015-10-22"), dFormat.parse("2017-10-31"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 30-10-2016"));
            hmClientDtl.put("086.001", createClientDetails("086.001", "NAUGHTY ANGEL CAFE", dFormat.parse("2015-10-23"), dFormat.parse("2018-12-23"), "Enterprise", 100, objNoSMSPackDtl, 1, "//licence extended on 23-11-2017,renewed on 26-12-2017 for 1 year till 23-12-2018"));
            hmClientDtl.put("087.001", createClientDetails("087.001", "MADHUKAR RESTAURANT", dFormat.parse("2015-11-18"), dFormat.parse("2018-11-18"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 18-11-2017 for 1 year till 18-11-2018"));
            hmClientDtl.put("088.001", createClientDetails("088.001", "A&K HOSPITALITY", dFormat.parse("2015-11-24"), dFormat.parse("2018-03-04"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 06-03-2016 for 1 year"));
            //JORUP ENTERPRISE LLP renamed to CAFFA (GUFTOZU)
            hmClientDtl.put("089.001", createClientDetails("089.001", "CAFFA", dFormat.parse("2015-12-17"), dFormat.parse("2018-10-03"), "Enterprise", 100, objNoSMSPackDtl, 1, " //JORUP ENTERPRISE LLP renamed to CAFFA (GUFTOZU)// renewed on 03-10-2017 till 03-10-2018 "));
            hmClientDtl.put("090.001", createClientDetails("090.001", "Mathura a fusion of pure VEG", dFormat.parse("2015-12-22"), dFormat.parse("2018-12-22"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 22-12-2016 by 1 year,renewed on 24-12-2017 for 1 year."));
            hmClientDtl.put("091.001", createClientDetails("091.001", "Gourmet Gelato Company Pvt. Ltd.", dFormat.parse("2015-12-23"), dFormat.parse("2016-01-23"), "Enterprise", 100, objNoSMSPackDtl, 1, "// Payment not received."));
            hmClientDtl.put("092.001", createClientDetails("092.001", "Shree Sound Pvt Ltd", dFormat.parse("2016-01-08"), dFormat.parse("2018-12-20"), "Enterprise", 100, new clsSMSPackDtl("SanguineERP", "2017@SanguineERP@2017", "SANPOS", "Transactional"), 0, "//HO  Waters  renewed on 19-12-2017 to 1 year till 20-12-2018"));
            hmClientDtl.put("092.002", createClientDetails("092.002", "Shree Sound Pvt Ltd", dFormat.parse("2016-01-08"), dFormat.parse("2018-12-20"), "Enterprise", 100, new clsSMSPackDtl("SanguineERP", "2017@SanguineERP@2017", "SANPOS", "Transactional"), 0, " //Outlet 1  "));
            hmClientDtl.put("092.003", createClientDetails("092.003", "Shree Sound Pvt Ltd", dFormat.parse("2016-01-08"), dFormat.parse("2018-12-20"), "Enterprise", 100, new clsSMSPackDtl("SanguineERP", "2017@SanguineERP@2017", "SANPOS", "Transactional"), 0, " //Outlet 2  "));
            hmClientDtl.put("093.001", createClientDetails("093.001", "MAGDALENA", dFormat.parse("2016-01-12"), dFormat.parse("2018-09-16"), "Enterprise", 100, objNoSMSPackDtl, 1, "//release on 16-09-2017 for 1 year"));
            hmClientDtl.put("094.001", createClientDetails("094.001", "MUCH MORE CAKES", dFormat.parse("2016-01-12"), dFormat.parse("2017-01-12"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("095.001", createClientDetails("095.001", "PIZZA N U", dFormat.parse("2016-01-12"), dFormat.parse("2018-01-27"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 27-01-2017 for 1 year"));
            hmClientDtl.put("096.001", createClientDetails("096.001", "Red Consulting Pvt Ltd", dFormat.parse("2016-01-16"), dFormat.parse("2016-04-16"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("096.002", createClientDetails("096.002", "Red Consulting Pvt Ltd", dFormat.parse("2016-01-16"), dFormat.parse("2016-04-16"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("096.003", createClientDetails("096.003", "Red Consulting Pvt Ltd", dFormat.parse("2016-01-16"), dFormat.parse("2016-04-16"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("097.001", createClientDetails("097.001", "GADGIL HOTELS PVT LTD", dFormat.parse("2016-01-23"), dFormat.parse("2016-08-25"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("098.001", createClientDetails("098.001", "SAUTEED STORIES", dFormat.parse("2016-02-01"), dFormat.parse("2018-02-02"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 02-02-2017 for 1 year"));
            hmClientDtl.put("099.001", createClientDetails("099.001", "HOTEL CITI PRIDE", dFormat.parse("2016-02-01"), dFormat.parse("2018-02-01"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 07-02-2017 for 1 year"));
            hmClientDtl.put("100.001", createClientDetails("100.001", "KRISH FOODS HOSPITALITY", dFormat.parse("2016-02-06"), dFormat.parse("2018-02-07"), "Enterprise", 100, objNoSMSPackDtl, 1, "//(Marakesh)renewed on 07-02-2017 for 1 year"));
            hmClientDtl.put("101.001", createClientDetails("101.001", "HUCKLEBERRYS", dFormat.parse("2016-02-23"), dFormat.parse("2018-02-22"), "Enterprise", 100, objNoSMSPackDtl, 1, "//outlet 1 renewed on 22-02-2017"));
            hmClientDtl.put("101.002", createClientDetails("101.002", "HUCKLEBERRYS", dFormat.parse("2016-02-23"), dFormat.parse("2018-02-22"), "Enterprise", 100, objNoSMSPackDtl, 1, "//outlet 2 renewed on 22-02-2017"));
            hmClientDtl.put("102.001", createClientDetails("102.001", "RANADE BROTHERS", dFormat.parse("2016-02-24"), dFormat.parse("2017-02-24"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("103.001", createClientDetails("103.001", "BOMBAY HIGH", dFormat.parse("2016-03-05"), dFormat.parse("2018-08-23"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 23-08-2017 till  23-08-2018 "));
            hmClientDtl.put("104.001", createClientDetails("104.001", "Bakers Treat", dFormat.parse("2016-03-07"), dFormat.parse("2017-04-07"), "Enterprise", 100, objNoSMSPackDtl, 1, "//Bakers Treat// HO Unit"));
            hmClientDtl.put("104.002", createClientDetails("104.002", "Bakers Treat", dFormat.parse("2016-03-07"), dFormat.parse("2017-04-07"), "Enterprise", 100, objNoSMSPackDtl, 1, "//Bakers Treat// Outlet 1"));
            hmClientDtl.put("104.003", createClientDetails("104.003", "Bakers Treat", dFormat.parse("2016-03-07"), dFormat.parse("2017-04-07"), "Enterprise", 100, objNoSMSPackDtl, 1, "//Bakers Treat// Outlet 2"));
            hmClientDtl.put("104.004", createClientDetails("104.004", "Bakers Treat", dFormat.parse("2016-03-07"), dFormat.parse("2017-04-07"), "Enterprise", 100, objNoSMSPackDtl, 1, "//Bakers Treat// Outlet 3"));
            hmClientDtl.put("104.005", createClientDetails("104.005", "Bakers Treat", dFormat.parse("2016-03-07"), dFormat.parse("2017-04-07"), "Enterprise", 100, objNoSMSPackDtl, 1, "//Bakers Treat// Outlet 4"));
            hmClientDtl.put("105.001", createClientDetails("105.001", "KASHI BHOJNALAY AT KALHER BHIWANDI", dFormat.parse("2016-03-11"), dFormat.parse("2017-04-17"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("106.001", createClientDetails("106.001", "Independence Brewing Co. Pvt Ltd", dFormat.parse("2016-03-21"), dFormat.parse("2017-03-21"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("107.001", createClientDetails("107.001", "APT FOODS AND HOSPITALITY", dFormat.parse("2016-03-23"), dFormat.parse("2018-03-21"), "Enterprise", 100, objNoSMSPackDtl, 1, "//white rose cafe renewed on 21-03-2017 for 1 year"));
            hmClientDtl.put("108.001", createClientDetails("108.001", "221 B Baker Street", dFormat.parse("2016-03-07"), dFormat.parse("2017-03-07"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("109.001", createClientDetails("109.001", "Chemistry 101", dFormat.parse("2016-04-11"), dFormat.parse("2017-04-11"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("110.001", createClientDetails("110.001", "CAKE SHOP", dFormat.parse("2016-01-23"), dFormat.parse("2016-05-30"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            // MERWANS CONFECTIONERS PVT LTD
            hmClientDtl.put("111.001", createClientDetails("111.001", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2017-12-30"), "Enterprise", 100, objNoSMSPackDtl, 1, "// MERWANS CONFECTIONERS PVT LTD//HO // renewed on 04-01-2017 for demo"));
            hmClientDtl.put("111.002", createClientDetails("111.002", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2017-12-30"), "Enterprise", 100, objNoSMSPackDtl, 1, "// MERWANS CONFECTIONERS PVT LTD//Outlet 1"));
            hmClientDtl.put("111.003", createClientDetails("111.003", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2017-12-30"), "Enterprise", 100, objNoSMSPackDtl, 1, "// MERWANS CONFECTIONERS PVT LTD//Outlet 2"));
            hmClientDtl.put("111.004", createClientDetails("111.004", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2017-12-30"), "Enterprise", 100, objNoSMSPackDtl, 1, "// MERWANS CONFECTIONERS PVT LTD//Outlet 3"));
            hmClientDtl.put("111.005", createClientDetails("111.005", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2017-12-30"), "Enterprise", 100, objNoSMSPackDtl, 1, "// MERWANS CONFECTIONERS PVT LTD//Outlet 4"));
            hmClientDtl.put("111.006", createClientDetails("111.006", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2017-12-30"), "Enterprise", 100, objNoSMSPackDtl, 1, "// MERWANS CONFECTIONERS PVT LTD//Outlet 5"));
            hmClientDtl.put("111.007", createClientDetails("111.007", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2017-12-30"), "Enterprise", 100, objNoSMSPackDtl, 1, "// MERWANS CONFECTIONERS PVT LTD//Outlet 6"));
            hmClientDtl.put("111.008", createClientDetails("111.008", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2017-12-30"), "Enterprise", 100, objNoSMSPackDtl, 1, "// MERWANS CONFECTIONERS PVT LTD//Outlet 7"));
            hmClientDtl.put("111.009", createClientDetails("111.009", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2017-12-30"), "Enterprise", 100, objNoSMSPackDtl, 1, "// MERWANS CONFECTIONERS PVT LTD//Outlet 8"));
            hmClientDtl.put("111.010", createClientDetails("111.010", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2017-12-30"), "Enterprise", 100, objNoSMSPackDtl, 1, "// MERWANS CONFECTIONERS PVT LTD//Outlet 9"));
            hmClientDtl.put("111.011", createClientDetails("111.011", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2017-12-30"), "Enterprise", 100, objNoSMSPackDtl, 1, "// MERWANS CONFECTIONERS PVT LTD//Outlet 10"));
            // GBC MEGA MOTELS(Carnival Group)
            hmClientDtl.put("112.001", createClientDetails("112.001", "GBC MEGA MOTELS", dFormat.parse("2016-05-11"), dFormat.parse("2017-05-11"), "Enterprise", 100, objNoSMSPackDtl, 1, "// GBC MEGA MOTELS(Carnival Group)//HO"));
            hmClientDtl.put("112.002", createClientDetails("112.002", "GBC MEGA MOTELS", dFormat.parse("2016-05-11"), dFormat.parse("2017-05-11"), "Enterprise", 100, objNoSMSPackDtl, 1, "// GBC MEGA MOTELS(Carnival Group)//outlet 1"));
            hmClientDtl.put("112.003", createClientDetails("112.003", "GBC MEGA MOTELS", dFormat.parse("2016-05-11"), dFormat.parse("2017-05-11"), "Enterprise", 100, objNoSMSPackDtl, 1, "// GBC MEGA MOTELS(Carnival Group)//outlet 2"));
            //WOODFIRE HOSPITALITY BY OLENT
            hmClientDtl.put("113.001", createClientDetails("113.001", "WOODFIRE HOSPITALITY BY OLENT", dFormat.parse("2016-05-14"), dFormat.parse("2017-05-14"), "Enterprise", 100, objNoSMSPackDtl, 1, "//WOODFIRE HOSPITALITY BY OLENT"));
            //Dr. Asif Khan Wellness Clinic LLP
            hmClientDtl.put("114.001", createClientDetails("114.001", "Dr. Asif Khan Wellness Clinic LLP", dFormat.parse("2016-05-14"), dFormat.parse("2017-05-14"), "Enterprise", 100, objNoSMSPackDtl, 1, "//Dr. Asif Khan Wellness Clinic LLP//HO"));
            hmClientDtl.put("114.002", createClientDetails("114.002", "Dr. Asif Khan Wellness Clinic LLP", dFormat.parse("2016-05-14"), dFormat.parse("2017-05-14"), "Enterprise", 100, objNoSMSPackDtl, 1, "//Dr. Asif Khan Wellness Clinic LLP//outlet 1"));
            //AHAAN THAI FOOD RESTAURANT
            hmClientDtl.put("115.001", createClientDetails("115.001", "AHAAN THAI FOOD RESTAURANT", dFormat.parse("2016-05-19"), dFormat.parse("2017-05-19"), "Enterprise", 100, objNoSMSPackDtl, 1, " //AHAAN THAI FOOD RESTAURANT"));
            //THE WOODSHED INN
            hmClientDtl.put("116.001", createClientDetails("116.001", "THE WOODSHED INN", dFormat.parse("2016-05-31"), dFormat.parse("2018-05-31"), "Enterprise", 100, objNoSMSPackDtl, 1, "//THE WOODSHED INN//renewed on 02-06-2017 for 1 year"));
            hmClientDtl.put("117.001", createClientDetails("117.001", "THE PREM'S HOTEL", dFormat.parse("2016-06-30"), dFormat.parse("2018-07-31"), "Enterprise", 100, objNoSMSPackDtl, 50, "//renwed on 19-07-2017 for 1 year"));
            hmClientDtl.put("118.001", createClientDetails("118.001", "PICCADILLY RESTAURANT", dFormat.parse("2016-07-04"), dFormat.parse("2018-12-05"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 05-12-2017 for 1 year till 05-12-2018"));
            hmClientDtl.put("119.001", createClientDetails("119.001", "MALPANI", dFormat.parse("2016-08-10"), dFormat.parse("2017-11-30"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed for DEMO on 01-11-2017"));
            hmClientDtl.put("120.001", createClientDetails("120.001", "Occasions India", dFormat.parse("2016-08-31"), dFormat.parse("2017-08-31"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("121.001", createClientDetails("121.001", "MEHTA HOSPITALITY", dFormat.parse("2016-09-21"), dFormat.parse("2016-10-11"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("122.001", createClientDetails("122.001", "FITNESS FUEL", dFormat.parse("2016-09-29"), dFormat.parse("2017-09-29"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("123.001", createClientDetails("123.001", "SARKAR COLLECTION", dFormat.parse("2016-10-13"), dFormat.parse("2016-11-12"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            hmClientDtl.put("124.001", createClientDetails("124.001", "ATITHYA DINING LLP", dFormat.parse("2016-10-18"), dFormat.parse("2018-10-31"), "Enterprise", 100, new clsSMSPackDtl("TeddyBoy", "2017@TeddyBoy@2017", "SANPOS", "Transactional"), 10, "//renewed to 10 Tabs, Teddy Boy renewed on 05-12-2017 till 31-10-2018"));
            //db old license
            hmClientDtl.put("125.001", createClientDetails("125.001", "DARYUSH", dFormat.parse("2016-10-22"), dFormat.parse("2018-11-11"), "Enterprise", 100, objNoSMSPackDtl, 0, "//db renamed to DARYUSH   renewed on 11-11-2017 for 1 year till 11-11-2018"));
            hmClientDtl.put("126.001", createClientDetails("126.001", "BAKERY", dFormat.parse("2016-10-22"), dFormat.parse("2016-11-22"), "Enterprise", 100, objNoSMSPackDtl, 1, ""));
            //Craft Bar
            hmClientDtl.put("127.001", createClientDetails("127.001", "Cumin Food & Beverage Pvt Ltd", dFormat.parse("2016-10-26"), dFormat.parse("2018-11-11"), "Enterprise", 100, objNoSMSPackDtl, 1, "//Craft Bar//Craft Bar released on 26-10-2016 renewed on 11-11-2017 for 1 year till 11-11-2018"));
            hmClientDtl.put("128.001", createClientDetails("128.001", "Yellow Cup", dFormat.parse("2016-10-26"), dFormat.parse("2018-10-26"), "Enterprise", 100, objNoSMSPackDtl, 1, "//renewed on 10-11-2017for 1 year till 2018-10-26"));
            hmClientDtl.put("129.001", createClientDetails("129.001", "GRILL 108 STREET", dFormat.parse("2016-11-01"), dFormat.parse("2017-10-31"), "Enterprise", 100, objNoSMSPackDtl, 1, "//released on 01-11-2016"));
            hmClientDtl.put("130.001", createClientDetails("130.001", "WORLD STREET", dFormat.parse("2016-11-15"), dFormat.parse("2017-11-15"), "Enterprise", 100, objNoSMSPackDtl, 1, "//released on 15-11-2016"));
            hmClientDtl.put("131.001", createClientDetails("131.001", "KETTLE AND KEG", dFormat.parse("2016-11-23"), dFormat.parse("2018-11-23"), "Enterprise", 100, objNoSMSPackDtl, 1, "//released on 23-11-2016 renamed on 23-12-2016(KETTLE AND KEG CAFE) ,renewed on 13-11-2017 for 1 year till 23-11-2018        "));
            hmClientDtl.put("132.001", createClientDetails("132.001", "JBDD Hospitality LLP", dFormat.parse("2016-12-05"), dFormat.parse("2018-03-31"), "Enterprise", 100, objNoSMSPackDtl, 5, "//(Delhi Darbar)renewed on  12-09-2017  HO"));
            hmClientDtl.put("132.002", createClientDetails("132.002", "JBDD Hospitality LLP", dFormat.parse("2016-12-05"), dFormat.parse("2018-03-31"), "Enterprise", 100, objNoSMSPackDtl, 5, "//(Delhi Darbar)renewed on  12-09-2017 Outlet 1"));
            hmClientDtl.put("132.003", createClientDetails("132.003", "JBDD Hospitality LLP", dFormat.parse("2016-12-05"), dFormat.parse("2018-03-31"), "Enterprise", 100, objNoSMSPackDtl, 6, "//(Delhi Darbar)renewed on  12-09-2017 Outlet 2"));
            hmClientDtl.put("132.004", createClientDetails("132.004", "JBDD Hospitality LLP", dFormat.parse("2016-12-05"), dFormat.parse("2018-03-31"), "Enterprise", 100, objNoSMSPackDtl, 5, "//(Delhi Darbar)renewed on  12-09-2017 Outlet 3"));
            hmClientDtl.put("132.005", createClientDetails("132.005", "JBDD Hospitality LLP", dFormat.parse("2016-12-05"), dFormat.parse("2018-03-31"), "Enterprise", 100, objNoSMSPackDtl, 5, "//(Delhi Darbar)renewed on  12-09-2017 Outlet 4"));
            hmClientDtl.put("133.001", createClientDetails("133.001", "CAFE EDESIA", dFormat.parse("2016-12-10"), dFormat.parse("2018-12-10"), "Enterprise", 100, objNoSMSPackDtl, 1, "//(MUMBAI)released on 10-12-2016 for 1 year,renewed on 14-12-2017 for 1 year till 10-12-2018            "));
            hmClientDtl.put("134.001", createClientDetails("134.001", "Aman Hospitality", dFormat.parse("2016-12-28"), dFormat.parse("2018-12-28"), "Enterprise", 100, objNoSMSPackDtl, 1, "//released on 19-12-2016 for 1 year till 28-12-2018           "));
            hmClientDtl.put("135.001", createClientDetails("135.001", "QBAA TERRACE", dFormat.parse("2016-12-29"), dFormat.parse("2017-01-31"), "Enterprise", 100, objNoSMSPackDtl, 1, "//released on 29-12-2016 for 1 month "));
            hmClientDtl.put("136.001", createClientDetails("136.001", "KINKI", dFormat.parse("2017-01-05"), dFormat.parse("2018-01-05"), "Enterprise", 100, objNoSMSPackDtl, 6, "//released on 05-01-2017 for 1 year     "));
            hmClientDtl.put("137.001", createClientDetails("137.001", "SWEY RESTAURANTS", dFormat.parse("2017-01-14"), dFormat.parse("2018-01-15"), "Enterprise", 100, objNoSMSPackDtl, 0, "//released on 14-01-2017 for 1 year"));
            hmClientDtl.put("138.001", createClientDetails("138.001", "Arabian Heritage Pvt Ltd", dFormat.parse("2017-01-14"), dFormat.parse("2018-01-15"), "Enterprise", 100, objNoSMSPackDtl, 0, "//released on 14-01-2017 for 1 year"));
            hmClientDtl.put("139.001", createClientDetails("139.001", "MELTING MORSEL", dFormat.parse("2017-01-25"), dFormat.parse("2018-01-26"), "Enterprise", 100, objNoSMSPackDtl, 0, "//released on 25-01-2017 for 1 year"));
            hmClientDtl.put("140.001", createClientDetails("140.001", "THAAL FOOD CONCEPTS PVT LTD", dFormat.parse("2017-01-25"), dFormat.parse("2018-01-26"), "Enterprise", 100, objNoSMSPackDtl, 0, "//released on 25-01-2017 for 1 year  "));
            //sanguine
            hmClientDtl.put("141.001", createClientDetails("141.001", "SANGUINE SOFTWARE SOLUTIONS PVT LTD", dFormat.parse("2017-01-30"), dFormat.parse("2099-01-30"), "Enterprise", 100, new clsSMSPackDtl("SanguineERP", "2017@SanguineERP@2017", "SANPOS", "Transactional"), 1000, "  //sanguine //released on 30-01-2017 for 1 year for sanguine  ,100 SPOS,1K APOS,SanguineERP                 "));
            hmClientDtl.put("142.001", createClientDetails("142.001", "SHIVSHAKTI SKYDINE", dFormat.parse("2017-02-04"), dFormat.parse("2018-02-04"), "Enterprise", 100, objNoSMSPackDtl, 0, "//released on 04-02-2017 for 1 year for Shivshakti Skydine  "));
            hmClientDtl.put("143.001", createClientDetails("143.001", "THE BIG EGG CAFE", dFormat.parse("2017-02-04"), dFormat.parse("2018-02-04"), "Enterprise", 100, objNoSMSPackDtl, 0, "//released on 04-02-2017 for 1 year for BIG EGG CAFE"));
            hmClientDtl.put("144.001", createClientDetails("144.001", "DEMO HO", dFormat.parse("2017-02-07"), dFormat.parse("2018-03-07"), "Enterprise", 100, objNoSMSPackDtl, 0, "//HO released on 07-02-2017 for 1 month for HO            "));
            hmClientDtl.put("144.002", createClientDetails("144.002", "DEMO OUTLET", dFormat.parse("2017-02-07"), dFormat.parse("2018-03-07"), "Enterprise", 100, objNoSMSPackDtl, 0, "//outlet 1 released on 07-02-2017 for 1 month for Outlet            "));
            hmClientDtl.put("145.001", createClientDetails("145.001", "YASH VENKATESH VENTURES", dFormat.parse("2017-02-08"), dFormat.parse("2018-02-08"), "Enterprise", 100, objNoSMSPackDtl, 0, "//release on 08-02-2017 for 1 year"));
            hmClientDtl.put("146.001", createClientDetails("146.001", "THE DARK ROAST", dFormat.parse("2017-02-08"), dFormat.parse("2018-02-08"), "Enterprise", 10, objNoSMSPackDtl, 0, "//release on 08-02-2017 for 1 year          "));
            hmClientDtl.put("147.001", createClientDetails("147.001", "Above and Beyond Hospitality LLP", dFormat.parse("2017-02-14"), dFormat.parse("2018-02-14"), "Enterprise", 100, objNoSMSPackDtl, 0, "//release on 14-02-2017 for 1 year"));
            hmClientDtl.put("148.001", createClientDetails("148.001", "MURPHIES", dFormat.parse("2017-02-16"), dFormat.parse("2018-02-16"), "Enterprise", 100, objNoSMSPackDtl, 1, "//release on 16-02-2017 for 1 year"));
            //GREEN BOX 10K SMS pack activated on 30-10-2017
            hmClientDtl.put("149.001", createClientDetails("149.001", "GREEN BOX VENTURES PVT LTD", dFormat.parse("2017-02-17"), dFormat.parse("2018-02-17"), "Enterprise", 10, new clsSMSPackDtl("GREENBOX", "2017@GREENBOX@2017", "SANPOS", "Transactional"), 0, "// HO (GREEN BOX VENTURES LLP RENAMED TO GREEN BOX VENTURES PVT LTD) release on 17-02-2017 for 1 year "));
            hmClientDtl.put("149.002", createClientDetails("149.002", "GREEN BOX VENTURES PVT LTD", dFormat.parse("2017-02-17"), dFormat.parse("2018-02-17"), "Enterprise", 10, new clsSMSPackDtl("GREENBOX", "2017@GREENBOX@2017", "SANPOS", "Transactional"), 0, "// Outlet 1 release on 17-02-2017 for 1 year "));
            hmClientDtl.put("149.003", createClientDetails("149.003", "GREEN BOX VENTURES PVT LTD", dFormat.parse("2017-02-17"), dFormat.parse("2018-02-17"), "Enterprise", 10, new clsSMSPackDtl("GREENBOX", "2017@GREENBOX@2017", "SANPOS", "Transactional"), 0, "// Outlet 2 release on 17-02-2017 for 1 year "));
            hmClientDtl.put("149.004", createClientDetails("149.004", "GREEN BOX VENTURES PVT LTD", dFormat.parse("2017-02-17"), dFormat.parse("2018-02-17"), "Enterprise", 10, new clsSMSPackDtl("GREENBOX", "2017@GREENBOX@2017", "SANPOS", "Transactional"), 0, "// Outlet 3 release on 17-02-2017 for 1 year "));
            hmClientDtl.put("149.005", createClientDetails("149.005", "GREEN BOX VENTURES PVT LTD", dFormat.parse("2017-02-17"), dFormat.parse("2018-02-17"), "Enterprise", 5, new clsSMSPackDtl("GREENBOX", "2017@GREENBOX@2017", "SANPOS", "Transactional"), 0, "// Outlet 4 release on 05-04-2017 for 1 year for Enterprise for 2 machines,renewed 3 more terminal"));
            hmClientDtl.put("150.001", createClientDetails("150.001", "AHB HOSPITALITY LLP", dFormat.parse("2017-02-18"), dFormat.parse("2018-02-18"), "Enterprise", 100, objNoSMSPackDtl, 0, "//  release on 18-02-2017 for 1 year"));
            hmClientDtl.put("151.001", createClientDetails("151.001", "Bottle Street Restaurant & Lounge", dFormat.parse("2017-02-23"), dFormat.parse("2018-02-23"), "Enterprise", 100, new clsSMSPackDtl("BottleStreet", "2017@BottleStreet@2017", "SANPOS", "Transactional"), 0, "//renewed on 14-09-2017 for 1 year till 2018-02-23 ,1000 SMS Pack release on 09-08-2017"));
            hmClientDtl.put("152.001", createClientDetails("152.001", "RAJARAJESHWARI", dFormat.parse("2017-03-04"), dFormat.parse("2018-03-04"), "Enterprise", 100, objNoSMSPackDtl, 0, "//(Renamed from RAJ RAJESHWARI)  release on 04-03-2017 for 1 year "));
            hmClientDtl.put("153.001", createClientDetails("153.001", "MALGUDI FOODS PVT LTD", dFormat.parse("2017-04-07"), dFormat.parse("2018-03-31"), "Enterprise", 10, objNoSMSPackDtl, 0, "//(Banana Leaf)Outlet 1  renewed on 03-07-2017  till 31-03-2018"));
            hmClientDtl.put("153.002", createClientDetails("153.002", "MALGUDI FOODS PVT LTD", dFormat.parse("2017-04-07"), dFormat.parse("2018-03-31"), "Enterprise", 10, objNoSMSPackDtl, 0, "//Outlet 2  renewed on 03-07-2017  till 31-03-2018"));
            hmClientDtl.put("153.003", createClientDetails("153.003", "MALGUDI FOODS PVT LTD", dFormat.parse("2017-04-07"), dFormat.parse("2018-03-31"), "Enterprise", 10, objNoSMSPackDtl, 0, "//Outlet 3  renewed on 03-07-2017  till 31-03-2018"));
            hmClientDtl.put("153.004", createClientDetails("153.004", "MALGUDI FOODS PVT LTD", dFormat.parse("2017-04-07"), dFormat.parse("2018-03-31"), "Enterprise", 10, objNoSMSPackDtl, 0, "//Outlet 4  renewed on 03-07-2017  till 31-03-2018"));
            hmClientDtl.put("153.005", createClientDetails("153.005", "MALGUDI FOODS PVT LTD", dFormat.parse("2017-04-07"), dFormat.parse("2018-03-31"), "Enterprise", 10, objNoSMSPackDtl, 0, "//Outlet 5  renewed on 03-07-2017  till 31-03-2018"));
            hmClientDtl.put("153.006", createClientDetails("153.006", "MALGUDI FOODS PVT LTD", dFormat.parse("2017-04-07"), dFormat.parse("2018-03-31"), "Enterprise", 10, objNoSMSPackDtl, 0, "//Outlet 6  renewed on 03-07-2017  till 31-03-2018"));
            hmClientDtl.put("153.007", createClientDetails("153.007", "MALGUDI FOODS PVT LTD", dFormat.parse("2017-04-07"), dFormat.parse("2018-03-31"), "Enterprise", 10, objNoSMSPackDtl, 0, "//Outlet 7  renewed on 03-07-2017  till 31-03-2018"));
            hmClientDtl.put("153.008", createClientDetails("153.008", "MALGUDI FOODS PVT LTD", dFormat.parse("2017-04-07"), dFormat.parse("2018-03-31"), "Enterprise", 10, objNoSMSPackDtl, 0, "//Outlet 8  renewed on 03-07-2017  till 31-03-2018"));
            hmClientDtl.put("153.009", createClientDetails("153.009", "MALGUDI FOODS PVT LTD", dFormat.parse("2017-04-07"), dFormat.parse("2018-03-31"), "Enterprise", 10, objNoSMSPackDtl, 0, "//Outlet 9  renewed on 03-07-2017  till 31-03-2018"));
            hmClientDtl.put("154.001", createClientDetails("154.001", "KP RESTAURANTS", dFormat.parse("2017-03-11"), dFormat.parse("2018-05-08"), "Enterprise", 10, objNoSMSPackDtl, 2, "//released on 08-05-2017 for 1 year  renewed on 08-05-2017 for 1 year "));
            hmClientDtl.put("155.001", createClientDetails("155.001", "CAVALLI THE LOUNGE", dFormat.parse("2017-03-24"), dFormat.parse("2018-03-24"), "Enterprise", 5, objNoSMSPackDtl, 7, "//renewed on 07-07-2017 for 1 year  Enterprise for 5 machines ,1 terminal added"));
            hmClientDtl.put("156.001", createClientDetails("156.001", "Raveki Hospitality", dFormat.parse("2017-03-27"), dFormat.parse("2018-03-27"), "Enterprise", 1, objNoSMSPackDtl, 0, "//Outlet 1 released on 27-03-2017 for 1 year  Enterprise for 1 machinesv"));
            hmClientDtl.put("156.002", createClientDetails("156.002", "Raveki Hospitality", dFormat.parse("2017-03-27"), dFormat.parse("2018-03-27"), "Enterprise", 1, objNoSMSPackDtl, 0, "//Outlet 2 released on 27-03-2017 for 1 year  Enterprise for 1 machines"));
            hmClientDtl.put("156.003", createClientDetails("156.003", "Raveki Hospitality", dFormat.parse("2017-03-27"), dFormat.parse("2018-03-27"), "Enterprise", 1, objNoSMSPackDtl, 0, "//Outlet 3 released on 27-03-2017 for 1 year  Enterprise for 1 machines"));
            //"SPICE & FLAVOURS RESTAURANT" renamed to "H & S Enterprise", H & S Enterprise renamed to Shah & Sanghvi Hospitality LLP
            hmClientDtl.put("157.001", createClientDetails("157.001", "Shah & Sanghvi Hospitality LLP", dFormat.parse("2017-03-28"), dFormat.parse("2018-03-28"), "Enterprise", 3, objNoSMSPackDtl, 3, "//SPICE & FLAVOURS RESTAURANT renamed to H & S Enterprise, H & S Enterprise renamed to Shah & Sanghvi Hospitality LLP//released on 28-03-2017 for 1 year  Enterprise for 3 machines             "));
            hmClientDtl.put("158.001", createClientDetails("158.001", "MALTI FOODS", dFormat.parse("2017-04-01"), dFormat.parse("2018-04-01"), "Enterprise", 1, objNoSMSPackDtl, 0, "//released on 01-04-2017 for 1 year  Enterprise for 1 machines"));
            hmClientDtl.put("159.001", createClientDetails("159.001", "BIG PLATE CUISINES LLP", dFormat.parse("2017-04-14"), dFormat.parse("2018-04-14"), "Enterprise", 5, objNoSMSPackDtl, 1, "//released on 14-04-2017 for 1 year  Enterprise for 5 machines"));
            hmClientDtl.put("160.001", createClientDetails("160.001", "POTHEADS FOOD PVT. LTD.", dFormat.parse("2017-04-17"), dFormat.parse("2018-04-17"), "Enterprise", 2, objNoSMSPackDtl, 0, "//released on 17-04-2017 for 1 year  Enterprise for 2 machines"));
            hmClientDtl.put("161.001", createClientDetails("161.001", "HOTEL GRAND CENTRAL", dFormat.parse("2017-04-18"), dFormat.parse("2018-01-20"), "Enterprise", 6, objNoSMSPackDtl, 6, "//renewed on 19-12-2017 for 1 months till 20-01-2018 Enterprise for 6 machines,6 APOS"));
            //"071.001", "La Bouchee dOr" First Outelet and this ia a Second  Outlet license
            hmClientDtl.put("162.001", createClientDetails("162.001", "SWEET LAVENDER FOODS LLP", dFormat.parse("2017-04-20"), dFormat.parse("2018-10-25"), "Enterprise", 1, objNoSMSPackDtl, 0, "//(071.001, La Bouchee dOr first licence ) released on 20-04-2017 for 1 year  Enterprise for 1 machines"));
            hmClientDtl.put("163.001", createClientDetails("163.001", "KADAR KHAN'S SHEESHA", dFormat.parse("2017-04-22"), dFormat.parse("2018-04-22"), "Enterprise", 100, new clsSMSPackDtl("Sheesha", "2017@Sheesha@2017", "SANPOS", "Transactional"), 8, "// renewed on 02-11-2017 for 1 year Enterprise for 2 machines, SMS pack for 1K SMS"));
            hmClientDtl.put("164.001", createClientDetails("164.001", "GLOBAL FOODS & BEVERAGES PVT. LTD.", dFormat.parse("2017-04-26"), dFormat.parse("2018-04-26"), "Enterprise", 1, objNoSMSPackDtl, 6, "// released on 26-04-2017 for 1 year  Enterprise for 1 terminal"));
            //cantos
            hmClientDtl.put("165.001", createClientDetails("165.001", "BCMA CANTO", dFormat.parse("2017-04-26"), dFormat.parse("2018-04-26"), "Enterprise", 7, objNoSMSPackDtl, 1, "  //cantos // renewed on 24-05-2017 for 1 year from start date  Enterprise for 3 terminals on desktop and 5 terminals on Windows TAB calcel 1 Win Lice n convert it into 1 APOS"));
            hmClientDtl.put("166.001", createClientDetails("166.001", "SUNNYS WORLD", dFormat.parse("2017-04-29"), dFormat.parse("2017-06-29"), "Enterprise", 100, objNoSMSPackDtl, 6, "// released on 29-04-2017 for 2 month  Enterprise for 3 terminals on desktop "));
            hmClientDtl.put("167.001", createClientDetails("167.001", "LAJMI RESTAURANT", dFormat.parse("2017-05-02"), dFormat.parse("2018-05-01"), "Enterprise", 2, objNoSMSPackDtl, 3, "// released on 02-05-2017 for 1 Year  Enterprise for 2 terminals on desktop "));
            hmClientDtl.put("168.001", createClientDetails("168.001", "LAJMI RESTAURANT N LOUNGE", dFormat.parse("2017-05-03"), dFormat.parse("2018-05-02"), "Enterprise", 2, objNoSMSPackDtl, 3, "// released on 03-05-2017 for 1 Year  Enterprise for 2 terminals on desktop "));
            hmClientDtl.put("169.001", createClientDetails("169.001", "A R HOSPITALITY", dFormat.parse("2017-05-04"), dFormat.parse("2018-05-02"), "Enterprise", 1, objNoSMSPackDtl, 0, "// released on 04-05-2017 for 1 Year  Enterprise for 1 terminals on desktop "));
            hmClientDtl.put("170.001", createClientDetails("170.001", "A1 HEIGHTS N HOSPITALITY PVT LTD", dFormat.parse("2017-05-04"), dFormat.parse("2018-05-02"), "Enterprise", 2, objNoSMSPackDtl, 6, " // released on 04-05-2017 for 1 Year  Enterprise for 2 terminals on desktop             "));
            hmClientDtl.put("171.001", createClientDetails("171.001", "CHINA GRILL-PIMPRI", dFormat.parse("2017-05-24"), dFormat.parse("2018-05-24"), "Enterprise", 100, new clsSMSPackDtl("ChinaGrill", "2017@ChinaGrill@2017", "SANPOS", "Transactional"), 2, "// renewed on 17-07-20147 for 1 year  Enterprise for 2 terminals on desktop ...2 days exceeded as per swapnali request and add 1 terminal, 1K SMS Pack activated"));
            hmClientDtl.put("172.001", createClientDetails("172.001", "DIOS HOTEL LLP", dFormat.parse("2017-05-26"), dFormat.parse("2018-05-25"), "Enterprise", 6, objNoSMSPackDtl, 2, "// renewed on 22-06-2017 for 1 year  Enterprise for 6 terminals on desktop "));
            hmClientDtl.put("173.001", createClientDetails("173.001", "Le Flamington", dFormat.parse("2017-05-29"), dFormat.parse("2018-05-29"), "Enterprise", 1, objNoSMSPackDtl, 0, "// released on 29-05-2017 for 1 year  Enterprise for 1 terminals on desktop     "));
            //Krimpson
            hmClientDtl.put("174.001", createClientDetails("174.001", "KRD Eateries Pvt Ltd", dFormat.parse("2017-08-02"), dFormat.parse("2017-11-30"), "Enterprise", 100, objNoSMSPackDtl, 0, "//Krimpson// //outlet 1 Wanwari 174.001 Krimpson(KRD Caters)  renewed  on 14-11-2017 for  till 30-11-2017 ,No SMS Pack "));
            hmClientDtl.put("174.002", createClientDetails("174.002", "KRD Eateries Pvt Ltd", dFormat.parse("2017-08-02"), dFormat.parse("2017-12-28"), "Enterprise", 100, objNoSMSPackDtl, 0, "//Krimpson// //outlet 2 Kharadi 174.002 Krimpson(KRD Caters)  renewed  on 23-12-2017 for  till 28-12-2017 ,No SMS Pack "));
            hmClientDtl.put("175.001", createClientDetails("175.001", "Tjs brew works", dFormat.parse("2017-06-06"), dFormat.parse("2018-06-06"), "Enterprise", 100, new clsSMSPackDtl("TJsBrew", "2017@TJsBrew@2017", "SANPOS", "Transactional"), 7, " // released on 06-06-2017 for 1 year ,SMS Pack for 1K SMS,  Enterprise for 2 terminals on desktop renewed 2 terminals  to 7"));
            hmClientDtl.put("176.001", createClientDetails("176.001", "FRANCOPHONE EUROPEAN BISTRO & PATISSERIE", dFormat.parse("2017-06-07"), dFormat.parse("2018-06-06"), "Enterprise", 1, objNoSMSPackDtl, 4, "// released on 07-06-2017 for 1 year  Enterprise for 1 terminals on desktop "));
            hmClientDtl.put("177.001", createClientDetails("177.001", "RAYYAN", dFormat.parse("2017-06-08"), dFormat.parse("2018-06-07"), "Enterprise", 2, objNoSMSPackDtl, 2, "// renewed on 28-11-2017 for 1 year till 06-07-2018  Enterprise for 2 terminals on desktop ,2 APOS"));
            hmClientDtl.put("178.001", createClientDetails("178.001", "UNWIND", dFormat.parse("2017-06-08"), dFormat.parse("2018-06-07"), "Enterprise", 100, objNoSMSPackDtl, 13, "// renewed on 06-07-2017 for 1 year  Enterprise for 6 terminals on desktop "));
            hmClientDtl.put("179.001", createClientDetails("179.001", "R & R HOSPITALITY", dFormat.parse("2017-06-14"), dFormat.parse("2018-06-14"), "Enterprise", 1, objNoSMSPackDtl, 0, "// released on 14-06-2017 renewed on 21-07-2017 for 1 year  Enterprise for 1 terminals on desktop No SMS Pack"));
            hmClientDtl.put("180.001", createClientDetails("180.001", "AVIKA HOTELS PVT LTD", dFormat.parse("2017-06-15"), dFormat.parse("2018-07-14"), "Enterprise", 3, objNoSMSPackDtl, 1, "// released on 15-06-2017 for 1 year  Enterprise for 3 terminals on desktop No SMS Pack renamed AVIKA GROUP LTD to AVIKA GROUP PVT LTD"));
            hmClientDtl.put("181.001", createClientDetails("181.001", "RMV COMMUNICATION PVT LTD", dFormat.parse("2017-06-20"), dFormat.parse("2017-07-20"), "Enterprise", 1, objNoSMSPackDtl, 2, "// released on 20-06-2017 for 1 month  Enterprise for 1 terminals on desktop No SMS Pack"));
            hmClientDtl.put("182.001", createClientDetails("182.001", "OOZO", dFormat.parse("2017-06-21"), dFormat.parse("2018-06-21"), "Enterprise", 4, objNoSMSPackDtl, 12, "// released on 21-06-2017 for 1 month  renewed on 09-08-2017 for 1 year Enterprise for 4 terminals on desktop No SMS Pack"));
            hmClientDtl.put("183.001", createClientDetails("183.001", "VD HOSPITALITY LLP", dFormat.parse("2017-06-30"), dFormat.parse("2018-06-30"), "Enterprise", 2, objNoSMSPackDtl, 5, "// released on 30-06-2017 for 1 year  Enterprise for 2 terminals on desktop No SMS Packs"));
            //"184.001", "BALAJI TRADERS PVT LTD" only MMS License
            hmClientDtl.put("185.001", createClientDetails("185.001", "MOHANLAL S MITHAIWALA", dFormat.parse("2017-07-19"), dFormat.parse("2018-08-19"), "Enterprise", 2, objNoSMSPackDtl, 0, " //(first license 072.001, MOHANLAL S MITHAIWALA) released on 19-07-2017 for 1 month,renewed on 21-08-2017 for 1 year  Enterprise for 2 terminals on desktop No SMS Pack"));
            hmClientDtl.put("186.001", createClientDetails("186.001", "JORDAN HOSPITALITY", dFormat.parse("2017-07-19"), dFormat.parse("2017-08-19"), "Enterprise", 1, objNoSMSPackDtl, 2, "// released on 19-07-2017 for 1 month  Enterprise for 1 terminals on desktop and APOS for 2 TABs  No SMS Pack"));
            hmClientDtl.put("187.001", createClientDetails("187.001", "ROSHAN BAKERY", dFormat.parse("2017-07-19"), dFormat.parse("2018-07-19"), "Enterprise", 2, objNoSMSPackDtl, 0, "// released on 19-07-2017 for 1 month,renewed on 05-10-2017 for 1 year  Enterprise for 2 terminals on desktop No SMS Pack"));
            hmClientDtl.put("188.001", createClientDetails("188.001", "MUMBAI CAFE", dFormat.parse("2017-07-20"), dFormat.parse("2018-07-20"), "Enterprise", 2, objNoSMSPackDtl, 2, "// released on 20-07-2017 for 1 year  Enterprise for 2 terminals on desktop  and APOA for 2 TAB No SMS Pack"));
            hmClientDtl.put("189.001", createClientDetails("189.001", "CLASSIC BANGLES", dFormat.parse("2017-07-25"), dFormat.parse("2018-07-25"), "Enterprise", 8, objNoSMSPackDtl, 0, "// released on 25-07-2017 for 1 year,  Enterprise for 8 terminals on desktop , No SMS Pack"));
            hmClientDtl.put("190.001", createClientDetails("190.001", "SQUARE ONE HOSPITALITY LLP", dFormat.parse("2017-08-03"), dFormat.parse("2017-10-05"), "Enterprise", 2, objNoSMSPackDtl, 1, "// released on 05-09-2017 for 1 month,  Enterprise for 2 terminals on desktop And 1 for APOS, No SMS Pack"));
            hmClientDtl.put("191.001", createClientDetails("191.001", "TOYAM INDUSTRIES LTD", dFormat.parse("2017-08-05"), dFormat.parse("2017-09-05"), "Enterprise", 3, objNoSMSPackDtl, 3, "// released on 05-08-2017 for 1 month,  Enterprise for 3 terminals on desktop And 4 for APOS, No SMS Pack            "));
            //"192.001" "precision food work"
            hmClientDtl.put("193.001", createClientDetails("193.001", "NAPLES STAPLES", dFormat.parse("2017-09-04"), dFormat.parse("2018-09-04"), "Enterprise", 100, objNoSMSPackDtl, 0, "//192.001 precision food work // released on 04-09-2017 for 1 year,  Enterprise for 2 terminals on desktop"));
            //Swig 2 Prems Restaurant SWIG 2.0 RENAMED TO SWIG
            hmClientDtl.put("194.001", createClientDetails("194.001", "SWIG", dFormat.parse("2017-09-06"), dFormat.parse("2018-09-06"), "Enterprise", 100, objNoSMSPackDtl, 5, "//Swig 2 Prems Restaurant SWIG 2.0 RENAMED TO SWIG// released on 06-09-2017 for 1 month renewed on 06-10-2017 for 1 year,  Enterprise for 2 terminals on desktop ,SWIG 2.0 RENAMED TO SWIG"));
            //"COO COUNTRY OF ORIGIN" for 3 locations renamed to "DESIGNSCAPE & COUNTRY OF ORIGIN" renamed to "COUNTRY OF ORIGIN"
            hmClientDtl.put("195.001", createClientDetails("195.001", "COUNTRY OF ORIGIN", dFormat.parse("2017-09-07"), dFormat.parse("2018-09-06"), "Enterprise", 5, objNoSMSPackDtl, 0, "//COO COUNTRY OF ORIGIN for 3 locations renamed to DESIGNSCAPE & COUNTRY OF ORIGIN renamed to COUNTRY OF ORIGIN //(Mumbai) released on 07-09-2017 for 1 year,  Enterprise for 5 terminals added later on desktop and 0 APOS cancel later,NO SMS Pack"));
            hmClientDtl.put("195.002", createClientDetails("195.002", "COUNTRY OF ORIGIN", dFormat.parse("2017-09-07"), dFormat.parse("2018-09-06"), "Enterprise", 2, objNoSMSPackDtl, 0, "//COO COUNTRY OF ORIGIN for 3 locations renamed to DESIGNSCAPE & COUNTRY OF ORIGIN renamed to COUNTRY OF ORIGIN//(Mumbai) released on 07-09-2017 for 1 year,  Enterprise for 2 terminals on desktop and 0 APOS,NO SMS Pack"));
            hmClientDtl.put("195.003", createClientDetails("195.003", "COUNTRY OF ORIGIN", dFormat.parse("2017-09-07"), dFormat.parse("2018-09-06"), "Enterprise", 2, objNoSMSPackDtl, 0, "//COO COUNTRY OF ORIGIN for 3 locations renamed to DESIGNSCAPE & COUNTRY OF ORIGIN renamed to COUNTRY OF ORIGIN//(Mumbai) released on 07-09-2017 for 1 year,  Enterprise for 2 terminals on desktop and 0 APOS,NO SMS Pack"));
            //CAFECHEATDAY renamed to "CHEATDAY CAFE"
            hmClientDtl.put("196.001", createClientDetails("196.001", "CHEATDAY CAFE", dFormat.parse("2017-09-15"), dFormat.parse("2017-12-31"), "Enterprise", 100, objNoSMSPackDtl, 0, "//CAFECHEATDAY renamed to CHEATDAY CAFE //(PUNE NIBM) released on 15-09-2017 for 1 month renewed on 20-12-2017 till 31-12-2017,  Enterprise for x terminals on desktop and 0 APOS,NO SMS Pack"));
            //renamed "REZBERRY RHINOCERES" to JUHU HOTEL PVT LTD.,  renamed "REZBERRY RHINOCERES" to "Juhu Hotel Pvt Ltd" 
            hmClientDtl.put("197.001", createClientDetails("197.001", "Juhu Hotel Pvt Ltd", dFormat.parse("2017-09-20"), dFormat.parse("2018-09-20"), "Enterprise", 8, objNoSMSPackDtl, 3, "//renamed REZBERRY RHINOCERES to Juhu Hotel Pvt Ltd //(MUMBAI)JUHU HOTEL PVT LTD. renewed on 22-11-2017 for 1 year till 20-09-2018 ,  Enterprise for 1 server ,7 clients terminals added later on desktop and 3 APOS 2 cancel ,1 Webstocks,NO SMS Pack"));
            hmClientDtl.put("198.001", createClientDetails("198.001", "FILL & CHILL", dFormat.parse("2017-09-20"), dFormat.parse("2018-09-20"), "Enterprise", 1, objNoSMSPackDtl, 0, "//(MUMBAI) released on 20-09-2017 for 1 month,renewed on 05-10-2017 for 1 year,  Enterprise for 1  terminals on desktop and 0 APOS,0 Webstocks,NO SMS Pack"));
            hmClientDtl.put("199.001", createClientDetails("199.001", "STAR EVENT & RESTAURANT", dFormat.parse("2017-09-28"), dFormat.parse("2018-09-28"), "Enterprise", 1, objNoSMSPackDtl, 0, "//(MUMBAI)Location 1 released on 28-09-2017 for 1 year,  Enterprise for 1  terminals on desktop and 0 APOS,0 Webstocks,NO SMS Pack"));
            //payment not received
            //hmClientDtl.put("199.002", createClientDetails("199.002", "STAR EVENT & RESTAURANT", dFormat.parse("2017-09-28"), dFormat.parse("2018-09-28"), "Enterprise",1, objNoSMSPackDtl,"")); //(MUMBAI)Location 2 released on 28-09-2017 for 1 year,  Enterprise for 1  terminals on desktop and 0 APOS,0 Webstocks,NO SMS Pack            
            hmClientDtl.put("200.001", createClientDetails("200.001", "AGASTYA HOSPITALITY PVT LTD", dFormat.parse("2017-09-28"), dFormat.parse("2018-09-28"), "Enterprise", 1, objNoSMSPackDtl, 0, "//(MUMBAI) released on 28-09-2017 for 1 year,  Enterprise for 1  terminals on desktop and 0 APOS,0 Webstocks,NO SMS Pack"));
            hmClientDtl.put("201.001", createClientDetails("201.001", "KINTA FOODS LLP", dFormat.parse("2017-09-28"), dFormat.parse("2018-09-28"), "Enterprise", 1, objNoSMSPackDtl, 0, "//(MUMBAI) released on 28-09-2017 for 1 year,  Enterprise for 1  terminals on desktop and 0 APOS,0 Webstocks,NO SMS Pack"));
            hmClientDtl.put("202.001", createClientDetails("202.001", "FLYHIGH HOSPITALITY PVT LTD", dFormat.parse("2017-09-29"), dFormat.parse("2018-09-30"), "Enterprise", 3, objNoSMSPackDtl, 0, "//(MUMBAI) released on 29-09-2017 for 1 year,  Enterprise for 3  terminals on desktop and 1 APOS for reporting,0 Webstocks,NO SMS Pack"));
            //"SF ENTERPRISES" renamed to PINK SUGAR
            hmClientDtl.put("203.001", createClientDetails("203.001", "PINK SUGAR", dFormat.parse("2017-09-29"), dFormat.parse("2018-09-29"), "Enterprise", 2, objNoSMSPackDtl, 1, "//SF ENTERPRISES renamed to PINK SUGAR (MUMBAI)// released on 29-09-2017 renewed on 28-10-2017 for 1 year  Enterprise for 1  terminals on desktop 1 for static IP and 1 APOS for reporting,0 Webstocks,NO SMS Pack"));
            hmClientDtl.put("204.001", createClientDetails("204.001", "MING YANG", dFormat.parse("2017-10-18"), dFormat.parse("2018-01-18"), "Enterprise", 6, objNoSMSPackDtl, 2, "//renewed on 18-12-2017 for 1 month till 18-01-2018,  Enterprise for 6  terminals on desktop and 2 APOS ,0 Webstocks,NO SMS Pack"));
            hmClientDtl.put("205.001", createClientDetails("205.001", "THE SPOONFUEL BISTRO LLP", dFormat.parse("2017-10-20"), dFormat.parse("2018-10-20"), "Enterprise", 1, objNoSMSPackDtl, 0, "//renewed on 07-11-2017 for 1 year till 20-10-2018,  Enterprise for 1  terminal on desktop"));
            //Raju Ki Chai("Gaurika Enterprises Pvt. Ltd.")
            hmClientDtl.put("206.001", createClientDetails("206.001", "Gaurika Enterprises Pvt. Ltd.", dFormat.parse("2017-11-03"), dFormat.parse("2018-11-03"), "Enterprise", 1, objNoSMSPackDtl, 0, "//Raju Ki Chai(Gaurika Enterprises Pvt. Ltd.) //released on 03-11-2017 ,renewed on 05-12-2017 for 1 year till 03-11-2018,  Enterprise for 1 SPOS,0 APOS,No SMS Pack"));
            //"THIKANA"
            hmClientDtl.put("207.001", createClientDetails("207.001", "AURA DINING", dFormat.parse("2017-11-10"), dFormat.parse("2018-01-07"), "Enterprise", 100, objNoSMSPackDtl, 10, "//THIKANA(PUNE) released on 10-11-2017 for 1 month,renewed on 10-12-2017 till 31-12-2017  Enterprise for 4  terminal on desktop and 10 APOS//renewed on 30-12-2017 for 8 days till 07-01-2018"));
            hmClientDtl.put("208.001", createClientDetails("208.001", "GOVINDA HOSPITALITY", dFormat.parse("2017-11-11"), dFormat.parse("2018-11-11"), "Enterprise", 4, objNoSMSPackDtl, 6, "//rename Govind HOSPITALITY to GOVINDA HOSPITALITY released on 11-11-2017 for 1 year till 11-11-2018,  Enterprise for 4  terminal on desktop and 6 APOS"));
            hmClientDtl.put("209.001", createClientDetails("209.001", "MODAKAM PURE VEG", dFormat.parse("2017-11-13"), dFormat.parse("2018-11-12"), "Enterprise", 6, objNoSMSPackDtl, 2, "//release on 13-11-2017 renewed on 29-11-2017 for 1 year for 6 SPOS and 2 APOS "));
            hmClientDtl.put("210.001", createClientDetails("210.001", "MAYUR TEXT TRADING LLP", dFormat.parse("2017-11-29"), dFormat.parse("2018-11-28"), "Enterprise", 2, objNoSMSPackDtl, 0, " //release on 29-11-2017 for 1 year till 2018-11-28 for 2 SPOS,updated 1 more terminal on 23-12-2017 and 0 APOS "));
            //"211.001", "CHEFS CORNER" MMS license
            hmClientDtl.put("212.001", createClientDetails("212.001", "Blue Sky", dFormat.parse("2017-11-30"), dFormat.parse("2018-11-30"), "Enterprise", 4, objNoSMSPackDtl, 1, "//release on 30-11-2017 for 1 month till 2017-12-30,renewed on 28-12-2017 for 1 year tlll 30-11-2018,for 4 SPOS and 1 APOS ,No SMS Pack"));
            //Saloon(Salon) Vimannagar 
            hmClientDtl.put("213.001", createClientDetails("213.001", "GMH UNISEX SALON", dFormat.parse("2017-12-07"), dFormat.parse("2017-12-15"), "Enterprise", 1, objNoSMSPackDtl, 0, " //Saloon(Salon) Vimannagar //release on 07-12-2017 for 15 days till 2017-12-15 for 1 SPOS and 0 APOS ,No SMS Pack."));
            hmClientDtl.put("214.001", createClientDetails("214.001", "MOSHES FINE FOODS PVT LTD", dFormat.parse("2017-12-08"), dFormat.parse("2018-12-07"), "Enterprise", 2, objNoSMSPackDtl, 0, "//(MUMBAI)release on 08-12-2017 for 1 year till 2018-12-07 for 2 SPOS and 0 APOS ,No SMS Pack."));
            hmClientDtl.put("215.001", createClientDetails("215.001", "VIVVED EDUTAINMENT PVT LTD", dFormat.parse("2017-12-08"), dFormat.parse("2018-12-07"), "Enterprise", 4, new clsSMSPackDtl("VIVVEDEDUTAINMENT", "2017@VIVVEDEDUTAINMENT@2017", "PLAYSC", "Transactional"), 0, " //(MUMBAI)release on 08-12-2017 for 1 year till 2018-12-07 for 4 SPOS and 0 APOS ,5K SMS Pack activated on 19-12-2017."));
            hmClientDtl.put("216.001", createClientDetails("216.001", "WOK MASTERS", dFormat.parse("2017-12-12"), dFormat.parse("2018-12-11"), "Enterprise", 1, objNoSMSPackDtl, 3, "//(PUNE)release on 12-12-2017 for 1 year till 2018-12-11 for 1 SPOS and 3 APOS ,No SMS Pack."));
            hmClientDtl.put("217.001", createClientDetails("217.001", "BURNT CRUST HOSPITALITY PVT LTD", dFormat.parse("2017-12-14"), dFormat.parse("2018-01-13"), "Enterprise", 6, objNoSMSPackDtl, 16, "//(MUMBAI)release on 14-12-2017 for 1 month till 2018-01-13 for 6 SPOS and 13 APOS,updated to 16 APOS,1 WebSttocks ,No SMS Pack."));
            hmClientDtl.put("218.001", createClientDetails("218.001", "THE LIQUID WISDOM CO.PVT LTD", dFormat.parse("2017-12-14"), dFormat.parse("2018-12-13"), "Enterprise", 3, objNoSMSPackDtl, 6, "//(MUMBAI)//renewed on 04-01-2018 for 1 year till 13-12-2018//release on 14-12-2017 for 1 month till 2018-01-13 for 3 SPOS and 6 APOS,No SMS Pack."));
            hmClientDtl.put("219.001", createClientDetails("219.001", "C J HOSPITALITIES LLP", dFormat.parse("2017-12-14"), dFormat.parse("2018-01-13"), "Enterprise", 2, objNoSMSPackDtl, 1, "//(MUMBAI)release on 14-12-2017 for 1 month till 2018-01-13 for 2 SPOS and 1 APOS,No SMS Pack."));
            hmClientDtl.put("220.001", createClientDetails("220.001", "70 DEGREE EAST", dFormat.parse("2017-12-18"), dFormat.parse("2018-01-17"), "Enterprise", 3, objNoSMSPackDtl, 10, "//(MUMBAI)release on 18-12-2017 for 1 month till 17-01-2018 for 3 SPOS and 10 APOS,No WebStocks,No SMS Pack."));
            hmClientDtl.put("221.001", createClientDetails("221.001", "NIKHIL CORPORATION", dFormat.parse("2018-01-04"), dFormat.parse("2018-01-31"), "Enterprise", 3, objNoSMSPackDtl, 1, "//(MUMBAI)release on 04-01-2018 for 1 month till 31-01-2018 for 3 SPOS and 1 APOS,No WebStocks,No SMS Pack."));

          //"222.001", "PURANCHAND & SONS" MMS License
    	    hmClientDtl.put("223.001", createClientDetails("223.001", "BANYAN TREE HOSPITALITY LLP", dFormat.parse("2018-01-17"), dFormat.parse("2018-04-16"), "Enterprise", 2, objNoSMSPackDtl, 3, "(MUMBAI)//renewed on 13-03-2018 for 1 month till 16-04-2018//renewed on 16-02-2018 for 1 month till 16-03-2018//release on 17-01-2018 for 1 month till 16-02-2018 for 2 SPOS and 3 APOS,1 WebStocks,No SMS Pack."));
    	    hmClientDtl.put("224.001", createClientDetails("224.001", "XO ZERO LOUNGE", dFormat.parse("2018-01-18"), dFormat.parse("2019-01-18"), "Enterprise", 3, objNoSMSPackDtl, 10, "(PUNE)//renewed on 16-02-2018 for 1 month till 18-03-2018//release on 18-01-2018 for 1 month till 18-02-2018 //renewed on 27-2-2018 for 1 year till 18-1-2019 for 3 SPOS and 10 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("225.001", createClientDetails("225.001", "SHEESHA SKY LOUNGE HOSPITALITY AND SERVICES PVT LTD", dFormat.parse("2018-01-22"), dFormat.parse("2018-02-21"), "Enterprise", 6, objNoSMSPackDtl, 8, "//(MUMBAI)release on 22-01-2018 for 1 month till 21-02-2018 for 6 SPOS and 8 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("226.001", createClientDetails("226.001", "AHLING CHINESE CORNER RESTAURANT",dFormat.parse("2018-01-29"), dFormat.parse("2019-01-28"), "Enterprise", 1, objNoSMSPackDtl, 0, "(PUNE)//renewed on 10-02-2018 for 1 year till 28-01-2019//renamed AHLING CHINESE RESTAURANT to AHLING CHINESE CORNER RESTAURANT//release on 29-01-2018 for 1 month till 28-02-2018 for 1 SPOS,0 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("227.001", createClientDetails("227.001", "HOR KIDDA FOODS", dFormat.parse("2018-02-13"), dFormat.parse("2019-02-12"), "Enterprise", 2, objNoSMSPackDtl, 1, "(MUMBAI)//renewed on 21-03-2018 for 1 year till 12-02-2019//release on 13-02-2018 for 1 month//renew for 1 month on 09-03-2018 till 12-04-2018 for 2 SPOS,1 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("228.001", createClientDetails("228.001", "Moti Mahal-MYK Foods and Beverage", dFormat.parse("2018-02-21"), dFormat.parse("2018-05-21"), "Enterprise", 10, objNoSMSPackDtl, 1, "(PUNE)//Outlet 1 Amanora//release on 21-02-2018 for 3 months till 21-05-2018 for 1 HO SPOS,0 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("228.002", createClientDetails("228.002", "Moti Mahal-MYK Foods and Beverage", dFormat.parse("2018-02-21"), dFormat.parse("2018-05-21"), "Enterprise", 1, objNoSMSPackDtl, 1, "(PUNE)//Outlet 2 Phoenix//release on 21-02-2018 for 3 months till 21-05-2018 for 1 SPOS,0 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("228.003", createClientDetails("228.003", "Moti Mahal-MYK Foods and Beverage", dFormat.parse("2018-02-21"), dFormat.parse("2018-05-21"), "Enterprise", 1, objNoSMSPackDtl, 1, "(PUNE)//HO Call Center//release on 21-02-2018 for 3 months till 21-05-2018 for 1 SPOS,0 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("229.001", createClientDetails("229.001", "ALTA A CREATIVE HIGH", dFormat.parse("2018-02-23"), dFormat.parse("2019-02-22"), "Enterprise", 6, objNoSMSPackDtl, 2, "(MUMBAI)//renewed on 21-03-2018 for 1 year till 22-02-2019//release on 23-02-2018 for 1 months till 23-03-2018 for 6 SPOS ,2 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("230.001", createClientDetails("230.001", "AL MAKAN CAFE", dFormat.parse("2018-02-23"), dFormat.parse("2019-02-23"), "Enterprise", 6, objNoSMSPackDtl, 0, "(UMAN)//Location 1//release on 23-02-2018 for 1 year till 23-02-2019 for 6 SPOS ,0 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("230.002", createClientDetails("230.002", "AL MAKAN CAFE", dFormat.parse("2018-02-23"), dFormat.parse("2019-02-23"), "Enterprise", 6, objNoSMSPackDtl, 0, "(UMAN)//Location 2//release on 23-02-2018 for 1 year till 23-02-2019 for 6 SPOS ,0 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("230.003", createClientDetails("230.003", "AL MAKAN CAFE", dFormat.parse("2018-02-23"), dFormat.parse("2019-02-23"), "Enterprise", 6, objNoSMSPackDtl, 0, "(UMAN)//Location 3//release on 23-02-2018 for 1 year till 23-02-2019 for 6 SPOS ,0 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("231.001", createClientDetails("231.001", "LE MAKAN CAFE", dFormat.parse("2018-02-23"), dFormat.parse("2019-02-23"), "Enterprise", 6, objNoSMSPackDtl, 0, "(UMAN)//Location 1//release on 23-02-2018 for 1 year till 23-02-2019 for 6 SPOS ,0 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("232.001", createClientDetails("232.001", "PLAYBOY CLUB MUMBAI", dFormat.parse("2018-02-26"), dFormat.parse("2018-03-25"), "Enterprise", 8, objNoSMSPackDtl, 2, "(MUMBAI)//release on 26-02-2018 for 1 months till 26-03-2018 for 8 SPOS ,2 APOS,NO WebStocks,No SMS Pack."));	    
    	    hmClientDtl.put("233.001", createClientDetails("233.001", "PLAYBOY INDIA", dFormat.parse("2018-03-16"), dFormat.parse("2018-04-15"), "Enterprise", 9, objNoSMSPackDtl, 5, "(MUMBAI)//release on 13-03-2018 for 1 months till 12-04-2018 for 9 SPOS ,5 APOS,1 WebStocks,No SMS Pack."));
    	    hmClientDtl.put("234.001", createClientDetails("234.001", "POSANA KITCHEN PVT LTD", dFormat.parse("2018-03-13"), dFormat.parse("2019-03-12"), "Enterprise", 2, objNoSMSPackDtl, 1, "(MUMBAI)//release on 13-03-2018 for 1 year till 12-03-2019 for 2 SPOS ,1 APOS,NO WebStocks,No SMS Pack."));	    
    	    hmClientDtl.put("235.001", createClientDetails("235.001", "CAFE HYDRO", dFormat.parse("2018-03-16"), dFormat.parse("2019-03-15"), "Enterprise", 3, objNoSMSPackDtl, 0, "(MUMBAI)//release on 16-03-2018 for 1 year till 15-03-2019 for 3 SPOS ,0 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("236.001", createClientDetails("236.001", "JAMUN HOSPITALITY CONSULTANCY LLP", dFormat.parse("2018-03-16"), dFormat.parse("2019-03-15"), "Enterprise", 5, objNoSMSPackDtl, 2, "(MUMBAI)//release on 16-03-2018 for 1 year till 15-03-2019 for 5 SPOS ,2 APOS,NO WebStocks,No SMS Pack."));
    	    hmClientDtl.put("237.001", createClientDetails("237.001", "ADM HOSPITALITY PVT LTD", dFormat.parse("2018-03-21"), dFormat.parse("2019-03-20"), "Enterprise", 2, objNoSMSPackDtl, 6, "(MUMBAI)//release on 21-03-2018 for 1 year till 20-03-2019 for 2 SPOS ,6 APOS,NO WebStocks,No SMS Pack."));
    	   
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //To print all client list details
    public static void main(String[] args)
    {
        funAddClientCodeAndName();

        SimpleDateFormat ddMMyyyy = new SimpleDateFormat("dd-MM-yyyy");

        for (Map.Entry<String, clsPOSClientDetails> entryOfLicense : hmClientDtl.entrySet())
        {
            String clientCode = entryOfLicense.getKey();
            clsPOSClientDetails objValue = entryOfLicense.getValue();

            String inDate = ddMMyyyy.format(objValue.installDate);
            String exDate = ddMMyyyy.format(objValue.expiryDate);

            System.out.println(clientCode + "!" + objValue.Client_Name + "!" + inDate + "!" + exDate + "!" + objValue.posVersion + "!" + objValue.intMAXAPOSTerminals + "!" + objValue.intMAXTerminal + "!" + objValue.objSMSPackDtl.getStrSMSPack() + "!" + objValue.getStrComments());
        }
    }

    public static Date funCheckPOSLicense(String clientCode, String clientName)
    {
        Date dt = null;

        try
        {
            /*for(Map.Entry<String, clsPOSClientDetails> entry : hmClientDtl.entrySet())
             {
             System.out.println(entry.getKey());
             }*/

            System.out.println(clientCode);
            if (hmClientDtl.containsKey(clientCode))
            {
                String cname = hmClientDtl.get(clientCode).Client_Name;
                System.out.println(cname);
                if (cname.equalsIgnoreCase(clientName))
                {
                    SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
                    dt = dFormat.parse(dFormat.format(clsPOSClientDetails.hmClientDtl.get(clientCode).expiryDate));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return dt;
    }

    public String getPosVersion()
    {
        return this.posVersion;
    }

    public void setPosVersion(String posVersion)
    {
        this.posVersion = posVersion;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getClient_Name()
    {
        return Client_Name;
    }

    public void setClient_Name(String Client_Name)
    {
        this.Client_Name = Client_Name;
    }

    public Date getInstallDate()
    {
        return installDate;
    }

    public void setInstallDate(Date installDate)
    {
        this.installDate = installDate;
    }

    public Date getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    public int getIntMAXTerminal()
    {
        return intMAXTerminal;
    }

    public void setIntMAXTerminal(int intMAXTerminal)
    {
        this.intMAXTerminal = intMAXTerminal;
    }

    public clsSMSPackDtl getObjSMSPackDtl()
    {
        return objSMSPackDtl;
    }

    public void setObjSMSPackDtl(clsSMSPackDtl objSMSPackDtl)
    {
        this.objSMSPackDtl = objSMSPackDtl;
    }

    public int getIntMAXAPOSTerminals()
    {
        return intMAXAPOSTerminals;
    }

    public void setIntMAXAPOSTerminals(int intMAXAPOSTerminals)
    {
        this.intMAXAPOSTerminals = intMAXAPOSTerminals;
    }

    public String getStrComments()
    {
        return strComments;
    }

    public void setStrComments(String strComments)
    {
        this.strComments = strComments;
    }

}
