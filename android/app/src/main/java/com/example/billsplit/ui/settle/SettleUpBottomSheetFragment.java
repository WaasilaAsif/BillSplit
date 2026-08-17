package com.example.billsplit.ui.settle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Settlement;
import com.example.billsplit.data.repository.ApiCallback;
import com.example.billsplit.data.repository.SettlementRepository;
import com.example.billsplit.util.Money;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SettleUpBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_FROM = "fromUserId";
    private static final String ARG_TO = "toUserId";
    private static final String ARG_AMOUNT = "amount";
    private static final String ARG_GROUP = "groupId";
    private static final String ARG_OTHER_NAME = "otherPartyName";

    private final SettlementRepository settlementRepository = new SettlementRepository();

    //no get user by id and it messes up the payment work some places look in deeper
    public static SettleUpBottomSheetFragment newInstance(String fromUserId, String toUserId,
                                                            double amount, @Nullable String groupId,
                                                            String otherPartyName) {
        SettleUpBottomSheetFragment fragment = new SettleUpBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FROM, fromUserId);
        args.putString(ARG_TO, toUserId);
        args.putDouble(ARG_AMOUNT, amount);
        args.putString(ARG_GROUP, groupId);
        args.putString(ARG_OTHER_NAME, otherPartyName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_settle_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = requireArguments();
        String fromUserId = args.getString(ARG_FROM);
        String toUserId = args.getString(ARG_TO);
        double amount = args.getDouble(ARG_AMOUNT);
        String groupId = args.getString(ARG_GROUP);
        String otherName = args.getString(ARG_OTHER_NAME, "");

        ((TextView) view.findViewById(R.id.settleTitleText))
                .setText(getString(R.string.settle_up_with, otherName));
        ((TextView) view.findViewById(R.id.settleAmountText)).setText(Money.format(amount));

        Button recordButton = view.findViewById(R.id.recordPaymentButton);
        view.findViewById(R.id.cancelText).setOnClickListener(v -> dismiss());
        recordButton.setOnClickListener(v -> {
            recordButton.setEnabled(false);
            settlementRepository.recordSettlement(groupId, fromUserId, toUserId, amount, new ApiCallback<Settlement>() {
                @Override
                public void onSuccess(Settlement result) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), R.string.payment_recorded, Toast.LENGTH_SHORT).show();
                    dismiss();
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) return;
                    recordButton.setEnabled(true);
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
