package com.legible.onixtagnameconverter;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.legible.onixtagnameconverter.ONIXTagnameConverter.ONIXTagnameConverterException;

public class App {
  public static void main(String[] args) throws ONIXTagnameConverterException {
    ONIXTagnameConverter converter = new ONIXTagnameConverter();
    Source source = new StreamSource(System.in);
    converter.convert(source, System.out);
  }
}
