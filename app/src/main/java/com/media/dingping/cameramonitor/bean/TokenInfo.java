package com.media.dingping.cameramonitor.bean;

import com.google.gson.Gson;

/**
 * Created by lu on 2017/5/25.
 */

public class TokenInfo {

    /**
     * msg : 获取成功
     * status : 200
     * data : {"address":{"ip":"192.168.1.2","port":"81"},"token":"f45fa4f1-b4b4-4863-b991-158b684c7617"}
     */

    private String msg;
    private int status;
    private DataBean data;

    private String deviceSerial = "";
    private String verifyCode = "";

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }


    public static class DataBean {
        /**
         * address : {"ip":"192.168.1.2","port":"81"}
         * token : f45fa4f1-b4b4-4863-b991-158b684c7617
         */

        private AddressBean address;
        private String token;

        public String getTokenJson() {
            return new Gson().toJson(new TonkenJson(getToken()));
        }

        public AddressBean getAddress() {
            return address;
        }

        public void setAddress(AddressBean address) {
            this.address = address;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }


        public static class AddressBean {
            /**
             * ip : 192.168.1.2
             * port : 81
             */

            private String ip;
            private String port;

            public AddressBean() {

            }

            public AddressBean(String ip, String port) {
                this.ip = ip;
                this.port = port;
            }

            public String getIp() {
                return ip;
            }

            public void setIp(String ip) {
                this.ip = ip;
            }

            public String getPort() {
                return port;
            }

            public void setPort(String port) {
                this.port = port;
            }


            public String getBaseUrl() {
                return "http://" + ip + ":" + port + "/";
            }

            @Override
            public String toString() {
                return "AddressBean{" +
                        "ip='" + ip + '\'' +
                        ", port='" + port + '\'' +
                        '}';
            }
        }

        public static class TonkenJson {
            private String AccessToken;

            public TonkenJson(String accessToken) {
                AccessToken = accessToken;
            }
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "address=" + address +
                    ", token='" + token + '\'' +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "TokenInfo{" +
                "msg='" + msg + '\'' +
                ", status=" + status +
                ", data=" + data +
                '}';
    }
}
