package com.github.legible;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.github.legible.ONIXTagnameConverter.ONIXTagnameConverterException;

public class App {
  public static void main(String[] args) throws ONIXTagnameConverterException {
    ONIXTagnameConverter converter = new ONIXTagnameConverter();
    Source source = new StreamSource(System.in);
    converter.convertToShortTags(source, System.out);
  }
}
