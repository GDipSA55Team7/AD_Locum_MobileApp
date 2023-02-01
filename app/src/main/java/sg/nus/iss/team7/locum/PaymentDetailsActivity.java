package sg.nus.iss.team7.locum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.pdf.PdfRenderer.Page;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;


import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import sg.nus.iss.team7.locum.Model.PaymentDTO;
import sg.nus.iss.team7.locum.Utilities.PdfService;

public class PaymentDetailsActivity extends AppCompatActivity {

    public PaymentDTO paymentDTO;
    //public Thread bgThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        Intent intent = getIntent();
        paymentDTO = intent.getParcelableExtra("paymentDetails");

        if (paymentDTO != null) {
            System.out.println(paymentDTO.getClinicAddress());
            System.out.println(paymentDTO.getClinicName());
            System.out.println(paymentDTO.getClinicPostalCode());
            System.out.println(paymentDTO.getJobDescription());
            System.out.println(paymentDTO.getJobStartDateTime());

            String pdfFilename = paymentDTO.getFlName().replaceAll(" ", "_") + "_jobId" + paymentDTO.getJobId() + ".pdf";

            PdfService pdfService = new PdfService();
            boolean isPdfCreated = pdfService.createPDF(getApplicationContext(), pdfFilename, paymentDTO);

//            File file;
//
//            if(isPdfCreated){
//                    String pdfUrl = pdfFilename;
//                        file = new File(getFilesDir(), pdfUrl);
//                        Uri uri = Uri.fromFile(file);
//                        Intent intentToViewPDF = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(uri, "application/pdf");
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        startActivity(intentToViewPDF);
//                }
//        }

            //         final File[] pdfFile = {null};
            //           ImageView imageView = findViewById(R.id.pdf_image_view);
//            if(isPdfCreated){
//                final String pdfUrl = pdfFilename;
//                bgThread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            pdfFile[0] = new File(getFilesDir(), pdfUrl);
//                            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile[0], ParcelFileDescriptor.MODE_READ_ONLY);
//                            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
//
//                            Bitmap bitmap = Bitmap.createBitmap(pdfRenderer.getPageWidth(0), pdfRenderer.getPageHeight(0), Bitmap.Config.ARGB_8888);
//                            //Bitmap bitmap = Bitmap.createBitmap(screenWidth, (screenWidth.toFloat() / page.width * page.height).toInt(), Bitmap.Config.ARGB_8888);
//                            pdfRenderer.openPage(0).render(bitmap, null, null, Page.RENDER_MODE_FOR_DISPLAY);
//                            pdfRenderer.close();
//                            fileDescriptor.close();
//
//                            // Update the ImageView with the rendered PDF page
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    imageView.setImageBitmap(bitmap);
//                                }
//                            });
//                        } catch (FileNotFoundException e) {
//                            Log.e("PdfRenderer", "File not found: " + pdfFile[0].getAbsolutePath(), e);
//                            // Show an error message to the user
//                        } catch (IOException e) {
//                            Log.e("PdfRenderer", "Error opening file", e);
//                            // Show an error message to the user
//                        }
//                    }
//                }).start();
//            }


            //com.github.barteksc.pdfviewer.PDFView
            //3rd party pdf viewer

            if (isPdfCreated) {
                File file = new File(getFilesDir(), pdfFilename);
                if (file.exists()) {
                    PDFView pdfView = findViewById(R.id.pdfView);
                    pdfView.fromFile(file)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .defaultPage(0)
                            .load();
                } else {
                    Log.e("PDFView", "File not found at: " +pdfFilename);
                }
            } else {
                Log.e("PDFView", "Error creating PDF");
            }
        }

    }
}
