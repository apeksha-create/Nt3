package com.nectarinfotel.retrofit;

import com.google.gson.JsonObject;
import com.nectarinfotel.ui.login.LoginActivity;
import com.nectarinfotel.utils.AppConstants;
import com.nectarinfotel.utils.NectarApplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetroAPIInterface {

    @GET("https://nt3test.nectarinfotel.com/webservices/login.php")
    Call<JsonObject> callLoginAPI(@Query("auth_user") String username,
                                  @Query("auth_pwd") String password,
                                  @Query("device_token") String device_token);

    @GET("https://nt3test.nectarinfotel.com/webservices/dashboard.php")
    Call<JsonObject> callDashboardAPI(@Query("category") String category,
                                      @Query("subcategory") String subcategory,
                                      @Query("userid") String userid);

    @GET("https://nt3test.nectarinfotel.com/webservices/ticket_list.php")
    Call<JsonObject> callStatusDetailAPI(@Query("category") int category,
                                         @Query("subcategory") String subcategory,
                                         @Query("status") String status,
                                         @Query("userid") String userid,
                                         @Query("urgency") int urgency);

    @GET("https://nt3test.nectarinfotel.com/webservices/ticket_list.php")
    Call<JsonObject> callAgentDetailAPI(@Query("category") int category,
                                        @Query("subcategory") String subcategory,
                                        @Query("agent") String agent,
                                        @Query("userid") String userid,
                                        @Query("urgency") int urgency);

    @GET("https://nt3test.nectarinfotel.com/webservices/ticket_list.php")
    Call<JsonObject> callDepartmentDetailAPI(@Query("category") int category,
                                             @Query("subcategory") String subcategory,
                                             @Query("department") String department,
                                             @Query("userid") String userid,
                                             @Query("urgency") int urgency);

    @GET("https://nt3test.nectarinfotel.com/webservices/ticket_info.php")
    Call<JsonObject> callTicketDetailAPI(@Query("category") int category,
                                             @Query("ticketid") String subcategory,
                                             @Query("userid") String department);


    @GET("https://nt3test.nectarinfotel.com/webservices/province.php")
    Call<JsonObject> callProvinceListAPI(@Query("category") int category);

    @GET("https://nt3test.nectarinfotel.com/webservices/organization.php")
    Call<JsonObject> callAreaListAPI(@Query("category") int category);

    @GET("https://nt3test.nectarinfotel.com/webservices/catagory.php")
    Call<JsonObject> callCategoriesListAPI(@Query("category") int category);

   @GET("https://nt3test.nectarinfotel.com/webservices/event.php")
    Call<JsonObject> callEventListAPI(@Query("category") int category);

    @GET("https://nt3test.nectarinfotel.com/webservices/reason.php")
    Call<JsonObject> callReasonsListAPI(@Query("category") int category);

    @GET("https://nt3test.nectarinfotel.com/webservices/affectedsites.php")
    Call<JsonObject> callAffactedSitesListAPI(@Query("category") int category);
}
//https://nt3.nectarinfotel.com/webservices