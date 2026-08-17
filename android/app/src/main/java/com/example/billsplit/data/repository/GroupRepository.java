package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.model.GroupDetail;
import com.example.billsplit.data.model.GroupMember;
import com.example.billsplit.data.model.MemberBalance;
import com.example.billsplit.data.model.User;
import com.example.billsplit.data.remote.ApiErrorMapper;
import com.example.billsplit.data.remote.ApiService;
import com.example.billsplit.data.remote.RetrofitClient;
import com.example.billsplit.data.remote.dto.AddMemberRequest;
import com.example.billsplit.data.remote.dto.ApiResponse;
import com.example.billsplit.data.remote.dto.CreateGroupRequest;
import com.example.billsplit.data.remote.dto.GroupBalanceDto;
import com.example.billsplit.data.remote.dto.GroupDetailResponseDto;
import com.example.billsplit.data.remote.dto.GroupDto;
import com.example.billsplit.data.remote.dto.GroupMemberDto;
import com.example.billsplit.data.remote.dto.RemovedMemberDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupRepository {

    private final ApiService apiService = RetrofitClient.getApiService();

    public void getGroups(ApiCallback<List<Group>> callback) {
        apiService.getGroups().enqueue(new Callback<ApiResponse<List<GroupDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<GroupDto>>> call, Response<ApiResponse<List<GroupDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Group> groups = new ArrayList<>();
                    for (GroupDto dto : response.body().getData()) {
                        groups.add(toGroup(dto));
                    }
                    callback.onSuccess(groups);
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<GroupDto>>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    public void getGroupDetail(String groupId, ApiCallback<GroupDetail> callback) {
        apiService.getGroupDetail(groupId).enqueue(new Callback<ApiResponse<GroupDetailResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<GroupDetailResponseDto>> call, Response<ApiResponse<GroupDetailResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GroupDetailResponseDto dto = response.body().getData();
                    Group group = toGroup(dto.getGroup());

                    List<GroupMember> members = new ArrayList<>();
                    for (GroupMemberDto m : dto.getMembers()) {
                        members.add(new GroupMember(m.getGroupId(), m.getUserId(), m.getJoinedAt(), m.getUsername(), m.getEmail()));
                    }

                    List<Expense> expenses = ExpenseRepository.toExpenseList(dto.getExpenses());
                    callback.onSuccess(new GroupDetail(group, members, expenses));
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GroupDetailResponseDto>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    public void createGroup(String name, ApiCallback<Group> callback) {
        apiService.createGroup(new CreateGroupRequest(name)).enqueue(new Callback<ApiResponse<GroupDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<GroupDto>> call, Response<ApiResponse<GroupDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(toGroup(response.body().getData()));
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GroupDto>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    public void addMember(String groupId, User user, ApiCallback<Void> callback) {
        apiService.addGroupMember(groupId, new AddMemberRequest(user.getId())).enqueue(new Callback<ApiResponse<GroupMemberDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<GroupMemberDto>> call, Response<ApiResponse<GroupMemberDto>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GroupMemberDto>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    public void removeMember(String groupId, String userId, ApiCallback<Void> callback) {
        apiService.removeGroupMember(groupId, userId).enqueue(new Callback<ApiResponse<RemovedMemberDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<RemovedMemberDto>> call, Response<ApiResponse<RemovedMemberDto>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RemovedMemberDto>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    public void getGroupBalances(String groupId, ApiCallback<List<MemberBalance>> callback) {
        apiService.getGroupBalances(groupId).enqueue(new Callback<ApiResponse<List<GroupBalanceDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<GroupBalanceDto>>> call, Response<ApiResponse<List<GroupBalanceDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MemberBalance> balances = new ArrayList<>();
                    for (GroupBalanceDto dto : response.body().getData()) {
                        balances.add(new MemberBalance(dto.getUserId(), dto.getUsername(), dto.getBalance()));
                    }
                    callback.onSuccess(balances);
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<GroupBalanceDto>>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    private static Group toGroup(GroupDto dto) {
        Group group = new Group(dto.getId(), dto.getName(), dto.getCreatedBy(), dto.isTemporary(), dto.isArchived(), dto.getCreatedAt());
        group.setCurrentUserBalance(dto.getCurrentUserBalance());
        group.setMemberCount(dto.getMemberCount());
        return group;
    }

    public static class DashboardSummary {
        public final double youOwe;
        public final double youAreOwed;

        public DashboardSummary(double youOwe, double youAreOwed) {
            this.youOwe = youOwe;
            this.youAreOwed = youAreOwed;
        }

        public double getNet() {
            return youAreOwed - youOwe;
        }
    }
}
