package com.legible.onixtagnameconverter;

import net.sf.saxon.s9api.*;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Paths;

/**
 * ONIXTagnameConverter
 */
public class ONIXTagnameConverter {
  private Processor processor;
  private XsltTransformer onixTagnameConverter;

  public static class ONIXTagnameConverterException extends Exception {
    public ONIXTagnameConverterException(Throwable throwable) {
      super(throwable);
    }
  }

  private class ClasspathResourceURIResolver implements URIResolver {
    @Override
    public Source resolve(String href, String base) throws TransformerException {
      return new StreamSource(this.getClass().getClassLoader().getResourceAsStream(href));
    }
  }

  public ONIXTagnameConverter() throws ONIXTagnameConverterException {
    try {
      StreamSource transformReferenceTagStylesheet = new StreamSource(
          this.getClass().getClassLoader().getResourceAsStream("convert.xsl"));

      Processor processor = new Processor(false);
      this.processor = processor;

      XsltCompiler compiler = processor.newXsltCompiler();

      XsltExecutable convertTagsStylesheet = compiler.compile(transformReferenceTagStylesheet);
      XsltTransformer ONIXTagnameConverter = convertTagsStylesheet.load();
      ONIXTagnameConverter.setURIResolver(new ClasspathResourceURIResolver());
      ONIXTagnameConverter.setDestination(processor.newSerializer());

      this.onixTagnameConverter = ONIXTagnameConverter;
    } catch (Exception e) {
      throw new ONIXTagnameConverterException(e);
    }
  }

  private void convert(Source source, Destination destination) throws SaxonApiException {
    this.onixTagnameConverter.setResultDocumentHandler(uri -> destination);
    this.onixTagnameConverter.setSource(source);
    this.onixTagnameConverter.transform();
  }

  public void convert(Source source, OutputStream outputStream) throws ONIXTagnameConverterException {
    try {
      this.convert(source, this.processor.newSerializer(outputStream));
    } catch (SaxonApiException e) {
      throw new ONIXTagnameConverterException(e);
    }
  }

  public void convert(Source source, Writer outputWriter) throws ONIXTagnameConverterException {
    try {
      this.convert(source, this.processor.newSerializer(outputWriter));
    } catch (SaxonApiException e) {
      throw new ONIXTagnameConverterException(e);
    }
  }

  public void transform(Source source, File outputFile) throws ONIXTagnameConverterException {
    try {
      this.convert(source, this.processor.newSerializer(outputFile));
    } catch (SaxonApiException e) {
      throw new ONIXTagnameConverterException(e);
    }
  }

}
