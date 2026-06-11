package com.never_give_up.automation.Component;//package com.never_give_up.automation.Component;
//
//import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
//import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
//import com.aliyun.teaopenapi.models.Config;
//import org.springframework.stereotype.Component;
//
///**
// * @version :
// * @authoe :CYH
// * @date :2022 03 28
// * @Description :
// * @modified By   ：
// */
//@Component
//public class SMSComponent {
//    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
//        Config config = new Config()
//                // 您的AccessKey ID
//                .setAccessKeyId(accessKeyId)
//                // 您的AccessKey Secret
//                .setAccessKeySecret(accessKeySecret);
//        // 访问的域名
//        config.endpoint = "dysmsapi.aliyuncs.com";
//        return new com.aliyun.dysmsapi20170525.Client(config);
//    }
//
//    public SendSmsResponse smsResponse(String PhoneNumbers, String Code) {
//        com.aliyun.dysmsapi20170525.Client client = null;
//        SendSmsResponse sendSmsResponse = null;
//        try {
//            client = SMSComponent.createClient("ACCESS_KEY_ID_REMOVED", "ACCESS_KEY_SECRET_REMOVED");
//            SendSmsRequest sendSmsRequest = new SendSmsRequest()
//                    .setSignName("阿里云短信测试")
//                    .setTemplateCode("SMS_154950909")
//                    .setPhoneNumbers(PhoneNumbers)
//                    .setTemplateParam("{\"code\":\"" + Code + "\"}");
//            // 复制代码运行请自行打印 API 的返回值
//            sendSmsResponse = client.sendSms(sendSmsRequest);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return sendSmsResponse;
//    }
//}
