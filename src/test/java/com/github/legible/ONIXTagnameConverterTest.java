package com.github.legible;

import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.github.legible.ONIXTagnameConverter.ONIXTagnameConverterException;

import org.junit.Test;

public class ONIXTagnameConverterTest {

  @Test
  public void shouldConvertReferenceNamesToShortTags() throws ONIXTagnameConverterException {

    InputStream referenceNamesSample = getClass().getClassLoader().getResourceAsStream("Onix3sample_refnames.xml");
    InputStream shortTagsSample = getClass().getClassLoader().getResourceAsStream("Onix3sample_shorttags.xml");

    ONIXTagnameConverter tagTransformer = new ONIXTagnameConverter();

    Source source = new StreamSource(referenceNamesSample);
    StringWriter writer = new StringWriter();
    tagTransformer.convertToShortTags(source, writer);

    String actual = writer.toString();
    String expected = new BufferedReader(new InputStreamReader(shortTagsSample, StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

    assertThat(actual, isIdenticalTo(expected).ignoreWhitespace());
  }

  @Test
  public void shouldConvertShortTagsToReferenceNames() throws ONIXTagnameConverterException {

    InputStream referenceNamesSample = getClass().getClassLoader().getResourceAsStream("Onix3sample_refnames.xml");
    InputStream shortTagsSample = getClass().getClassLoader().getResourceAsStream("Onix3sample_shorttags.xml");

    ONIXTagnameConverter tagTransformer = new ONIXTagnameConverter();

    Source source = new StreamSource(shortTagsSample);
    StringWriter writer = new StringWriter();
    tagTransformer.convertToReferenceTags(source, writer);

    String actual = writer.toString();
    String expected = new BufferedReader(new InputStreamReader(referenceNamesSample, StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

    assertThat(actual, isIdenticalTo(expected).ignoreWhitespace());
  }

  @Test
  public void shouldKeepReferenceTagsIfOutputFormatIsReference() throws ONIXTagnameConverterException {
    InputStream referenceTagsSample = getClass().getClassLoader().getResourceAsStream("Onix3sample_refnames.xml");
    InputStream referenceTagsSampleDuplicate = getClass().getClassLoader().getResourceAsStream("Onix3sample_refnames.xml");

    ONIXTagnameConverter tagTransformer = new ONIXTagnameConverter();

    Source source = new StreamSource(referenceTagsSample);
    StringWriter writer = new StringWriter();
    tagTransformer.convertToReferenceTags(source, writer);

    String actual = writer.toString();
    String expected = new BufferedReader(new InputStreamReader(referenceTagsSampleDuplicate, StandardCharsets.UTF_8)).lines()
      .collect(Collectors.joining("\n"));

    assertThat(actual, isIdenticalTo(expected).ignoreWhitespace());
  }

  @Test
  public void shouldKeepShortTagsIfOutputFormatIsShort() throws ONIXTagnameConverterException {
    InputStream shortTagsSample = getClass().getClassLoader().getResourceAsStream("Onix3sample_shorttags.xml");
    InputStream shortTagsSampleDuplicate = getClass().getClassLoader().getResourceAsStream("Onix3sample_shorttags.xml");

    ONIXTagnameConverter tagTransformer = new ONIXTagnameConverter();

    Source source = new StreamSource(shortTagsSample);
    StringWriter writer = new StringWriter();
    tagTransformer.convertToShortTags(source, writer);

    String actual = writer.toString();
    String expected = new BufferedReader(new InputStreamReader(shortTagsSampleDuplicate, StandardCharsets.UTF_8)).lines()
      .collect(Collectors.joining("\n"));

    assertThat(actual, isIdenticalTo(expected).ignoreWhitespace());
  }
}
