package com.iflytek.cetsim.common.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pengwang on 2017/3/23.
 */
public class Dom4jUtils {
    /**
     * 通过文件的路径获取xml的document对象
     *
     * @param path  文件的路径
     * @return      返回文档对象
     */
    public static Document getXMLByFilePath(String path) {
        if (null == path) {
            return null;
        }
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 获取某个元素的所有的子节点
     * @param node  制定节点
     * @return      返回所有的子节点
     */
    public static List<Element> getChildElements(Element node) {
        if (null == node) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<Element> lists = node.elements();
        return lists;
    }

    /**
     * 通过XPath获取结点
     * @param doc
     * @param rule
     * @return
     */
    public static NodeList getNodesByXPath(Document doc, String rule) {
        try {
            doc.selectNodes("/papers/paper");
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(rule);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            return nodes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
