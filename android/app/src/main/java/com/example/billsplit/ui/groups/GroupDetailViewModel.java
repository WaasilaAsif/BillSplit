package com.example.billsplit.ui.groups;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.model.GroupMember;
import com.example.billsplit.data.model.Settlement;
import com.example.billsplit.data.repository.ExpenseRepository;
import com.example.billsplit.data.repository.GroupRepository;
import com.example.billsplit.local.AppDataStore;
import com.example.billsplit.util.BalanceCalculator;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupDetailViewModel extends ViewModel {

    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US);

    private final GroupRepository groupRepository = new GroupRepository();
    private final ExpenseRepository expenseRepository = new ExpenseRepository();
    private final AppDataStore store = AppDataStore.getInstance();

    private String groupId;

    private final MutableLiveData<Group> group = new MutableLiveData<>();
    private final MutableLiveData<List<Object>> expenseItems = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Double>> memberBalances = new MutableLiveData<>();
    private final MutableLiveData<String> balanceBannerText = new MutableLiveData<>();
    private final MutableLiveData<Integer> balanceBannerColor = new MutableLiveData<>();
    private final MutableLiveData<Double> groupSpend = new MutableLiveData<>();
    private final MutableLiveData<Double> yourPaidTotal = new MutableLiveData<>();
    private final MutableLiveData<Double> yourShareTotal = new MutableLiveData<>();

    public LiveData<Group> getGroup() {
        return group;
    }

    public LiveData<List<Object>> getExpenseItems() {
        return expenseItems;
    }

    public LiveData<Map<String, Double>> getMemberBalances() {
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

    public String getGroupId() {
        return groupId;
    }

    public void load(String groupId) {
        this.groupId = groupId;

        Group g = groupRepository.getGroup(groupId);
        List<Expense> expenses = expenseRepository.getExpensesForGroup(groupId);
        List<Settlement> settlements = store.getSettlementsForGroup(groupId);
        String userId = AppDataStore.CURRENT_USER_ID;

        double balance = BalanceCalculator.applySettlements(
                BalanceCalculator.userBalance(expenses, userId), settlements, userId);
        if (g != null) g.setCurrentUserBalance(balance);
        group.setValue(g);

        expenseItems.setValue(groupByMonth(expenses));
        memberBalances.setValue(groupRepository.getMemberBalances(groupId));
        renderBanner(groupId, expenses, settlements, userId, balance);

        double spend = groupRepository.getGroupSpendTotal(groupId);
        groupSpend.setValue(spend);

        double paid = 0;
        double share = 0;
        for (Expense e : expenses) {
            if (userId.equals(e.getPaidBy())) paid += e.getAmount();
            if (e.getSplits() != null) {
                for (com.example.billsplit.data.model.ExpenseSplit s : e.getSplits()) {
                    if (userId.equals(s.getUserId())) share += s.getShareAmount();
                }
            }
        }
        yourPaidTotal.setValue(paid);
        yourShareTotal.setValue(share);
    }

    private void renderBanner(String groupId, List<Expense> expenses, List<Settlement> settlements,
                               String userId, double balance) {
        if (Math.abs(balance) < 0.01) {
            balanceBannerText.setValue(null); // hidden when settled
            return;
        }

        String counterpartName = null;
        double biggest = 0;
        for (GroupMember m : store.getGroupMembers(groupId)) {
            if (userId.equals(m.getUserId())) continue;
            double pair = BalanceCalculator.pairBalance(expenses, settlements, userId, m.getUserId());
            if (Math.abs(pair) > Math.abs(biggest)) {
                biggest = pair;
                counterpartName = m.getUsername();
            }
        }

        boolean bannerColorIsError = balance < 0;
        balanceBannerColor.setValue(bannerColorIsError
                ? com.example.billsplit.R.color.color_error
                : com.example.billsplit.R.color.primary);

        String amount = com.example.billsplit.util.Money.format(balance);
        if (counterpartName != null) {
            balanceBannerText.setValue(balance < 0
                    ? "You owe " + amount + " to " + counterpartName
                    : counterpartName + " owes you " + amount);
        } else {
            balanceBannerText.setValue(balance < 0 ? "You owe " + amount : "You're owed " + amount);
        }
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
