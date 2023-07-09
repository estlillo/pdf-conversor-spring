package cl.barbatos.pdfconversor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.barbatos.pdfconversor.service.api.PdfService;

@RestController
@RequestMapping("/pdf")
public class PdfController {

  @Autowired
  private PdfService pdfService;

  @PostMapping(value = "/convert", consumes = MediaType.TEXT_HTML_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public byte[] convertHtmlToPdf(@RequestBody String htmlContent) {
    return pdfService.convertHtmlToPdf(htmlContent);
  }
}
