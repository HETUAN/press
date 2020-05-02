package com.xiyue;

public class Main {

    /**
     * 入口参数
     * -type type         url类型，url or file path
     * -url url         url类型，url or file path
     * -param param        url请求参数，json string or file path
     * -method method       请求方式 get or post
     * -reqs requests     要执行的请求次数
     * -c concurrency  并发数量
     * -times times        请求时间
     * -timeout timeout      响应时间
     * java -jar press.jar -type url -url http://localhost:8010 -method get -reqs 1000 -c 30 -timeout 100
     * java -jar press.jar -type url -url http://localhost:8010 -method get -reqs 1000 -c 30 -timeout 100
     * java -jar press.jar -type file -url ‪C:\Users\YuLongZ\Desktop\param.txt -method get -reqs 1000 -c 30 -timeout 100
     * java -jar press.jar -type url -url http://localhost:8010 -method post -param '{"id":1231}'  -reqs 1000 -c 30 -timeout 100
     * java -jar press.jar -type file -url http://localhost:8010 -param ‪C:\Users\YuLongZ\Desktop\param1.txt -method post -reqs 1000 -c 30 -timeout 100
     * @param args
     */
    public static void main(String[] args) {
        // write your code here
        System.out.println("press start");
        if (args == null || args.length == 0) {
            System.out.println("args is null");
        } else {
            //System.out.println(String.join(" ", args));
            ParamDealUtils.ParamModel paramModel = ParamDealUtils.deal(args);
            System.out.println(paramModel.toString());
            ExecUtils execUtils = new ExecUtils();
            execUtils.run(paramModel);
        }
        System.out.println("press end");
    }


}
