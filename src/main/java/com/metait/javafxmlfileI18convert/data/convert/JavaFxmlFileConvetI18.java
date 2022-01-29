package com.metait.javafxmlfileI18convert.data.convert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaFxmlFileConvetI18 {
    private File inputFileFxml;
    private File outputFileFxml;
    private File outputFileProperties;
    private JavaFxmlDataConvetI18 converter = null;

    public JavaFxmlFileConvetI18()
    {
        converter = new JavaFxmlDataConvetI18();
    }

    public File getInputFileFxml() {
        return inputFileFxml;
    }

    public void setInputFileFxml(File inputFileFxml) {
        this.inputFileFxml = inputFileFxml;
    }

    public File getOutputFileFxml() {
        return outputFileFxml;
    }

    public void setOutputFileFxml(File outputFileFxml) {
        this.outputFileFxml = outputFileFxml;
    }

    public File getOutputFileProperties() {
        return outputFileProperties;
    }

    public void setOutputFileProperties(File outputFileProperties) {
        this.outputFileProperties = outputFileProperties;
    }

    public JavaFxmlDataConvetI18 getConverter() {
        return converter;
    }

    public String createCheckReport(String uiFxmlxml, String uilangProperties)
    {
        converter = new JavaFxmlDataConvetI18(uiFxmlxml, uilangProperties);
        return converter.createCheckReport(uiFxmlxml, uilangProperties);
    }

    public String convert(boolean bWriteIntoOutputFiles)
            throws IOException
    {
        String ret = "";
        if (outputFileFxml != null && inputFileFxml != null
           && !outputFileFxml.getAbsolutePath().equals(inputFileFxml.getAbsolutePath()))
                return JavaFxmlDataConvetI18.constInputFileAndOutputFileAreSame;

        if (inputFileFxml != null && inputFileFxml.exists() && inputFileFxml.isFile())
        {
            String strInputFileFxml = getStringOfFile(this.inputFileFxml);
            String strOutputFileProperties = "";
            if (outputFileProperties != null && outputFileProperties.exists() && outputFileProperties.isFile())
                strOutputFileProperties = getStringOfFile(this.outputFileProperties);
            converter = new JavaFxmlDataConvetI18(strInputFileFxml, strOutputFileProperties);
            ret = converter.convertData();
            if (bWriteIntoOutputFiles) {
                String strOutputFileFxml = converter.getStrOutputFileFxml();
                strOutputFileProperties = converter.getStrOutputFileProperties();
                if (strOutputFileFxml != null && strOutputFileProperties != null) {
                    writeIntoFile(this.outputFileFxml, strOutputFileFxml);
                    writeIntoFile(this.outputFileProperties, strOutputFileProperties);
                }
            }
        }
        return ret;
    }

    public static void writeIntoFile(File outf, String strData)
            throws IOException
    {
        if (outf == null)
            return;
        if (!outf.exists())
            return;
        String fileName = outf.getAbsolutePath();
        Path path = Path.of(fileName);
        Files.writeString(path, strData);
    }

    public static String getStringOfFile(File inf)
            throws IOException
    {
        if (inf == null)
            return "";
        if (!inf.exists())
            return "";

        String ret = "";
        String fileName = inf.getAbsolutePath();
        ret = Files.readString(Path.of(fileName));
        return ret;
    }
}
