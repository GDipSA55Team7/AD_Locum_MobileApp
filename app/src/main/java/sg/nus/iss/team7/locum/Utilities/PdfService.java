package sg.nus.iss.team7.locum.Utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import sg.nus.iss.team7.locum.Model.PaymentDetailsDTO;
import sg.nus.iss.team7.locum.PaymentDetailsActivity;

public class PdfService {
    // creating a ByteArrayOutputStream to hold the generated PDF data instead of a file
    public boolean createTempPDF(Context context, PaymentDetailsDTO paymentDTO){
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            Double ratePerHr;
            Integer numOfHours = 2;

            if(paymentDTO == null){
                return false;
            }
            if(paymentDTO.getJobRatePerHr() != null){
                ratePerHr  = paymentDTO.getJobRatePerHr();;
            }
            else{
                ratePerHr  = 0.00;
            }
            Double subTotal = numOfHours * ratePerHr ;
            //subTotal = paymentDTO.getJobTotalRate();
            String subTotalTo2DP = String.format("%.2f",subTotal);

            Double Total = subTotal ;
            //Total = paymentDTO.getJobTotalRate();
            String totalTo2DP = String.format("%.2f",Total);

            String ratePerHrTo2DP = String.format("%.2f",ratePerHr);


            PdfPTable irdTable = new PdfPTable(2);
            irdTable.addCell(getIRDCell("Invoice No"));
            irdTable.addCell(getIRDCell("Invoice Date"));
            irdTable.addCell(getIRDCell("Payment Reference")); // pass invoice number
            irdTable.addCell(getIRDCell("Payment Date")); // pass invoice date

            PdfPTable irhTable = new PdfPTable(3);
            irhTable.setWidthPercentage(100);

            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("Job Invoice", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            PdfPCell invoiceTable = new PdfPCell (irdTable);
            invoiceTable.setBorder(0);
            irhTable.addCell(invoiceTable);

            FontSelector fs = new FontSelector();
            Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 13, Font.BOLD);
            fs.addFont(font);
            Phrase locumDetails = fs.process("Locum Details :");
            Paragraph locumName = new Paragraph("Name : " + paymentDTO.getFlName());
            locumName.setIndentationLeft(20);
            Paragraph locumContact = new Paragraph("Contact No. : " + paymentDTO.getFlContact());
            locumContact.setIndentationLeft(20);
            Paragraph locumAddress = new Paragraph("Email : " + paymentDTO.getFlEmail());
            locumAddress.setIndentationLeft(20);
            Paragraph locumMedicalNo = new Paragraph( "Medical License No. : " +  paymentDTO.getFlMedicalLicenseNo());
            locumMedicalNo.setIndentationLeft(20);
            locumMedicalNo.setSpacingAfter(20f);

            Phrase ClinicDetails = fs.process("Clinic Details :");
            //
            Paragraph clinicName = new Paragraph("Name : " + paymentDTO.getClinicName());
            clinicName.setIndentationLeft(20);
            Paragraph clinicContact = new Paragraph("Contact No. : " + paymentDTO.getClinicContact());
            clinicContact.setIndentationLeft(20);
            Paragraph clinicAddress = new Paragraph("Email : " + paymentDTO.getClinicAddress());
            clinicAddress .setIndentationLeft(20);
            Paragraph clinicPostalCode = new Paragraph( "Medical License No. : " +  paymentDTO.getClinicPostalCode());
            clinicPostalCode .setIndentationLeft(20);
            Paragraph clinicHCICode = new Paragraph( "HCI Code : " +  paymentDTO.getClinicHciCode());
            clinicHCICode .setIndentationLeft(20);
            clinicHCICode.setSpacingAfter(20f);


            Phrase JobId = fs.process("JobId : " + paymentDTO.getJobId());

            // clinic  name,address,clinic postalcode , cliniccontact ,hci code
            PdfPTable billTable = new PdfPTable(6);
            billTable.setWidthPercentage(100);
            billTable.setWidths(new float[] { 2, 3,6,2,2,2 });
            //billTable.setSpacingBefore(30.0f);
            billTable.setSpacingBefore(10.0f);
            billTable.addCell(getBillHeaderCell("Index"));
            billTable.addCell(getBillHeaderCell("Service Type"));
            billTable.addCell(getBillHeaderCell("Job Description"));
            billTable.addCell(getBillHeaderCell("Unit Price" + "\n" + "(SGD)"));
            billTable.addCell(getBillHeaderCell("No. of Hrs"));
            billTable.addCell(getBillHeaderCell("SubTotal"+ "\n" + "(SGD)"));

            //jobdescript,startdate,end date
            String invoiceDesc = paymentDTO.getJobDescription() + "\n\n"
                    + "Start Time : " + paymentDTO.getJobStartDateTime() + "\n\n"
                    + "End Time : " + paymentDTO.getJobEndDateTime() + "\n\n";

            billTable.addCell(getBillRowCell("1"));
            billTable.addCell(getBillRowCell("Basic Consultation"));
            billTable.addCell(getBillRowCell(invoiceDesc));
            billTable.addCell(getBillRowCell(ratePerHrTo2DP));
            billTable.addCell(getBillRowCell(numOfHours.toString()));
            billTable.addCell(getBillRowCell(totalTo2DP));

            billTable.addCell(getBillRowCell("2"));
            billTable.addCell(getBillRowCell("Extra"));
            billTable.addCell(getBillRowCell("Dummy"));
            billTable.addCell(getBillRowCell("Dummy"));
            billTable.addCell(getBillRowCell("1"));
            billTable.addCell(getBillRowCell("200.00"));


            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            PdfPTable validity = new PdfPTable(1);
            validity.setWidthPercentage(100);
            validity.addCell(getValidityCell(" "));
            validity.addCell(getValidityCell("Extra comments"));
            validity.addCell(getValidityCell(" * Dummy line \n   (Dummy)"));
            validity.addCell(getValidityCell(" * Dummy"));
            PdfPCell summaryL = new PdfPCell (validity);
            summaryL.setColspan (3);
            summaryL.setPadding (1.0f);
            billTable.addCell(summaryL);

            PdfPTable accounts = new PdfPTable(2);
            accounts.setWidthPercentage(100);
            accounts.addCell(getAccountsCell("Subtotal"));
            accounts.addCell(getAccountsCellR(subTotalTo2DP));
//            accounts.addCell(getAccountsCell("Discount (10%)"));
//            accounts.addCell(getAccountsCellR("1262.00"));
            accounts.addCell(getAccountsCell("Tax(2.5%)"));
            accounts.addCell(getAccountsCellR("315.55"));
            accounts.addCell(getAccountsCell("Total"));
            accounts.addCell(getAccountsCellR(totalTo2DP));
            PdfPCell summaryR = new PdfPCell (accounts);
            summaryR.setColspan (3);
            billTable.addCell(summaryR);

//            String companyInfo = "Clinic Name : " +  paymentDTO.getClinicName() + "\n"
//                    + "Clinic Address : " + paymentDTO.getClinicAddress()   + "\n"
//                    + "Clinic Contact : " + paymentDTO.getClinicContact()  + "\n"
//                    + "Clinic PostalCode : " + paymentDTO.getClinicPostalCode()  + "\n"
//                    + "Clinic HCI Code : " + paymentDTO.getClinicHciCode()  + "\n";

//            PdfPTable describer = new PdfPTable(1);
//            describer.setWidthPercentage(100);
//            describer.addCell(getdescCell(" "));
//            describer.addCell(getdescCell(companyInfo));

            document.open();//PDF document opened........
            System.out.println("doc opened..");
            //document.add(image);
            document.add(irhTable);
            document.add(locumDetails);
            document.add(locumName);
            document.add(locumContact);
            document.add(locumAddress);
            document.add(locumMedicalNo);


            document.add(ClinicDetails);
            document.add(clinicName);
            document.add(clinicContact);
            document.add(clinicAddress);
            document.add(clinicPostalCode);
            document.add(clinicHCICode);

            document.add(JobId);

            document.add(billTable);
            //document.add(validity);
            // document.add(accounts);

            document.close();

            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            if (inputStream == null) {
                System.out.println("PaymentDetails inputStream is null");
            }

            else{
                ByteArrayOutputStream pdfDataStream = new ByteArrayOutputStream();
                int read;
                byte[] buffer = new byte[4096];
                while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
                    pdfDataStream.write(buffer, 0, read);
                }
                byte[] pdfData = pdfDataStream.toByteArray();

                if(pdfData != null ){
                    String pdfDataBase64 = Base64.encodeToString(pdfData, Base64.DEFAULT);
                    System.out.println("pdfData is not null and size is " + pdfData.length);
                    System.out.println(pdfDataBase64);
                    Intent intent = new Intent(context, PaymentDetailsActivity.class);
                    intent.putExtra("pdfData", pdfDataBase64 );
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    return true;
                }else{
                    System.out.println("pdfData is null");
                }
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

//            if (outputStream == null) {
//                System.out.println("PaymentDetails outputStream is null");
//            } else {
//                System.out.println("PaymentDetails outputStream size: " + outputStream.size());
//            }

            // Convert the output stream to a byte array
           // byte[] pdfData = outputStream.toByteArray();
            //It's a synchronous operation that can be performed on the same thread as the rest of your code. no need new thread

//            byte[] pdfData = null;
//
//            try {
//                pdfData = outputStream.toByteArray();
//            } catch (Exception e) {
//                System.out.println("PaymentDetails error converting outputStream to byte[]");
//                e.printStackTrace();
//            }
//
//            // Pass the byte array to the PDF viewer
//            if(pdfData != null){
//                System.out.println("PaymentDetails pdfData size: " + pdfData.length);
//                Intent intent = new Intent(context, PaymentDetailsActivity.class);
//                intent.putExtra("pdfData", pdfData);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//            }
//            else{
//                System.out.println("PaymentDetails pdfData is null");
//            }
 //           return true;

    public boolean createPDF (Context context, String pdfFilename,PaymentDetailsDTO paymentDTO){

        try {
            File file = new File(context.getFilesDir(), pdfFilename);
            System.out.println("file created at" + file.getAbsolutePath());
            //readable by webView
            file.setReadable(true, false);
            OutputStream outputStream = new FileOutputStream(file);

            Document document = new Document();
            PdfWriter.getInstance(document, outputStream );

            //Inserting Image in PDF
//            Image image = Image.getInstance ("src/resources/logo.jpg");//Header Image
//            image.scaleAbsolute(540f, 72f);//image width,height

            //DatetimeParser.parseDate(paymentDTO.getJobStartDateTime());
            //payment ref,date add to dto

            Integer numOfHours = 2;

            Double ratePerHr = paymentDTO.getJobRatePerHr();;
            Double subTotal = numOfHours * ratePerHr ;
            //subTotal = paymentDTO.getJobTotalRate();
            String subTotalTo2DP = String.format("%.2f",subTotal);

            Double Total = subTotal ;
            //Total = paymentDTO.getJobTotalRate();
            String totalTo2DP = String.format("%.2f",Total);

            String ratePerHrTo2DP = String.format("%.2f",ratePerHr);


            PdfPTable irdTable = new PdfPTable(2);
            irdTable.addCell(getIRDCell("Invoice No"));
            irdTable.addCell(getIRDCell("Invoice Date"));
            irdTable.addCell(getIRDCell("Payment Reference")); // pass invoice number
            irdTable.addCell(getIRDCell("Payment Date")); // pass invoice date

            PdfPTable irhTable = new PdfPTable(3);
            irhTable.setWidthPercentage(100);

            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("Job Invoice", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            PdfPCell invoiceTable = new PdfPCell (irdTable);
            invoiceTable.setBorder(0);
            irhTable.addCell(invoiceTable);

            FontSelector fs = new FontSelector();
            Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 13, Font.BOLD);
            fs.addFont(font);
            Phrase locumDetails = fs.process("Locum Details :");
            Paragraph locumName = new Paragraph("Name : " + paymentDTO.getFlName());
            locumName.setIndentationLeft(20);
            Paragraph locumContact = new Paragraph("Contact No. : " + paymentDTO.getFlContact());
            locumContact.setIndentationLeft(20);
            Paragraph locumAddress = new Paragraph("Email : " + paymentDTO.getFlEmail());
            locumAddress.setIndentationLeft(20);
            Paragraph locumMedicalNo = new Paragraph( "Medical License No. : " +  paymentDTO.getFlMedicalLicenseNo());
            locumMedicalNo.setIndentationLeft(20);
            locumMedicalNo.setSpacingAfter(20f);

            Phrase ClinicDetails = fs.process("Clinic Details :");
            //
            Paragraph clinicName = new Paragraph("Name : " + paymentDTO.getClinicName());
            clinicName.setIndentationLeft(20);
            Paragraph clinicContact = new Paragraph("Contact No. : " + paymentDTO.getClinicContact());
            clinicContact.setIndentationLeft(20);
            Paragraph clinicAddress = new Paragraph("Email : " + paymentDTO.getClinicAddress());
            clinicAddress .setIndentationLeft(20);
            Paragraph clinicPostalCode = new Paragraph( "Medical License No. : " +  paymentDTO.getClinicPostalCode());
            clinicPostalCode .setIndentationLeft(20);
            Paragraph clinicHCICode = new Paragraph( "HCI Code : " +  paymentDTO.getClinicHciCode());
            clinicHCICode .setIndentationLeft(20);
            clinicHCICode.setSpacingAfter(20f);


            Phrase JobId = fs.process("JobId : " + paymentDTO.getJobId());

            // clinic  name,address,clinic postalcode , cliniccontact ,hci code
            PdfPTable billTable = new PdfPTable(6);
            billTable.setWidthPercentage(100);
            billTable.setWidths(new float[] { 2, 3,6,2,2,2 });
            //billTable.setSpacingBefore(30.0f);
            billTable.setSpacingBefore(10.0f);
            billTable.addCell(getBillHeaderCell("Index"));
            billTable.addCell(getBillHeaderCell("Service Type"));
            billTable.addCell(getBillHeaderCell("Job Description"));
            billTable.addCell(getBillHeaderCell("Unit Price" + "\n" + "(SGD)"));
            billTable.addCell(getBillHeaderCell("No. of Hrs"));
            billTable.addCell(getBillHeaderCell("SubTotal"+ "\n" + "(SGD)"));

            //jobdescript,startdate,end date
            String invoiceDesc = paymentDTO.getJobDescription() + "\n\n"
                                  + "Start Time : " + paymentDTO.getJobStartDateTime() + "\n\n"
                                  + "End Time : " + paymentDTO.getJobEndDateTime() + "\n\n";



            billTable.addCell(getBillRowCell("1"));
            billTable.addCell(getBillRowCell("Basic Consultation"));
            billTable.addCell(getBillRowCell(invoiceDesc));
            billTable.addCell(getBillRowCell(ratePerHrTo2DP));
            billTable.addCell(getBillRowCell(numOfHours.toString()));
            billTable.addCell(getBillRowCell(totalTo2DP));

            billTable.addCell(getBillRowCell("2"));
            billTable.addCell(getBillRowCell("Extra"));
            billTable.addCell(getBillRowCell("Dummy"));
            billTable.addCell(getBillRowCell("Dummy"));
            billTable.addCell(getBillRowCell("1"));
            billTable.addCell(getBillRowCell("200.00"));


            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));


            PdfPTable validity = new PdfPTable(1);
            validity.setWidthPercentage(100);
            validity.addCell(getValidityCell(" "));
            validity.addCell(getValidityCell("Extra comments"));
            validity.addCell(getValidityCell(" * Dummy line \n   (Dummy)"));
            validity.addCell(getValidityCell(" * Dummy"));
            PdfPCell summaryL = new PdfPCell (validity);
            summaryL.setColspan (3);
            summaryL.setPadding (1.0f);
            billTable.addCell(summaryL);



            PdfPTable accounts = new PdfPTable(2);
            accounts.setWidthPercentage(100);
            accounts.addCell(getAccountsCell("Subtotal"));
            accounts.addCell(getAccountsCellR(subTotalTo2DP));
//            accounts.addCell(getAccountsCell("Discount (10%)"));
//            accounts.addCell(getAccountsCellR("1262.00"));
            accounts.addCell(getAccountsCell("Tax(2.5%)"));
            accounts.addCell(getAccountsCellR("315.55"));
            accounts.addCell(getAccountsCell("Total"));
            accounts.addCell(getAccountsCellR(totalTo2DP));
            PdfPCell summaryR = new PdfPCell (accounts);
            summaryR.setColspan (3);
            billTable.addCell(summaryR);

//            String companyInfo = "Clinic Name : " +  paymentDTO.getClinicName() + "\n"
//                    + "Clinic Address : " + paymentDTO.getClinicAddress()   + "\n"
//                    + "Clinic Contact : " + paymentDTO.getClinicContact()  + "\n"
//                    + "Clinic PostalCode : " + paymentDTO.getClinicPostalCode()  + "\n"
//                    + "Clinic HCI Code : " + paymentDTO.getClinicHciCode()  + "\n";

//            PdfPTable describer = new PdfPTable(1);
//            describer.setWidthPercentage(100);
//            describer.addCell(getdescCell(" "));
//            describer.addCell(getdescCell(companyInfo));

            document.open();//PDF document opened........
            System.out.println("doc opened..");
            //document.add(image);
            document.add(irhTable);
            document.add(locumDetails);
            document.add(locumName);
            document.add(locumContact);
            document.add(locumAddress);
            document.add(locumMedicalNo);


            document.add(ClinicDetails);
            document.add(clinicName);
            document.add(clinicContact);
            document.add(clinicAddress);
            document.add(clinicPostalCode);
            document.add(clinicHCICode);

            document.add(JobId);

            document.add(billTable);
            //document.add(validity);
           // document.add(accounts);

            document.close();
            System.out.println("Pdf created successfully..");

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setHeader() {

    }


    public static PdfPCell getIRHCell(String text, int alignment) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 16);
        /*	font.setColor(BaseColor.GRAY);*/
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static PdfPCell getIRDCell(String text) {
        PdfPCell cell = new PdfPCell (new Paragraph (text));
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
        //cell.setBorderColor(BaseColor.LIGHT_GRAY);
        cell.setBorderColor(BaseColor.BLACK);
        return cell;
    }

    public static PdfPCell getBillHeaderCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 11);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
        return cell;
    }

    public static PdfPCell getBillRowCell(String text) {
        PdfPCell cell = new PdfPCell (new Paragraph (text));
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
//        cell.setBorderWidthBottom(0);
//        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell getBillFooterCell(String text) {
        PdfPCell cell = new PdfPCell (new Paragraph (text));
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell getValidityCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorder(0);
        return cell;
    }

    public static PdfPCell getAccountsCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        cell.setPadding (5.0f);
        return cell;
    }
    public static PdfPCell getAccountsCellR(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
        cell.setPadding (5.0f);
        cell.setPaddingRight(20.0f);
        return cell;
    }

    public static PdfPCell getdescCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
//        cell.setBorderWidthLeft(0);
  //      cell.setBorderWidthTop(0);
        cell.setBorder(0);
        return cell;
    }
    }

