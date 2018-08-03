/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class clsSendMail
{
	
	@Autowired
	clsUtilityController obUtilityController;
	
	@Autowired
	clsPOSSetupUtility objPOSSetupUtility; 
    /*
     public static void main(String args[])
     {
     String data=" This is send mail profram to send mail on the server ";
     funSendMail(data);
     }*/

    public int funSendMail(double totalSales, double totalDiscount, double totalPayment, String filePath,String strClientCode,String strPOSCode,String strPOSName,String strPOSDate)
    {
        int ret = 0;
        //String to="ingaleprashant8@gmail.com";//change accordingly
		   try{
			    String gReceiverEmailIds = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gReceiverEmailIds");
		  		
			    final String gSenderEmailId = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gSenderEmailId");
		  		
			    final String gSenderMailPassword= objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gSenderMailPassword");
		  		
		  		
		        String to = gReceiverEmailIds;//change accordingly
		        //Get the session object
		        Properties props = new Properties();
		        props.put("mail.smtp.host", "smtp.gmail.com");
		        props.put("mail.smtp.socketFactory.port", "465");
		        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		        props.put("mail.smtp.auth", "true");
		        props.put("mail.smtp.port", "465");
		
		        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
		        {
		            protected PasswordAuthentication getPasswordAuthentication()
		            {
		                return new PasswordAuthentication(gSenderEmailId, gSenderMailPassword);//change accordingly
		            }
		        });
		
		        //compose message
		        try
		        {
		            MimeMessage message = new MimeMessage(session);
		            message.setFrom(new InternetAddress(gSenderEmailId));//change accordingly
		            String[] arrRecipient = to.split(",");
		
		            if (to.length() > 0)
		            {
		                System.out.println(to);
		                for (int cnt = 0; cnt < arrRecipient.length; cnt++)
		                {
		                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(arrRecipient[cnt]));
		                }
		            }
		            message.setSubject("DAY END REPORT " + strPOSName + " " + strPOSDate);
		            String amount1 = "Total Sales=";
		            amount1 = amount1 + String.valueOf(totalSales);
		            String amount2 = "\n" + "Total Discount=";
		            amount2 = amount2 + String.valueOf(totalDiscount);
		            String amount3 = "\n" + "Total Payment=";
		            amount3 = amount3 + String.valueOf(totalPayment);
		            String msgBody = amount1 + amount2 + amount3;
		            //message.setText(msgBody);
		
		            // Create the message part 
		            BodyPart messageBodyPart = new MimeBodyPart();
		
		            DataSource source = new FileDataSource(filePath);
		            String data = "";
		            File file = new File(filePath);
		           // file.createNewFile();
		            FileReader fread = new FileReader(file);
		            FileInputStream fis = fis = new FileInputStream(file);
		            BufferedReader KOTIn = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
		            String line = "";
		            while ((line = KOTIn.readLine()) != null)
		            {
		                data = data + line + "\n";
		            }
		            
		            
		            // Fill the message
		            messageBodyPart.setText(data);
		
		            Multipart multipart = new MimeMultipart();
		
		            // Set text message part
		            //multipart.addBodyPart(messageBodyPart);
		            // Part two is attachment
		            messageBodyPart = new MimeBodyPart();
		            messageBodyPart.setDataHandler(new DataHandler(source));
		            messageBodyPart.setFileName(filePath);
		            multipart.addBodyPart(messageBodyPart);
		
		            String dayEndReportFilePath = System.getProperty("user.dir");
		            File dayEndReportFile = new File(dayEndReportFilePath + "/Reports");
		            if (dayEndReportFile.exists())
		            {
		                File[] filesPath = dayEndReportFile.listFiles();
		                for (int i = 0; i < filesPath.length; i++)
		                {
		                    messageBodyPart = new MimeBodyPart();
		                    source = new FileDataSource(filesPath[i]);
		                    messageBodyPart.setDataHandler(new DataHandler(source));
		                    messageBodyPart.setFileName(filesPath[i].getAbsolutePath());
		                    multipart.addBodyPart(messageBodyPart);
		                }
		            }
		
		            // Send the complete message parts
		            message.setContent(multipart);
		
		            if (to.length() > 0)
		            {
		                //send message  
		                Transport.send(message);
		                System.out.println("message sent successfully");
		            }
		            else
		            {
		                System.out.println("Email has No Recipient");
		            }
		        }
		        catch (MessagingException e)
		        {
		            e.printStackTrace();
		            //throw new RuntimeException(e);
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		        }
		        ret = 1;
		        
		    }
		    catch(Exception e)
		    {
		    	e.printStackTrace();
		    }
        return ret;
    }

    /* Function to send email 
     Param 1 - Receivers amil Ids.
     Param 2 - Path of file to attach.
    
     */
    public int funSendMail(String receiverMailIds, String filePath,String strClientCode,String strPOSCode,String strPOSName,String strPOSDate)
    {
        int ret = 0;
        try
	    {
        	String gReceiverEmailIds = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gReceiverEmailIds");
	  		
        	final String gSenderEmailId = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gSenderEmailId");
	  		
        	final String gSenderMailPassword = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gSenderMailPassword");
	  		
        	String strClientName = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gClientName");
	  		 
	  		
	        //String to="ingaleprashant8@gmail.com";//change accordingly
	        String to = gReceiverEmailIds;//change accordingly
	        //Get the session object
	        Properties props = new Properties();
	        props.put("mail.smtp.host", "smtp.gmail.com");
	        props.put("mail.smtp.socketFactory.port", "465");
	        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.port", "465");
	
	        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
	        {
	            protected PasswordAuthentication getPasswordAuthentication()
	            {
	                //return new PasswordAuthentication("paritoshkumar112@gmail.com","singhparitosh123");//change accordingly  
	                return new PasswordAuthentication(gSenderEmailId, gSenderMailPassword);//change accordingly
	            }
	        });
	
	        //compose message
	       
	            MimeMessage message = new MimeMessage(session);
	            //message.setFrom(new InternetAddress("paritoshkumar112@gmail.com"));//change accordingly
	            message.setFrom(new InternetAddress(gSenderEmailId));//change accordingly
	            String[] arrRecipient = to.split(",");
	
	            if (to.trim().length() > 0)
	            {
	                for (int cnt = 0; cnt < arrRecipient.length; cnt++)
	                {
	                    System.out.println(arrRecipient[cnt]);
	                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(arrRecipient[cnt]));
	                }
	            }
	
	            message.setSubject("DAY END REPORT " + strPOSName + " " + strPOSDate);
	
	            String msgBody = strClientName + " DB Backup for " + obUtilityController.funGetCurrentDateTime();
	            //message.setText(msgBody);
	
	            // Create the message part 
	            BodyPart messageBodyPart = new MimeBodyPart();
	            DataSource source = new FileDataSource(filePath);
	
	            String data="";
	             File file=new File(filePath);
	             FileInputStream fis=fis = new FileInputStream(file);
	             BufferedReader KOTIn = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
	             String line = "";
	             while ((line = KOTIn.readLine()) != null) {
	             data = data + line + "\n";
	             }
	            // Fill the message
	            messageBodyPart.setText(msgBody);
	
	            Multipart multipart = new MimeMultipart();
	
	            // Set text message part
	            multipart.addBodyPart(messageBodyPart);
	
	            // Part two is attachment
	            messageBodyPart = new MimeBodyPart();
	            messageBodyPart.setDataHandler(new DataHandler(source));
	            messageBodyPart.setFileName(filePath);
	            multipart.addBodyPart(messageBodyPart);
	
	            // Send the complete message parts
	            message.setContent(multipart);
	
	            if (to.length() > 0)
	            {
	                //send message  
	                Transport.send(message);
	                System.out.println("message sent successfully");
	            }
	            else
	            {
	                System.out.println("Email has No Recipient");
	            }
	        }
        catch (MessagingException e)
        {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ret = 1;
       return ret;
    }

    public int funSendMail(String receiverMailIds, File[] filesPath,String strClientCode,String strPOSCode,String strPOSName,String strPOSDate)
    {
        int ret = 0;
        try
		{
        	String gReceiverEmailIds = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gReceiverEmailIds");
	  		
	  		final String gSenderEmailId = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gSenderEmailId");
	  		
	  		final String gSenderMailPassword= objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gSenderMailPassword");
	  		
	  		String strClientName= objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gClientName");
	  		 
		        //String to="ingaleprashant8@gmail.com";//change accordingly
		        String to = receiverMailIds;//change accordingly
		        //Get the session object
		        Properties props = new Properties();
		        props.put("mail.smtp.host", "smtp.gmail.com");
		        props.put("mail.smtp.socketFactory.port", "465");
		        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		        props.put("mail.smtp.auth", "true");
		        props.put("mail.smtp.port", "465");
		
		        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
		        {
		            protected PasswordAuthentication getPasswordAuthentication()
		            {
		                //return new PasswordAuthentication("paritoshkumar112@gmail.com","singhparitosh123");//change accordingly  
		                return new PasswordAuthentication(gSenderEmailId, gSenderMailPassword);//change accordingly
		            }
		        });
		
		        //compose message
		       
		            MimeMessage message = new MimeMessage(session);
		            //message.setFrom(new InternetAddress("paritoshkumar112@gmail.com"));//change accordingly
		            message.setFrom(new InternetAddress(gSenderEmailId));//change accordingly
		            String[] arrRecipient = to.split(",");
		
		            for (int cnt = 0; cnt < arrRecipient.length; cnt++)
		            {
		                System.out.println(arrRecipient[cnt]);
		                message.addRecipient(Message.RecipientType.TO, new InternetAddress(arrRecipient[cnt]));
		            }
		            message.setSubject("DAY END REPORT " + strPOSName + " " + strPOSDate);
		
		            String msgBody = strClientName + " DB Backup for " + obUtilityController.funGetCurrentDateTime();
		            //message.setText(msgBody);
		
		            // Create the message part 
		            BodyPart messageBodyPart = new MimeBodyPart();
		            	/*
		            String data="";
		             File file=new File(filePath);
		             FileInputStream fis=fis = new FileInputStream(file);
		             BufferedReader KOTIn = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
		             String line = "";
		             while ((line = KOTIn.readLine()) != null) {
		             data = data + line + "\n";
		             }*/
		            // Fill the message
		            messageBodyPart.setText(msgBody);
		
		            Multipart multipart = new MimeMultipart();
		
		            // Set text message part
		            multipart.addBodyPart(messageBodyPart);
		
		            // Part two is attachment
		            for (int i = 0; i < filesPath.length; i++)
		            {
		                messageBodyPart = new MimeBodyPart();
		                DataSource source = new FileDataSource(filesPath[i]);
		                messageBodyPart.setDataHandler(new DataHandler(source));
		                messageBodyPart.setFileName(filesPath[i].getAbsolutePath());
		                multipart.addBodyPart(messageBodyPart);
		            }
		
		            // Send the complete message parts
		            message.setContent(multipart);
		
		            //send message  
		            if (to.length() > 0)
		            {
		                //send message  
		                Transport.send(message);
		                System.out.println("message sent successfully");
		            }
		            else
		            {
		                System.out.println("Email has No Recipient");
		            }
		        }
        catch (MessagingException e)
        {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ret = 1;
        return ret;
    }

    public int funSendMailForConsolodatePOS(double totalSales, double totalDiscount, double totalPayment, String filePath,String strClientCode,String strPOSCode,String strPOSName,String strPOSDate)
    {
        int ret = 0;
        try
		  { 
        	String gReceiverEmailIds = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gReceiverEmailIds");
	  		
	  		final String gSenderEmailId = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gSenderEmailId");
	  		
	  		final String gSenderMailPassword= objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,strPOSCode, "gSenderMailPassword");
	  		
		        //String to="ingaleprashant8@gmail.com";//change accordingly
		        String to = gReceiverEmailIds;//change accordingly
		        //Get the session object
		        Properties props = new Properties();
		        props.put("mail.smtp.host", "smtp.gmail.com");
		        props.put("mail.smtp.socketFactory.port", "465");
		        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		        props.put("mail.smtp.auth", "true");
		        props.put("mail.smtp.port", "465");
		
		        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
		        {
		            protected PasswordAuthentication getPasswordAuthentication()
		            {
		                //return new PasswordAuthentication("paritoshkumar112@gmail.com","singhparitosh123");//change accordingly  
		                return new PasswordAuthentication(gSenderEmailId, gSenderMailPassword);//change accordingly
		            }
		        });
		
		        //compose message
		        
		            MimeMessage message = new MimeMessage(session);
		            //message.setFrom(new InternetAddress("paritoshkumar112@gmail.com"));//change accordingly
		            message.setFrom(new InternetAddress(gSenderEmailId));//change accordingly
		            String[] arrRecipient = to.split(",");
		
		            for (int cnt = 0; cnt < arrRecipient.length; cnt++)
		            {
		                System.out.println(arrRecipient[cnt]);
		                message.addRecipient(Message.RecipientType.TO, new InternetAddress(arrRecipient[cnt]));
		            }
		            message.setSubject("DAY END REPORT " + strPOSName + " " + strPOSDate);
		            String amount1 = "Total Sales=";
		            amount1 = amount1 + String.valueOf(totalSales);
		            String amount2 = "\n" + "Total Discount=";
		            amount2 = amount2 + String.valueOf(totalDiscount);
		            String amount3 = "\n" + "Total Payment=";
		            amount3 = amount3 + String.valueOf(totalPayment);
		            String msgBody = amount1 + amount2 + amount3;
		            //message.setText(msgBody);
		
		            // Create the message part 
		            BodyPart messageBodyPart = new MimeBodyPart();
		
		            DataSource source = new FileDataSource(filePath);
		            String data = "";
		            File file = new File(filePath);
		            FileReader fread = new FileReader(file);
		            FileInputStream fis = fis = new FileInputStream(file);
		            BufferedReader KOTIn = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
		            String line = "";
		            while ((line = KOTIn.readLine()) != null)
		            {
		                data = data + line + "\n";
		            }
		            // Fill the message
		            messageBodyPart.setText(data);
		
		            Multipart multipart = new MimeMultipart();
		
		            // Set text message part
		            multipart.addBodyPart(messageBodyPart);
		
		            // Part two is attachment
		            messageBodyPart = new MimeBodyPart();
		
		            messageBodyPart.setDataHandler(new DataHandler(source));
		            messageBodyPart.setFileName(filePath);
		            multipart.addBodyPart(messageBodyPart);
		
		            // Send the complete message parts
		            message.setContent(multipart);
		
		            //send message  
		            if (to.length() > 0)
		            {
		                //send message  
		                Transport.send(message);
		                System.out.println("message sent successfully");
		            }
		            else
		            {
		                System.out.println("Email has No Recipient");
		            }
		        }
        catch (MessagingException e)
        {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ret = 1;
       return ret;

    }

    
    public int funSendMail(String posCode,String posName,String fromDate,String toDate,String strClientCode, List listOfReports)
    {
        int ret = 0;
        try
        {
       
        //String to="ingaleprashant8@gmail.com";//change accordingly
      //  String to = clsGlobalVarClass.gReceiverEmailIds;//change accordingly
        //Get the session object
        	String to = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gReceiverEmailIds");
  		
  		final String gSenderEmailId = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gSenderEmailId");
  		
  		final String gSenderMailPassword = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gSenderMailPassword");
  		
       	
  		String strClientName = objPOSSetupUtility.funGetParameterValuePOSWise(strClientCode,posCode, "gClientName");
  		
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(gSenderEmailId, gSenderMailPassword);//change accordingly
            }
        });

        //compose message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(gSenderEmailId));//change accordingly
            String[] arrRecipient = to.split(",");

            if (to.length() > 0)
            {
                System.out.println(to);
                for (int cnt = 0; cnt < arrRecipient.length; cnt++)
                {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(arrRecipient[cnt]));
                }
            }
            message.setSubject("DAY END REPORTS FOR " + posName + " FROM " + fromDate+" To "+toDate);
           

            // Create the message part 
            BodyPart messageBodyPart = new MimeBodyPart();

           
            String data = "";
            for(int i=0;i<listOfReports.size();i++)
            {                
                data+=listOfReports.get(i).toString();;
                data+="\n";
            }
            data+="\n\n\n\n\n\n\n\n";            
            data+="\nThank You,";
            data+="\nTeam SANGUINE";
            
            // Fill the message
            messageBodyPart.setText(data);

            Multipart multipart = new MimeMultipart();

          
            FileDataSource source;
          
            multipart.addBodyPart(messageBodyPart);

            String dayEndReportFilePath = System.getProperty("user.dir");
            File dayEndReportFile = new File(dayEndReportFilePath + "/Reports");
            if (dayEndReportFile.exists())
            {
                File[] filesPath = dayEndReportFile.listFiles();
                for (int i = 0; i < filesPath.length; i++)
                {
                    messageBodyPart = new MimeBodyPart();
                    source = new FileDataSource(filesPath[i]);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(filesPath[i].getAbsolutePath());
                    multipart.addBodyPart(messageBodyPart);
                }
            }

            // Send the complete message parts
            message.setContent(multipart);

            if (to.length() > 0)
            {
                //send message  
                Transport.send(message);
                System.out.println("message sent successfully");
            }
            else
            {
                System.out.println("Email has No Recipient");
            }
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ret = 1;
        return ret;
    }

}
