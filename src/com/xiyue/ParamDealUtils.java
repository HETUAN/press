package com.xiyue;

/**
 * 参数请求处理
 */
public class ParamDealUtils {
    public static ParamModel deal(String[] args) {
        /*String argStr = String.join(" ", args);
        String[] argArray = argStr.split("-");
        for()*/
        ParamModel param = new ParamModel();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String nextArg = args[i + 1];
            switch (arg) {
                case "-type":
                    if (!checkIsParamName(nextArg)) {
                        param.setType(nextArg);
                        i++;
                    }
                    break;
                case "-url":
                    if (!checkIsParamName(nextArg)) {
                        if (nextArg.substring(0, 1).equals("?")) {
                            param.setUrl(nextArg.substring(1));
                        } else {
                            param.setUrl(nextArg);
                        }
                        i++;
                    }
                    break;
                case "-param":
                    if (!checkIsParamName(nextArg)) {
                        if (nextArg.substring(0, 1).equals("?")) {
                            param.setParam(nextArg.substring(1));
                        } else {
                            param.setParam(nextArg);
                        }
                        i++;
                    }
                    break;
                case "-method":
                    if (!checkIsParamName(nextArg)) {
                        param.setMethod(nextArg);
                        i++;
                    }
                    break;
                case "-reqs":
                    if (!checkIsParamName(nextArg)) {
                        param.setRequests(Integer.valueOf(nextArg));
                        i++;
                    }
                    break;
                case "-c":
                    if (!checkIsParamName(nextArg)) {
                        param.setConcurrency(Integer.valueOf(nextArg));
                        i++;
                    }
                    break;
                case "-times":
                    if (!checkIsParamName(nextArg)) {
                        param.setTimes(Long.valueOf(nextArg));
                        i++;
                    }
                    break;
                case "-timeout":
                    if (!checkIsParamName(nextArg)) {
                        param.setTimeout(Integer.valueOf(nextArg));
                        i++;
                    }
                    break;
                default:
                    continue;
            }
        }
        return param;
    }

    private static boolean checkIsParamName(String str) {
        if (str != null && str.length() > 0) {
            if (str.substring(0, 1).equals("-")) {
                return true;
            }
        }
        return false;
    }

    /**
     * -type type         url类型，url or file path
     * -url url         url类型，url or file path
     * -param param        url请求参数，json string or file path
     * -method method       请求方式 get or post
     * -reqs requests     要执行的请求次数
     * -c concurrency  并发数量
     * -times times        请求时间
     * -timeout timeout      响应时间
     */
    public static class ParamModel {
        /**
         * get or post
         */
        private String method;
        /**
         * request url
         */
        private String url;
        /**
         * request param
         */
        private String param;
        /**
         * file or url
         */
        private String type;
        /**
         * 请求总数
         */
        private Long times;
        /**
         * 请求总数
         */
        private Integer requests;
        /**
         * 并发数
         */
        private Integer concurrency;
        /**
         * 超时时间
         */
        private Integer timeout;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getTimes() {
            if (times == null) {
                return 0L;
            }
            return times;
        }

        public void setTimes(Long times) {
            this.times = times;
        }

        public Integer getRequests() {
            return requests;
        }

        public void setRequests(Integer requests) {
            this.requests = requests;
        }

        public Integer getConcurrency() {
            return concurrency;
        }

        public void setConcurrency(Integer concurrency) {
            this.concurrency = concurrency;
        }

        public Integer getTimeout() {
            if (timeout == null) {
                return 300;
            }
            return timeout;
        }

        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }

        @Override
        public String toString() {
            return "ParamModel{" +
                    "method='" + method + '\'' +
                    ", url='" + url + '\'' +
                    ", param='" + param + '\'' +
                    ", type='" + type + '\'' +
                    ", times=" + times +
                    ", requests=" + requests +
                    ", concurrency=" + concurrency +
                    ", timeout=" + timeout +
                    '}';
        }
    }
}
