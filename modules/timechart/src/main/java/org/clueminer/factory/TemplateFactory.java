package org.clueminer.factory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.clueminer.chart.ChartFrame;
import org.clueminer.chart.Template;
import org.clueminer.utils.FileUtils;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.factory.ChartFactory;
import org.clueminer.chart.factory.OverlayFactory;
import org.clueminer.xml.XMLUtil;
import org.openide.filesystems.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Tomas Barton
 */
public class TemplateFactory {

    private static TemplateFactory instance;
    private HashMap<String, Template> templates;
    private String defTemp;
    private File defaultTemplate;
    private File templatesXML;

    public static TemplateFactory getDefault() {
        if (instance == null) {
            instance = new TemplateFactory();
        }
        return instance;
    }

    private TemplateFactory() {
        defaultTemplate = FileUtils.templatesFile("default.xml");
        templatesXML = FileUtils.templatesFile("templates.xml");
        templates = new HashMap<String, Template>();
        if (!templatesXML.exists()) {
            try {
                FileUtil.copy(
                        Template.class.getResourceAsStream("default.xml"),
                        FileUtil.createData(defaultTemplate).getOutputStream());
                FileUtil.copy(
                        Template.class.getResourceAsStream("templates.xml"),
                        FileUtil.createData(templatesXML).getOutputStream());
            } catch (IOException ex) {
                XMLUtil.createXMLDocument(defaultTemplate);
                XMLUtil.createXMLDocument(templatesXML, XMLUtil.TEMPLATES_NODE);
            }
            initTemplates();
        } else {
            initTemplates();
        }
    }

    public String getDefaultTemplate() {
        return defTemp;
    }

    public void setDefaultTemplate(String name) {
        defTemp = name;
        Document document = XMLUtil.loadXMLDocument(defaultTemplate);
        Element root = XMLUtil.getRoot(document);
        root.setTextContent(name);
        XMLUtil.saveXMLDocument(document, defaultTemplate);
    }

    public Object[] getTemplateNames() {
        return templates.keySet().toArray();
    }

    public Template getTemplate(Object key) {
        return templates.get(key);
    }

    public boolean templateExists(String name) {
        return templates.containsKey(name);
    }

    private void initTemplates() {
        Document document;
        document = XMLUtil.loadXMLDocument(defaultTemplate);
        Element temp = XMLUtil.getRoot(document);
        defTemp = temp.getTextContent();

        document = XMLUtil.loadXMLDocument(templatesXML);
        Element root = XMLUtil.getRoot(document, XMLUtil.TEMPLATES_NODE);

        NodeList nodeList = root.getElementsByTagName(XMLUtil.TEMPLATE_NODE);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            Template template = new Template(XMLUtil.getNameAttr(element));

            Element chart = XMLUtil.getChartNode(element);
            template.setChart(ChartFactory.getInstance().getProvider(XMLUtil.getNameAttr(chart)));
            Element chartProperties = XMLUtil.getPropertiesNode(chart);
            template.getChartProperties().loadFromTemplate(chartProperties);

            Element overlays = XMLUtil.getOverlaysNode(element);
            NodeList overlaysList = overlays.getElementsByTagName(XMLUtil.OVERLAY_NODE);
            for (int j = 0; j < overlaysList.getLength(); j++) {
                Element overlayNode = (Element) overlaysList.item(j);
                Overlay overlay = OverlayFactory.getInstance().getProvider(
                        XMLUtil.getNameAttr(overlayNode)).newInstance();
                Element overlayProperties = XMLUtil.getPropertiesNode(overlayNode);
                overlay.loadFromTemplate(overlayProperties);
                template.addOverlay(overlay);
            }

            templates.put(template.getName(), template);
        }
    }

    public void saveToTemplate(String name, ChartFrame chartFrame) {
        Document document = XMLUtil.loadXMLDocument(templatesXML);
        Element root = XMLUtil.getRoot(document, XMLUtil.TEMPLATES_NODE);

        // create the template node
        Element template = XMLUtil.addTemplateNode(document, root, name);
        // save template details
        //Element chart = XMLUtil.addChartNode(document, template, chartFrame.getChartData().getChart());
        //Element chartProperties = XMLUtil.addPropertiesNode(document, chart);
        //chartFrame.getChartProperties().saveToTemplate(document, chartProperties);

        List<Overlay> overlays = chartFrame.getMainPanel().getSplitPanel().getChartPanel().getOverlays();
        if (!overlays.isEmpty()) {
            Element overlaysNode = XMLUtil.addOverlaysNode(document, template);
            for (Overlay overlay : overlays) {
                //Element overlayNode = XMLUtil.addOverlayNode(document, overlaysNode, overlay);
                //Element overlayProperties = XMLUtil.addPropertiesNode(document, overlayNode);
                //overlay.saveToTemplate(document, overlayProperties);
            }
        }

        // save changes
        XMLUtil.saveXMLDocument(document, templatesXML);

        templates.clear();
        initTemplates();
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void removeTemplate(String name) {
        Document document = XMLUtil.loadXMLDocument(templatesXML);
        Element root = XMLUtil.getRoot(document, XMLUtil.TEMPLATES_NODE);
        NodeList nodeList = root.getElementsByTagName(XMLUtil.TEMPLATE_NODE);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            node.getAttributes().getNamedItem(XMLUtil.NAME_ATTR).getNodeValue();
            if (node.getAttributes().getNamedItem(XMLUtil.NAME_ATTR).getNodeValue().equals(name)) {
                root.removeChild(node);
                break;
            }
        }
        XMLUtil.saveXMLDocument(document, templatesXML);

        templates.clear();
        initTemplates();
    }
}
