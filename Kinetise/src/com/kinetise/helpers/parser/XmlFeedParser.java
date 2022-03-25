package com.kinetise.helpers.parser;

import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Namespaces;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.UsingFields;
import com.kinetise.support.logger.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.Charset;

public class XmlFeedParser {

    // 8192 is the default buffer size for buffered input; we need to read only first line of XML so should be enough
    public static final int ENCODING_READ_LIMIT = 8192;
    private static final String ENCODING_VALUE_PREFIX = "encoding=";


    public static DataFeed parseFromStream(InputStream stream, String itemXPath,
                                           Namespaces namespaces, String keyNotFoundMessage, UsingFields usingFields, String nextPageXPath) throws IOException, SAXException, ParserConfigurationException {
        DataFeed feedDesc = new DataFeed();

        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

        // detect encoding based on attribute in XML
        String encoding = readEncodingForStream(stream);
        stream.reset();


        try {
            parser.parse(new InputSource(new InputStreamReader(stream, encoding)), new XpathFeedParserHandler(feedDesc, itemXPath, namespaces, keyNotFoundMessage, usingFields, nextPageXPath));
        } catch (SAXEndedEarly ex) {
            Logger.v("FeedParser", "parseFromStream", "Parser ended early");
            ex.printStackTrace();
        }
        feedDesc.trimValues();

        return feedDesc;
    }

    /**
     * Check encoding without reading entire stream
     */
    private static String readEncodingForStream(InputStream is) throws InterruptedIOException {
        // for the current implementation input stream should be buffered for better performance

        is.mark(ENCODING_READ_LIMIT);
        String encoding = "UTF-8";
        try {
            encoding = getEncoding(is);
        } catch (InterruptedIOException e){
            throw e;
        }
        catch (Exception e) {
            //W przypadku problemu zakadamy ze kodownie jest standardowe
            encoding = "UTF-8";
            e.printStackTrace();
        } finally {
            try {
                is.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return encoding;
    }

    private static String getEncoding(InputStream is) throws IOException {

        int charIndex = 0;
        int currentCharacter;
        int charNum = 0;
        int maxEncodingIndex = ENCODING_READ_LIMIT - (ENCODING_VALUE_PREFIX.length() + 10);

        while ((currentCharacter = is.read()) != -1 && charNum < maxEncodingIndex) {
            charNum++;
            if ((char) currentCharacter == ENCODING_VALUE_PREFIX.charAt(charIndex)) {
                charIndex++;
            } else {
                charIndex = 0;
            }
            if (charIndex == ENCODING_VALUE_PREFIX.length()) {
                if ((currentCharacter = is.read()) == '"' || currentCharacter == '\'') {
                    String charset = readEncoding(is);
                    if (isCharsetSupported(charset)) {
                        return charset;
                    } else {
                        break;
                    }
                }
            }
        }

        return "UTF-8";
    }

    private static String readEncoding(InputStream bis) throws IOException {
        StringBuilder sb = new StringBuilder();
        int character;
        while ((character = bis.read()) != -1 && character != '\'' && character != '"') {
            sb.append((char) character);
        }
        return sb.toString();
    }

    private static boolean isCharsetSupported(String name) {
        try {
            Charset.forName(name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
