package com.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class WechatController {



    private final String URL = "http://192.168.255.10:8090";

    private final String ROBOTID = "wxid_pxbnrmij6hqz12";

    private List<String> keywords = Arrays.asList("天气预报","天气","点一首","我想听","点歌","毒鸡汤","二次元","经典语录","历史上的今天","电脑壁纸","Pepsi","抖音视频","小段子","每日一言","网抑云","土味情话","舔狗日记");

    private final List<String> groups = Arrays.asList("19213818230@chatroom"); //群聊wxid

    private static Map<String,List<String>> pushs = new HashMap();

    static {

        //设置推送人或群聊的wxid
        //"19213818230@chatroom"-chang,,wxid_3kgy79o49rrv22 -yue wxid_pxbnrmij6hqz12 -me wxid_a3z12qhjhyhf22 -zx   wxid_0b3292k9q9aa22 -dzy
        // 18408512944@chatroom -home   wxid_9ijc8t9v552922 -yt  sunnychen90 -sxc   17953557620@chatroom -cs    3466945781@chatroom  -wangm    wxid_hb3t0i2x475n21 -baibai
        //List<String> sends = Arrays.asList("wxid_pxbnrmij6hqz12");

        pushs.put("wxid_pxbnrmij6hqz12",Arrays.asList("毒鸡汤","娄底天气","杭州天气","长沙天气","历史上的今天","土味情话","小段子","网易云","每日一言","舔狗日记","喝水提醒"));
       // pushs.put("24106530046@chatroom",Arrays.asList("毒鸡汤","长沙天气","武汉天气","历史上的今天","舔狗日记","网易云"));
        //pushs.put("wxid_9ijc8t9v552922",Arrays.asList("毒鸡汤","长沙天气","历史上的今天","ACG榜","每日一言"));

        pushs.put("19213818230@chatroom",Arrays.asList("长沙天气","毒鸡汤","土味情话","网易云","每日一言","舔狗日记"));//,"毒鸡汤","历史上的今天","土味情话","网易云","每日一言","舔狗日记"));
      //  pushs.put("wxid_0b3292k9q9aa22",Arrays.asList("杭州天气","土味情话","网易云","喝水提醒"));
        pushs.put("wxid_a3z12qhjhyhf22",Arrays.asList("毒鸡汤","武汉天气","土味情话","小段子","网易云","每日一言","喝水提醒"));
        pushs.put("danjuan315",Arrays.asList("毒鸡汤","土味情话","小段子","网易云","每日一言","西安天气"));
       // pushs.put("wxid_hb3t0i2x475n21",Arrays.asList("网易云","每日一言","土味情话"));
        pushs.put("18408512944@chatroom",Arrays.asList("娄底天气"));

        pushs.put("lm7712251008",Arrays.asList("毒鸡汤","土味情话","每日一言","舔狗日记"));

        //pushs.put("wxid_3kgy79o49rrv22",Arrays.asList("毒鸡汤","温州天气","历史上的今天","土味情话","每日一言","舔狗日记","网易云"));
        //pushs.put("sunnychen90",Arrays.asList("毒鸡汤","长沙天气","历史上的今天","土味情话","小段子","每日一言","舔狗日记"));
    //    pushs.put("diulove123",Arrays.asList("毒鸡汤","长沙天气","土味情话","小段子","每日一言","舔狗日记","网易云"));

        //pushs.put("3466945781@chatroom",Arrays.asList("毒鸡汤","天津天气","大连天气","土味情话","小段子","每日一言","舔狗日记","网易云"));

       // pushs.put("wxid_zg32ngpb6bfk22",Arrays.asList("毒鸡汤","长沙天气","土味情话","小段子","每日一言","舔狗日记","网易云"));

        //pushs.put("3466945781@chatroom",Arrays.asList("毒鸡汤","土味情话","小段子","每日一言","网易云"));
    }

    @Autowired
    private RestTemplate restTemplate;


    @PostMapping("/robot")
    public void robot(HttpServletRequest request, HttpServletResponse response,@RequestBody JSONObject jsonObject)
    {

        System.out.println("---收到的消息---"+jsonObject);
        //request.getHeader("Udid").toString()

        //获取发送方的微信id,和机器人id
        String final_from_wxid = jsonObject.get("final_from_wxid").toString();//发该消息的用户微信id

        String final_from_name = jsonObject.get("final_from_name").toString();//发该消息的用户微信昵称

        String  from_wxid= jsonObject.get("from_wxid").toString(); //消息来自的用户或者群

        String  robot_wxid= jsonObject.get("robot_wxid").toString(); //机器人id

        String  event= jsonObject.get("event").toString(); //事件名称

        //如果是自己
        //if(ROBOTID.equals(from_wxid))return;
        //屏蔽可爱猫交流群消息
        if("18221469840@chatroom".equals(from_wxid))return;


        //构造返回消息
        //1、构建body参数
         /*{
                "success":true,
                "message":"successful!",
                "event":"SendTextMsg",
                "robot_wxid":"wxid_pxbnrmij6hqz12",
                "to_wxid":"filehelper",
                "member_wxid":"",
                "msg":"Im robot message"
        }*/
        JSONObject body = new JSONObject();
        body.put("success","true");
        body.put("message","successful");

        body.put("event","SendTextMsg");//默认发送文本消息
        body.put("robot_wxid",robot_wxid);//发送机器人的id
        body.put("to_wxid",from_wxid); //发送人的id
        //body.put("member_wxid","");

        //收到的消息内容
        String msg = jsonObject.get("msg").toString();

        if(keywords.stream().anyMatch(param -> msg.contains(param))){

            //body.put("msg","666我是机器人的message");
            if(event.equals("EventGroupMsg") && groups.contains(from_wxid)){
                // 发送群消息并艾特(4.4只能艾特一人) robot_wxid, group_wxid, member_wxid, member_name, msg
                body.put("group_wxid",from_wxid);
                body.put("event","SendGroupMsgAndAt");//SendGroupMsgAndAt
                body.put("member_wxid",final_from_wxid);//
                body.put("member_name",final_from_name);
                body.put("msg","好的！稍等...");
                if(!msg.contains("@at") && !msg.contains("wxid="+ROBOTID)){
                    sendPostHttp(body,URL);
                }
            }

            String menu [] = {"点一首","我想听","点歌"};
            List<String> menuList = Arrays.asList(menu);
            for (String name :menuList) {
                if(msg.contains(name)){
                    String songName = msg.substring(msg.indexOf(name)+name.length(),msg.length());
                    if(StringUtils.isEmpty(songName)){
                        return;
                    }
                    body.put("event","SendMusicMsg");
                    JSONObject music = new JSONObject();
                    music.put("name",songName);
                    music.put("type","0");
                    body.put("msg",music);
                }
            }

            if(msg.contains("经典语录")){
                String res = sendPostHttp(null,"https://api.oick.cn/yulu/api.php");
                System.out.println("---经典语录消息---"+res);
                body.put("msg", StringUtils.isEmpty(res)?"我是经典语录":res);
            }

            if(msg.contains("毒鸡汤")){
                String res = sendPostHttp(null,"https://api.oick.cn/dutang/api.php");
                System.out.println("---毒鸡汤---"+res);
                body.put("msg", StringUtils.isEmpty(res)?"毒鸡汤":res);
            }
            if(msg.contains("每日一言")){
                String res = sendPostHttp(null,"http://api.guaqb.cn/v1/onesaid/");
                System.out.println("---每日一言---"+res);
                body.put("msg", StringUtils.isEmpty(res)?"每日一言":"“"+res+"“");
            }
            if(msg.contains("土味情话")){
                String res = sendGetHttp("https://api.lovelive.tools/api/SweetNothings");
                System.out.println("---土味情话---"+res);
                body.put("msg", StringUtils.isEmpty(res)?"土味情话":"“"+res+"“");
            }

            if(msg.contains("小段子")){
                String title = "【精彩段子】\n";
                String res = sendPostHttp(null,"https://api.apiopen.top/getJoke?page=0&count=1&type=text");
                System.out.println("---小段子---"+res);
                JSONObject result = JSONObject.parseObject(res);
                List<JSONObject> joke = JSONObject.parseArray(result.get("result").toString(), JSONObject.class);
                String jokeMsg = joke.size()>0?"“"+joke.get(0).get("text").toString()+"“":"小段子";
                if(!StringUtils.isEmpty(joke.get(0).get("top_comments_content"))){
                    jokeMsg +="\n\n神论："+joke.get(0).get("top_comments_content");
                }
                body.put("msg", title+jokeMsg);
            }

            if(msg.contains("二次元")){
                body.put("event","SendImageMsg");
                JSONObject image = new JSONObject();
                image.put("name",Math.random());
                image.put("url","https://api.oick.cn/random/api.php?type=pe");
               //image.put("patch","https://api.oick.cn/random/pc/466f79e8ly1fw5oh9ttnij21hc0zqnbd.jpg");
                body.put("msg", image);
            }

            if(msg.contains("电脑壁纸")){
                body.put("event","SendImageMsg");
                JSONObject image = new JSONObject();
                image.put("name",Math.random());
                image.put("url","https://api.oick.cn/random/api.php?type=pc");
                // image.put("patch","https://api.oick.cn/random/pc/466f79e8ly1fw5oh9ttnij21hc0zqnbd.jpg");
                body.put("msg", image);
            }

           /* if(msg.contains("历史上的今天")){
                String res = sendGetHttp("https://api.oick.cn/lishi/api.php");
                System.out.println("---历史的今天---"+res);
                JSONObject  history = JSONObject.parseObject(res);
                List<JSONObject> result = JSONObject.parseArray(history.get("result").toString(), JSONObject.class);

                String hisInfo = "【历史的今天】\n";
                for (int i = 0; i < result.size(); i++) {
                    String data = result.get(i).get("date").toString();
                    String title = result.get(i).get("title").toString();
                    hisInfo+= (i+1)+".\t"+data+"\n"+title+"\n";
                }
                body.put("msg", StringUtils.isEmpty(hisInfo)?"历史的今天":hisInfo);
            }*/

            if(msg.contains("@at") && msg.contains("wxid="+ROBOTID)){
                String keyword = msg.substring( msg.indexOf(ROBOTID+"]" )+(ROBOTID+"]").length(),msg.length());
                if(keyword.trim().isEmpty()){
                    return;
                }
                String res = sendGetHttp("http://api.qingyunke.com/api.php?key=free&appid=0&msg="+keyword.trim());
                JSONObject result = JSONObject.parseObject(res);
                System.out.println("---人工智障---"+res);
                body.put("msg", StringUtils.isEmpty(res)?"我是人工智障":result.get("content").toString().replace("{br}","\n")+"\n\n*** 此消息为机器人自动回复 ***");
            }
            if(msg.contains("#抖音1小视频#")){
                body.put("event","SendFileMsg");
                 // 发送视频消息 robot_wxid to_wxid(群/好友) msg(name[md5值或其他唯一的名字，包含扩展名例如1.mp4], url)
                String res = sendGetHttp("https://api.oick.cn/douyin/api.php?url=https://www.douyin.com/video/7064135125780434211?modeFrom=hotDetail");
                JSONObject result = JSONObject.parseObject(res);

                JSONObject video = new JSONObject();
                video.put("name",Math.random());
                video.put("url",  result.get("play"));
                video.put("type","0");
                System.out.println("paly====="+result.get("play"));
                body.put("msg", video);
            }

            List<String> sortList = Arrays.asList("热歌榜","新歌榜","飙升榜","抖音榜","电音榜");
            String top = sortList.get((int)(Math.random()*(sortList.size())));
            if(msg.contains("网抑云")){
                String res = sendPostHttp(null,"https://api.uomg.com/api/rand.music?format=json&sort="+top);
                JSONObject data = (JSONObject) JSONObject.parseObject(res).get("data");
                String  songName = data.get("name").toString();
                if(StringUtils.isEmpty(songName)){
                    return;
                }
                body.put("msg", "【网易云"+top+"推荐】");
                sendPostHttp(body,URL);
                body.put("event","SendMusicMsg");
                JSONObject music = new JSONObject();
                music.put("name",songName);
                music.put("type","0");
                body.put("msg",music);

            }

            if(msg.contains("舔狗日记")){
                String res = sendGetHttp("https://api.oick.cn/dog/api.php");
                System.out.println("---舔狗日记---"+res);
                body.put("msg", StringUtils.isEmpty(res)?"舔狗日记":res);
            }

            if(msg.contains("天气预报")){
                String title = "【天-气-预-报】\n";
                String res = sendGetHttp("http://api.qingyunke.com/api.php?key=free&appid=0&msg=长沙天气");
                JSONObject result = JSONObject.parseObject(res);
                String sendMsg = title+result.get("content").toString().replace("{br}","\n");
                //返回的消息
                body.put("msg", sendMsg);
            }
            else if(msg.contains("天气")&&msg.length()<=6){
                String title = "【"+msg+"】\n";
                String air = msg.substring(0,msg.indexOf("天气"));
                if(StringUtils.isEmpty(air)){
                    return;
                }
                String res = sendGetHttp("http://api.qingyunke.com/api.php?key=free&appid=0&msg="+msg);
                JSONObject result = JSONObject.parseObject(res);
                String sendMsg = title+result.get("content").toString().replace("{br}","\n");
                //返回的消息
                body.put("msg", sendMsg);
            }






            //返回消息
            sendPostHttp(body,URL);

        }

        else{
            if(from_wxid.equals("wxid_zg32ngpb6bfk22")  || from_wxid.equals("wxid_vil2wlh4b2aa22")  ){
                if(msg.trim().isEmpty()){
                    return;
                }
                String res = sendGetHttp("http://api.qingyunke.com/api.php?key=free&appid=0&msg="+msg.trim());
                JSONObject result = JSONObject.parseObject(res);
                System.out.println("---人工智障---"+res);
                body.put("msg", StringUtils.isEmpty(res)?"我是人工智障":result.get("content").toString().replace("{br}","\n")+"\n\n*** 此消息为机器人自动回复 ***");
                sendPostHttp(body,URL);
            }
        }
    }

    // @Scheduled(cron = "@*/5 * * * * ?") //每隔5秒推送
    public void test() {
        //格式化
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String str = "2022-02-13 14:10:01";
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        System.out.println("自定义时间:"+dateTime.format(formatter)+"======="+dateTime.getHour()+":"+dateTime.getMinute());

        JSONObject body = new JSONObject();
        body.put("success","true");
        body.put("message","successful");
        body.put("event","SendTextMsg");//默认发送文本消息
        body.put("robot_wxid",ROBOTID);//发送机器人的id
        String title = "【喝水提醒】\n";
        String res = "主人~ 到喝水水的时间啦！^_^\n\n";

        String word = sendPostHttp(null,"https://api.oick.cn/dutang/api.php");;
        String sendMsg = title+res+word;
        if(!StringUtils.isEmpty(sendMsg)){
            //返回的消息
            body.put("msg", sendMsg);
            //发送人的wxid
            body.put("to_wxid","wxid_pxbnrmij6hqz12");

            String sendRes = sendPostHttp(body,URL);
            JSONObject parse = JSONObject.parseObject(sendRes);
            if (parse.get("code").toString().equals("-1")) {
                String resend = sendPostHttp(body,URL);
            }
        }
        System.out.println("111111111111111111111111");

    }

    //定时任务推送
    @Scheduled(cron = "00 00,30,45 07,08,09,11,12,13,14,15,16,17,19,22,23,00 * * ?") // tips: 秒 分 时  时分的第一个0会去掉
    public void tips() {
        JSONObject body = new JSONObject();
        body.put("success","true");
        body.put("message","successful");
        body.put("event","SendTextMsg");//默认发送文本消息
        body.put("robot_wxid",ROBOTID);//发送机器人的id

        System.out.println("------整点定时推送开始------");
        //格式化
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //当前时间
        LocalDateTime now = LocalDateTime.now();
        System.out.println("当前时间:"+now.format(formatter)+"======="+now.getHour()+":"+now.getMinute());


        pushs.forEach((k, v)->{
            String sendMsg = "";//发送的消息
            switch (now.getHour()+":"+now.getMinute()){

                case "7:0":{//天气推送
                    //天气、翻译、藏头诗、笑话、歌词、计算、域名信息/备案/收录查询、IP查询、手机号码归属、人工智能聊天
                        for (String item:v) {
                            if(item.contains("天气")){
                                String title = "【天气预报】\n";
                                String res = sendGetHttp("http://api.qingyunke.com/api.php?key=free&appid=0&msg="+item);
                                JSONObject result = JSONObject.parseObject(res);
                                sendMsg = title+result.get("content").toString().replace("{br}","\n");
                                //返回的消息
                                body.put("msg", sendMsg);
                                //发送人的wxid
                                body.put("to_wxid",k);
                                sendPostHttp(body,URL);
                            }
                        }
                      sendMsg= ""; //置空消息
                }break;

                case "8:0": {
                    if(v.contains("历史上的今天")){
                        for (String item:v) {
                            if(item.contains("历史上的今天")){
                                String res = sendGetHttp("https://api.oick.cn/lishi/api.php");
                                JSONObject history = JSONObject.parseObject(res);
                                List<JSONObject> result = JSONObject.parseArray(history.get("result").toString(), JSONObject.class);
                                String hisInfo = "【历史的今天】\n";
                                for (int i = 0; i < result.size(); i++) {
                                    String data = result.get(i).get("date").toString();
                                    String title = result.get(i).get("title").toString();
                                    hisInfo+= (i+1)+".\t"+data+"\n"+title+"\n";
                                }
                                sendMsg = hisInfo;
                            }
                        }
                    }
                    //
                    if(v.contains("喝水提醒")){
                        String title = "【喝水提醒】\n";
                        String res = "8：00 主人,睡醒一杯水,精神一整天~";
                        sendMsg = title+res;
                    }


                }break;
                case "9:0": {
                    if(v.contains("喝水提醒")){
                        String title = "【喝水提醒】\n";
                        String res = "9：00 今天的第二杯水啦~";
                        sendMsg = title+res;
                    }
                }break;
                case "11:0": {
                    if(v.contains("喝水提醒")){
                        String title = "【喝水提醒】\n";
                        String res = "11：00 饭前一杯水增加饱腹感~";
                        sendMsg = title+res;
                    }
                }break;

                case "12:0": {
                    if(v.contains("毒鸡汤")){
                        String title = "【每日毒鸡汤】\n";
                        String res = sendPostHttp(null,"https://api.oick.cn/dutang/api.php");
                        sendMsg = title+res;
                    }
                }break;
                case "12:45": {
                    if(v.contains("喝水提醒")){
                        String title = "【喝水提醒】\n";
                        String res = "12:45 饭后一杯水促进消化~";
                        sendMsg = title+res;
                    }
                }break;
                case "13:0": {
                    if(v.contains("土味情话")){
                            String title = "【土味情话】\n";
                            String res = sendGetHttp("https://api.lovelive.tools/api/SweetNothings");
                            sendMsg = title+"''"+res+"''";
                    }
                }break;
                case "14:0": {
                    if(v.contains("每日一言")){
                                String title = "【每日一言】\n";
                                String res = sendPostHttp(null,"http://api.guaqb.cn/v1/onesaid/");
                                sendMsg = title+"''"+res+"''";
                    }
                    if(v.contains("喝水提醒")){
                        String title = "【喝水提醒】\n";
                        String res = "14:00 缓解身体疲劳~";
                        sendMsg = title+res;
                    }
                }break;
                /*case "15:0": {
                    if(v.contains("小段子")){
                        for (String item:v) {
                            if(item.contains("小段子")){
                                String title = "【每日笑话】\n";
                                String res = sendPostHttp(null,"https://api.apiopen.top/getJoke?page=0&count=1&type=text");
                                System.out.println("---小段子---"+res);
                                JSONObject result = JSONObject.parseObject(res);
                                List<JSONObject> joke = JSONObject.parseArray(result.get("result").toString(), JSONObject.class);
                                String jokeMsg = joke.size()>0?"''"+joke.get(0).get("text").toString()+"''":"小段子";
                                if(!StringUtils.isEmpty(joke.get(0).get("top_comments_content"))){
                                    jokeMsg +="\n\n神评："+joke.get(0).get("top_comments_content");
                                }
                                sendMsg = title+jokeMsg;
                            }
                        }
                    }
                }break;*/
                case "15:0": {
                    if(v.contains("舔狗日记")){
                        String title = "【舔狗日记】\n";
                        String res = sendGetHttp("https://api.oick.cn/dog/api.php");
                        sendMsg = title+res;
                    }
                }break;
                case "15:30": {
                    if(v.contains("喝水提醒")){
                        String title = "【喝水提醒】\n";
                        String res = "15:30 减负又减肥~";
                        sendMsg = title+res;
                    }
                }break;
                case "17:30": {
                    if(v.contains("喝水提醒")){
                        String title = "【喝水提醒】\n";
                        String res = "17:30 增加晚饭前的饱腹感哦~";
                        sendMsg = title+res;
                    }
                }break;
                case "19:0": {
                    if(v.contains("喝水提醒")){
                        String title = "【喝水提醒】\n";
                        String res = "19:00 主人,这是今天最后一次喝水提醒啦~";
                        sendMsg = title+res;
                    }
                }break;

                //case "22:0":  sendMsg = "北京时间：22:00\n 『温馨提示』这么晚了，还在上网？早点洗洗睡吧，睡前记得洗洗脸喔！";break;
                //case "23:0":  sendMsg = "北京时间：23:00\n 『温馨提示』该睡觉咯，晚安！";break;
                //case "0:0":  sendMsg = "北京时间：00:00\n 『温馨提示』现在已经凌晨了，身体是无价的资本喔，早点休息吧！";break;


            }

            if(!StringUtils.isEmpty(sendMsg)){
                //返回的消息
                body.put("msg", sendMsg);
                //发送人的wxid
                body.put("to_wxid",k);
                sendPostHttp(body,URL);
            }
        });




    }

    //朝九晚五工作时间内每半小时
    //@Scheduled(cron = "@*/10 * * * * ?")
    @Scheduled(cron = "0 0/30 9-20 * * ?")//0 0/30 9-17 * * ?  59 59 9-17 * * ?
    //https://www.free-api.com/doc/302
   //  @Scheduled(cron = "00 00 23,00 * * ?") // tips: 秒 分 时  时分的第一个0会去掉
    public void tipWater() {

        JSONObject body = new JSONObject();
        body.put("success","true");
        body.put("message","successful");
        body.put("event","SendTextMsg");//默认发送文本消息
        body.put("robot_wxid",ROBOTID);//发送机器人的id
        String title = "【喝水提醒】\n";
        String res = "小主~ 到喝水水的时间啦！^_^\n\n";

        String word = sendPostHttp(null,"https://api.oick.cn/dutang/api.php");
        String sendMsg = title+res+word.replace("null","");

        if(!StringUtils.isEmpty(sendMsg)){
            //返回的消息
            body.put("msg", sendMsg);
            //发送人的wxid
            body.put("to_wxid","danjuan315");
            String sendRes = sendPostHttp(body,URL);
            JSONObject parse = JSONObject.parseObject(sendRes);
            if (parse.get("code").toString().equals("-1")) {
                String resend = sendPostHttp(body,URL);
            }


        }
    }


    @Scheduled(cron = "0 0 9-18 * * ?")
    public void tipWalk() {
        JSONObject body = new JSONObject();
        body.put("success","true");
        body.put("message","successful");
        body.put("event","SendTextMsg");//默认发送文本消息
        body.put("robot_wxid",ROBOTID);//发送机器人的id
        String title = "【运动提醒】\n";
        String res = "主人~ 该走动走动啦！^_^\n\n";

        String word = sendPostHttp(null,"http://api.guaqb.cn/v1/onesaid/");
        String sendMsg = title+res+word;
        if(!StringUtils.isEmpty(sendMsg)){
            //返回的消息
            body.put("msg", sendMsg);
            //发送人的wxid
            body.put("to_wxid","danjuan315");
            String sendRes = sendPostHttp(body,URL);
            JSONObject parse = JSONObject.parseObject(sendRes);
            if (parse.get("code").toString().equals("-1")) {
                String resend = sendPostHttp(body,URL);
            }
        }
    }

    @Scheduled(cron = "0 0 9-18 * * ?")
    public void tipWater2() {
        JSONObject body = new JSONObject();
        body.put("success","true");
        body.put("message","successful");
        body.put("event","SendTextMsg");//默认发送文本消息
        body.put("robot_wxid",ROBOTID);//发送机器人的id
        String title = "【喝水提醒】\n";
        String res = "小主~ 到喝水水的时间啦！^_^\n\n";

        String word = sendPostHttp(null,"https://api.oick.cn/dutang/api.php");
        String sendMsg = title+res+word;
        if(!StringUtils.isEmpty(sendMsg)){
            //返回的消息
            body.put("msg", sendMsg);
            //发送人的wxid
            body.put("to_wxid","lm7712251008");
            String sendRes = sendPostHttp(body,URL);
            JSONObject parse = JSONObject.parseObject(sendRes);
            if (parse.get("code").toString().equals("-1")) {
                String resend = sendPostHttp(body,URL);
            }
        }
    }


    //发送http请求
    public String sendPostHttp(JSONObject jsonObject,String url ){

        //2、添加请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json");

        //3、组装请求头和参数
        HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(jsonObject), headers);

        //4、发起post请求
        ResponseEntity<String> stringResponseEntity = null;
        try {
            System.out.println("请求的url为=========================="+url);
            // 设置restemplate编码为utf-8
            restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
            stringResponseEntity = restTemplate.postForEntity(url, formEntity, String.class);
            System.out.println("ResponseEntity----"+stringResponseEntity);
            //5、获取http状态码
            int statusCodeValue = stringResponseEntity.getStatusCodeValue();
            System.out.println("httpCode-----"+statusCodeValue);

            //6、获取返回体
            String body = stringResponseEntity.getBody();
            System.out.println("body-----"+body);

            //7、映射实体类
            //  Wrapper wrapper = JSONObject.parseObject(body, Wrapper.class);
            // String data = wrapper.getData();
            // System.out.println("data-----"+data);

            return body;
        } catch (RestClientException e) {
            e.printStackTrace();
            return null;
        }


    }

    public String sendGetHttp(String url ){

        ResponseEntity <String>  responseEntity  = restTemplate.getForEntity(url, String.class);
        String body  = responseEntity.getBody(); // 获取响应体
        System.out.println("HTTP 响应body：" + body);


        //以下是getForEntity比getForObject多出来的内容
        HttpStatus statusCode = responseEntity.getStatusCode(); // 获取响应码
        int statusCodeValue = responseEntity.getStatusCodeValue(); // 获取响应码值
        HttpHeaders headers = responseEntity.getHeaders(); // 获取响应头

        System.out.println("HTTP 响应状态：" + statusCode);
        System.out.println("HTTP 响应状态码：" + statusCodeValue);
        System.out.println("HTTP Headers信息：" + headers);

        return body;
    }


}
