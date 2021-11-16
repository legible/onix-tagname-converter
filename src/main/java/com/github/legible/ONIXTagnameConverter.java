package com.github.legible;

import net.sf.saxon.s9api.*;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

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

  /**
   * Convert converts the XML input according to the
   * `convert.xsl` XSL stylesheet compiled in the
   * constructor of this class.
   * @param source Source of raw XML
   * @param destination Destination where converted XML will be directed
   * @throws SaxonApiException
   */
  private void convert(Source source, Destination destination) throws SaxonApiException {
    this.onixTagnameConverter.setResultDocumentHandler(uri -> destination);
    this.onixTagnameConverter.setSource(source);
    this.onixTagnameConverter.transform();
  }

  /**
   * convertToShortTags converts XML input to XML output with ONIX short tags.
   * The output format of this conversion will be short tags regardless
   * of the input format.
   * @param source Source of raw XML
   * @param destination Destination where converted XML will be directed
   * @throws SaxonApiException
   */
  private void convertToShortTags(Source source, Destination destination) throws ONIXTagnameConverterException {
    try {
      this.onixTagnameConverter.setParameter(new QName("output-format"), new XdmAtomicValue("short"));
      this.convert(source, destination);
    } catch (SaxonApiException e) {
      throw new ONIXTagnameConverterException(e);
    }
  }

  public void convertToShortTags(Source source, OutputStream outputStream) throws ONIXTagnameConverterException {
    this.convertToShortTags(source, this.processor.newSerializer(outputStream));
  }

  public void convertToShortTags(Source source, Writer outputWriter) throws ONIXTagnameConverterException {
    this.convertToShortTags(source, this.processor.newSerializer(outputWriter));
  }

  public void convertToShortTags(Source source, File outputFile) throws ONIXTagnameConverterException {
    this.convertToShortTags(source, this.processor.newSerializer(outputFile));
  }

  /**
   * convertToReferenceTags converts XML input to XML output with ONIX reference tags.
   * The output format of this conversion will be reference tags regardless
   * of the input format.
   * @param source Source of raw XML
   * @param destination Destination where converted XML will be directed
   * @throws SaxonApiException
   */
  private void convertToReferenceTags(Source source, Destination destination) throws ONIXTagnameConverterException {
    try {
      this.onixTagnameConverter.setParameter(new QName("output-format"), new XdmAtomicValue("reference"));
      this.convert(source, destination);
    } catch (SaxonApiException e) {
      throw new ONIXTagnameConverterException(e);
    }
  }

  public void convertToReferenceTags(Source source, OutputStream outputStream) throws ONIXTagnameConverterException {
    this.convertToReferenceTags(source, this.processor.newSerializer(outputStream));
  }

  public void convertToReferenceTags(Source source, Writer outputWriter) throws ONIXTagnameConverterException {
    this.convertToReferenceTags(source, this.processor.newSerializer(outputWriter));
  }

  public void convertToReferenceTags(Source source, File outputFile) throws ONIXTagnameConverterException {
    this.convertToReferenceTags(source, this.processor.newSerializer(outputFile));
  }
}
