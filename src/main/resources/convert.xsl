<?xml version="1.0" encoding="UTF-8"?>
<!-- switch ONIX tagnames for ONIX 3.0, XSLT 2.0 -->
<!-- © EDitEUR, licence doi:https://doi.org/10.4400/nwgj -->
<!-- version 1.1, modified to deal with XHTML markup -->
<!-- version 1.2, modified to deal with xmlns attribute in root element -->
<!-- version 1.3, modified to use XSD schema when available -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://ns.editeur.org/">
  <xsl:param name="input-path" select="'schemas'" />
  <xsl:param name="result-document" select="'dummy.xml'" />
  <xsl:param name="dtd-path" select="''" />
  <xsl:variable name="new-namespace">
    <xsl:choose>
      <xsl:when test="local-name(/*)='ONIXMessage'">
        <xsl:value-of select="replace(namespace-uri(/*),'/reference','/short')" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="replace(namespace-uri(/*),'/short','/reference')" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="release" select="/*/@release" />
  <xsl:variable name="input-path-cleaned" select="translate(replace($input-path,' ','%20'),'\','/')" />
  <xsl:variable name="xsd-path">
    <xsl:choose>
      <xsl:when test="local-name(/*)='ONIXMessage'">
        <xsl:value-of select="'ONIX_BookProduct_3.0_reference.xsd'" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'ONIX_BookProduct_3.0_short.xsd'" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="xsd-path-absolute">
    <xsl:call-template name="make-absolute-xsd-path">
      <xsl:with-param name="input-path" select="$input-path-cleaned" />
      <xsl:with-param name="xsd-path" select="$xsd-path" />
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="xsd-content">
    <xsl:if test="$xsd-path!=''">
      <xsl:copy-of select="document($xsd-path-absolute)" />
    </xsl:if>
  </xsl:variable>
  <xsl:variable name="target">
    <xsl:choose>
      <!-- v1.1 was xsl:when test="/ONIXMessage">short</xsl:when -->
      <xsl:when test="local-name(/*)='ONIXMessage'">short</xsl:when>
      <xsl:otherwise>reference</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="/*[not(@refname or @shortname)] and count($xsd-content/*) = 0">
        <xsl:message>TRANSFORMATION ABORTED! No XSD schema or DTD found!</xsl:message>
      </xsl:when>
      <xsl:when test="not($dtd-path='')">
        <xsl:result-document href="{$result-document}" method="xml" doctype-system="{$dtd-path}">
          <xsl:apply-templates />
        </xsl:result-document>
      </xsl:when>
      <xsl:otherwise>
        <xsl:result-document href="{$result-document}" method="xml" exclude-result-prefixes="xs">
          <xsl:apply-templates />
        </xsl:result-document>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="*">
    <xsl:variable name="target-name">
      <xsl:choose>
        <xsl:when test="$target='short' and $xsd-content//xs:element[@name=local-name(current())]">
          <xsl:value-of select="$xsd-content//xs:element[@name=local-name(current())]//xs:attribute[@name='shortname']//xs:enumeration/@value" />
        </xsl:when>
        <xsl:when test="$target='short' and not(@shortname)">
          <xsl:value-of select="name()" />
        </xsl:when>
        <xsl:when test="$target='short'">
          <xsl:value-of select="@shortname" />
        </xsl:when>
        <xsl:when test="$xsd-content//xs:element[@name=local-name(current())]">
          <xsl:value-of select="$xsd-content//xs:element[@name=local-name(current())]//xs:attribute[@name='refname']//xs:enumeration/@value" />
        </xsl:when>
        <xsl:when test="not(@refname)">
          <xsl:value-of select="name()" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@refname" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="{$target-name}" namespace="{$new-namespace}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="name()='refname' or name()='shortname'" />
          <xsl:when test="name()='xsi:schemaLocation' and $target='short'">
            <xsl:attribute name="xsi:schemaLocation">
              <xsl:value-of select="replace(.,'(/|_)reference','$1short')" />
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='xsi:schemaLocation'">
            <xsl:attribute name="xsi:schemaLocation">
              <xsl:value-of select="replace(.,'(/|_)short','$1reference')" />
            </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:copy-of select="." />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <xsl:apply-templates select="*|text()" />
    </xsl:element>
  </xsl:template>
  <xsl:template match="text()">
    <xsl:copy />
  </xsl:template>
  <xsl:template name="make-absolute-xsd-path">
    <xsl:param name="input-path" />
    <xsl:param name="xsd-path" />
    <xsl:choose>
      <xsl:when test="starts-with($xsd-path,'file:/') and contains('ABCDEFGHIJKLMNOPQRSTUVWXYZ',substring($xsd-path,7,1)) and substring($xsd-path,8,1)=':'">
        <xsl:value-of select="concat('///',substring-after($xsd-path,'file:/'))" />
      </xsl:when>
      <xsl:when test="starts-with($xsd-path,'file:/')">
        <xsl:value-of select="substring-after($xsd-path,'file:')" />
      </xsl:when>
      <xsl:when test="starts-with($xsd-path,'/')">
        <xsl:value-of select="$xsd-path" />
      </xsl:when>
      <xsl:when test="starts-with($xsd-path,'./')">
        <xsl:value-of select="concat($input-path,substring-after($xsd-path,'.'))" />
      </xsl:when>
      <xsl:when test="starts-with($xsd-path,'../')">
        <xsl:variable name="new-input-path">
          <xsl:call-template name="remove-input-path-segment">
            <xsl:with-param name="input-path" select="$input-path" />
          </xsl:call-template>
        </xsl:variable>
        <xsl:call-template name="make-absolute-xsd-path">
          <xsl:with-param name="input-path" select="$new-input-path" />
          <xsl:with-param name="xsd-path" select="substring-after($xsd-path,'../')" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat($input-path,'/',$xsd-path)" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="remove-input-path-segment">
    <xsl:param name="input-path" />
    <xsl:param name="in-path" select="false()" />
    <xsl:if test="contains($input-path,'/')">
      <xsl:if test="$in-path=true()">/</xsl:if>
      <xsl:value-of select="substring-before($input-path,'/')" />
      <xsl:call-template name="remove-input-path-segment">
        <xsl:with-param name="input-path" select="substring-after($input-path,'/')" />
        <xsl:with-param name="in-path" select="true()" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
