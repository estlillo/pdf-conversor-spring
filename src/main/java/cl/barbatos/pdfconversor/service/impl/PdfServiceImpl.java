package cl.barbatos.pdfconversor.service.impl;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import cl.barbatos.pdfconversor.service.api.PdfService;

@Service
public class PdfServiceImpl implements PdfService {

  // Convierte el contenido HTML en PDF
  public byte[] convertHtmlToPdf(String htmlContent) {
    try {
      String inputHTML = htmlContent;
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(inputHTML.getBytes(StandardCharsets.UTF_8));

      File tempFile = createTempFile(byteArrayInputStream);

      FileInputStream fileInputStream = new FileInputStream(tempFile);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      PdfDocument pdfDocument = createPdfDocument(outputStream);

      convertHtmlToPdf(fileInputStream, pdfDocument);

      ByteArrayOutputStream outputStreamResult = new ByteArrayOutputStream();

      PdfDocument resultantDocument = createResultantPdfDocument(outputStreamResult);

      copyPagesToResultantDocument(pdfDocument, outputStream, resultantDocument);

      addPageNumbering(resultantDocument);

      closeResources(fileInputStream, tempFile, pdfDocument, resultantDocument);
      return outputStreamResult.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  // Crea un archivo temporal y copia el contenido HTML en él
  private File createTempFile(ByteArrayInputStream byteArrayInputStream) throws IOException {
    File tempFile = File.createTempFile("temp", ".html");
    Files.copy(byteArrayInputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    return tempFile;
  }

  // Crea un nuevo documento PDF y establece el tamaño de página predeterminado
  private PdfDocument createPdfDocument(ByteArrayOutputStream outputStream) {
    PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
    pdfDocument.setDefaultPageSize(PageSize.A3);
    return pdfDocument;
  }

  // Convierte el contenido HTML en PDF utilizando iText HTML Converter
  private void convertHtmlToPdf(FileInputStream fileInputStream, PdfDocument pdfDocument) throws IOException {
    ConverterProperties converterProperties = new ConverterProperties();
    converterProperties.setCharset("UTF-8");
    HtmlConverter.convertToPdf(fileInputStream, pdfDocument, converterProperties);
  }

  // Crea un nuevo documento PDF para el resultado y establece el tamaño de página
  // predeterminado
  private PdfDocument createResultantPdfDocument(ByteArrayOutputStream outputStreamResult) {
    PdfDocument resultantDocument = new PdfDocument(new PdfWriter(outputStreamResult));
    resultantDocument.setDefaultPageSize(PageSize.A4);

    return resultantDocument;
  }

  // Copia las páginas del documento original al documento resultante
  private void copyPagesToResultantDocument(PdfDocument pdfDocument, ByteArrayOutputStream outputStream,
      PdfDocument resultantDocument) throws IOException {

    pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(outputStream.toByteArray())));

    for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
      PdfPage page = pdfDocument.getPage(i);
      PdfFormXObject formXObject = page.copyAsFormXObject(resultantDocument);
      PdfCanvas pdfCanvas = new PdfCanvas(resultantDocument.addNewPage());
      pdfCanvas.addXObjectWithTransformationMatrix(formXObject, 0.7f, 0, 0, 0.7f, 6, 0);
      pdfCanvas.release();
    }
  }

  private void addPageNumbering(PdfDocument resultantDocument) throws IOException {
    for (int i = 1; i <= resultantDocument.getNumberOfPages(); i++) {
      PdfPage page = resultantDocument.getPage(i);
      PdfCanvas pdfCanvas = new PdfCanvas(page);
      pdfCanvas.beginText();
      pdfCanvas.setFontAndSize(PdfFontFactory.createFont(), 8);
      pdfCanvas.moveText(500, 15);
      pdfCanvas.showText("Página " + i + " de " + resultantDocument.getNumberOfPages());
      pdfCanvas.endText();
      pdfCanvas.release();
    }
  }

  // Cierra los recursos utilizados por el archivo temporal y los documentos PDF
  private void closeResources(FileInputStream fileInputStream, File tempFile, PdfDocument pdfDocument,
      PdfDocument resultantDocument) throws IOException {
    fileInputStream.close();
    tempFile.delete();
    pdfDocument.close();
    resultantDocument.close();
  }
}