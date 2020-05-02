package com.xiyue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class ExecUtils {

    private Object sign = new Object();
    private volatile int num;
    private volatile int waiting;
    private volatile int ok;
    private List<Long> times = new ArrayList<>();

    public synchronized void addNum() {
        num++;
    }

    public synchronized void addWaiting() {
        waiting++;
    }

    public synchronized void addOk() {
        ok++;
    }

    public synchronized void addTime(long time) {
        times.add(time);
    }

    /**
     * 给一个批量执行任务发送命令
     */
    public void start() {
        synchronized (sign) {
            sign.notifyAll();
        }
    }

    /**
     * 请求列表
     */
    private List<Runnable> requesterList = new ArrayList<>();

    /**
     * 开始执行
     *
     * @param model
     */
    public void run(ParamDealUtils.ParamModel model) {
        if (Constants.PARAM_TYPE_URL.equals(model.getType())) {
            // 处理单url测试
            if (Constants.PARAM_METHOD_GET.equals(model.getMethod())) {
                // 处理 GET 请求
                if (model.getTimes() > 0) {
                    // 如果传入了总请求时间那么就以此为准，否则根据总请求数量
                    runUrlRequester(model);
                } else if (model.getRequests() > 0) {
                    runUrlRequester(model);
                }
            } else {
                // 处理 POST 请求
                if (model.getTimes() > 0) {
                    // 如果传入了总请求时间那么就以此为准，否则根据总请求数量
                    runUrlRequester(model);
                } else if (model.getRequests() > 0) {
                    runUrlRequester(model);
                }
            }
        } else {
            // 处理多url请求
            // 处理单url测试
            if (Constants.PARAM_METHOD_GET.equals(model.getMethod())) {
                if (model.getTimes() > 0) {
                    // 如果传入了总请求时间那么就以此为准，否则根据总请求数量
                    runFileRequester(model);
                } else if (model.getRequests() > 0) {
                    runFileRequester(model);
                }
            } else {
                if (model.getTimes() > 0) {
                    // 如果传入了总请求时间那么就以此为准，否则根据总请求数量
                    runFileRequester(model);
                } else if (model.getRequests() > 0) {
                    runFileRequester(model);
                }
            }
        }
        synchronized (times) {
            long allTime = 0;
            for (Long time : times) {
                allTime += time;
            }
            System.out.println("------------------->>> avg time:" + allTime / times.size() + "<<<------------------------");
        }
    }

    /**
     * 单个url的固定数量的请求
     *
     * @param model
     */
    public void runUrlRequester(ParamDealUtils.ParamModel model) {
        try {
            long times = 0;
            int nn = 0;
            for (; ; ) {
                long start = System.currentTimeMillis();
                for (int i = 0; i < model.getConcurrency(); i++) {
                    Runnable requester = null;
                    if (Constants.PARAM_METHOD_GET.equals(model.getMethod())) {
                        requester = new GetRequester(this, sign, model.getUrl(), model.getTimeout(), nn + i);
                    } else {
                        requester = new PostRequester(this, sign, model.getUrl(), model.getTimeout(), nn + i, model.getParam());
                    }
                    Thread thread = new Thread(requester);
                    thread.start();
                    requesterList.add(requester);
                }
                while (true) {
                    Thread.sleep(100);
                    if (this.waiting >= model.getConcurrency()) {
                        break;
                    }
                }
                start();
                while (true) {
                    Thread.sleep(100);
                    if (this.ok >= model.getConcurrency()) {
                        break;
                    }
                }
                nn += model.getConcurrency();
                times += (System.currentTimeMillis() - start);
                if (model.getTimes() > 0) {
                    System.out.println("----------------------------> run once total time: " + times + " <----------------------------");
                    if (times > model.getTimes()) {
                        break;
                    }
                } else {
                    System.out.println("----------------------------> run once: " + num + " <----------------------------");
                    if (nn >= model.getRequests()) {
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 多个url的固定数量的请求
     *
     * @param model
     */
    public void runFileRequester(ParamDealUtils.ParamModel model) {
        try {
            List<String> urls = null;
            List<String> params = null;
            if (Constants.PARAM_METHOD_GET.equals(model.getMethod())) {
                urls = FileUtils.readAllLines(model.getUrl());
                urls.removeIf(x -> x == null || x == "");
            } else {
                params = FileUtils.readAllLines(model.getParam());
                params.removeIf(x -> x == null || x == "");
            }

            long times = 0;
            int nn = 0;
            for (; ; ) {
                long start = System.currentTimeMillis();
                for (int i = 0; i < model.getConcurrency(); i++) {
                    Runnable requester = null;
                    if (Constants.PARAM_METHOD_GET.equals(model.getMethod())) {
                        requester = new GetRequester(this, sign, getUrl(urls, i), model.getTimeout(), nn + i);

                    } else {
                        requester = new PostRequester(this, sign, model.getUrl(), model.getTimeout(), nn + i, getUrl(params, i));
                    }
                    Thread thread = new Thread(requester);
                    thread.start();
                    requesterList.add(requester);
                }
                while (true) {
                    Thread.sleep(100);
                    if (this.waiting >= model.getConcurrency()) {
                        break;
                    }
                }
                start();
                while (true) {
                    Thread.sleep(100);
                    if (this.ok >= model.getConcurrency()) {
                        break;
                    }
                }
                nn += model.getConcurrency();
                times += (System.currentTimeMillis() - start);
                if (model.getTimes() > 0) {
                    System.out.println("run once total time:" + times);
                    if (times > model.getTimes()) {
                        break;
                    }
                } else {
                    System.out.println("run once:" + num);
                    if (nn >= model.getRequests()) {
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 按照顺序取出url或者param
     *
     * @param urls
     * @param index
     * @return
     */
    private String getUrl(List<String> urls, int index) {
        int len = urls.size();
        if (index >= len) {
            index = len % (index + 1);
        }
        return urls.get(index);
    }

    public class GetRequester implements Runnable {
        private Object sign;
        private HttpClient client;
        private String url;
        private int timeout;
        private ExecUtils util;
        private int id;

        public GetRequester(ExecUtils util, Object sign, String url, int timeout, int id) {
            super();
            this.url = url;
            this.timeout = timeout;
            this.sign = sign;
            this.util = util;
            this.id = id;
            client = new HttpClient();
        }

        @Override
        public void run() {
            try {
                System.out.println("wait: " + id);
                util.addWaiting();
                synchronized (sign) {
                    sign.wait();
                }
                System.out.println("start exec: " + id);
                long start = System.currentTimeMillis();
                //Thread.sleep(100);
                System.out.println("start exec1: " + id);
                //String urlStr = url + "?id=" + id;
                if (client.sendGet(url, timeout)) {
                    util.addNum();
                }
                long time = System.currentTimeMillis() - start;
                util.addTime(time);
                System.out.println("end exec: " + id + "; using time:" + time);
                util.addOk();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class PostRequester implements Runnable {
        private Object sign;
        private HttpClient client;
        private String url;
        private int timeout;
        private ExecUtils util;
        private int id;
        private String params;

        public PostRequester(ExecUtils util, Object sign, String url, int timeout, int id, String params) {
            super();
            this.url = url;
            this.timeout = timeout;
            this.sign = sign;
            this.util = util;
            this.id = id;
            this.params = params;
            client = new HttpClient();
        }

        @Override
        public void run() {
            try {
                System.out.println("wait: " + id);
                util.addWaiting();
                synchronized (sign) {
                    sign.wait();
                }
                System.out.println("start exec: " + id);
                long start = System.currentTimeMillis();
                if (client.sendPost(url, params, timeout)) {
                    util.addNum();
                }
                long time = System.currentTimeMillis() - start;
                util.addTime(time);
                System.out.println("end exec: " + id + "; using time:" + time);
                util.addOk();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
