package cl.barbatos.pdfconversor.service.api;

public interface PdfService {

  byte[] convertHtmlToPdf(String htmlContent);

}
