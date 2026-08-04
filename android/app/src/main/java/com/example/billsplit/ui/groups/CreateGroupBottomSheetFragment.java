package com.example.billsplit.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Group;
import com.example.billsplit.data.repository.GroupRepository;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CreateGroupBottomSheetFragment extends BottomSheetDialogFragment {

    private final GroupRepository groupRepository = new GroupRepository();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_create_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText nameInput = view.findViewById(R.id.groupNameInput);
        view.findViewById(R.id.createButton).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), R.string.group_name_required, Toast.LENGTH_SHORT).show();
                return;
            }
            Group group = groupRepository.createGroup(name);
            dismiss();
            androidx.fragment.app.Fragment parent = getParentFragment();
            View parentView = parent != null ? parent.getView() : requireActivity().findViewById(android.R.id.content);
            if (parentView != null) {
                Bundle args = new Bundle();
                args.putString("groupId", group.getId());
                androidx.navigation.Navigation.findNavController(parentView)
                        .navigate(R.id.action_global_groupDetailFragment, args);
            }
        });
    }
}
