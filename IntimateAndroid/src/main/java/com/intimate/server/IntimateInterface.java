package com.intimate.server;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Created by yurii_laguta on 28/07/13.
 *
 * @GET("/group/{id}/users") List<User> groupList(@Path("id") int groupId);
 * Query parameters can also be added.
 * @GET("/group/{id}/users") List<User> groupList(@Path("id") int groupId, @Query("sort") String sort);
 * @FormUrlEncoded
 * @POST("/user/edit") User updateUser(@Field("first_name") String first, @Field("last_name") String last);
 * Multipart requests are used when @Multipart is present on the method. Parts are declared using the @Part annotation.
 * @Multipart
 * @PUT("/user/photo") User updateUser(@Part("photo") TypedFile photo, @Part("description") TypedString description);
 */
public interface IntimateInterface {
    final boolean USE_LOCAL = false;
    String LOCAL_URL = "http://" + "192.168.89.79:8081";// "http://192.168.2.43:8081";
    String BASE_URL = USE_LOCAL ? LOCAL_URL : "http://54.213.95.44:8080";
    String TOKEN = "token";

    @FormUrlEncoded
    @POST("/signup")
    void signup(@Field("nickname") String nickname, @Field("email") String email,
                @Field("password") String password, Callback<Response> callback);

    @FormUrlEncoded
    @POST("/login")
    void login(@Field("authHash") String authHash, @Field("user") String email, Callback<Response> callback);

    /*
    it('user1 can get information about themselves', function(next) {
		var rep = request.get(url+"/secure/"+user1.token , function(err,res,body){
			assert.equal(JSON.parse(body).payload.nickname,"sally");

			next();
		});
	});
     */

    @GET("/secure/{token}")
    void getUserInfoWithRooms(@Path(TOKEN) String token, Callback<Response> cb);

    @GET("/secure/{token}/roomid/{room}")
    void getRoomId(@Path(TOKEN) String token, @Path("room") String room, Callback<Response> cb);

    @GET("/secure/{token}/room/{roomId}")
    void getRoom(@Path(TOKEN) String token, @Path("roomId") String roomId, Callback<Response> cb);


    /*
    var image = require('fs').readFileSync('tests/resources/flower.jpg');
		var imageBuffer = new Buffer(image, 'binary');
		request({
			method: 'POST',
			uri: url+"/secure/"+user1.token+"/resource/",
			headers: {'content-type': 'multipart/form-data'},
			multipart: [{
				'Content-Disposition': 'form-data; name="my_file"; filename="flower.jpg"',
				'Content-Type': 'image/jpeg',
				'Content-Transfer-Encoding': 'base64',
				body: imageBuffer
			}]
		}
     */

    @Multipart
    @POST("/secure/{token}/media/")
    void addMedia(@Path(TOKEN) String token, @Part("Content-Disposition") String contentDisposition,
                  @Part("Content-Type") String contentType, @Part("Content-Transfer-Encoding") String contentTranEncoding,
                  @Part("body") TypedFile filePhoto, Callback<Response> callback);

    @GET("/secure/{token}/room/{roomId}/resource/{resId}/media/{mediaId}")
    void getMedia(@Path(TOKEN) String token, @Path("roomId") String roomId, @Path("resId") String resId, @Path("mediaId") String mediaId, Callback<Response> cb);

    @FormUrlEncoded
    @POST("/secure/{token}/resource/")
    void createResource(@Path(TOKEN) String token, @Field("type") String type, @Field("mediaId") String mediaId, Callback<Response> cb);

//    it('user1 can associate a resource id with a room ', function(next){
//        var rep = request.get(url+"/secure/"+user1.token+"/room/"+user1.room+"/asc/"+user1.flowerpicId, function(err,res,body){
//            assert.equal((JSON.parse(body).payload+"").length,13);
//            next();
//        });
//    });

    @POST("/secure/{token}/resources/")
    void getResources(@Path(TOKEN) String token, Callback<Response> cb);

    @GET("/secure/{token}/room/{room}/asc/{resourceId}")
    void associateResource(@Path(TOKEN) String token, @Path("room") String room, @Path("resourceId") String resourceId, Callback<Response> cb);

    @POST("/secure/{token}/contacts")
    void getContacts(@Path(TOKEN) String token, Callback<Response> cb);

    @GET("/secure/{token}/contact/invite/{email}")
    void inviteUser(@Path(TOKEN) String token, @Path("email") String email, Callback<Response> cb);
}
