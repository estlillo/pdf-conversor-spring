package cl.barbatos.pdfconversor.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

@Controller
public class PdfController {

  public static class ConvertPdfRequest {
    public String html;
  }

  @PostMapping(value = "/pdf/convert", consumes = MediaType.TEXT_HTML_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  @ResponseBody
  public byte[] convertHtmlToPdf(@RequestBody String htmlContent) {

    try {
      String inputHTML = htmlContent;

      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
          inputHTML.getBytes(StandardCharsets.UTF_8));

      File tempFile = File.createTempFile("temp", ".html");
      java.nio.file.Files.copy(byteArrayInputStream, tempFile.toPath(),
          java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      FileInputStream fileInputStream = new FileInputStream(tempFile);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));

      pdfDocument.setDefaultPageSize(PageSize.A3);

      ConverterProperties converterProperties = new ConverterProperties();
      converterProperties.setCharset("UTF-8");

      HtmlConverter.convertToPdf(fileInputStream, pdfDocument, converterProperties);

      ByteArrayOutputStream outputStreamResult = new ByteArrayOutputStream();

      PdfDocument resultantDocument = new PdfDocument(new PdfWriter(outputStreamResult));
      resultantDocument.setDefaultPageSize(PageSize.A4);
      pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(outputStream.toByteArray())));
      for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
        PdfPage page = pdfDocument.getPage(i);
        PdfFormXObject formXObject = page.copyAsFormXObject(resultantDocument);
        PdfCanvas pdfCanvas = new PdfCanvas(resultantDocument.addNewPage());
        pdfCanvas.addXObjectWithTransformationMatrix(formXObject, 0.7f, 0, 0, 0.7f, 6, 0);
      }

      fileInputStream.close();
      tempFile.delete();

      pdfDocument.close();
      resultantDocument.close();

      // Return the PDF content as a byte array
      return outputStreamResult.toByteArray();
    } catch (IOException e) {
      // Handle any exceptions
      return null;
    }
  }

}
