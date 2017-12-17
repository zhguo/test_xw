package hyj.xw.util;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Map;

/**
 * Created by asus on 2017/12/17.
 */

public class NodeActionUtil {

    public static boolean isContainsStrs(AccessibilityNodeInfo root,String str2){
        String str1 = ParseRootUtil.getCurrentViewAllTexts(root);
        boolean flag = true;
        String[] str2s = str2.split("\\|");
        for(String str :str2s){
            if(!str1.contains(str)){
                flag = false;
                break;
            }
        }
        return flag;
    }

    public static boolean doClickByNodePathAndText(AccessibilityNodeInfo root,String str2,String nodePath,String nodeText,Map<String,String> record,String action,long ms){
        boolean isClick = false;
        boolean isContainsStrs = isContainsStrs(root,str2);
        AccessibilityNodeInfo nodeInfo = null;
        if(nodeText!=null){
            nodeInfo = ParseRootUtil.getNodeByPathAndText(root,nodePath,nodeText);
        }else {
            nodeInfo = ParseRootUtil.getNodePath(root,nodePath);
        }
        if(nodeInfo==null){
            System.out.println("doClickByNodePathAndText nodeText获取-->"+nodeText);
            nodeInfo = AutoUtil.findNodeInfosByText(root,nodeText);
        }
        if(isContainsStrs){
            isClick = AutoUtil.performClick(nodeInfo,record,action,ms);
        }
        System.out.println("doClickByNodePathAndText -->"+str2);
        System.out.println("doClickByNodePathAndText isContainsStrs -->"+isContainsStrs);
        System.out.println("doClickByNodePathAndText nodeInfo -->"+nodeInfo);
        System.out.println("doClickByNodePathAndText isclick -->"+isClick+" "+action);
        return isClick;
    }
    public static boolean doBack(AccessibilityService context,AccessibilityNodeInfo root, String str2, Map<String,String> record, String action, long ms){
        boolean isClick = false;
        boolean isContainsStrs = isContainsStrs(root,str2);
        if(isContainsStrs){
            AutoUtil.performBack(context,record,action);
        }
        System.out.println("doClickByNodePathAndText -->"+str2);
        System.out.println("doClickByNodePathAndText isContainsStrs -->"+isContainsStrs);
        return isClick;
    }
    public static boolean doClickByNodePathAndText(AccessibilityNodeInfo root,String str2,String nodePath,String nodeText,Map<String,String> record,String action){
        boolean isClick = false;
        boolean isContainsStrs = isContainsStrs(root,str2);
        AccessibilityNodeInfo nodeInfo = null;
        if(nodeText!=null){
            nodeInfo = ParseRootUtil.getNodeByPathAndText(root,nodePath,nodeText);
        }else {
            nodeInfo = ParseRootUtil.getNodePath(root,nodePath);
        }
        if(nodeInfo==null){
            System.out.println("doClickByNodePathAndText nodeText获取-->"+nodeText);
            nodeInfo = AutoUtil.findNodeInfosByText(root,nodeText);
        }
        if(isContainsStrs){
            isClick = AutoUtil.performClick(nodeInfo,record,action);
        }
        System.out.println("doClickByNodePathAndText -->"+str2);
        System.out.println("doClickByNodePathAndText isContainsStrs -->"+isContainsStrs);
        System.out.println("doClickByNodePathAndText nodeInfo -->"+nodeInfo);
        System.out.println("doClickByNodePathAndText isclick -->"+isClick+" "+action);
        return isClick;
    }
    public static boolean doInputByNodePathAndText(AccessibilityNodeInfo root,String str2,String nodePath,String inputStr,Map<String,String> record,String action,long ms){
        boolean isInput = false;
        boolean isContainsStrs = isContainsStrs(root,str2);
        AccessibilityNodeInfo nodeInfo = ParseRootUtil.getNodePath(root,nodePath);
        if(isContainsStrs){
            isInput = AutoUtil.performSetText(nodeInfo,inputStr,record,action);
            if(isInput){
                AutoUtil.sleep(ms);
            }
        }
        System.out.println("doClickByNodePathAndText -->"+str2);
        System.out.println("doClickByNodePathAndText isContainsStrs -->"+isContainsStrs);
        System.out.println("doClickByNodePathAndText nodeInfo -->"+nodeInfo);
        System.out.println("doClickByNodePathAndText isclick -->"+isInput+" "+action+"  >"+inputStr);
        return isInput;
    }

}