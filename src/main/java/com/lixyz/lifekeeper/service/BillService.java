package com.lixyz.lifekeeper.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.csvreader.CsvReader;
import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.bill.bill.*;
import com.lixyz.lifekeeper.bean.bill.billaccount.BillAccount;
import com.lixyz.lifekeeper.bean.bill.billcategory.BillCategory;
import com.lixyz.lifekeeper.dao.BillDao;
import com.lixyz.lifekeeper.util.Constant;
import com.lixyz.lifekeeper.util.FileUtil;
import com.lixyz.lifekeeper.util.TimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class BillService {

    private final BillDao billDao;

    public BillService(BillDao billDao) {
        this.billDao = billDao;
    }

    /**
     * 插入账单
     */
    public Result insertBill(Bill bill) {
        try {
            int addResult = billDao.insertBill(bill);
            return new Result(true, null, null, addResult > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 删除账单
     */
    public Result deleteBill(String objectId) {
        try {
            int deleteResult = billDao.deleteBill(objectId);
            return new Result(deleteResult > 0, null, null, deleteResult > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 获取某年某月的账单数据
     */
    public Result getBillByMonth(String userId, int year, int month) {
        try {
            long start = TimeUtil.getMonthStart(year, month);
            long end = TimeUtil.getMonthEnd(year, month);
            double incomeCount = billDao.getIncomeCount(userId, start, end);
            double expendCount = billDao.getExpendCount(userId, start, end);
            List<Bill> bills = billDao.selectBillByMonth(userId, start, end);

            for (Bill bill : bills) {
                ArrayList<String> images = billDao.getBillImage(bill.getBillId());
                bill.setBillImage(images);
            }

            List<BillCategory> categories = billDao.getBillCategories(userId);
            HashMap<String, String> categoryMap = new HashMap<>(categories.size());
            for (BillCategory category : categories) {
                categoryMap.put(category.getCategoryId(), category.getCategoryName());
            }
            ArrayList<BillAccount> billAccounts = billDao.getBillAccounts(userId);
            HashMap<String, String> accountMap = new HashMap<>(billAccounts.size());
            for (BillAccount account : billAccounts) {
                accountMap.put(account.getAccountId(), account.getAccountName());
            }
            for (Bill bill : bills) {
                bill.setBillCategory(categoryMap.get(bill.getBillCategory()));
                bill.setBillAccount(accountMap.get(bill.getBillAccount()));
            }
            BillMonthOverview overview = new BillMonthOverview();
            overview.setYear(year);
            overview.setMonth(month);
            overview.setIncomeCount(incomeCount);
            overview.setExpendCount(expendCount);
            overview.setBills(bills);
            return new Result(true, null, null, overview);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }


    public Result getBillsByTimeRange(String userId, int year, int month) {
        Calendar calendar = Calendar.getInstance();
        BillOverview overview = new BillOverview();
        ArrayList<BillDayGroupBean> list = new ArrayList<>();
        Calendar currTime = Calendar.getInstance();
        int currYear = currTime.get(Calendar.YEAR);
        int currMonth = currTime.get(Calendar.MONTH) + 1;
        int dayCount;
        if (currYear == year && currMonth == month) {
            dayCount = currTime.get(Calendar.DAY_OF_MONTH);
        } else {
            dayCount = TimeUtil.getDaysOfMonth(year, month);
        }
        for (int i = dayCount; i >= 1; i--) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, i);
            long dayStart = DateUtil.beginOfDay(calendar.getTime()).getTime();
            long dayEnd = DateUtil.endOfDay(calendar.getTime()).getTime();
            ArrayList<Bill> bills = billDao.getBillsByTimeRange(userId, dayStart, dayEnd);
            double incomeCount = 0;
            double expendCount = 0;
            for (Bill bill : bills) {
                if (bill.getBillProperty() > 0) {
                    incomeCount += bill.getBillMoney();
                } else {
                    expendCount += bill.getBillMoney();
                }
                String categoryName = billDao.getBillCategoryName(userId, bill.getBillCategory());
                bill.setBillCategory(categoryName);
                String accountName = billDao.getBillAccountName(userId, bill.getBillAccount());
                bill.setBillAccount(accountName);
            }
            BillDayGroupBean bean = new BillDayGroupBean();
            bean.setBills(bills);
            String monthStr;
            if (month > 9) {
                monthStr = "" + month;
            } else {
                monthStr = "0" + month;
            }
            String dayStr;
            if (i > 9) {
                dayStr = "" + i;
            } else {
                dayStr = "0" + i;
            }
            bean.setDate(year + "-" + monthStr + "-" + dayStr);
            bean.setIncomeCountForDay(incomeCount);
            bean.setExpendCountForDay(expendCount);
            list.add(bean);
        }
        overview.setYear(year);
        overview.setMonth(month);
        overview.setList(list);
        long startMonth = DateUtil.beginOfMonth(calendar.getTime()).getTime();
        long endMonth = DateUtil.endOfMonth(calendar.getTime()).getTime();
        double incomeCountForMonth = billDao.getIncomeCountForMonth(userId, startMonth, endMonth);
        double expendCountForMonth = billDao.getExpendCountForMonth(userId, startMonth, endMonth);
        overview.setIncomeCount(incomeCountForMonth);
        overview.setExpendCount(expendCountForMonth);
        return new Result(true, null, null, overview);
    }


    /**
     * 上传图片
     *
     * @param multipartFiles
     * @param billId
     */
    public Result uploadImg(MultipartFile[] multipartFiles, String userId, String billId) {
        try {
            for (MultipartFile file : multipartFiles) {
                if (file.isEmpty()) {
                    return new Result(false, "文件为空，请检查后重试", new Exception("文件为空"), null);
                }
            }
            //创建目录
            File dir = new File("/files/LifeKeeperBillImage/" + userId);
            if (!dir.exists()) {
                boolean mkdirResult = dir.mkdirs();
                if (!mkdirResult) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("创建账单图片目录出错"), null);
                }
            }
            File source = new File("/files/LifeKeeperBillImage/" + userId + "/source/");
            if (!source.exists()) {
                boolean mkdirResult = source.mkdirs();
                if (!mkdirResult) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("创建账单图片目录出错"), null);
                }
            }
            File coverDir = new File("/files/LifeKeeperBillImage/" + userId + "/cover/");
            if (!coverDir.exists()) {
                boolean mkdirResult = coverDir.mkdirs();
                if (!mkdirResult) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("创建账单 Cover 目录出错"), null);
                }
            }
            File thumbnailDir = new File("/files/LifeKeeperBillImage/" + userId + "/thumbnail/");
            if (!thumbnailDir.exists()) {
                boolean mkdirResult = thumbnailDir.mkdirs();
                if (!mkdirResult) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("创建账单 Thumbnail 目录出错"), null);
                }
            }
            File webpDir = new File("/files/LifeKeeperBillImage/" + userId + "/webp/");
            if (!webpDir.exists()) {
                boolean mkdirResult = webpDir.mkdirs();
                if (!mkdirResult) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("创建账单 webp 目录出错"), null);
                }
            }
            ArrayList<BillImageBean> list = new ArrayList<>(multipartFiles.length);
            for (MultipartFile file : multipartFiles) {
                File dest = new File("/files/LifeKeeperBillImage/" + userId + "/source/" + UUID.randomUUID() + "." + FileUtil.getFileFormat(file.getOriginalFilename()));
                FileUtil.multipartFileTransferToFile(file, dest);
                File cover = new File("/files/LifeKeeperBillImage/" + userId + "/cover/" + UUID.randomUUID() + "." + FileUtil.getFileFormat(file.getOriginalFilename()));
                FileUtil.getCoverFile(dest, cover);
                File thumbnail = new File("/files/LifeKeeperBillImage/" + userId + "/thumbnail/" + UUID.randomUUID() + "." + FileUtil.getFileFormat(file.getOriginalFilename()));
                FileUtil.getThumbnailFile(cover, thumbnail);

                File webpFile = new File("/files/LifeKeeperBillImage/" + userId + "/webp/" + UUID.randomUUID() + ".webp");
                BufferedImage bufferedImage = ImageIO.read(thumbnail);
                ImageIO.write(bufferedImage, "webp", webpFile);
                BillImageBean bean = new BillImageBean();
                bean.setObjectId(UUID.randomUUID().toString());
                bean.setBillId(billId);
                bean.setImageId(UUID.randomUUID().toString());
                bean.setImageCoverName(cover.getName());
                bean.setImageSourceName(dest.getName());
                bean.setImageThumbnailName(thumbnail.getName());
                bean.setImageWebpName(webpFile.getName());
                bean.setImageUser(userId);
                list.add(bean);
            }
            int result = billDao.addBillImage(list);
            return new Result(result > 0, null, null, null);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public double getCurrentMonthIncomeMoneyCount(String userId, long start, long end) {
        try {
            return billDao.getCurrentMonthIncomeCount(userId, start, end);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getCurrentMonthExpendMoneyCount(String userId, long start, long end) {
        try {
            return billDao.getCurrentMonthExpendCount(userId, start, end);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Result getBillChartData(String userId, long start, long end) {
        try {
            BillChartDataBean bean = new BillChartDataBean();
            bean.setStart(start);
            bean.setEnd(end);
            bean.setIncome(billDao.getRangeMoneyCount(start, end, 1, "-1", "-1", userId));
            bean.setExpend(billDao.getRangeMoneyCount(start, end, -1, "-1", "-1", userId));
            ArrayList<BillMoneyCountForCategoryGroup> incomeCategoryMoneyCount = billDao.getCategoryMoneyCount(start, end, userId, 1);
            for (BillMoneyCountForCategoryGroup billMoneyCountForCategoryGroup : incomeCategoryMoneyCount) {
                billMoneyCountForCategoryGroup.setBillCategory(billDao.getCategoryNameByCategoryId(userId, billMoneyCountForCategoryGroup.getBillCategory()));
            }
            ArrayList<BillMoneyCountForCategoryGroup> expendCategoryMoneyCount = billDao.getCategoryMoneyCount(start, end, userId, -1);
            for (BillMoneyCountForCategoryGroup billMoneyCountForCategoryGroup : expendCategoryMoneyCount) {
                billMoneyCountForCategoryGroup.setBillCategory(billDao.getCategoryNameByCategoryId(userId, billMoneyCountForCategoryGroup.getBillCategory()));
            }
            bean.setIncomeCategoryData(incomeCategoryMoneyCount);
            bean.setExpendCategoryData(expendCategoryMoneyCount);

            ArrayList<BillMoneyCountForDay> billMoneyCountForDay = getBillMoneyCountForDay(userId, start, end);
            bean.setBillMoneyCountForDays(billMoneyCountForDay);
            return new Result(true, null, null, bean);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public ArrayList<BillMoneyCountForDay> getBillMoneyCountForDay(String userId, long start, long end) {
        try {
            ArrayList<BillMoneyCountForDay> list = new ArrayList<>();
            while (true) {
                BillMoneyCountForDay bean = new BillMoneyCountForDay();
                //凭借start，获取当天的格式化日期
                String formatToday = DateUtil.formatDate(new Date(start));
                bean.setDate(formatToday);
                //再获取当天的起始和截止
                long beginOfDay = DateUtil.beginOfDay(new Date(start)).getTime();
                long endOfDay = DateUtil.endOfDay(new Date(start)).getTime();
                //如果截止日期大于传入的 end，则跳出
                if (endOfDay > end) {
                    endOfDay = end;
                    double income = billDao.getMoneyCount(beginOfDay, endOfDay, userId, 1);
                    double expend = billDao.getMoneyCount(beginOfDay, endOfDay, userId, -1);
                    bean.setIncome(income);
                    bean.setExpend(expend);
                    list.add(bean);
                    break;
                } else {
                    double income = billDao.getMoneyCount(beginOfDay, endOfDay, userId, 1);
                    double expend = billDao.getMoneyCount(beginOfDay, endOfDay, userId, -1);
                    bean.setIncome(income);
                    bean.setExpend(expend);
                    list.add(bean);
                    //如果截止日期不大于传入的 end，则给start+1天
                    Date newDate = DateUtil.offset(new Date(start), DateField.DAY_OF_MONTH, 1);
                    start = newDate.getTime();
                }
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Result getRangeOverview(String userId, long start, long end) {
        try {
            ArrayList<BillCategory> billCategories = billDao.getAllBillCategories(userId);
            HashMap<String, BillCategory> map = new HashMap<>(billCategories.size());
            for (BillCategory category : billCategories) {
                map.put(category.getCategoryId(), category);
            }
            double income = billDao.getMoneyCount(start, end, userId, 1);
            double expend = billDao.getMoneyCount(start, end, userId, -1);
            ArrayList<Bill> top10Income = billDao.getTop10Bills(start, end, userId, 1);
            ArrayList<Bill> top10Expend = billDao.getTop10Bills(start, end, userId, -1);
            ArrayList<BillMoneyCountForCategoryGroup> top10AllCategory = billDao.getTop10Category(start, end, userId, 0);
            ArrayList<BillMoneyCountForCategoryGroup> top10IncomeCategory = billDao.getTop10Category(start, end, userId, 1);
            ArrayList<BillMoneyCountForCategoryGroup> top10ExpendCategory = billDao.getTop10Category(start, end, userId, -1);
            RangeOverview2 overview = new RangeOverview2();
            overview.setStart(TimeUtil.longToString(start));
            overview.setEnd(TimeUtil.longToString(end));
            overview.setIncome(income);
            overview.setExpend(expend);

            for (Bill bill : top10Income) {
                bill.setBillCategory(map.get(bill.getBillCategory()).getCategoryName());
            }
            for (Bill bill : top10Expend) {
                bill.setBillCategory(map.get(bill.getBillCategory()).getCategoryName());
            }
            overview.setIncomeTop10(top10Income);
            overview.setExpendTop10(top10Expend);
            for (BillMoneyCountForCategoryGroup billMoneyCountForCategoryGroup : top10AllCategory) {
                int isIncome = map.get(billMoneyCountForCategoryGroup.getBillCategory()).getIsIncome();
                billMoneyCountForCategoryGroup.setIsIncome(isIncome);
                billMoneyCountForCategoryGroup.setBillCategory(map.get(billMoneyCountForCategoryGroup.getBillCategory()).getCategoryName());
            }
            for (BillMoneyCountForCategoryGroup billMoneyCountForCategoryGroup : top10IncomeCategory) {
                billMoneyCountForCategoryGroup.setIsIncome(map.get(billMoneyCountForCategoryGroup.getBillCategory()).getIsIncome());
                billMoneyCountForCategoryGroup.setBillCategory(map.get(billMoneyCountForCategoryGroup.getBillCategory()).getCategoryName());
            }
            for (BillMoneyCountForCategoryGroup billMoneyCountForCategoryGroup : top10ExpendCategory) {
                billMoneyCountForCategoryGroup.setIsIncome(map.get(billMoneyCountForCategoryGroup.getBillCategory()).getIsIncome());
                billMoneyCountForCategoryGroup.setBillCategory(map.get(billMoneyCountForCategoryGroup.getBillCategory()).getCategoryName());
            }
            overview.setAllCategoryTop10(top10AllCategory);
            overview.setIncomeCategoryTop10(top10IncomeCategory);
            overview.setExpendCategoryTop10(top10ExpendCategory);
            return new Result(true, null, null, overview);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "服务器出错啦，请稍候重试...", null, null);
        }
    }

    public Result getRangeBills(String userId, long start, long end, int pageNum, int pageSize, String sortName, String sortOrder) {
        try {
            ArrayList<Bill> bills = billDao.getRangeBills(userId, start, end, (pageNum - 1) * pageSize, pageSize, sortName, sortOrder);
            for (Bill bill : bills) {
                ArrayList<String> images = billDao.getBillImage(bill.getBillId());
                bill.setBillImage(images);
            }
            long count = billDao.getRangeBillCount(userId, start, end);
            ArrayList<BillCategory> categories = billDao.getBillCategories(userId);
            HashMap<String, String> categoryMap = new HashMap<>(categories.size());
            for (BillCategory category : categories) {
                categoryMap.put(category.getCategoryId(), category.getCategoryName());
            }
            ArrayList<BillAccount> billAccounts = billDao.getBillAccounts(userId);
            HashMap<String, String> accountMap = new HashMap<>(billAccounts.size());
            for (BillAccount account : billAccounts) {
                accountMap.put(account.getAccountId(), account.getAccountName());
            }
            for (Bill bill : bills) {
                bill.setBillCategory(categoryMap.get(bill.getBillCategory()));
                bill.setBillAccount(accountMap.get(bill.getBillAccount()));
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("Count", count);
            map.put("Bills", bills);
            return new Result(true, null, null, map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage(), null, null);
        }
    }

    public Result getBillCategoryAndBillAccount(String userId) {
        try {
            ArrayList<BillAccount> billAccounts = billDao.getBillAccounts(userId);
            ArrayList<BillCategory> billCategories = billDao.getBillCategories(userId);
            ArrayList<BillCategory> incomeCategories = new ArrayList<>();
            ArrayList<BillCategory> expendCategories = new ArrayList<>();
            for (BillCategory category : billCategories) {
                if (category.getIsIncome() > 0) {
                    incomeCategories.add(category);
                } else {
                    expendCategories.add(category);
                }
            }
            BillCategoryAndBillAccount bean = new BillCategoryAndBillAccount();
            bean.setBillAccounts(billAccounts);
            bean.setAllBillCategories(billCategories);
            bean.setIncomeBillCategories(incomeCategories);
            bean.setExpendBillCategories(expendCategories);
            return new Result(true, null, null, bean);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result getBillByCategory(String userId, String categoryName, int billProperty, long beginOfMonth, long endOfMonth) {
        try {
            String categoryId = billDao.getBillCategoryByCategoryName(userId, categoryName, billProperty);
            ArrayList<Bill> bills = billDao.getBillsByCategoryId(userId, categoryId, billProperty, beginOfMonth, endOfMonth);
            return new Result(true, null, null, bills);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result getWebTableData(String userId, long start, long end, int limit, int offset, String sortName, String sortOrder) {
        try {
            int count = billDao.getRangeBillCount(userId, start, end);
            int page = count / 20;
            if (count % 20 != 0) {
                page = page + 1;
            }
            ArrayList<Bill> bills = billDao.getRangeBills(userId, start, end, limit, offset, sortName, sortOrder);
            BillWebDataBean bean = new BillWebDataBean();
            bean.setBills(bills);
            bean.setPageCount(page);
            return new Result(true, null, null, bean);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    String reg = "\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2} ([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

    public void importBill(String userId, int type) {
        if (type == 1) {
            try {
                CsvReader reader = new CsvReader("/files/微信支付账单(20220930-20221222).csv", ',', StandardCharsets.UTF_8);
                while (reader.readRecord()) {
                    String[] split = reader.getRawRecord().split(",");
                    if (split.length > 0) {
                        if (Pattern.matches(reg, split[0])) {
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
                        }
                    }
                }
                //数据取完了，关闭文件
                reader.close();
            } catch (IOException | ParseException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
