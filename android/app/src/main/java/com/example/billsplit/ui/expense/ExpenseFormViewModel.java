package com.example.billsplit.ui.expense;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplit.data.model.Expense;
import com.example.billsplit.data.model.ExpenseSplit;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.model.SplitType;
import com.example.billsplit.data.model.User;
import com.example.billsplit.data.repository.ExpenseRepository;
import com.example.billsplit.data.repository.GroupRepository;
import com.example.billsplit.local.AppDataStore;

import java.util.List;

public class ExpenseFormViewModel extends ViewModel {

    private final GroupRepository groupRepository = new GroupRepository();
    private final ExpenseRepository expenseRepository = new ExpenseRepository();

    private String expenseId; // null in add mode
    private String selectedGroupId;
    private String paidByUserId = AppDataStore.CURRENT_USER_ID;
    private String selectedCategory = "Food";

    private final MutableLiveData<Group> group = new MutableLiveData<>();
    private final MutableLiveData<Expense> existingExpense = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();

    public LiveData<Group> getGroup() {
        return group;
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
            Expense expense = expenseRepository.getExpense(expenseId);
            existingExpense.setValue(expense);
            if (expense != null) {
                selectedGroupId = expense.getGroupId();
                paidByUserId = expense.getPaidBy();
                selectedCategory = expense.getCategory() != null ? expense.getCategory() : "Food";
            }
        } else {
            selectedGroupId = groupId;
        }
        group.setValue(groupRepository.getGroup(selectedGroupId));
    }

    public List<Group> getAllGroups() {
        return groupRepository.getGroups();
    }

    public void selectGroup(Group g) {
        selectedGroupId = g.getId();
        group.setValue(g);
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
            errorMessage.setValue("Enter an amount greater than $0");
            return;
        }
        if (description == null || description.trim().isEmpty()) {
            errorMessage.setValue("Description is required");
            return;
        }

        List<User> members = groupRepository.getGroupMemberUsers(selectedGroupId);
        List<String> memberIds = new java.util.ArrayList<>();
        for (User u : members) memberIds.add(u.getId());
        List<ExpenseSplit> splits = ExpenseRepository.buildEqualSplits(amount, memberIds);

        if (isEditMode()) {
            Expense updated = existingExpense.getValue();
            if (updated == null) return;
            updated.setAmount(amount);
            updated.setDescription(description.trim());
            updated.setCategory(selectedCategory);
            updated.setPaidBy(paidByUserId);
            updated.setSplitType(SplitType.EQUAL);
            updated.setSplits(splits);
            expenseRepository.updateExpense(updated);
        } else {
            expenseRepository.addExpense(selectedGroupId, paidByUserId, amount, description.trim(),
                    selectedCategory, SplitType.EQUAL, splits);
        }
        saveSuccess.setValue(true);
    }

    public void delete() {
        if (expenseId != null) {
            expenseRepository.deleteExpense(expenseId);
            deleteSuccess.setValue(true);
        }
    }
}
