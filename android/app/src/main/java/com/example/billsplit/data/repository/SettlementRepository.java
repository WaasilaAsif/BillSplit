package com.example.billsplit.data.repository;

import com.example.billsplit.data.model.Settlement;
import com.example.billsplit.data.model.SettlementStatus;
import com.example.billsplit.data.remote.ApiErrorMapper;
import com.example.billsplit.data.remote.ApiService;
import com.example.billsplit.data.remote.RetrofitClient;
import com.example.billsplit.data.remote.dto.ApiResponse;
import com.example.billsplit.data.remote.dto.SettlementDto;
import com.example.billsplit.data.remote.dto.SettlementRequest;
import com.example.billsplit.data.remote.dto.SettlementResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettlementRepository {

    private final ApiService apiService = RetrofitClient.getApiService();

    //idempotency key is generated on the client side to ensure that if the same request is sent multiple times, it will only be processed once by the server. This is important for operations like settlements where you don't want to accidentally create multiple settlements for the same transaction.
    public void recordSettlement(String groupId, String fromUserId, String toUserId, double amount,
                                  ApiCallback<Settlement> callback) {
        String idempotencyKey = UUID.randomUUID().toString();
        SettlementRequest request = new SettlementRequest(groupId, toUserId, amount, idempotencyKey);
        apiService.createSettlement(request).enqueue(new Callback<ApiResponse<SettlementResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SettlementResponse>> call, Response<ApiResponse<SettlementResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SettlementResponse body = response.body().getData();
                    Settlement settlement = new Settlement(groupId, fromUserId, toUserId, amount, idempotencyKey);
                    settlement.setId(body.getSettlementId());
                    settlement.setStatus(SettlementStatus.COMPLETED);
                    callback.onSuccess(settlement);
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SettlementResponse>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    public void getSettlements(ApiCallback<List<Settlement>> callback) {
        apiService.getSettlements().enqueue(new Callback<ApiResponse<List<SettlementDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SettlementDto>>> call, Response<ApiResponse<List<SettlementDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Settlement> settlements = new ArrayList<>();
                    for (SettlementDto dto : response.body().getData()) {
                        Settlement s = new Settlement(dto.getGroupId(), dto.getFromUserId(), dto.getToUserId(), dto.getAmount(), null);
                        s.setId(dto.getId());
                        s.setCreatedAt(dto.getCreatedAt());
                        s.setStatus(parseStatus(dto.getStatus()));
                        settlements.add(s);
                    }
                    callback.onSuccess(settlements);
                } else {
                    callback.onError(ApiErrorMapper.extractServerErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SettlementDto>>> call, Throwable t) {
                callback.onError(ApiErrorMapper.networkErrorMessage(t));
            }
        });
    }

    private static SettlementStatus parseStatus(String value) {
        if (value == null) return SettlementStatus.PENDING;
        try {
            return SettlementStatus.valueOf(value.toUpperCase(java.util.Locale.US));
        } catch (IllegalArgumentException e) {
            return SettlementStatus.PENDING;
        }
    }
}
