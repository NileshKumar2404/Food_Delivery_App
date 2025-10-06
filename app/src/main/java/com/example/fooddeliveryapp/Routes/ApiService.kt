package com.example.fooddeliveryapp.Routes

import com.example.fooddeliveryapp.DataModel.AddAddressRequest
import com.example.fooddeliveryapp.DataModel.AddAddressResponse
import com.example.fooddeliveryapp.DataModel.AddFavouriteMenuItemResponse
import com.example.fooddeliveryapp.DataModel.AddtoCartModelResponse
import com.example.fooddeliveryapp.DataModel.AddtoCartRequest
import com.example.fooddeliveryapp.DataModel.DeleteSavedAddressResponse
import com.example.fooddeliveryapp.DataModel.FeaturedRestaurantResponse
import com.example.fooddeliveryapp.DataModel.GetAllRestaurantResponse
import com.example.fooddeliveryapp.DataModel.GetCartModelResponse
import com.example.fooddeliveryapp.DataModel.GetListOfFavouritesResponse
import com.example.fooddeliveryapp.DataModel.GetMenuItemsModelResponse
import com.example.fooddeliveryapp.DataModel.LoginUserRequest
import com.example.fooddeliveryapp.DataModel.LoginUserResponse
import com.example.fooddeliveryapp.DataModel.RegisterUserRequest
import com.example.fooddeliveryapp.DataModel.RegisterUserResponse
import com.example.fooddeliveryapp.DataModel.RemoveFavouriteMenuItemResponse
import com.example.fooddeliveryapp.DataModel.RemoveFavouriteRestaurantResponse
import com.example.fooddeliveryapp.DataModel.RemoveItemFromCartModelResponse
import com.example.fooddeliveryapp.DataModel.SavedAddressResponse
import com.example.fooddeliveryapp.DataModel.SearchMenuItemResponse
import com.example.fooddeliveryapp.DataModel.SearchRestaurantResponse
import com.example.fooddeliveryapp.DataModel.TopRatedRestaurantResponse
import com.example.fooddeliveryapp.DataModel.UpdateAddressModelResponse
import com.example.fooddeliveryapp.DataModel.UpdateAddressRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("user/register-user") fun registerUser(@Body registerUserRequest: RegisterUserRequest): Call<RegisterUserResponse>
    @POST("user/login-user") fun loginUser(@Body loginUserRequest: LoginUserRequest): Call<LoginUserResponse>
    @GET("search/search-restaurant") fun searchRestaurants(@Query("query") query: String): Call<SearchRestaurantResponse>
    @GET("search/search-menu") fun searchMenuItems(@Query("query") query: String): Call<SearchMenuItemResponse>
    @GET("restaurant/get-featured-restaurants") fun featuredRestaurants(): Call<FeaturedRestaurantResponse>
    @GET("restaurant/get-toprated-restaurants") fun topRatedRestaurants(): Call<TopRatedRestaurantResponse>
    @GET("restaurant/get-all-restaurants") fun getAllRestaurant(): Call<GetAllRestaurantResponse>
    @GET("favourite/get-favourites") fun getFavourite(): Call<GetListOfFavouritesResponse>
    @DELETE("favourite/remove-favourite-restaurant/{restaurantId}") fun removeFavouriteRestaurant(@Path("restaurantId") restaurantId: String): Call<RemoveFavouriteRestaurantResponse>
    @DELETE("favourite/remove-favourite-menuItem/{menuItemId}") fun removeFavouriteMenuItem(@Path("menuItemId") menuItemId: String): Call<RemoveFavouriteMenuItemResponse>
    @POST("address/add-address") fun addAddress(@Body AddAddressRequest: AddAddressRequest): Call<AddAddressResponse>
    @GET("address/getUserAddress") fun getAddress(): Call<SavedAddressResponse>
    @DELETE("address/delete-address/{addressId}") fun deleteAddress(@Path("addressId") addressId: String): Call<DeleteSavedAddressResponse>
    @PATCH("address/update-address/{addressId}") fun updateAddress(
        @Body updateAddressRequest: UpdateAddressRequest,
        @Path("addressId") addressId: String )
    : Call<UpdateAddressModelResponse>
    @GET("menuItem/get-menuItem/{menuItemId}") fun getMenuItemById(@Path("menuItemId") menuItemId: String): Call<GetMenuItemsModelResponse>
    @POST("cart/add-cart") fun addToCart(@Body cartRequest: AddtoCartRequest): Call<AddtoCartModelResponse>
    @POST("favourite/add-favourite-menuItem/{menuItemId}") fun addFavouriteMenuItem(@Path("menuItemId") menuItemId: String): Call<AddFavouriteMenuItemResponse>
    @GET("cart/get-cart") fun getCart(): Call<GetCartModelResponse>
    @DELETE("cart/remove-item/{cartItemId}") fun removeItemFromCart(@Path("cartItemId") cartItemId: String): Call<RemoveItemFromCartModelResponse>
}