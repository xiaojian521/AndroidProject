package vr.suntec.net.vrapp.vrService.eventSys;

import android.util.Log;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VrXmlToMsg {
    private Document m_dom;
    private Element m_root;

    public VrXmlToMsg(String xmlStr) {
        initCheck(xmlStr);
    }


    public boolean initCheck(String xmlStr) {

        try {
            m_dom = DocumentHelper.parseText(xmlStr);
        } catch (DocumentException e) {
//            Log.d(VrServiceLogTag.TAG_SERVICE, "VrXmlToMsg error !!!");
            e.printStackTrace();
        }

        m_root = m_dom.getRootElement();

        if (m_root == null) {
            return false;
        }

        return true;
    }

    public String GetXMLNodeValue(String nodePath, String nodeName) {
        String nodeValue = "";
        if (m_root == null || nodeName.isEmpty()) {
            return "";
        }

        if (nodePath.isEmpty()) {
            Element element = m_root.element(nodeName);
            if (element!= null) {
                nodeValue = element.getText();
            }
        }
        else {
            String[] nodes = nodePath.split("/");

            if (nodes.length == 1) {
                for (Iterator<Element> i = m_root.elementIterator(nodePath); i.hasNext(); ) {
                    Element element = (Element)i.next();
                    nodeValue = element.elementText(nodeName);
                }
            }
            else if (nodes.length == 2){
                Element element = m_root.element(nodes[0]);
                if (element != null) {
                    Element element2 = element.element(nodes[1]);
                    if (element2 != null) {
                        Element element3 = element2.element(nodeName);
                        if (element!= null) {
                            nodeValue = element3.getText();
                        }
                    }
                }
            }
            else if (nodes.length == 3){
                Element element = m_root.element(nodes[0]);
                if (element != null) {
                    Element element2 = element.element(nodes[1]);
                    if (element2 != null) {
                        Element element3 = element2.element(nodes[2]);
                        if (element3 != null) {
                            Element element4 = element3.element(nodeName);
                            if (element4!= null) {
                                nodeValue = element4.getText();
                            }
                        }
                    }
                }
            }
        }

        return nodeValue;
    }

    public String GetXMLRootAttr(String attr) {
        if (m_root == null) {
            return "";
        }

        if (!attr.isEmpty()) {
            Attribute attrNode = m_root.attribute(attr);
            if (attrNode != null) {
                return attrNode.getValue();
            }
        }

        return "";
    }

    public List<String> GetXMLNodeList(String nodePath, String nodeName) {
        List<String> list = new ArrayList<String>();

        if (m_root == null || nodeName.isEmpty()) {
            return list;
        }

        if (nodePath.isEmpty()) {
            for (Iterator<Element> i = m_root.elementIterator(nodeName); i.hasNext(); ) {
                Element e = (Element)i.next();
                if (e != null) {
                    list.add(e.getText());
                }
            }
        }
        else {
            Element element = m_root.element(nodePath);
            if (element != null) {
                for (Iterator<Element> j = element.elementIterator(nodeName); j.hasNext(); ) {
                    Element e = (Element)j.next();
                    if (e != null) {
                        list.add(e.getText());
                    }
                }
            }
        }

        return list;
    }

    public List<Map<String, String>> GetXMlListMap(String nodePath, String nodeName) {
        String[] nodes = nodePath.split("/");

        List<Map<String, String> > list = new ArrayList<Map<String, String> >();

        if (nodeName.isEmpty() || m_root == null) {
            return list;
        }

        if (nodePath.isEmpty()) {
            for (Iterator<Element> i = m_root.elementIterator(nodeName); i.hasNext(); ) {
                Element elem = (Element)i.next();

                Map<String, String> map = new HashMap<String, String>();
                for (Iterator<Element> j = elem.elementIterator(); j.hasNext(); ) {
                    Element e = (Element)j.next();
                    map.put(e.getName(), e.getText());
                }
                list.add(map);
            }
        }
        else if (nodes.length == 1) {
            Element element = m_root.element(nodePath);
            for (Iterator<Element> i = element.elementIterator(nodeName); i.hasNext(); ) {
                Element elem = (Element)i.next();
                Map<String, String> map = new HashMap<String, String>();
                for (Iterator<Element> j = elem.elementIterator(); j.hasNext(); ) {
                    Element e = (Element)j.next();
                    map.put(e.getName(), e.getText());
                }
                list.add(map);
            }
        }
        else if (nodes.length == 2) {
            Element element = m_root.element(nodes[0]);
            Element element2 = element.element(nodes[1]);

            for (Iterator<Element> i = element2.elementIterator(nodeName); i.hasNext(); ) {
                Element elem = (Element)i.next();
                Map<String, String> map = new HashMap<String, String>();
                for (Iterator<Element> j = elem.elementIterator(); j.hasNext(); ) {
                    Element e = (Element)j.next();
                    map.put(e.getName(), e.getText());
                }
                list.add(map);
            }
        }

        return list;
    }

    public String GetXMLNodeValuePath(String nodePath) {
        Node node = m_dom.selectSingleNode(nodePath);

        if (node != null) {
            return node.getText();
        }

        return "";
    }

    public List<String> GetXMLNodeListPath(String nodePath) {
        List<Node> listNode = m_dom.selectNodes(nodePath);
        List<String> list = new ArrayList<String>();

        for (Node node : listNode) {
            if (node != null) {
                list.add(node.getText());
            }
        }

        return list;
    }

    public List<Map<String, String> > GetXMlListMapPath(String nodePath) {
        List<Node> listNodes = m_dom.selectNodes(nodePath);
        List<Map<String, String> > list = new ArrayList<Map<String, String> >();

        for (Node node : listNodes) {
            if (node != null) {
                Element elem = (Element)node;
                Map<String, String> map = new HashMap<String, String>();
                for (Iterator<Element> j = elem.elementIterator(); j.hasNext(); ) {
                    Element e = (Element)j.next();
                    map.put(e.getName(), e.getText());
                }
                list.add(map);
            }
        }

        return list;
    }
}
