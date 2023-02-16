package sg.nus.iss.team7.locum;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

import sg.nus.iss.team7.locum.Model.PaymentDetailsDTO;
import sg.nus.iss.team7.locum.Utilities.DatetimeParser;

public class PaymentDetailsActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 2;
    private final ActivityResultContracts.CreateDocument createDocumentContract =
            new ActivityResultContracts.CreateDocument("application/pdf");
    private PaymentDetailsDTO paymentDetailsDTO;
    private ByteArrayOutputStream outputStream;
    private Document document = null;
    private ActivityResultLauncher activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        Intent intent = getIntent();
        paymentDetailsDTO = intent.getParcelableExtra("paymentDetails");

        //registers callback for createDocumentContract
        activityResultLauncher = registerForActivityResult(createDocumentContract, new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                //Uri is the location of saved PDF
                if (uri != null) {
                    try {
                        savePdfToUri(uri);
                        Toast.makeText(PaymentDetailsActivity.this, "PDF saved successfully!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (paymentDetailsDTO != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    outputStream = generateTempPDF(paymentDetailsDTO);
                    if (outputStream != null) {
                        displayPDF(outputStream.toByteArray());
                    } else {
                        System.out.println("generateTempPDF failed to generate byteArrOutputStream");
                    }
                }
            }).start();
        }

        Button downloadButton = findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });
    }

    private void requestPermission() {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            showFilePicker();
        } else {
            getLifecycle().addObserver(new DefaultLifecycleObserver() {
                @Override
                public void onStart(@NonNull LifecycleOwner owner) {
                    showFilePicker();
                    getLifecycle().removeObserver(this);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showFilePicker();
        } else {
            Toast.makeText(this, "Storage permission is required to save PDF document.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFilePicker() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "mydoc.pdf");//suggets filename
        activityResultLauncher.launch(paymentDetailsDTO.getFlName() + "_jobId_" + paymentDetailsDTO.getJobId());
        System.out.println("after startActivity");
    }

    private void savePdfToUri(Uri uri) {
        try {
            // opens an output stream to the specified Uri
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            //Writes the byte array to the output stream
            outputStream.write(generateTempPDF(paymentDetailsDTO).toByteArray());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private ByteArrayOutputStream generateTempPDF(PaymentDetailsDTO paymentDTO) {
        try {
            if (document == null) {
                outputStream = new ByteArrayOutputStream();
                document = new Document();
                PdfWriter.getInstance(document, outputStream);
                createPDFDoc(document, paymentDTO);
            }
            return outputStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void displayPDF(byte[] pdfByteArrData) {
        try {
            PDFView pdfView = findViewById(R.id.pdfView);
            pdfView.fromBytes(pdfByteArrData)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPDFDoc(Document document, PaymentDetailsDTO paymentDTO) throws ParseException {

        Double ratePerHr;
        String totalTo2DP, ratePerHrTo2DP, subTotalTo2DP;
        String paymentDate = "N.A.", paymentRefNo = "N.A.";

        //jobDuration
        String jobDurationString = DatetimeParser.getHoursBetween(paymentDTO.getJobStartDateTime(), paymentDTO.getJobEndDateTime());
        String removedExtraTxt = jobDurationString.substring(0, jobDurationString.indexOf(" "));
        Double jobDurationRoundedToHalfHr = Math.round(Double.valueOf(removedExtraTxt) * 2) / 2.0;

        //Rate
        ratePerHr = paymentDTO.getJobRatePerHr();
        ratePerHrTo2DP = String.format("%.2f", ratePerHr);

        PdfPTable irdTable = new PdfPTable(2);
        irdTable.addCell(getIRDCell("Invoice No"));
        irdTable.addCell(getIRDCell("Invoice Date"));
        Log.e("paymentDate", paymentDTO.getJobPaymentDate());
        Log.e("paymentRefNo", paymentDTO.getJobPaymentRefNo());

        if (!paymentDTO.getJobPaymentDate().equalsIgnoreCase("") &&
                !paymentDTO.getJobPaymentDate().equalsIgnoreCase("null")
        ) {
            paymentDate = paymentDTO.getJobPaymentDate();
        }

        Log.e("paymentDate", "paymentDate is: " + paymentDate);
        if (!paymentDTO.getJobPaymentRefNo().equalsIgnoreCase("") ||
                !paymentDTO.getJobPaymentDate().equalsIgnoreCase("null")
        ) {
            paymentRefNo = paymentDTO.getJobPaymentRefNo();
        }

        // Invoice number and date
        irdTable.addCell(getIRDCell(paymentRefNo));
        irdTable.addCell(getIRDCell(paymentDate));

        PdfPTable irhTable = new PdfPTable(3);
        irhTable.setWidthPercentage(100);

        irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
        irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
        irhTable.addCell(getIRHCell("Payment Slip", PdfPCell.ALIGN_RIGHT));
        irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
        irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
        PdfPCell invoiceTable = new PdfPCell(irdTable);
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
        Paragraph locumMedicalNo = new Paragraph("Medical License No. : " + paymentDTO.getFlMedicalLicenseNo());
        locumMedicalNo.setIndentationLeft(20);
        locumMedicalNo.setSpacingAfter(20f);

        Phrase ClinicDetails = fs.process("Clinic Details :");

        Paragraph clinicName = new Paragraph("Name : " + paymentDTO.getClinicName());
        clinicName.setIndentationLeft(20);
        Paragraph clinicContact = new Paragraph("Contact No. : " + paymentDTO.getClinicContact());
        clinicContact.setIndentationLeft(20);
        Paragraph clinicAddress = new Paragraph("Address : " + paymentDTO.getClinicAddress());
        clinicAddress.setIndentationLeft(20);
        Paragraph clinicPostalCode = new Paragraph("Postal Code. : " + paymentDTO.getClinicPostalCode());
        clinicPostalCode.setIndentationLeft(20);
        Paragraph clinicHCICode = new Paragraph("HCI Code : " + paymentDTO.getClinicHciCode());
        clinicHCICode.setIndentationLeft(20);
        clinicHCICode.setSpacingAfter(20f);

        Phrase JobId = fs.process("JobId : " + paymentDTO.getJobId());

        PdfPTable billTable = new PdfPTable(6);
        billTable.setWidthPercentage(100);
        try {
            billTable.setWidths(new float[]{2, 3, 6, 2, 2, 2});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        billTable.setSpacingBefore(10.0f);
        billTable.addCell(getBillHeaderCell("Index"));
        billTable.addCell(getBillHeaderCell("Service Type"));
        billTable.addCell(getBillHeaderCell("Job Description"));
        billTable.addCell(getBillHeaderCell("Unit Price" + "\n" + "(SGD)"));
        billTable.addCell(getBillHeaderCell("No. of Hrs"));
        billTable.addCell(getBillHeaderCell("SubTotal" + "\n" + "(SGD)"));

        String startDateTime = paymentDTO.getJobStartDateTime();
        String[] SDT = startDateTime.split("T");
        String startDate = SDT[0];
        String startTime = SDT[1];

        String endDateTime = paymentDTO.getJobEndDateTime();
        String[] EDT = endDateTime.split("T");
        String endDate = EDT[0];
        String endTime = EDT[1];

        String invoiceDesc = "Job Description : " + paymentDTO.getJobDescription() + "\n\n"
                + "Start Date : " + startDate + "\n\n"
                + "Start Time : " + startTime + "\n\n"
                + "End Date : " + endDate + "\n\n"
                + "End Time : " + endTime + "\n\n";
        Log.e("desc", paymentDTO.getJobDescription());
        Log.e("start", paymentDTO.getJobStartDateTime());
        Log.e("end", paymentDTO.getJobEndDateTime());

        //total rate
        Double subTotal = jobDurationRoundedToHalfHr * ratePerHr;
        subTotalTo2DP = String.format("%.2f", subTotal);
        String jobDurationRoundedToHalfHrStr = String.format("%.1f", jobDurationRoundedToHalfHr);

        billTable.addCell(getBillRowCell("1"));
        billTable.addCell(getBillRowCell("Basic Consultation"));
        billTable.addCell(getBillRowCell(invoiceDesc));
        billTable.addCell(getBillRowCell(ratePerHrTo2DP));
        billTable.addCell(getBillRowCell(jobDurationRoundedToHalfHrStr));
        billTable.addCell(getBillRowCell(subTotalTo2DP));

        Double Total = subTotal;

        int idx = 2;
        long count = 0;
        String additionalFeeStr = paymentDTO.getJobAdditionalFees();
        //if length >1 ,at least 1 additional fee
        if (additionalFeeStr.length() > 1) {
            count = additionalFeeStr.chars().filter(c -> c == ';').count();
            //at least 2 additional fees
            if (count == 1) {
                String[] allAdditionalFees = paymentDTO.getJobAdditionalFees().split(";");
                for (int i = 0; i <= count; i++) {
                    String[] eachAdditionalFee = allAdditionalFees[i].split(",");
                    String additionalFee = eachAdditionalFee[0];
                    System.out.println(additionalFee);
                    String description = eachAdditionalFee[1];
                    System.out.println(description);

                    billTable.addCell(getBillRowCell(String.valueOf(idx)));
                    billTable.addCell(getBillRowCell("Additional"));
                    billTable.addCell(getBillRowCell(description));
                    billTable.addCell(getBillRowCell("NA"));
                    billTable.addCell(getBillRowCell("NA"));
                    billTable.addCell(getBillRowCell(additionalFee));

                    idx += 1;
                    subTotal += Double.valueOf(additionalFee);
                    Total += Double.valueOf(additionalFee);
                }
            }
            //1 additional fee
            else {
                Log.e("1 additional fees", "1 additional fees");
                String[] eachAdditionalFee = additionalFeeStr.split(",");
                String additionalFee = eachAdditionalFee[0];
                String description = eachAdditionalFee[1];

                billTable.addCell(getBillRowCell(String.valueOf(idx)));
                billTable.addCell(getBillRowCell("Additional"));
                billTable.addCell(getBillRowCell(description));
                billTable.addCell(getBillRowCell("NA"));
                billTable.addCell(getBillRowCell("NA"));
                billTable.addCell(getBillRowCell(additionalFee));

                subTotal += Double.valueOf(additionalFee);
                Total += Double.valueOf(additionalFee);
            }
        }
        //no additional fee if ""
        subTotalTo2DP = String.format("%.2f", subTotal);
        totalTo2DP = String.format("%.2f", Total);

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
        //  validity.addCell(getValidityCell(" * Dummy line \n   (Dummy)"));
        // validity.addCell(getValidityCell(" * Dummy"));
        PdfPCell summaryL = new PdfPCell(validity);
        summaryL.setColspan(3);
        summaryL.setPadding(1.0f);
        billTable.addCell(summaryL);

        PdfPTable accounts = new PdfPTable(2);
        accounts.setWidthPercentage(100);
        accounts.addCell(getAccountsCell("Subtotal(SGD)"));
        accounts.addCell(getAccountsCellR(subTotalTo2DP));
        accounts.addCell(getAccountsCell("Total(SGD)"));
        accounts.addCell(getAccountsCellR(totalTo2DP));
        PdfPCell summaryR = new PdfPCell(accounts);
        summaryR.setColspan(3);
        billTable.addCell(summaryR);

        document.open();
        System.out.println("doc opened..");
        try {
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
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();
    }

    private PdfPCell getValidityCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(0);
        return cell;
    }

    private PdfPCell getAccountsCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        cell.setPadding(5.0f);
        return cell;
    }

    private PdfPCell getAccountsCellR(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5.0f);
        cell.setPaddingRight(20.0f);
        return cell;
    }

    private PdfPCell getBillRowCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        return cell;
    }

    private PdfPCell getIRHCell(String text, int alignment) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 16);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private PdfPCell getIRDCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        cell.setBorderColor(BaseColor.BLACK);
        return cell;
    }

    private PdfPCell getBillHeaderCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 11);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        return cell;
    }
}
