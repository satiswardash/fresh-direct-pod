package com.incture.cherrywork.freshdirect.Utils;

/**
 * Created by Arun on 08-09-2016.
 */
public class FDUrls {
    //public static final String DOMAIN_DEV = "dev.cherrywork.in/fduat/";
    public static final String DOMAIN_DEV = "dev.cherrywork.in:3001/";
    public static final String DOMAIN_UAT = "nj01ldincture01.nj01:3001/";
    public static final String DOMAIN_STAGING = "52.39.185.168";
    public static final String PRODUCTION = "delivery.freshdirect.com/api/";
    public static final String DOMAIN = PRODUCTION;
    public static final String BASE_URL = "https://" + DOMAIN;
    //login and reset password
    public static final String LOGIN_URL = BASE_URL + "login";
    public static final String RESET_PASSWORD = BASE_URL + "api/v1/users/";
    //Orders(Fetch and Complete)
    public static final String FETCH_ORDERS = BASE_URL + "api/v1/pod/orders/";
    public static final String UPDATE_ORDERS = BASE_URL + "api/v1/pod/order/complete/";

    //Start,Cancel and Complete Trip
    public static final String START_TRIP = BASE_URL + "api/v1/pod/trip/start/";
    public static final String CANCEL_TRIP = BASE_URL + "api/v1/pod/trip/cancel/";
    public static final String COMPLETE_TRIP = BASE_URL + "api/v1/pod/trip/complete/";
    //Update and Start Location(start and end )
    public static final String UPDATE_LOCATION = BASE_URL + "api/v1/pod/order/location";
    public static final String START_LOCATION = BASE_URL + "api/v1/pod/order/start";
    public static final String END_LOCATION = BASE_URL + "api/v1/pod/order/stop";
    //Get Feeds
    public static final String GET_WORKITEM = BASE_URL + "api/v1/workitems";
    public static final String GET_WORKITEM_WITH_PAGINATION = BASE_URL + "api/v1/workitems?limit=";
    public static final String LIKE = BASE_URL + "api/v1/like/workitem/";
    public static final String ADD_COMMENT = BASE_URL + "api/v1/comment/workitem/";
    public static final String GET_WORKITEM_DETAIL = BASE_URL + "api/v1/workitem/";

    public static final String AVATAR_BASE = "http://d1sqfcb5cb617.cloudfront.net/images/";
    //leaderBoard
    public static final String LEADERBOARD = "https://delivery.freshdirect.com/leaderboard/#/mobile/leader-board?skipCount=0&filterType=day&sortType=delayedOrders&sortOrder=1";//production
    //public static final String LEADERBOARD ="http://dev.cherrywork.in:7002/#/mobile/leader-board?skipCount=0&filterType=day&sortType=delayedOrders&sortOrder=1";//dev
    //public static final String LEADERBOARD ="http://nj01ldincture01.nj01:3003/#/mobile/leader-board?skipCount=0&filterType=day&sortType=delayedOrders&sortOrder=1";//uat
    //public static final String SESSION_EXPIRED ="http://dev.cherrywork.in:7001/#/mobile/session-expired";

    public static final String LEADERBOARD_COOKIE = "delivery.freshdirect.com";//production
    //public static final String LEADERBOARD_COOKIE ="dev.cherrywork.in";//dev
    //public static final String LEADERBOARD_COOKIE ="nj01ldincture01.nj01";//uat

    //app update
    public static final String APP_UPDATE = BASE_URL + "api/v1/pod/app/version";
    public static final String APP_URL = "https://delivery.freshdirect.com/";//prod
   // public static final String APP_URL = "http://dev.cherrywork.in:3001/";//dev


    // public static final String TRIP_ID=FDPreferences.getTripId();
}
