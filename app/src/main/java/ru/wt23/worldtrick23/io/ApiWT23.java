package ru.wt23.worldtrick23.io;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiWT23 {

    @GET("get_users")
    Call<ArrayList<WT23User>> getWtUsers(@Query("login_id") int myId);

    @FormUrlEncoded
    @POST("signin")
    Call<ResponseBody> signIn(@Field("login") String login, @Field("password") String password);

    @FormUrlEncoded
    @POST("get_info_about_me")
    Call<Me> getInfoAboutMe(@Field("login") String login, @Field("password") String password);

    @FormUrlEncoded
    @POST("set_info_about_me")
    Call<ResponseBody> setInfoAboutMe(@Field("login") String login, @Field("password") String password,
                                      @Field("new_password") String newPassword, @Field("new_name") String newName,
                                      @Field("new_surname") String newSurname, @Field("new_patronymic") String newPatronymic,
                                      @Field("new_email") String newEmail, @Field("new_date_old") String newDateOld,
                                      @Field("new_instagram") String newInstagram, @Field("new_about") String newAbout,
                                      @Field("new_break") String newBreak, @Field("new_tricking") String newTricking,
                                      @Field("new_trampoline") String newTrampoline, @Field("new_parkour") String newParkour);

    @GET("getnews")
    Call<ArrayList<New>> getNews(@Query("from") String newsId, @Query("count") int count);

    @GET("getnews")
    Call<ArrayList<New>> getNews(@Query("count") int count);

    @GET("getoboi")
    Call<ArrayList<Wallpaper>> getOboi();

    @GET("getmagazine")
    Call<ArrayList<Magazine>> getMagazine();

    @GET("get_duels_for_me")
    Call<ArrayList<MyDuel>> getDuelsForMe(@Query("login_id") int myId);

    @GET("get_all_individ_battles")
    Call<ArrayList<IndividBattle>> getAllIndividBattles();

    @GET("get_group_battles")
    Call<ArrayList<GroupBattle>> getGroupBattles();

    @GET("get_users_group_battle")
    Call<ArrayList<UsersGroupBattle>> getUsersGroupBattle(@Query("battle_id") String battleId);

    @GET("get_gen_users_group_battle")
    Call<ArrayList<GenUsersGroupBattle>> getGenUsersGroupBattle(@Query("battle_id") String battleId);

    @FormUrlEncoded
    @POST("stop_battle")
    Call<ResponseBody> stopBattle(@Field("battle_id") String battleId, @Field("login_id") int myId, @Field("cancel_duel") String noText);

    @FormUrlEncoded
    @POST("accept_battle")
    Call<ResponseBody> acceptBattle(@Field("battle_id") String battleId, @Field("login_id") int myId, @Field("accept_duel") String noText);

    @FormUrlEncoded
    @POST("accept_group_battle")
    Call<ResponseBody> acceptGroupBattle(@Field("battle_id") String battleId, @Field("login_id") int myId, @Field("accept") String noText);

    @FormUrlEncoded
    @POST("start_battle")
    Call<ResponseBody> startBattle(@Field("from_user") int fromUser, @Field("to_user") String toUser, @Field("category") String category, @Field("start_duel") String noText);

    @GET("get_individ_{category}")
    Call<ArrayList<CategoryIndividBattle>> getIndividBattlesByCategory(@Path("category") String category, @Query("login_id") int myId);

    @FormUrlEncoded
    @POST("regauth/restorepass")
    Call<ResponseBody> restorePassword(@Field("email") String email, @Field("restore") String noText);

    @FormUrlEncoded
    @POST("regauth/registration")
    Call<ResponseBody> registration(@Field("login") String login, @Field("password") String password,
                                    @Field("email") String email, @Field("surname") String surname,
                                    @Field("name") String name, @Field("patronymic") String patronymic,
                                    @Field("date_old") String date_old, @Field("reg") String noText);

    //@FormUrlEncoded//доделать...
    @Multipart
    @POST("upload_video_group_battle")
    Call<ResponseBody> uploadVideoGroupBattle(@Part("battle_id") RequestBody battleId, @Part("user_id") RequestBody myId,
                                              @Part("upload_file") RequestBody noText,
                                              @Part MultipartBody.Part video,
                                              @Part("who_user") RequestBody user1ORuser2,
                                              @Part("who_video") RequestBody video1ORvideo2,
                                              @Part("raund") RequestBody raund);

    @Multipart
    @POST("upload_video")
    Call<ResponseBody> uploadVideoIndBattle(@Query("battle_id") String battleId, @Query("who") String who, @Part MultipartBody.Part video);

    @GET("read_comments")
    Call<ArrayList<NewsComment>> getNewsComments(@Query("news_id") String newsId);

    @FormUrlEncoded
    @POST("write_comment")
    Call<ResponseBody> writeComment(@Field("login_id") int myId, @Field("password") String myPassword, @Field("text_comment") String comment, @Field("news_id") String newsId);

    @FormUrlEncoded
    @POST("delete_comment")
    Call<ResponseBody> deleteComment(@Field("comment_id") String commentId);


    @FormUrlEncoded
    @POST("order_magazine")
    Call<ResponseBody> sendMagazineOrder(@Field("user_info") String JSON_userInfo, @Field("data") String JSON_data);

    @GET("get_stream_battles")
    Call<ArrayList<StreamBattle>> getStreamBattles();

}
