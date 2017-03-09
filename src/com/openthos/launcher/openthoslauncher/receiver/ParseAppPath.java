package com.openthos.launcher.openthoslauncher.receiver;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.launcher3.R;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ParseAppPath {
    private String mConfigurexml = "app_path.xml";
    private String mTag = "OpenthosFontsAPP";
    private Context mContext;
    public ParseAppPath(Context context){
        mContext = context;
    }

    protected Map<String,String> getAppPrivatePath(String packageName) {
        Map<String, String> appInfo = new HashMap<String, String>();
        DocumentBuilder db = null;
        DocumentBuilderFactory dbf = null;
        Element element = null;
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            Document dt = db.parse(mContext.getResources().openRawResource(R.raw.app_path));
            element = dt.getDocumentElement();
            //System.out.println("root" + element.getNodeName());
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if ("app".equals(node.getNodeName())) {
                    //Log.d(mTag, "App: "
                    //         + node.getAttributes().getNamedItem("name").getNodeValue() + ". ");
                    NodeList nodeDetail = node.getChildNodes();
                    for (int j = 0; j < nodeDetail.getLength(); j++) {
                        Node detail = nodeDetail.item(j);
                        if ("font".equals(detail.getNodeName())) {
                            //Log.d(mTag, "path: "
                            //         + detail.getAttributes().getNamedItem("name").getNodeValue()
                            //         + " " + detail.getTextContent());
                            appInfo.put(detail.getAttributes().getNamedItem("name").getNodeValue(),
                                    detail.getTextContent());
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    protected ArrayList<String> getAppList() {
        ArrayList<String> applist = new ArrayList<>();
        DocumentBuilder db = null;
        DocumentBuilderFactory dbf = null;
        Element element = null;
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            Document dt = db.parse(mContext.getResources().openRawResource(R.raw.app_path));
            element = dt.getDocumentElement();
            //Log.e("root", element.getNodeName());
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if ("app".equals(node.getNodeName())) {
                    //Log.e(mTag,node.getAttributes().getNamedItem("name").getNodeValue());
                    applist.add(node.getAttributes().getNamedItem("name").getNodeValue());
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return applist;
    }
}
