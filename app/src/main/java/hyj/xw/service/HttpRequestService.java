package hyj.xw.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;

import hyj.xw.common.CommonConstant;
import hyj.xw.dao.AppConfigDao;
import hyj.xw.model.LitePalModel.Wx008Data;
import hyj.xw.model.PhoneInfo;
import hyj.xw.model.PixPoint;
import hyj.xw.modelHttp.Device;
import hyj.xw.modelHttp.PhoneQueryVO;
import hyj.xw.modelHttp.ResponseData;
import hyj.xw.util.OkHttpUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/7/5 0005.
 */

public class HttpRequestService {
    String host;
    String deviceNum;
    String username;
    String password;
    public HttpRequestService(){
        //host = "http://lizq.ngrok.xiaomiqiu.cn";
        host = "http://"+AppConfigDao.findContentByCode(CommonConstant.APPCONFIG_HOST);
        deviceNum = AppConfigDao.findContentByCode(CommonConstant.APPCONFIG_DEVICE);
        username = AppConfigDao.findContentByCode(CommonConstant.APPCONFIG_USERNAME);
        password = AppConfigDao.findContentByCode(CommonConstant.APPCONFIG_PWD);
    }

    public String login(){
        String result = "连接失败";
        String url = host+"/user/login";
        String postBody = "{\"name\":\""+username+"\",\"password\":\""+password+"\"}";//json数据.
        System.out.println("HttpRequestService postBody---->"+postBody);
        String res  = OkHttpUtil.okHttpPostBody(url,postBody);
        System.out.println("HttpRequestService res---->"+res);
        if(res.contains("success")){
            ResponseData responseData = JSONObject.parseObject(res,ResponseData.class);
            if("成功".equals(responseData.getMsg())){
                JSONObject jsonObject = JSONObject.parseObject(responseData.getData());
                String token = jsonObject.getString("token");
                System.out.println("HttpRequestService token---->"+token);
                AppConfigDao.saveOrUpdate(CommonConstant.APPCONFIG_WY_TOKEN, token);
            }
            result = responseData.getMsg();;
        }
        System.out.println("HttpRequestService result---->"+result);
       return result;
    }
    //获取手机号
    public String getPhone(String domain){
        String result = "";
        try {
            PhoneQueryVO phoneQueryVO = new PhoneQueryVO(username,deviceNum);
            phoneQueryVO.setDomain(domain);
            String reqBody = JSON.toJSONString(phoneQueryVO);
            String url =host+"/phone/query";
            System.out.println("HttpRequestService getPhone url-->"+url);
            System.out.println("HttpRequestService getPhone reqBody-->"+reqBody);
            String res = OkHttpUtil.okHttpPostBody(url,reqBody);
            System.out.println("HttpRequestService getPhone res-->"+res);
            if(res.contains("phone")){
                JSONObject jsonObject = getJSONObjectData(res);
                result = jsonObject.getString("phone");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("HttpRequestService getPhone result-->"+result);
        return result;
    }

    //更新注册状态
    public String updateRegStatus(String phone,String loginResult){
        String result = "";
        int status=0;
        if(loginResult.contains("success")){
            status = 1;
        }else if(loginResult.contains("regExp")){
            if(loginResult.contains("二维码")){
                status = 2;
            }else if(loginResult.contains("改机失败")) {
                status = 3;
            }else if(loginResult.contains("发送短信失败或超过最大尝试次数")) {
                status = 4;
            }else{
                status = 9;
            }
        }
        try {
            String url =host+"/phone/update-reg-status/"+phone+"/"+status;
            System.out.println("Test HttpRequestService updateRegStatus url-->"+url);
            result = OkHttpUtil.okHttpPostBody(url,getTestJSON());
            System.out.println("Test HttpRequestService updateRegStatus res-->"+result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public String getTestJSON(){
        String json = "{\"a\":1}";
        return json;
    }

    //获取维护数据
    public Wx008Data getMaintainData(){
        Wx008Data wx008Data = null;
        try {
            String url =host+"/wxdata/get-maintain/"+deviceNum;
            System.out.println("HttpRequestService getMaintainData url-->"+url);
            String res = OkHttpUtil.okHttpGet(url);
            System.out.println("HttpRequestService getMaintainData res-->"+res);
            if(res.contains("data")){
                ResponseData responseData = JSONObject.parseObject(res,ResponseData.class);
                wx008Data = JSONObject.parseObject(responseData.getData(),Wx008Data.class);
                System.out.println("HttpRequestService getMaintainData wx008Data-->"+ JSON.toJSONString(wx008Data));
                wx008Data.setPhoneInfo1(new PhoneInfo());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return wx008Data;
    }
    //反馈维护结果
    public String updateMaintainStatus(String json){
        String url =host+"/wxdata/complete-maintain";
        System.out.println("HttpRequestService updateMaintainStatus url-->"+url);
        String res = OkHttpUtil.okHttpPostBody(url,json);
        System.out.println("HttpRequestService updateMaintainStatus res-->"+res);
        return res;
    }

    //设备连接
    public String deviceConnectServer(){
        String url =host+"/device/connect";
        System.out.println("HttpRequestService deviceConnectServer url--->"+url);
        Device device = new Device();
        device.setNum(deviceNum);
        String res = OkHttpUtil.okHttpPostBody(url,JSON.toJSONString(device));
        System.out.println("HttpRequestService deviceConnectServer --->"+res);
        return res;
    }
    //获取设备配置
    public String getStartConifgFromServer(String deviceNum){
        String url =host+"/device/num/"+deviceNum;
        System.out.println("HttpRequestService getStartConifgFromServer url--->"+url);
        String res = OkHttpUtil.okHttpGet(url);
        System.out.println("HttpRequestService getStartConifgFromServer res--->"+res);
        return res;
    }



    public String uploadPhoneData(String wx008DataStr){
        String result = "";
        String url =host+"/wxdata/save";
        System.out.println("提交数据 HttpRequestService uploadPhoneData wx008DataStr--->"+wx008DataStr);
        System.out.println("HttpRequestService uploadPhoneData url--->"+url);
        String res = OkHttpUtil.okHttpPostBody(url,wx008DataStr);
        System.out.println("HttpRequestService uploadPhoneData res--->"+res);
        if(res.contains("data")){
            ResponseData responseData = JSONObject.parseObject(res,ResponseData.class);
            result = responseData.getData();
        }
        return result;
    }

    public JSONObject getJSONObjectData(String json){
        ResponseData responseData = JSONObject.parseObject(json,ResponseData.class);
        JSONObject jsonObject = JSONObject.parseObject(responseData.getData());
        return jsonObject;
    }


    /**
     * 上传图片
     */
    //File file = new File(FilePathCommon.fkScreenShotPath);
                    /*File file = waitAndGetFile();
                    if(file!=null){
                          System.out.println("doA main res-->fileName:"+file.getName()+" length:"+file.length());
                          String host = "http://192.168.1.5";
                          String url =host+"/commons/pic-loc";
                          String res = OkHttpUtil.upload(url,file);
                          System.out.println("doA main OkHttpUtil res-->"+res);
                          if(res.contains("data")){
                              FileUtil.writeContent2FileForceUtf8(FilePathCommon.fkFilePath, res);
                              boolean flag = file.delete();
                              System.out.println("main res 删除："+flag);
                          }
                      }else {
                          System.out.println("doA main res-->file is null");
                      }*/


    //上次图片
    public static String uploadImage(String url,File file){
        String reponseData = "";
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file","head_image",fileBody)
                .addFormDataPart("name","file");

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwibmFtZSI6ImFkbWluIiwibmlja25hbWUiOiLnrqHnkIblkZgiLCJhdmF0YXIiOiIyMDE4MDcwMjA0NTAyMy5wbmciLCJpYXQiOjE1MzA0NzgyMzMsImV4cCI6MTUzODI1NDIzM30.2Ji5dmWTpKKZAW15vli7Of4ggjgzvB5zPFq7PlpsP1GkTG-F0U6Joqsu_HkSEbl5iIwpqT3hY-J5fpuPgOwOAA")
                .post(requestBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            reponseData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  reponseData;

    }
}