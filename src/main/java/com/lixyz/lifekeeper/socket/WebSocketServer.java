package com.lixyz.lifekeeper.socket;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.csvreader.CsvReader;
import com.lixyz.lifekeeper.ApplicationContextRegister;
import com.lixyz.lifekeeper.bean.bill.bill.Bill;
import com.lixyz.lifekeeper.bean.bill.billaccount.BillAccount;
import com.lixyz.lifekeeper.bean.bill.billcategory.BillCategory;
import com.lixyz.lifekeeper.dao.BillDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Pattern;

/**
 * @author muyuer 182443947@qq.com
 * @version 1.0
 * @date 2019-07-22 18:17
 */
@Component
@Service
@ServerEndpoint("/web/socket/{sid}")
public class WebSocketServer {


    static Log log = LogFactory.get(WebSocketServer.class);
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    private static ConcurrentHashMap<Session, WebSocketServer> hashMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收sid
     */
    private String sid = "";


    String reg = "\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2} ([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        BillDao billDao = (BillDao) ApplicationContextRegister.getApplicationContext().getBean(BillDao.class);
        //加入set中
        this.sid = sid;
        this.session = session;
        webSocketSet.add(this);
        System.out.println("open thread:" + Thread.currentThread().getName() + "|" + Thread.currentThread().getId());
        log.info("new windows listening:" + sid + ",now connection count " + getOnlineCount());
        try {
            String userId = sid.split("-")[0];
            CsvReader reader = new CsvReader("/files/LifeKeeperImportBillWeChat/" + userId + "/" + sid + ".csv", ',', StandardCharsets.UTF_8);
            while (reader.readRecord()) {
                String[] split = reader.getRawRecord().split(",");
                if (split.length > 0) {
                    if (Pattern.matches(reg, split[0])) {
                        int count = billDao.billIsImported(split[8]);
                        if(count > 0){
                            sendMessage(split[0] + " 已存在，已跳过");
                            continue;
                        }
                        Bill bill = new Bill();
                        bill.setObjectId(split[8]);
                        bill.setBillId(split[8]);
                        bill.setBillDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(split[0]).getTime());
                        bill.setBillMoney(Double.parseDouble(split[5].replace("¥", "")));
                        if (split[4].equals("收入")) {
                            bill.setBillProperty(1);
                        } else if (split[4].equals("支出")) {
                            bill.setBillProperty(-1);
                        } else {
                            continue;
                        }
                        String categoryId = billDao.getBillCategoryId(userId, split[1]);
                        if (categoryId == null) {
                            String id = UUID.randomUUID().toString();
                            BillCategory category = new BillCategory();
                            category.setObjectId(UUID.randomUUID().toString());
                            category.setCategoryId(id);
                            category.setCategoryUser(userId);
                            category.setCategoryName(split[1]);
                            if (split[4].equals("收入")) {
                                category.setIsIncome(1);
                            } else if (split[4].equals("支出")) {
                                category.setIsIncome(-1);
                            }
                            category.setCategoryStatus(1);
                            category.setCategoryType(0);
                            category.setCreateTime(System.currentTimeMillis());
                            category.setUpdateTime(0);
                            category.setOrderIndex(0);
                            billDao.addBillCategory(category);
                            bill.setBillCategory(id);
                        } else {
                            bill.setBillCategory(categoryId);
                        }
                        String accountName = split[6];
                        if (accountName.equals("零钱") || accountName.equals("/")) {
                            accountName = "微信零钱";
                        }
                        String accountId = billDao.getBillAccountId(userId, accountName);
                        if (accountId == null) {
                            String id = UUID.randomUUID().toString();
                            BillAccount account = new BillAccount();
                            account.setObjectId(UUID.randomUUID().toString());
                            account.setAccountId(id);
                            account.setAccountUser(userId);
                            account.setAccountName(accountName);
                            account.setAccountStatus(1);
                            account.setAccountType(0);
                            account.setCreateTime(System.currentTimeMillis());
                            account.setUpdateTime(0);
                            account.setOrderIndex(0);
                            billDao.addBillAccount(account);
                            bill.setBillAccount(id);
                        } else {
                            bill.setBillAccount(accountId);
                        }
                        bill.setBillRemark(split[3]);
                        bill.setBillUser(userId);
                        bill.setBillShop(split[2]);
                        bill.setBillStatus(1);
                        bill.setBillType(0);
                        bill.setCreateTime(System.currentTimeMillis());
                        bill.setUpdateTime(0);
                        bill.setBillImage(null);
                        billDao.insertBill(bill);
                        try {
                            sendMessage(split[0] + " 插入完成");
                        } catch (IOException e) {
                            log.error("websocket IO异常");
                            System.out.println("websocket io error");
                        }
                    }
                }
            }
            sendMessage("1");
            //数据取完了，关闭文件
            reader.close();
        } catch (IOException | ParseException | SQLException e) {
            try {
                sendMessage("-1");
                e.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();
        log.info("one close ,now connection count ：" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("receiver message from " + sid + ":" + message);
        for (WebSocketServer item : webSocketSet) {
            if (item.session == session) {
                try {
                    item.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnMessage
    public void onMessage(Session session, byte[] message) {
        System.out.println("this sid = " + sid);
        for (WebSocketServer item : webSocketSet) {
            System.out.println("sid in set :" + item.sid);
        }
//        log.info("receiver message from " + sid + ":size = " + message.length);
//        for (WebSocketServer item : webSocketSet) {
//            if (item.sid.equals(this.sid)) {
//                BufferedOutputStream bos = null;
//                FileOutputStream fos = null;
//                try {
//
//                    File file = new File("/files/aaa");
//                    if (!file.getParentFile().exists()) {
//                        //文件夹不存在 生成
//                        file.getParentFile().mkdirs();
//                    }
//                    fos = new FileOutputStream(file);
//                    bos = new BufferedOutputStream(fos);
//                    bos.write(message);
//                    item.sendMessage("11111");
//                    item.sendMessage("22222");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (bos != null) {
//                        try {
//                            bos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (fos != null) {
//                        try {
//                            fos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
    }


    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("error!");
        System.out.println(error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(SocketMessage message) throws IOException {
        this.session.getBasicRemote().sendText(JSONUtil.toJsonStr(message));
    }


    /**
     * 群发自定义消息
     */
    public static void sendInfo(SocketMessage message, @PathParam("sid") String sid) throws IOException {
        log.info("send message to window " + sid + "，content:" + message);
        for (WebSocketServer item : webSocketSet) {
            try {
                if (sid == null) {
                    item.sendMessage(message);
                } else if (item.sid.equals(sid)) {
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}