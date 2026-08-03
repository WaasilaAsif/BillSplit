package com.example.billsplit.ui.expense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.model.User;
import com.example.billsplit.data.repository.GroupRepository;
import com.example.billsplit.local.AppDataStore;
import com.example.billsplit.util.Money;

import java.util.List;

public class ExpenseFormFragment extends Fragment {

    private ExpenseFormViewModel viewModel;
    private final GroupRepository groupRepository = new GroupRepository();

    private TextView formTitleText;
    private TextView groupChip;
    private EditText amountInput;
    private EditText descriptionInput;
    private TextView paidBySummaryText;
    private LinearLayout membersRow;
    private android.widget.Button saveExpenseButton;
    private TextView deleteExpenseText;

    private View[] categoryDots;
    private View[] categoryContainers;
    private final String[] categories = {"Food", "Transport", "Housing", "Other"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ExpenseFormViewModel.class);

        formTitleText = view.findViewById(R.id.formTitleText);
        groupChip = view.findViewById(R.id.groupChip);
        amountInput = view.findViewById(R.id.amountInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        paidBySummaryText = view.findViewById(R.id.paidBySummaryText);
        membersRow = view.findViewById(R.id.membersRow);
        saveExpenseButton = view.findViewById(R.id.saveExpenseButton);
        deleteExpenseText = view.findViewById(R.id.deleteExpenseText);

        categoryDots = new View[]{
                view.findViewById(R.id.categoryFoodDot), view.findViewById(R.id.categoryTransportDot),
                view.findViewById(R.id.categoryHousingDot), view.findViewById(R.id.categoryOtherDot)
        };
        categoryContainers = new View[]{
                view.findViewById(R.id.categoryFood), view.findViewById(R.id.categoryTransport),
                view.findViewById(R.id.categoryHousing), view.findViewById(R.id.categoryOther)
        };
        for (int i = 0; i < categories.length; i++) {
            String category = categories[i];
            categoryContainers[i].setOnClickListener(v -> {
                viewModel.selectCategory(category);
                renderCategorySelection();
            });
        }

        view.findViewById(R.id.closeButton).setOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());
        view.findViewById(R.id.saveIcon).setOnClickListener(v -> save());
        saveExpenseButton.setOnClickListener(v -> save());

        groupChip.setOnClickListener(this::showGroupPicker);
        View editPaidByText = view.findViewById(R.id.editPaidByText);
        editPaidByText.setOnClickListener(this::showPaidByPicker);
        paidBySummaryText.setOnClickListener(this::showPaidByPicker);

        deleteExpenseText.setOnClickListener(v -> {
            viewModel.delete();
            Navigation.findNavController(view).navigateUp();
        });

        Bundle args = getArguments();
        String groupId = args != null ? args.getString("groupId") : null;
        String expenseId = args != null ? args.getString("expenseId") : null;
        viewModel.init(groupId, expenseId);

        boolean editMode = viewModel.isEditMode();
        formTitleText.setText(editMode ? R.string.edit_expense_title : R.string.add_expense_title);
        saveExpenseButton.setText(editMode ? R.string.save_changes : R.string.save_expense);
        deleteExpenseText.setVisibility(editMode ? View.VISIBLE : View.GONE);

        viewModel.getGroup().observe(getViewLifecycleOwner(), g -> {
            if (g != null) {
                groupChip.setText(g.getName());
                renderMembers(g.getId());
                renderPaidBySummary();
            }
        });
        viewModel.getExistingExpense().observe(getViewLifecycleOwner(), expense -> {
            if (expense != null) {
                amountInput.setText(String.valueOf(expense.getAmount()));
                descriptionInput.setText(expense.getDescription());
                renderCategorySelection();
                renderPaidBySummary();
            }
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });
        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) Navigation.findNavController(view).navigateUp();
        });

        renderCategorySelection();
    }

    private void save() {
        viewModel.save(amountInput.getText().toString().trim(), descriptionInput.getText().toString());
    }

    private void renderCategorySelection() {
        String selected = viewModel.getSelectedCategory();
        for (int i = 0; i < categories.length; i++) {
            boolean isSelected = categories[i].equals(selected);
            categoryDots[i].setBackgroundTintList(getResources().getColorStateList(
                    isSelected ? R.color.primary : R.color.color_neutral_variant, null));
        }
    }

    private void renderPaidBySummary() {
        String payerId = viewModel.getPaidByUserId();
        String name = AppDataStore.CURRENT_USER_ID.equals(payerId) ? "You" : nameFor(payerId);
        paidBySummaryText.setText(getString(R.string.paid_by_split_equally, name));
    }

    private String nameFor(String userId) {
        User user = AppDataStore.getInstance().getUserById(userId);
        return user != null ? user.getUsername() : "";
    }

    private void renderMembers(String groupId) {
        membersRow.removeAllViews();
        List<User> members = groupRepository.getGroupMemberUsers(groupId);
        int size = (int) (36 * getResources().getDisplayMetrics().density);
        for (int i = 0; i < members.size(); i++) {
            View avatar = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            if (i > 0) params.setMarginStart((int) (-10 * getResources().getDisplayMetrics().density));
            avatar.setLayoutParams(params);
            avatar.setBackgroundResource(R.drawable.bg_circle);
            int colorRes = i % 3 == 0 ? R.color.primary : (i % 3 == 1 ? R.color.color_neutral_variant : R.color.color_outline_variant);
            avatar.setBackgroundTintList(getResources().getColorStateList(colorRes, null));
            membersRow.addView(avatar);
        }
    }

    private void showGroupPicker(View anchor) {
        List<Group> groups = viewModel.getAllGroups();
        PopupMenu menu = new PopupMenu(requireContext(), anchor);
        for (Group g : groups) {
            menu.getMenu().add(g.getName());
        }
        menu.setOnMenuItemClickListener(item -> {
            for (Group g : groups) {
                if (g.getName().contentEquals(item.getTitle())) {
                    viewModel.selectGroup(g);
                    break;
                }
            }
            return true;
        });
        menu.show();
    }

    private void showPaidByPicker(View anchor) {
        Group group = viewModel.getGroup().getValue();
        if (group == null) return;
        List<User> members = groupRepository.getGroupMemberUsers(group.getId());
        PopupMenu menu = new PopupMenu(requireContext(), anchor);
        for (User u : members) {
            menu.getMenu().add(AppDataStore.CURRENT_USER_ID.equals(u.getId()) ? "You" : u.getUsername());
        }
        menu.setOnMenuItemClickListener(item -> {
            for (User u : members) {
                String label = AppDataStore.CURRENT_USER_ID.equals(u.getId()) ? "You" : u.getUsername();
                if (label.contentEquals(item.getTitle())) {
                    viewModel.selectPaidBy(u.getId());
                    renderPaidBySummary();
                    break;
                }
            }
            return true;
        });
        menu.show();
    }
}
