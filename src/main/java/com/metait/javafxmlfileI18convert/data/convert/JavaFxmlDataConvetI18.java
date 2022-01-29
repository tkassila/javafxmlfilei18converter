package com.metait.javafxmlfileI18convert.data.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaFxmlDataConvetI18 {

    private String strInputFileFxml;
    private String strOutputFileFxml;
    private String strOutputFileProperties;
    private StringBuffer spOutputProperties;

    private List<String> oldLangVariables = new ArrayList<>();
    private int iMax_oldLangVariables = 0;
    private boolean bConvertedData = false;
    private boolean bDataModifiedByFunctions = false;
    private int iGenerateFxmlControlNameCounter = 0;
    private int iUsed_GenerateFxmlControlNameCounter = 0;
    public static String constEmptyInputData = "Input Data is empty";
    public static String constWrongInputData = "Input Data has error!";
    public static String constInputFileAndOutputFileAreSame = "Input File and output file are the ame!";
    private String uilangProperties = null;
    private StringBuffer sbReport = new StringBuffer();
    public JavaFxmlDataConvetI18(){}

    public JavaFxmlDataConvetI18(String p_strInputFileFxml, String p_strOutputFileProperties)
    {
        strInputFileFxml = p_strInputFileFxml;
        strOutputFileProperties =  p_strOutputFileProperties;
    }

    public List<String> getOldLangVariables() {
        return oldLangVariables;
    }

    public boolean isbConvertedData() {
        return bConvertedData;
    }

    public String getStrInputFileFxml() {
        return strInputFileFxml;
    }

    public void setStrInputFileFxml(String strInputFileFxml) {
        if (this.strInputFileFxml != null && strInputFileFxml != null)
        {
            if (!this.strInputFileFxml.equals(strInputFileFxml))
                bDataModifiedByFunctions = true;
        }
        else
        {
            if (this.strInputFileFxml != strInputFileFxml)
                bDataModifiedByFunctions = true;
        }
        this.strInputFileFxml = strInputFileFxml;
    }

    public String getStrOutputFileFxml() {
        return strOutputFileFxml;
    }

    public void setStrOutputFileFxml(String strOutputFileFxml) {
        if (this.strOutputFileFxml != null && strOutputFileFxml != null)
        {
            if (!this.strOutputFileFxml.equals(strOutputFileFxml))
                bDataModifiedByFunctions = true;
        }
        else
        {
            if (this.strOutputFileFxml != strOutputFileFxml)
                bDataModifiedByFunctions = true;
        }
        this.strOutputFileFxml = strOutputFileFxml;
    }

    public String getStrOutputFileProperties() {
        return strOutputFileProperties;
    }

    public void setStrOutputFileProperties(String strOutputFileProperties) {
        if (this.strOutputFileProperties != null && strOutputFileProperties != null)
        {
            if (!this.strOutputFileProperties.equals(strOutputFileProperties))
                bDataModifiedByFunctions = true;
        }
        else
        {
            if (this.strOutputFileProperties != strOutputFileProperties)
                bDataModifiedByFunctions = true;
        }
        this.strOutputFileProperties = strOutputFileProperties;
    }

    public String convertData() {
        oldLangVariables = new ArrayList<>();
        iGenerateFxmlControlNameCounter = 0;
        iUsed_GenerateFxmlControlNameCounter = 0;
        spOutputProperties = new StringBuffer();
        String strConvertState = ""; // ok
        strOutputFileFxml = null;
        strOutputFileProperties = null;
        if (strInputFileFxml == null || strInputFileFxml.trim().length() == 0)
            return constEmptyInputData;
        boolean bCollect_oldLangVariables = true;
        boolean bCheckReportDataAgainstThisData = false;
        convertInputDataIntoOutPutData(strInputFileFxml, "text", bCollect_oldLangVariables, bCheckReportDataAgainstThisData);
        iGenerateFxmlControlNameCounter = 0;
        iUsed_GenerateFxmlControlNameCounter = 0;
        spOutputProperties = new StringBuffer();
        strConvertState = ""; // ok
        strOutputFileFxml = null;
        strOutputFileProperties = null;
        bCollect_oldLangVariables = false;
        if (iMax_oldLangVariables > 0)
            iGenerateFxmlControlNameCounter = iMax_oldLangVariables;
        String newstrConvertState = convertInputDataIntoOutPutData(strInputFileFxml, "text", bCollect_oldLangVariables,
                bCheckReportDataAgainstThisData);
        strOutputFileFxml = newstrConvertState;
        strOutputFileProperties = spOutputProperties.toString();
        return strConvertState;
    }

    private boolean noLookedAttributes(String foundedTextValue)
    {
        boolean ret = false;
        if (foundedTextValue == null || foundedTextValue.trim().length()==0)
            return true;
        int ind1 = foundedTextValue.indexOf("text");
        int ind2 = foundedTextValue.indexOf("accessibleHelp");
        int ind3 = foundedTextValue.indexOf("accessibleText");
        if (ind2 == -1 && ind1 == -1 && ind3 == -1)
        {
           return true;
        }
        return ret;
    }

    private String getNewVariableName(String strFoundedTextControlStart,
                                      final String newVarText, boolean bCollect_oldLangVariables)
    {
        int iTemp = iGenerateFxmlControlNameCounter +1;
        String retTmp = "" +strFoundedTextControlStart +"" + iTemp;
        while (!bCollect_oldLangVariables && oldLangVariables != null && oldLangVariables.contains("%"+retTmp))
        {
            iTemp = iTemp +1;
            retTmp = "" +strFoundedTextControlStart +"" + iTemp;
        }
        iUsed_GenerateFxmlControlNameCounter = iTemp;
        return retTmp;
    }

    private String getIndVariable(String strInd)
    {
        if (strInd == null || strInd.trim().length() == 0)
            return null;
        int ind1_1 = strInd.indexOf('%');
        if (ind1_1 > -1)
        {
            strInd = strInd.substring(ind1_1);
            int ind1_2 = strInd.indexOf('"');
            if (ind1_2 > -1)
            {
                strInd = strInd.substring(0, ind1_2);
            }
            return strInd;
        }
        return null;
    }

    private void checkVariableAgainstUiProperttyData(String strInd)
    {
        if (strInd == null || strInd.trim().length() == 0)
            return;
        if (uilangProperties == null || uilangProperties.trim().length() == 0)
            return;
        int ind = uilangProperties.indexOf(strInd.replace("%",""));
        if (ind == -1)
            sbReport.append("Variable " +strInd +" does not in ui property data!\n");
        else
        { // check that data has lang vlaue:
            String strVariableStart = uilangProperties.substring(ind);
            int indNewLine = strVariableStart.indexOf('\n');
            if (indNewLine > -1) {
                int ind2 = strVariableStart.substring(0, indNewLine).indexOf('=');
                if (ind2 > -1)
                {
                    String strVariableValue = strVariableStart.substring(0, indNewLine).substring(ind2+1);
                    if (strVariableValue == null || strVariableValue.trim().length() == 0)
                        sbReport.append("Variable " +strInd +" does not have no language value!\n");
                }
                else
                {
                    sbReport.append("Variable " +strInd +" does missing = character after it!\n");
                }
            }
            else
            {
                sbReport.append("Variable " +strInd +" is in a wrong format!\n");
            }
        }
    }

    private void checkReportDataAgainstThisData(String foundedTextValue, int ind1, int ind2, int ind3)
    {
        if (this.uilangProperties == null || this.uilangProperties.trim().length() == 0)
            return;
        if (foundedTextValue == null || foundedTextValue.trim().length() == 0)
            return;
        if (ind1 == -1 && ind2  == -1 && ind3  == -1)
            return;
        String strInd1 = null;
        String strInd2 = null;
        String strInd3 = null;
        if (ind1 > -1 ) {
            strInd1 = foundedTextValue.substring(ind1);
            strInd1 = getIndVariable(strInd1);
            checkVariableAgainstUiProperttyData(strInd1);
        }
        if (ind2 > -1 ) {
            strInd2 = foundedTextValue.substring(ind2);
            strInd2 = getIndVariable(strInd2);
            checkVariableAgainstUiProperttyData(strInd2);
        }
        if (ind3 > -1 ) {
            strInd3 = foundedTextValue.substring(ind3);
            strInd3 = getIndVariable(strInd3);
            checkVariableAgainstUiProperttyData(strInd3);
        }
    }

    private String getNew_foundedTextValue(String strFoundedTextControlStart,
                                           String foundedTextValue, boolean bCollect_oldLangVariables,
                                           boolean bCheckReportDataAgainstThisData)
    {
        String ret = "";
        if (foundedTextValue != null && foundedTextValue.trim().length()>0) {
            final String cntText = "text";
            final String cntAccessibleHelp = "accessibleHelp";
            final String cntAccessibleText = "accessibleText";
            int ind1 = foundedTextValue.indexOf(cntText);
            int ind2 = foundedTextValue.indexOf(cntAccessibleHelp);
            int ind3 = foundedTextValue.indexOf(cntAccessibleText);
            String newVarText = cntText;
            if (ind2 > -1)
                newVarText = cntAccessibleHelp;
            else
            if (ind3 > -1)
                newVarText = cntAccessibleText;

            if (bCheckReportDataAgainstThisData)
                checkReportDataAgainstThisData(foundedTextValue, ind1, ind2, ind3);
            String newFoundedTextValue = foundedTextValue;
            String newControlVariableName = getNewVariableName(strFoundedTextControlStart,
                    newVarText, bCollect_oldLangVariables);
            if (ind1 > -1) {
                newFoundedTextValue = getNewFoundedTextValueForVariable(newControlVariableName,
                        newFoundedTextValue, cntText, bCollect_oldLangVariables );
            }
            if (ind2 > -1) {
                newFoundedTextValue = getNewFoundedTextValueForVariable(newControlVariableName,
                        newFoundedTextValue, cntAccessibleHelp, bCollect_oldLangVariables );
            }
            if (ind3 > -1) {
                newFoundedTextValue = getNewFoundedTextValueForVariable(newControlVariableName,
                        newFoundedTextValue, cntAccessibleText, bCollect_oldLangVariables );
            }
            return newFoundedTextValue;
        }
        return ret;
    }

    private int getOldVarNumber(String textvalue)
    {
        if (textvalue == null || textvalue.trim().length() == 0)
            return 0;
        int ind = textvalue.indexOf("_");
        if (ind > -1)
        {
            String strValue = textvalue.substring(0, ind);
            String patternString = "\\d";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(strValue);
            int iValue = -1;
            if (matcher.find())
            {
                String strNumber = strValue.substring(matcher.start());
                iValue = Integer.parseInt(strNumber);
            }
            return iValue;
            /*
            int max = strValue.length();
            int i = 0;
            char ch = 0;
            while (max > 0 && i < max && strValue != null
                && !Character.isDigit(strValue[i]))
            {
                i++;
            }
            strValue = strValue.substring(i);
            int iValue = Integer.parseInt(strValue);
            return iValue;
             */
        }
        return 0;
    }

    private String getKeyValuuOf(String textvalue)
    {
        String ret = textvalue;
        int ind = textvalue.indexOf("_");
        if (ind > -1)
        {
            String newKey = textvalue.substring(0, ind);
            return newKey;
        }
        return ret;
    }

    private String getNewFoundedTextValueForVariable(String strControlVariableName,
                                                     String foundedTextValue, String strAttribute,
                                                     boolean bCollect_oldLangVariables)
    {
        String ret = "";
        String patternString = "(?s)" +strAttribute+ "\\s*=\\s*(\"|')(.*?)(\"|')";
        // String patternString = "<(\\w+)\\s+(.*?)(/*>)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(foundedTextValue);
        int iStart, iEnd, iCurrentPosition = 0, iMax = foundedTextValue.length();
        if (matcher.find())
        {
            iStart = matcher.start();
            iEnd = matcher.end();
            String strStrike = matcher.group(1);
            String textvalue = matcher.group(2);
            if (textvalue == null || textvalue.trim().startsWith("%")) {
                if (bCollect_oldLangVariables && textvalue != null
                        && textvalue.trim().startsWith("%")) {
                    int iOldVarNumber = getOldVarNumber(textvalue);
                    if (iOldVarNumber > this.iMax_oldLangVariables)
                        this.iMax_oldLangVariables = iOldVarNumber;
                    String newKey = getKeyValuuOf(textvalue);
                    if (!oldLangVariables.contains(newKey))
                        oldLangVariables.add(newKey);
                }
                return foundedTextValue;
            }
            String strBefore = foundedTextValue.substring(0, iStart);
            String strAfter = foundedTextValue.substring(iEnd);
            String newTextValue = getNewTextValue(strControlVariableName, strAttribute);
            if (newTextValue == null || newTextValue.trim().length() == 0)
                return foundedTextValue;
            spOutputProperties.append(newTextValue +"=" +textvalue+"\n");
            iGenerateFxmlControlNameCounter = iUsed_GenerateFxmlControlNameCounter;
            ret = strBefore +strAttribute +"=" +strStrike +"%" +newTextValue +strStrike + strAfter;
        }
        return ret;
    }

    private String getNewTextValue(String strControlVariableName, String strAttribute)
    {
        String ret = "" +strControlVariableName +"_" +strAttribute;
        return ret;
    }

    private String convertInputDataIntoOutPutData(String inputData, String strFoundTextAttribute,
                                                  boolean bCollect_oldLangVariables, boolean bCheckReportDataAgainstThisData)
    {
        StringBuffer sp = new StringBuffer();
        String patternString = "(?s)<(\\w+)\\s+(.*?)(/*>)";
        // String patternString = "<(\\w+)\\s+(.*?)(/*>)";
        Pattern pattern = Pattern.compile(patternString), pattern2;
        Matcher matcher = pattern.matcher(inputData);
        Matcher matcher2;
        int iStart, iEnd, iCurrentPosition = 0, iMax = inputData.length();
        String strFoundedTextControlStart;
        String strStrike = "", patternString2;
        String foundedTextValue;
        String strFoundedTextControlEnd;
        String strBefore, strModifiedTextControl;
        String strAttiributes;
        String new_foundedTextValue;

        while (matcher.find())
        {
            iStart = matcher.start();
            iEnd = matcher.end();
            strBefore = inputData.substring(iCurrentPosition, iStart);
            strFoundedTextControlStart = matcher.group(1);
            foundedTextValue = matcher.group(2);
            strFoundedTextControlEnd = matcher.group(3);
            if (noLookedAttributes(foundedTextValue))
                continue;
            new_foundedTextValue = getNew_foundedTextValue(strFoundedTextControlStart, foundedTextValue, bCollect_oldLangVariables,
                    bCheckReportDataAgainstThisData);
            if (new_foundedTextValue == null || new_foundedTextValue.trim().length() == 0)
                continue;
            strModifiedTextControl = "<" +strFoundedTextControlStart +" " +new_foundedTextValue +strFoundedTextControlEnd;
            /* strFoundedTextControlEnd = matcher.group(3);
             if (strAttiributes == null || !strAttiributes.contains(strFoundTextAttribute))
               // continue;
             patternString2 = strFoundTextAttribute + "(?s)\\s*=\\s*(\"|')(.*?)(\"|')";
             pattern2 = Pattern.compile(patternString2);
             matcher2 = pattern.matcher(strAttiributes);
            if (matcher2.find())
            {
                foundedTextValue = matcher2.group(2);
            }
            else
                continue;
            strBefore = inputData.substring(iCurrentPosition, iStart);
            /*
            strStrike =  matcher.group(2);
            foundedTextValue = matcher.group(3);
            strFoundedTextControlEnd = matcher.group(4);
            strBefore = inputData.substring(iCurrentPosition, iStart);
                        strModifiedTextControl = getModifiedTextControl(strFoundTextAttribute,
                                            strStrike, strFoundedTextControlStart,
                                            foundedTextValue, strFoundedTextControlEnd);

             */
            sp.append(strBefore);
            sp.append(strModifiedTextControl); //  +"\n"
            iCurrentPosition = iEnd;
        }
        if (iCurrentPosition < iMax)
        {
            String strEndPart = inputData.substring(iCurrentPosition);
            sp.append(strEndPart);
        }
        return sp.toString();
    }

    public String getModifiedTextControl(String strFoundTextAttribute, String strStrike, String strFoundedTextControlStart,
                                         String foundedTextValue, String strFoundedTextControlEnd)
    {
        String ret = "";

        String patternString1 = "<(\\w+)";
        Pattern pattern1 = Pattern.compile(patternString1);
        Matcher matcher1 = pattern1.matcher("<" +strFoundedTextControlStart);
        String fxmlControlName = "";
        if (matcher1.find())
            fxmlControlName = matcher1.group(1);

        String patternString = "fx:id\\s*=\\s*(\"|')(.*?)(\"|')";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(strFoundedTextControlStart);
        int iStart, iEnd, iCurrentPosition = 0, iMax = strFoundedTextControlStart.length();
        String strBefore;
        StringBuffer sp = new StringBuffer();
        String fxidVariableName = null;
        while (matcher.matches())
        {
            iStart = matcher.start();
            iEnd = matcher.end();
            fxidVariableName = matcher.group(2);
            iCurrentPosition = iEnd;
            if (fxidVariableName != null && fxidVariableName.trim().length() > 0)
                break;
        }
        sp.append("<" +strFoundedTextControlStart);
        String textVariableName = null;
        if (fxidVariableName != null && fxidVariableName.trim().length()> 0)
            textVariableName = fxidVariableName;
        else
        {
                if (fxmlControlName != null && fxmlControlName.trim().length() > 0) {
                    textVariableName = fxmlControlName + iGenerateFxmlControlNameCounter;
                    iGenerateFxmlControlNameCounter++;
                }
        }
        String strNewTextVariableName = textVariableName +"_" +strFoundTextAttribute;
        spOutputProperties.append(strNewTextVariableName +"=" +foundedTextValue+"\n");
        sp.append("%" + strNewTextVariableName +strFoundedTextControlEnd +"\n");
        return sp.toString();
    }

    public String createCheckReport(String uiFxmlxml, String uilangProperties)
    {
        String ret = "";
        if (uiFxmlxml == null || uiFxmlxml.trim().length() == 0)
            ret = "No fxml xml data! No repost.";
        else
        if (uilangProperties == null || uilangProperties.trim().length() == 0)
            ret = "No language properties data! No repost.";
        else
        {
            sbReport = new StringBuffer();
            boolean bCollect_oldLangVariables = false;
            boolean bCheckReportDataAgainstThisData = true;
            this.uilangProperties = uilangProperties;
            String newstrConvertState = convertInputDataIntoOutPutData(uiFxmlxml, "text", bCollect_oldLangVariables,
                    bCheckReportDataAgainstThisData);
            return sbReport.toString();

        }
        return ret;
    }
}
