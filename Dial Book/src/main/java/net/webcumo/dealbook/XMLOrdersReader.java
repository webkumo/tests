package net.webcumo.dealbook;

import net.webcumo.dealbook.entity.AddOrder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class XMLOrdersReader extends DefaultHandler {
    private final OrdersProcessor processor;

    public XMLOrdersReader(OrdersProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if ("AddOrder".equals(qName)) {
            int id = Integer.parseInt(attributes.getValue("orderId"));
            String book = attributes.getValue("book");
            String operation = attributes.getValue("operation");
            String price = attributes.getValue("price");
            int volume = Integer.parseInt(attributes.getValue("volume"));
            processor.addOrder(book, new AddOrder(id, price, operation, volume));
        } else if ("DeleteOrder".equals(qName)) {
            int id = Integer.parseInt(attributes.getValue("orderId"));
            String book = attributes.getValue("book");
            processor.removeOrder(book, id);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        processor.ordersStop();
    }
}
