package com.example.billsplit.ui.expense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.billsplit.data.model.GroupMember;
import com.example.billsplit.data.repository.ApiCallback;
import com.example.billsplit.local.TokenManager;

import java.util.List;

public class ExpenseFormFragment extends Fragment {

    private ExpenseFormViewModel viewModel;

    private TextView formTitleText;
    private TextView groupChip;
    private EditText amountInput;
    private EditText descriptionInput;
    private TextView paidBySummaryText;
    private LinearLayout membersRow;
    private android.widget.Button saveExpenseButton;
    private TextView deleteExpenseText;

    private View[] categoryDots;
    private ImageView[] categoryIcons;
    private TextView[] categoryLabels;
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
        categoryIcons = new ImageView[]{
                view.findViewById(R.id.categoryFoodIcon), view.findViewById(R.id.categoryTransportIcon),
                view.findViewById(R.id.categoryHousingIcon), view.findViewById(R.id.categoryOtherIcon)
        };
        categoryLabels = new TextView[]{
                view.findViewById(R.id.categoryFoodLabel), view.findViewById(R.id.categoryTransportLabel),
                view.findViewById(R.id.categoryHousingLabel), view.findViewById(R.id.categoryOtherLabel)
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

        deleteExpenseText.setOnClickListener(v -> viewModel.delete());

        Bundle args = getArguments();
        String groupId = args != null ? args.getString("groupId") : null;
        String expenseId = args != null ? args.getString("expenseId") : null;
        viewModel.init(groupId, expenseId);

        boolean editMode = viewModel.isEditMode();
        formTitleText.setText(editMode ? R.string.edit_expense_title : R.string.add_expense_title);
        saveExpenseButton.setText(editMode ? R.string.save_changes : R.string.save_expense);
        deleteExpenseText.setVisibility(editMode ? View.VISIBLE : View.GONE);

        viewModel.getGroup().observe(getViewLifecycleOwner(), g -> {
            if (g != null) groupChip.setText(g.getName());
        });
        viewModel.getGroupMembers().observe(getViewLifecycleOwner(), members -> {
            renderMembers(members);
            renderPaidBySummary();
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
        viewModel.getDeleteSuccess().observe(getViewLifecycleOwner(), success -> {
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
            // Selected sits on a solid teal circle, so the glyph needs to flip to white for
            // contrast; unselected stays the same neutral icon color used everywhere else.
            categoryIcons[i].setImageTintList(getResources().getColorStateList(
                    isSelected ? R.color.white : R.color.icon_default, null));
            categoryLabels[i].setTextColor(getResources().getColor(
                    isSelected ? R.color.text_primary : R.color.text_secondary, null));
        }
    }

    private void renderPaidBySummary() {
        String payerId = viewModel.getPaidByUserId();
        String currentUserId = TokenManager.getInstance().getCurrentUserId();
        String name = payerId != null && payerId.equals(currentUserId) ? "You" : nameFor(payerId);
        paidBySummaryText.setText(getString(R.string.paid_by_split_equally, name));
    }

    private String nameFor(String userId) {
        List<GroupMember> members = viewModel.getGroupMembers().getValue();
        if (members == null || userId == null) return "";
        for (GroupMember m : members) {
            if (userId.equals(m.getUserId())) return m.getUsername();
        }
        return "";
    }

    private void renderMembers(List<GroupMember> members) {
        membersRow.removeAllViews();
        if (members == null) return;
        float density = getResources().getDisplayMetrics().density;
        int outerSize = (int) (36 * density);
        int innerSize = (int) (32 * density);

        for (int i = 0; i < members.size(); i++) {
            GroupMember member = members.get(i);
            int colorRes = i % 3 == 0 ? R.color.primary : (i % 3 == 1 ? R.color.color_neutral_variant : R.color.color_outline_variant);

            // A white ring (outer circle) behind a slightly smaller colored
            // circle keeps adjacent overlapping avatars from visually
            // merging at the seam.
            android.widget.FrameLayout wrapper = new android.widget.FrameLayout(requireContext());
            LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(outerSize, outerSize);
            if (i > 0) wrapperParams.setMarginStart((int) (-10 * density));
            wrapper.setLayoutParams(wrapperParams);
            wrapper.setBackgroundResource(R.drawable.bg_circle);
            wrapper.setBackgroundTintList(getResources().getColorStateList(R.color.color_surface, null));

            View avatar = new View(requireContext());
            android.widget.FrameLayout.LayoutParams avatarParams =
                    new android.widget.FrameLayout.LayoutParams(innerSize, innerSize, android.view.Gravity.CENTER);
            avatar.setLayoutParams(avatarParams);
            avatar.setBackgroundResource(R.drawable.bg_circle);
            avatar.setBackgroundTintList(getResources().getColorStateList(colorRes, null));
            wrapper.addView(avatar);

            TextView initial = new TextView(requireContext());
            initial.setLayoutParams(new android.widget.FrameLayout.LayoutParams(innerSize, innerSize, android.view.Gravity.CENTER));
            initial.setGravity(android.view.Gravity.CENTER);
            initial.setTextSize(12);
            initial.setTextColor(getResources().getColor(
                    colorRes == R.color.primary ? R.color.white : R.color.text_primary, null));
            String name = member.getUsername();
            initial.setText(name.isEmpty() ? "" : name.substring(0, 1).toUpperCase(java.util.Locale.US));
            wrapper.addView(initial);

            membersRow.addView(wrapper);
        }
    }

    private void showGroupPicker(View anchor) {
        viewModel.loadAllGroups(new ApiCallback<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                if (!isAdded()) return;
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

            @Override
            public void onError(String message) {
                if (isAdded()) Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPaidByPicker(View anchor) {
        List<GroupMember> members = viewModel.getGroupMembers().getValue();
        if (members == null) return;
        String currentUserId = TokenManager.getInstance().getCurrentUserId();

        PopupMenu menu = new PopupMenu(requireContext(), anchor);
        for (GroupMember m : members) {
            menu.getMenu().add(m.getUserId().equals(currentUserId) ? "You" : m.getUsername());
        }
        menu.setOnMenuItemClickListener(item -> {
            for (GroupMember m : members) {
                String label = m.getUserId().equals(currentUserId) ? "You" : m.getUsername();
                if (label.contentEquals(item.getTitle())) {
                    viewModel.selectPaidBy(m.getUserId());
                    renderPaidBySummary();
                    break;
                }
            }
            return true;
        });
        menu.show();
    }
}
