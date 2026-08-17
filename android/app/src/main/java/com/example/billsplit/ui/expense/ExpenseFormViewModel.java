package com.example.billsplit.ui.expense;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.model.GroupDetail;
import com.example.billsplit.data.model.GroupMember;
import com.example.billsplit.data.repository.ApiCallback;
import com.example.billsplit.data.repository.ExpenseRepository;
import com.example.billsplit.data.repository.GroupRepository;
import com.example.billsplit.local.TokenManager;

import java.util.ArrayList;
import java.util.List;

public class ExpenseFormViewModel extends ViewModel {

    private final GroupRepository groupRepository = new GroupRepository();
    private final ExpenseRepository expenseRepository = new ExpenseRepository();

    private String expenseId; // null in add mode
    private String selectedGroupId;
    private String paidByUserId = TokenManager.getInstance().getCurrentUserId();
    private String selectedCategory = "Food";

    private final MutableLiveData<Group> group = new MutableLiveData<>();
    private final MutableLiveData<List<GroupMember>> groupMembers = new MutableLiveData<>();
    private final MutableLiveData<Expense> existingExpense = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();

    public LiveData<Group> getGroup() {
        return group;
    }

    public LiveData<List<GroupMember>> getGroupMembers() {
        return groupMembers;
    }

    public LiveData<Expense> getExistingExpense() {
        return existingExpense;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    public LiveData<Boolean> getDeleteSuccess() {
        return deleteSuccess;
    }

    public boolean isEditMode() {
        return expenseId != null;
    }

    public String getPaidByUserId() {
        return paidByUserId;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void init(String groupId, String expenseId) {
        this.expenseId = expenseId;

        if (expenseId != null) {
            expenseRepository.getExpense(expenseId, new ApiCallback<Expense>() {
                @Override
                public void onSuccess(Expense expense) {
                    existingExpense.setValue(expense);
                    selectedGroupId = expense.getGroupId();
                    paidByUserId = expense.getPaidBy();
                    selectedCategory = expense.getCategory() != null ? expense.getCategory() : "Food";
                    loadGroupDetail(selectedGroupId);
                }

                @Override
                public void onError(String message) {
                    errorMessage.setValue(message);
                }
            });
        } else {
            selectedGroupId = groupId;
            if (selectedGroupId != null) loadGroupDetail(selectedGroupId);
        }
    }

    private void loadGroupDetail(String groupId) {
        groupRepository.getGroupDetail(groupId, new ApiCallback<GroupDetail>() {
            @Override
            public void onSuccess(GroupDetail detail) {
                group.setValue(detail.getGroup());
                groupMembers.setValue(detail.getMembers());
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    public void loadAllGroups(ApiCallback<List<Group>> callback) {
        groupRepository.getGroups(callback);
    }

    public void selectGroup(Group g) {
        selectedGroupId = g.getId();
        group.setValue(g);
        loadGroupDetail(g.getId());
    }

    public void selectPaidBy(String userId) {
        this.paidByUserId = userId;
    }

    public void selectCategory(String category) {
        this.selectedCategory = category;
    }

    public void save(String amountText, String description) {
        if (selectedGroupId == null) {
            errorMessage.setValue("Choose a group first");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException | NullPointerException e) {
            amount = 0;
        }
        if (amount <= 0) {
            errorMessage.setValue("Enter an amount greater than Rs0");
            return;
        }
        if (description == null || description.trim().isEmpty()) {
            errorMessage.setValue("Description is required");
            return;
        }

        List<GroupMember> members = groupMembers.getValue();
        List<String> participantIds = new ArrayList<>();
        if (members != null) {
            for (GroupMember m : members) participantIds.add(m.getUserId());
        }

        ApiCallback<String> resultCallback = new ApiCallback<String>() {
            @Override
            public void onSuccess(String newExpenseId) {
                saveSuccess.setValue(true);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        };

        if (isEditMode()) {
            expenseRepository.updateExpense(expenseId, selectedGroupId, paidByUserId, amount,
                    description.trim(), selectedCategory, participantIds, resultCallback);
        } else {
            expenseRepository.addExpense(selectedGroupId, paidByUserId, amount, description.trim(),
                    selectedCategory, participantIds, resultCallback);
        }
    }

    public void delete() {
        if (expenseId == null) return;
        expenseRepository.deleteExpense(expenseId, new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                deleteSuccess.setValue(true);
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }
        });
    }
}
