package com.ferru97.beatbracelet.data;

public class API {
    public static String login = "http://192.168.178.41:3000/api/login";
    public static String add_bracelet = "http://192.168.178.41:3000/api/add_bracelet";
    public static String get_bracelets = "http://192.168.178.41:3000/api/get_bracelets";
    public static String get_braceletInfo = "http://192.168.178.41:3000/api/brc_info";
    public static String set_braceletInfo = "http://192.168.178.41:3000/api/set_brc_info";

    public static String client_id = null;
    public static String client_psw = null;
    public static String broker_url = "tcp://192.168.178.41:1883";

    public static String mqtt_subAlert = "/alert";

}
