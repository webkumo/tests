package net.webcumo.dealbook;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        String ordersXml;
        if (args.length > 0) {
            ordersXml = args[0];
        } else {
            ordersXml = "./sample-data/orders.xml";
        }
        OrdersProcessor processor = new OrdersProcessor();
        XMLOrdersReader reader = new XMLOrdersReader(processor);
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(ordersXml, reader);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
