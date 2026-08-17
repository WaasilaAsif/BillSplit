package com.example.billsplit.ui.groups;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.model.GroupDetail;
import com.example.billsplit.data.model.GroupMember;
import com.example.billsplit.data.model.MemberBalance;
import com.example.billsplit.data.repository.ApiCallback;
import com.example.billsplit.data.repository.GroupRepository;
import com.example.billsplit.local.TokenManager;
import com.example.billsplit.util.Money;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupDetailViewModel extends ViewModel {

    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US);

    private final GroupRepository groupRepository = new GroupRepository();

    private String groupId;

    private final MutableLiveData<Group> group = new MutableLiveData<>();
    private final MutableLiveData<List<Object>> expenseItems = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> memberNames = new MutableLiveData<>();
    private final MutableLiveData<List<MemberBalance>> memberBalances = new MutableLiveData<>();
    private final MutableLiveData<String> balanceBannerText = new MutableLiveData<>();
    private final MutableLiveData<Integer> balanceBannerColor = new MutableLiveData<>();
    private final MutableLiveData<Double> groupSpend = new MutableLiveData<>();
    private final MutableLiveData<Double> yourPaidTotal = new MutableLiveData<>();
    private final MutableLiveData<Double> yourShareTotal = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Group> getGroup() {
        return group;
    }

    public LiveData<List<Object>> getExpenseItems() {
        return expenseItems;
    }

    public LiveData<Map<String, String>> getMemberNames() {
        return memberNames;
    }

    public LiveData<List<MemberBalance>> getMemberBalances() {
        return memberBalances;
    }

    public LiveData<String> getBalanceBannerText() {
        return balanceBannerText;
    }

    public LiveData<Integer> getBalanceBannerColor() {
        return balanceBannerColor;
    }

    public LiveData<Double> getGroupSpend() {
        return groupSpend;
    }

    public LiveData<Double> getYourPaidTotal() {
        return yourPaidTotal;
    }

    public LiveData<Double> getYourShareTotal() {
        return yourShareTotal;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public String getGroupId() {
        return groupId;
    }

    public void load(String groupId) {
        this.groupId = groupId;

        groupRepository.getGroupDetail(groupId, new ApiCallback<GroupDetail>() {
            @Override
            public void onSuccess(GroupDetail detail) {
                onGroupDetailLoaded(detail);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });

        groupRepository.getGroupBalances(groupId, new ApiCallback<List<MemberBalance>>() {
            @Override
            public void onSuccess(List<MemberBalance> otherMembers) {
                onBalancesLoaded(otherMembers);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    private void onGroupDetailLoaded(GroupDetail detail) {
        Group g = detail.getGroup();
        group.setValue(g);

        Map<String, String> names = new HashMap<>();
        for (GroupMember m : detail.getMembers()) {
            names.put(m.getUserId(), m.getUsername());
        }
        memberNames.setValue(names);

        List<Expense> expenses = detail.getExpenses();
        expenseItems.setValue(groupByMonth(expenses));

        renderBanner(g);

        String userId = TokenManager.getInstance().getCurrentUserId();
        double spend = 0;
        double paid = 0;
        double share = 0;
        for (Expense e : expenses) {
            spend += e.getAmount();
            if (userId != null && userId.equals(e.getPaidBy())) paid += e.getAmount();
            if (e.getYourShare() != null) share += e.getYourShare();
        }
        groupSpend.setValue(spend);
        yourPaidTotal.setValue(paid);
        yourShareTotal.setValue(share);
    }

    private void onBalancesLoaded(List<MemberBalance> otherMembers) {
        List<MemberBalance> withSelf = new ArrayList<>();
        String userId = TokenManager.getInstance().getCurrentUserId();
        Group g = group.getValue();
        if (userId != null && g != null) {
            withSelf.add(new MemberBalance(userId, "You", g.getCurrentUserBalance()));
        }
        withSelf.addAll(otherMembers);
        memberBalances.setValue(withSelf);

        renderBannerCounterpart(otherMembers);
    }

    /** Sets the banner's amount/color from the group's own balance; the counterpart name fills in once balances arrive. */
    private void renderBanner(Group g) {
        double balance = g.getCurrentUserBalance();
        if (Math.abs(balance) < 0.01) {
            balanceBannerText.setValue(null); // hidden when settled
            return;
        }
        balanceBannerColor.setValue(balance < 0 ? R.color.color_error : R.color.primary);
        balanceBannerText.setValue(balance < 0
                ? "You owe " + Money.format(balance)
                : "You're owed " + Money.format(balance));
    }

    private void renderBannerCounterpart(List<MemberBalance> otherMembers) {
        Group g = group.getValue();
        if (g == null || Math.abs(g.getCurrentUserBalance()) < 0.01) return;

        MemberBalance biggest = null;
        for (MemberBalance m : otherMembers) {
            if (biggest == null || Math.abs(m.getBalance()) > Math.abs(biggest.getBalance())) {
                biggest = m;
            }
        }
        if (biggest == null) return;

        double balance = g.getCurrentUserBalance();
        String amount = Money.format(balance);
        balanceBannerText.setValue(balance < 0
                ? "You owe " + amount + " to " + biggest.getUsername()
                : biggest.getUsername() + " owes you " + amount);
    }

    private List<Object> groupByMonth(List<Expense> expenses) {
        List<Object> items = new ArrayList<>();
        String lastMonth = null;
        for (Expense e : expenses) {
            String month = MONTH_FORMAT.format(Instant.parse(e.getCreatedAt()).atZone(ZoneOffset.UTC))
                    .toUpperCase(Locale.US);
            if (!month.equals(lastMonth)) {
                items.add(month);
                lastMonth = month;
            }
            items.add(e);
        }
        return items;
    }
}
