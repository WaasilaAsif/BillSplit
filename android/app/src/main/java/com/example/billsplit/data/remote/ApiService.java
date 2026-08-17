package com.example.billsplit.data.remote;

import com.example.billsplit.data.model.User;
import com.example.billsplit.data.remote.dto.AddMemberRequest;
import com.example.billsplit.data.remote.dto.ApiResponse;
import com.example.billsplit.data.remote.dto.AuthResponse;
import com.example.billsplit.data.remote.dto.CreateExpenseRequest;
import com.example.billsplit.data.remote.dto.CreateGroupRequest;
import com.example.billsplit.data.remote.dto.ExpenseCreatedDto;
import com.example.billsplit.data.remote.dto.ExpenseDetailDto;
import com.example.billsplit.data.remote.dto.ExpenseDto;
import com.example.billsplit.data.remote.dto.ExpenseVoidedDto;
import com.example.billsplit.data.remote.dto.FriendBalanceDto;
import com.example.billsplit.data.remote.dto.FriendDto;
import com.example.billsplit.data.remote.dto.GroupBalanceDto;
import com.example.billsplit.data.remote.dto.GroupDetailResponseDto;
import com.example.billsplit.data.remote.dto.GroupDto;
import com.example.billsplit.data.remote.dto.GroupMemberDto;
import com.example.billsplit.data.remote.dto.LoginRequest;
import com.example.billsplit.data.remote.dto.RegisterRequest;
import com.example.billsplit.data.remote.dto.RemovedMemberDto;
import com.example.billsplit.data.remote.dto.SettlementDto;
import com.example.billsplit.data.remote.dto.SettlementRequest;
import com.example.billsplit.data.remote.dto.SettlementResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

//AppDataSource's RetrofitClient uses this interface to generate a concrete implementation of the API endpoints, which is then used by the Repository to make network calls.
public interface ApiService {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @GET("auth/me")
    Call<ApiResponse<User>> getCurrentUser();

    @GET("groups")
    Call<ApiResponse<List<GroupDto>>> getGroups();

    @POST("groups")
    Call<ApiResponse<GroupDto>> createGroup(@Body CreateGroupRequest request);

    @GET("groups/{id}")
    Call<ApiResponse<GroupDetailResponseDto>> getGroupDetail(@Path("id") String groupId);

    @POST("groups/{id}/members")
    Call<ApiResponse<GroupMemberDto>> addGroupMember(@Path("id") String groupId, @Body AddMemberRequest request);

    @DELETE("groups/{id}/members/{userId}")
    Call<ApiResponse<RemovedMemberDto>> removeGroupMember(@Path("id") String groupId, @Path("userId") String userId);

    @GET("groups/{id}/expenses")
    Call<ApiResponse<List<ExpenseDto>>> getGroupExpenses(@Path("id") String groupId);

    @GET("groups/{id}/balances")
    Call<ApiResponse<List<GroupBalanceDto>>> getGroupBalances(@Path("id") String groupId);

    @POST("expenses")
    Call<ApiResponse<ExpenseCreatedDto>> createExpense(@Body CreateExpenseRequest request);

    @GET("expenses/{id}")
    Call<ApiResponse<ExpenseDetailDto>> getExpenseDetail(@Path("id") String expenseId);

    @PUT("expenses/{id}")
    Call<ApiResponse<ExpenseCreatedDto>> updateExpense(@Path("id") String expenseId, @Body CreateExpenseRequest request);

    @DELETE("expenses/{id}")
    Call<ApiResponse<ExpenseVoidedDto>> deleteExpense(@Path("id") String expenseId);

    @GET("friends")
    Call<ApiResponse<List<FriendDto>>> getFriends();

    @GET("friends/{userId}/balance")
    Call<ApiResponse<FriendBalanceDto>> getFriendBalance(@Path("userId") String userId);

    @GET("friends/{userId}/expenses")
    Call<ApiResponse<List<ExpenseDto>>> getSharedExpenses(@Path("userId") String userId);

    @POST("settlements")
    Call<ApiResponse<SettlementResponse>> createSettlement(@Body SettlementRequest request);

    @GET("settlements")
    Call<ApiResponse<List<SettlementDto>>> getSettlements();

    @GET("users")
    Call<ApiResponse<List<User>>> searchUsers(@Query("search") String query);
}
